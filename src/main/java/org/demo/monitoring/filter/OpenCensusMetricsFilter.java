package org.demo.monitoring.filter;

import io.opencensus.stats.Stats;
import io.opencensus.stats.View;
import io.opencensus.stats.ViewManager;
import org.demo.monitoring.metrics.Endpoint;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

import static org.demo.monitoring.metrics.OpenCensusMetrics.*;


public class OpenCensusMetricsFilter implements Filter {

    private static final String UNKNOWN_LABEL_VALUE = "unknown";
    static ViewManager vmgr;

    public OpenCensusMetricsFilter() {
        this.vmgr = Stats.getViewManager();
        registerAllViews(vmgr,
                new View[]{
                        createApplicationLatencyView(),
                        createApplicationResponseView(),

                });
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;

        String path = request.getRequestURI();
        String resourceName = Endpoint.getEndpoint(path).map(Enum::name).orElse(path);

        String territory = getParamOrDefault(request, UNKNOWN_LABEL_VALUE);
        String header = getHeaderOrDefault(request, "Test-Header", UNKNOWN_LABEL_VALUE);

        long startTimeNs = System.nanoTime();

        try {
            chain.doFilter(request, response);
        } finally {
            String[] tagValues = { resourceName, territory, header,
                                   statusCodeFrom((HttpServletResponse) response)};

            addApplicationResponse(tagValues);
            addApplicationLatency(tagValues,
                    sinceInMilliseconds(startTimeNs));
        }
    }
    private String getHeaderOrDefault(HttpServletRequest request, String headerName, String defaultValue) {
        return Optional.ofNullable(request.getHeader(headerName)).map(String::toLowerCase).orElse(defaultValue);
    }

    private String getParamOrDefault(HttpServletRequest request, String defaultValue) {
        return Optional.ofNullable(request.getQueryString()).map(String::toLowerCase).orElse(defaultValue);
    }
    private String statusCodeFrom(HttpServletResponse response) {
        return response.getStatus() + "";
    }
}
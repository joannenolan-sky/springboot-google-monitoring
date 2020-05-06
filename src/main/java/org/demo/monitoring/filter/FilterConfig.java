package org.demo.monitoring.filter;

import io.opencensus.contrib.http.servlet.OcHttpServletFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    public FilterConfig() { }

    @Bean
    public FilterRegistrationBean<OpenCensusMetricsFilter> applicationResponsesMetricsFilterRegistrationBean() {
        FilterRegistrationBean<OpenCensusMetricsFilter> metricsFilterRegistrationBean = new FilterRegistrationBean<>();
        metricsFilterRegistrationBean.setFilter(new OpenCensusMetricsFilter());
        metricsFilterRegistrationBean.setOrder(1);

        return metricsFilterRegistrationBean;
    }

    @Bean
    public FilterRegistrationBean<OcHttpServletFilter> loggingFilter() {
        FilterRegistrationBean<OcHttpServletFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new OcHttpServletFilter());
        registrationBean.addUrlPatterns("/*");

        return registrationBean;
    }
}

package org.demo.monitoring;

import com.google.api.MonitoredResource;
import io.opencensus.contrib.http.util.HttpViews;
import io.opencensus.exporter.stats.prometheus.PrometheusStatsCollector;
import io.opencensus.exporter.stats.stackdriver.StackdriverStatsConfiguration;
import io.opencensus.exporter.stats.stackdriver.StackdriverStatsExporter;
import io.prometheus.client.exporter.HTTPServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@SpringBootApplication
public class SpringMonitoringApplication {

    public static void main(String[] args) {
        try {
            setupPrometheusExporter();
//            googleStackdriver();
        } catch (IOException e) {
            System.err.println("Failed to create and register OpenCensus Prometheus Stats exporter " + e);
            return;
        }
        SpringApplication.run(SpringMonitoringApplication.class, args);
    }

    // This is for running Locally so you can see the metrics in Prometheus
    public static void setupPrometheusExporter() throws IOException {
        // Create and register the Prometheus exporter
        PrometheusStatsCollector.createAndRegister();

        // Run the server as a daemon on address "localhost:8889"
        HTTPServer server = new HTTPServer("localhost", 8889, true);
    }


    // This method will push the metrics to StackDriver Monitoring
    // Useful for Cloud Applications where the metrics endpoint is not available as you cannot see each instance
    public static void googleStackdriver() throws IOException {
        HttpViews.registerAllServerViews();
        MonitoredResource myResource = MonitoredResource.newBuilder()
                .setType("generic_node")
                .putLabels("node_id", Optional.ofNullable(System.getenv("K_REVISION")).orElse("LOCAL") + "-" + UUID.randomUUID().toString())
                .putLabels("namespace", "phoenix-sandbox-one")
                .putLabels("location", "europe-west1")
                .putLabels("project_id", "phoenix-sandbox-one")
                .build();
        StackdriverStatsExporter.createAndRegister(
                StackdriverStatsConfiguration.builder()
                        .setMonitoredResource(myResource)
                        .build());
    }
}

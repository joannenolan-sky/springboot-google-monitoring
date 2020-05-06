package org.demo.monitoring.metrics;

import io.opencensus.common.Scope;
import io.opencensus.stats.*;
import io.opencensus.stats.Aggregation.Distribution;
import io.opencensus.stats.Measure.MeasureDouble;
import io.opencensus.stats.Measure.MeasureLong;
import io.opencensus.stats.View.Name;
import io.opencensus.tags.*;

import java.util.Arrays;
import java.util.List;

public class OpenCensusMetrics {


    static Aggregation latencyDistribution = Distribution.create(BucketBoundaries.create(
            Arrays.asList(
                    // [>=0ms, >=25ms, >=50ms, >=75ms, >=100ms, >=200ms, >=400ms, >=600ms, >=800ms, >=1s]
                    0.0, 25.0, 50.0, 75.0, 100.0, 200.0, 400.0, 600.0, 800.0, 1000.0)
    ));

    static Aggregation countAggregation = Aggregation.Count.create();

    public static final TagKey RESOURCE_NAME = TagKey.create("resourceName");
    public static final TagKey COUNTRY = TagKey.create("country");
    public static final TagKey TEST = TagKey.create("testHeader");
    public static final TagKey STATUS = TagKey.create("status");

    public static final MeasureDouble M_LATENCY_MS = MeasureDouble.create("repl/latency", "The latency in milliseconds for each response", "ms");
    public static final MeasureLong M_COUNT = MeasureLong.create("repl/response", "The distribution of response", "By");

    private static final Tagger tagger = Tags.getTagger();
    private static final StatsRecorder statsRecorder = Stats.getStatsRecorder();

    static TagKey[] keys = {RESOURCE_NAME, COUNTRY, TEST, STATUS};
    static List<TagKey> labelNames = Arrays.asList(keys);

    public static void addApplicationResponse(String[] values) {
        incrementMetric(keys, values, statsRecorder.newMeasureMap().put(M_COUNT, 1));
    }

    public static void addApplicationLatency(String[] values, Double d) {
        incrementMetric(keys, values, statsRecorder.newMeasureMap().put(M_LATENCY_MS, d));
    }

    private static void incrementMetric(TagKey[] keys, String[] values, MeasureMap put) {
        TagContextBuilder builder = tagger.emptyBuilder();
        for (int i = 0; i < keys.length; i++) {
            builder.put(keys[i], TagValue.create(values[i]));
        }
        TagContext tctx = builder.build();

        try (Scope ss = tagger.withTagContext(tctx)) {
            put.record();
        }
    }

    public static double sinceInMilliseconds(long startTimeNs) {
        return ((double) (System.nanoTime() - startTimeNs)) / 1e6;
    }

    public static void registerAllViews(ViewManager vmgr, View[] views) {
        // Then finally register the views
        for (View view : views) {
            vmgr.registerView(view);
        }
    }

    public static View createApplicationResponseView() {
        return View.create(Name.create("application/response"),
                "The distribution of line lengths",
                M_COUNT,
                countAggregation,
                labelNames);
    }

    public static View createApplicationLatencyView() {
        return View.create(Name.create("application/latency"),
                "The distribution of latencies",
                M_LATENCY_MS,
                latencyDistribution,
                labelNames);
    }
}
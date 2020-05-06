package org.demo.monitoring.metrics;

import java.util.Arrays;
import java.util.Optional;

import static java.util.regex.Matcher.quoteReplacement;
import static org.springframework.http.HttpMethod.*;

public enum Endpoint {

    GET_COUNTRY("/territory", PathPattern.PATH_PATTERN_WITH_PARAMS),
    GET_HELLO("/hello");

    private String path;
    private String regexPathPattern;

    Endpoint(String path, String template) {
        this.path = path;
        this.regexPathPattern = PathPattern.generatePathPattern(path, template);
    }

    Endpoint(String path) {
        this(path, PathPattern.PATH_PATTERN);
    }

    public static Optional<Endpoint> getEndpoint(String path) {
        return Arrays.stream(Endpoint.values())
                .filter(endpoint -> path.matches(endpoint.getRegexPathPattern()))
                .findFirst();
    }

    private String getRegexPathPattern() {
        return regexPathPattern;
    }

    private static class PathPattern {
        private static final String PATH_PATTERN = "^.*(%s)$";
        private static final String PATH_PATTERN_WITH_PARAMS = "^.*(%s)(\\?.*)?$";

        private static String generatePathPattern(String path, String basePattern) {
            return String.format(basePattern, path.replaceAll("/", quoteReplacement("\\/")));
        }
    }
}

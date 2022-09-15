package com.maleg.server.http;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class HttpServerRequest {

    private static final Pattern MAIN_HEADER_PATTERN = Pattern.compile("^(?<method>[A-Z]{3,10}) (?<path>[^ ]+) (?<protocol>\\S+)");
    private static final Pattern HEADERS_BODY_PATTERN = Pattern.compile("(?<header>(?<key>[A-Za-z0-9-]+): (?<values>[^\\n]+\\n))(?<body>.*)");

    private final String method;
    private final String path;
    private final String protocol;
    private final Map<String, List<String>> headers;
    private final String body;

    private HttpServerRequest(String method, String path, String protocol, Map<String, List<String>> headers, String body) {
        this.method = method;
        this.path = path;
        this.protocol = protocol;
        this.headers = headers;
        this.body = body;
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getProtocol() {
        return protocol;
    }

    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

    public static HttpServerRequest create(InputStream is) throws Exception {

        int availableBytes = is.available();
        if (availableBytes == 0) {
            throw new IOException("Input stream is empty");
        }
        final var bytes = new byte[availableBytes];
        is.read(bytes);
        final var content = new String(bytes);

        final var mhMatcher = MAIN_HEADER_PATTERN.matcher(content);
        if (!mhMatcher.find()) {
            throw new IllegalArgumentException("Illegal request");
        }
        final var protocol = mhMatcher.group("protocol");
        if (!protocol.contains("HTTP")) {
            throw new IllegalArgumentException("Request isn't request with HTTP protocol");
        }
        final var method = mhMatcher.group("method");
        final var path = mhMatcher.group("path");

        final var hbMatcher = HEADERS_BODY_PATTERN.matcher(content);
        final var headers = new HashMap<String, List<String>>();
        String body = null;
        while (hbMatcher.find()) {
            final var key = hbMatcher.group("key");
            final var values = Arrays.stream(hbMatcher.group("values").split(";")).toList();
            headers.put(key, values);
            body = hbMatcher.group("body");
            if (body != null) {
                break;
            }
        }

        return new HttpServerRequest(method, path, protocol, headers, body == null ? "" : body);
    }
}

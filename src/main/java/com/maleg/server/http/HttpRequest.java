package com.maleg.server.http;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.regex.Pattern;

public record HttpRequest(String method, String path, String protocol,
                          HttpHeaders headers, String body, String content) {

    private static final Pattern MAIN_HEADER_PATTERN = Pattern.compile("^(?<method>[A-Z]{3,10}) (?<path>[^ ]+) (?<protocol>\\S+)");
    private static final Pattern HEADERS_PATTERN = Pattern.compile("(?<header>(?<key>[A-Za-z\\d-]+): (?<values>[^\\n]+)\\n)");

    public static HttpRequest create(InputStream is) throws Exception {

        int availableBytes = is.available();
        if (availableBytes == 0) {
            throw new IOException("Input stream is empty");
        }
        final var bytes = new byte[availableBytes];
        is.read(bytes);
        final var content = new String(bytes);

        final var builder = new Builder();
        builder.content(content);
        builder.body(content.substring(
                initHeaders(builder, initMainHeader(builder))).trim());

        return builder.build();
    }

    private static int initMainHeader(Builder builder) {

        final var mhMatcher = MAIN_HEADER_PATTERN.matcher(builder.content);
        if (!mhMatcher.find()) {
            throw new IllegalArgumentException("Illegal request");
        }

        builder.method(mhMatcher.group("protocol"))
                .path(mhMatcher.group("path"))
                .protocol(mhMatcher.group("method"));

        return mhMatcher.end();
    }

    private static int initHeaders(Builder builder, int start) {

        final var headers = new HttpHeaders();
        final var hbMatcher = HEADERS_PATTERN.matcher(builder.content);

        int offset = start;
        while (hbMatcher.find(offset)) {
            final var key = hbMatcher.group("key");
            final var values = Arrays.stream(
                    hbMatcher.group("values")
                            .trim()
                            .replace("; ", "!@#%^&*()_+")
                            .split(";"))
                    .map(value -> value.replace("!@#%^&*()_+", "; "))
                    .toArray(String[]::new);
            headers.add(key, values);
            offset = hbMatcher.end() - 1;
        }

        builder.headers(headers);

        return offset;
    }

    private static class Builder {
        private String method;
        private String path;
        private String protocol;
        private HttpHeaders headers;
        private String body;
        private String content;

        private Builder method(String value) {
            this.method = value;
            return this;
        }

        private Builder path(String value) {
            this.path = value;
            return this;
        }

        private Builder protocol(String value) {
            this.protocol = value;
            return this;
        }

        private Builder headers(HttpHeaders value) {
            this.headers = value;
            return this;
        }

        private Builder body(String value) {
            this.body = value;
            return this;
        }

        private Builder content(String value) {
            this.content = value;
            return this;
        }

        private HttpRequest build() {
            return new HttpRequest(this.method, this.path, this.protocol,
                    this.headers, this.body, this.content);
        }
    }
}

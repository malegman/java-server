package com.maleg.server.http;

public record HttpResponse(String protocol, HttpResponseStatus status,
                           HttpHeaders headers, String body) {

    public String content() {

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.protocol).append(" ")
                .append(this.status.code()).append(" ")
                .append(this.status.status()).append("\r\n");

        this.headers.getHeaders().forEach((key, values) -> {
            stringBuilder.append(key).append(": ");
            values.forEach(value -> stringBuilder.append(value).append(";"));
            stringBuilder.deleteCharAt(stringBuilder.lastIndexOf(";")).append("\r\n");
        });

        if (this.body != null) {
            stringBuilder.append("\r\n").append(this.body);
        }

        return stringBuilder.toString();
    }

    public static HttpResponseBuilder builder() {
        return new HttpResponseBuilder();
    }

    public static class HttpResponseBuilder {

        private String protocol;
        private HttpResponseStatus status;
        private final HttpHeaders headers = new HttpHeaders();
        private String body;

        public HttpResponseBuilder protocol(String value) {
            this.protocol = value;
            return this;
        }

        public HttpResponseBuilder status(HttpResponseStatus value) {
            this.status = value;
            return this;
        }

        public HttpResponseBuilder header(String key, String... values) {
            headers.add(key, values);
            return this;
        }

        public HttpResponseBuilder body(String value) {
            this.body = value;
            if (this.body.length() > 0) {
                this.header("Content-Length", String.valueOf(this.body.length()));
            }
            return this;
        }

        public HttpResponse build() {
            return new HttpResponse(this.protocol, this.status, this.headers, this.body);
        }
    }
}

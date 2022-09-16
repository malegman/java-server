package com.maleg.server.http;

public enum HttpResponseStatus {
    HRS_OK(200, "OK"),
    HRS_NO_CONTENT(204, "No Content"),
    HRS_NOT_FOUND(404, "Not Found");

    private final int code;
    private final String status;

    HttpResponseStatus(int code, String status) {
        this.code = code;
        this.status = status;
    }

    public int code() {
        return this.code;
    }

    public String status() {
        return this.status;
    }
}

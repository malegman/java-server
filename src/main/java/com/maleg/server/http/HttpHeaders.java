package com.maleg.server.http;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpHeaders {

    private final Map<String, List<String>> headers = new HashMap<>();

    public Map<String, List<String>> getHeaders() {
        return this.headers;
    }

    public void add(String key, String... values) {
        if (!this.headers.containsKey(key)) {
            this.headers.put(key, List.of(values));
        } else {
            this.headers.computeIfPresent(key, (k, v) -> {
                v.addAll(List.of(values));
                return v.stream().distinct().toList();
            });
        }
    }

    public void remove(String key) {
        this.headers.remove(key);
    }

    public void remove(String key, String value) {
        this.headers.computeIfPresent(key, (k, v) -> {
            v.remove(value);
            return v;
        });
    }
}

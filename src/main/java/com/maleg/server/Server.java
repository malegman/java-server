package com.maleg.server;

import com.maleg.server.http.HttpServer;

public abstract class Server {

    protected final String host;
    protected final int port;

    public Server(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public static Server create(ServerType serverType, String host, int port) {

        if (serverType == null) {
            throw new IllegalArgumentException("Server type is null");
        }
        switch (serverType) {
            case HTTP -> {
                return new HttpServer(host, port);
            }
            default -> throw new IllegalArgumentException("Server type not support");
        }
    }

    abstract public void start() throws Exception;
}

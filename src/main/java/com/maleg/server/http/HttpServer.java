package com.maleg.server.http;

import com.maleg.server.Server;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.charset.StandardCharsets;

public final class HttpServer extends Server {

    private final static HttpResponse DEFAULT_HTTP_RESPONSE = HttpResponse.builder()
            .protocol("HTTP/1.1")
            .status(HttpResponseStatus.HRS_NOT_FOUND)
            .header("Content-Type", "text/html")
            .header("Connection", "keep-alive")
            .body("""
                        <html>
                        <head><title>404 Not Found</title></head>
                        <body bgcolor="white">
                        <center><h1>404 Not Found</h1></center>
                        </body>
                        </html>""")
            .build();

    public HttpServer(String host, int port) {
        super(host, port);
    }

    @Override
    public void start() throws Exception {

        try (final var serverSocketChannel = ServerSocketChannel.open()) {

            serverSocketChannel.bind(new InetSocketAddress(this.host, this.port));
            serverSocketChannel.configureBlocking(true);

            while (true) {

                final var socketChannel = serverSocketChannel.accept();
                if (socketChannel != null) {

                    final var socket = socketChannel.socket();
                    final var sis = socket.getInputStream();
                    final var sos = socket.getOutputStream();

                    final var available = sis.available();
                    if (available != 0) {
                        final var request = HttpRequest.create(sis);

                        // TODO: Handle request

                        this.sendResponse(sos, DEFAULT_HTTP_RESPONSE);
                    }

                    sis.close();
                    sos.close();
                    socket.close();
                }
            }
        }
    }

    private void sendResponse(OutputStream os, HttpResponse response) throws IOException {

        os.write(response.content().getBytes(StandardCharsets.UTF_8));
        os.flush();
    }
}

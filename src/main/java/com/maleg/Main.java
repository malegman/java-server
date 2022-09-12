package com.maleg;

import com.maleg.server.http.HttpServerRequest;

import java.net.InetSocketAddress;
import java.net.StandardProtocolFamily;
import java.nio.channels.ServerSocketChannel;

public class Main {

    public static void main(String[] args) throws Exception {

        try (final var serverSocketChannel = ServerSocketChannel.open(StandardProtocolFamily.INET)
                .bind(new InetSocketAddress("192.168.0.102", 8080))) {

            serverSocketChannel.configureBlocking(false);

            while (true) {

                final var socketChannel = serverSocketChannel.accept();
                if (socketChannel != null) {

                    final var socket = socketChannel.socket();
                    final var sis = socket.getInputStream();
                    final var sos = socket.getOutputStream();

                    final var request = HttpServerRequest.create(sis);

                    sos.close();
                    socket.close();
                }
            }
        }
    }
}

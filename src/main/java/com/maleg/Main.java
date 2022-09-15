package com.maleg;

import com.maleg.server.http.HttpServerRequest;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.StandardProtocolFamily;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class Main {

    public static void main(String[] args) {

        try (Selector selector = Selector.open();
             ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {

            serverSocketChannel.configureBlocking(false);

            InetSocketAddress inetSocketAddress = new InetSocketAddress("192.168.0.102", 8080);
            serverSocketChannel.bind(inetSocketAddress);

            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            while (true) {
                //System.out.println(selector.isOpen());
                if (selector.select() == 0) {
                    continue;
                }

                Set<SelectionKey> readyKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = readyKeys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();

                    if (key.isAcceptable()) {
                        SocketChannel client = serverSocketChannel.accept();
                        client.configureBlocking(false);
                        client.register(selector, SelectionKey.OP_READ);
                    }

                    if (key.isReadable()) {
                        try (SocketChannel client = (SocketChannel) key.channel()) {
                            int BUFFER_SIZE = 1024;
                            ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
                            client.read(buffer);
                            byte[] bytes = new byte[buffer.position()];
                            buffer.flip().get(bytes);
                            String content = new String(bytes);
                            System.out.println(content);
                            //selector.wakeup();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    iterator.remove();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        /*try (final var serverSocketChannel = ServerSocketChannel.open(StandardProtocolFamily.INET)
                .bind(new InetSocketAddress("192.168.0.102", 8080))) {

            serverSocketChannel.configureBlocking(true);

            while (true) {

                final var socketChannel = serverSocketChannel.accept();
                if (socketChannel != null) {

                    final var socket = socketChannel.socket();
                    System.out.println(socket.isClosed());
                    final var sis = socket.getInputStream();
                    final var sos = socket.getOutputStream();

                    try {
                        final var request = HttpServerRequest.create(sis);
                        System.out.println(request.getBody());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    sis.close();
                    sos.close();
                    socket.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }
}

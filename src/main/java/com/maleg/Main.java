package com.maleg;

import com.maleg.server.Server;
import com.maleg.server.ServerType;

public class Main {

    private static final String HOST = "localhost";
    private static final int PORT = 8080;

    public static void main(String[] args) {

        try {
            Server server = Server.create(ServerType.HTTP, HOST, PORT);
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

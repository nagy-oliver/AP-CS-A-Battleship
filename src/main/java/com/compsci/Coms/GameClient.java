package com.compsci.Coms;
import java.io.IOException;
import java.net.*;

public class GameClient extends Socket {
    public GameClient(String host, int port) throws UnknownHostException, IOException {
        super(host, port);
    }
}

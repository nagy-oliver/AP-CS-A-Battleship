package com.compsci.Coms;
import java.io.IOException;

import java.net.*;

import com.compsci.Utils;

import java.io.*; 

public class GameClient extends Socket {
    private PrintWriter out;
    private BufferedReader in;

    public GameClient(String host, int port) throws UnknownHostException, IOException {
        super(host, port);

        // Init streams
        PrintWriter out = new PrintWriter(getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(getInputStream()));
        
        // Check data transfer  (order S-R)
        out.println(Utils.testBundle);
        if (Utils.testBundle.equals(in.readLine())) {
            System.out.println("Recognized test greeting");
        }
        else {
            System.out.println("Unrecognized test greeting");
        }
    }
}

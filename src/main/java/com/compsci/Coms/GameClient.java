package com.compsci.Coms;
import java.io.IOException;

import java.net.*;

import com.compsci.Config;
import com.compsci.Utils;

import java.io.*; 
import org.slf4j.*;

public class GameClient extends Socket {
    private PrintWriter out;
    private BufferedReader in;

    public GameClient(String host, int port) throws UnknownHostException, IOException {
        super(host, port);
        Logger logger = LoggerFactory.getLogger(GameClient.class);
        

        // Init streams
        PrintWriter out = new PrintWriter(getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(getInputStream()));
        
        // Check data transfer  (order S-R)
        out.println(Utils.testBundle);
        if (Utils.testBundle.equals(in.readLine())) {
            logger.debug("Recognized test greeting");
        }
        else {
            logger.debug("Unrecognized test greeting");
        }

        logger.info("Successfully connected to server");

        // Send config for validation
        ObjectOutputStream oos = new ObjectOutputStream(getOutputStream());
        oos.writeObject(new Config());

    }
}

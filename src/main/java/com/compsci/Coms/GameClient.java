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
    ObjectOutputStream oos;
    // Classwide logger
    Logger logger;

    public GameClient(String host, int port) throws UnknownHostException, IOException {
        super(host, port);
        logger = LoggerFactory.getLogger(GameClient.class);
        

        // Init streams
        out = new PrintWriter(getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(getInputStream()));
        
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
        oos = new ObjectOutputStream(getOutputStream());
        oos.writeObject(new Config());
        oos.flush();

        // After initial check enter permanent reciever thread
        Thread commands = new Thread() {
            public void run() {
                while(true) {
                    try {
                        String data = in.readLine();
                        if (data == null) continue;
                        logger.debug(data);
                        String[] splitPacket = data.split(" ");

                        switch(splitPacket[0]) {
                            case "REQ_CONF":
                                oos.writeObject(new Config());
                                oos.flush();
                                break;
                        }

                    } catch (IOException e) { }
                }
            }
        };
        commands.start(); 
    }
}

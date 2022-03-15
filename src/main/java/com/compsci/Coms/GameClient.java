package com.compsci.Coms;
import java.io.IOException;

import java.net.*;
import java.util.*;

import com.compsci.Board;
import com.compsci.Config;
import com.compsci.Utils;

import java.io.*; 
import org.slf4j.*;

public class GameClient extends Socket {
    // coms
    private PrintWriter out;
    private BufferedReader in;
    private ObjectOutputStream oos;

    // conn
    private Socket dataTransfer;

    // data
    private Config localConfig;
    private Board localPlacement;
    public boolean started; 

    // Classwide logger
    Logger logger;

    public GameClient(String host, int port) throws UnknownHostException, IOException, InterruptedException {
        super(host, port);
        logger = LoggerFactory.getLogger(GameClient.class);

        // init data
        localConfig = new Config();
        localPlacement = new Board(localConfig.size);

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

        dataTransfer = new Socket(host, port);

        // Send config for validation
        oos = new ObjectOutputStream(dataTransfer.getOutputStream());

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
                            case "START":
                                started = true;
                                Utils.Clear();
                                System.out.println("Starting game! Random selected player to start: " + splitPacket[1] + " (0-Server, 1-Client)");

                            case "REQ_CONF":
                                localConfig = new Config();
                                oos.writeUnshared(localConfig);
                                oos.flush();
                                break;
                            case "REQ_BOARD":
                                localPlacement = new Board(localConfig.size);
                                oos.writeUnshared(localPlacement);
                                oos.flush();
                                break;
                            case "TERM":
                                System.exit(Integer.parseInt(splitPacket[1]));
                                break;
                            case "INV_SBOARD":
                                System.out.println("Opponents board doesnt match the config requirements!");
                                break;
                            case "INV_BOARD":
                                System.out.println("Your board doesnt match the config requirements!");
                                break;
                        }
                    } catch (EOFException e) { } catch (IOException e) { } 
                }

            }
        };
        commands.start(); 
    
        EnterCommandLoop();
    }

    void EnterCommandLoop() throws IOException {
        // Enter command loop
        Scanner input = new Scanner(System.in);
        while(true) {
            System.out.print(">: ");
            
            String data = input.nextLine();
            if (data == null) continue;
            String[] splitCommand = data.split(" ");

            // Handle data logic 
            switch(splitCommand[0]) {
                case "move":

                    break;
            }
        }
    }
}

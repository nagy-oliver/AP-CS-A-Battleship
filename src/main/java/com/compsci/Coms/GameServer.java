package com.compsci.Coms;
import com.compsci.Config;
import com.compsci.Utils;

import java.net.InetAddress;
import java.net.NetworkInterface;

import java.net.*;
import java.io.*;
import java.util.*; 

import org.slf4j.*;

public class GameServer extends ServerSocket {
    boolean isInitialized;
    boolean isStarted;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
       
    public GameServer(int port) throws IOException {
        super(port);
        Logger logger = LoggerFactory.getLogger(GameServer.class);
    
        // Setup host
        try{
            String addr = "";
            Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
            while(en.hasMoreElements()){
                NetworkInterface ni=(NetworkInterface) en.nextElement();
                Enumeration<InetAddress> ee = ni.getInetAddresses();
                while(ee.hasMoreElements()) {
                    InetAddress ia= (InetAddress) ee.nextElement();
                    String res = ia.getHostAddress();
                    if (Utils.IPv4ValidatorRegex.isValid(res) && !ia.isLoopbackAddress()) {
                        addr = res;
                    }
                }
            }
            logger.info("You are now hosting a game on [ " +  addr.trim() + " ]");
        } catch (SocketException a) {
            logger.error("Creating server failed, check connection.");
            System.exit(1);
        }

        // WAIT FOR 2-ND PLAYER TO JOIN
        clientSocket = accept();
        logger.info("Second player succesfully joined the game.");

        // Init streams
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        
        // Check data transfer (order R-S)
        if (Utils.testBundle.equals(in.readLine())) {
            logger.debug("Recognized test greeting");
        }
        else {
            logger.debug("Unrecognized test greeting");
        }
        out.println(Utils.testBundle);

        // Compatibility check
        ObjectInputStream is = new ObjectInputStream(clientSocket.getInputStream());
        try {
            Config returnMessage = (Config) is.readObject();
        } catch(ClassNotFoundException exc) {
            
        }
    }

    public void startGame() {

    }

    @Override
    public void close() throws IOException {
        in.close();
        out.close();
        clientSocket.close();
        super.close();
    }
}
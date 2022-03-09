package com.compsci.Coms;
import com.compsci.Utils;

import java.net.InetAddress;
import java.net.NetworkInterface;

import java.net.*;
import java.io.*;
import java.util.*; 

import org.slf4j.*;

public class GameServer extends ServerSocket implements ILoggerFactory {
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
            System.out.println("You are now hosting a game on [ " +  addr.trim() + " ]");
        } catch (SocketException a) {
            System.out.println("Creating server failed, check connection.");
            System.exit(1);
        }

        // WAIT FOR 2-ND PLAYER TO JOIN
        clientSocket = accept();
        System.out.println("Second player succesfully joined the game.");

        // Init streams
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        
        // Check data transfer (order R-S)
        if (Utils.testBundle.equals(in.readLine())) {
            logger.info("Recognized test greeting");
        }
        else {
            logger.info("Unrecognized test greeting");
        }
        out.println(Utils.testBundle);
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

    @Override
    public Logger getLogger(String name) {
        // TODO Auto-generated method stub
        return null;
    }
}
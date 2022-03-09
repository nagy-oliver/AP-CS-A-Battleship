package com.compsci.Coms;
import com.compsci.Utils;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.*;
import java.io.*;
import java.util.*;  

public class GameServer extends ServerSocket {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    public GameServer(int port) throws IOException {
        super(port);
    }

    public void start(int port) throws IOException  {

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

        }

        clientSocket = serverSocket.accept();

        // Init streams
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        
        String greeting = in.readLine();
        System.out.println(greeting); 
        if ("hello server".equals(greeting)) {
            out.println("hello client");
        }
        else {
            out.println("unrecognised greeting");
        }
    }

    public void stop() throws IOException {
        in.close();
        out.close();
        clientSocket.close();
        serverSocket.close();
    }
}
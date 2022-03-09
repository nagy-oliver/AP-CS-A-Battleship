package com.compsci;
import org.json.*;
import java.util.*;  
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.*;
import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class App 
{

    public static class IPv4ValidatorRegex {
        
        private static final String IPV4_PATTERN =
                "^(([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])(\\.(?!$)|$)){4}$";
        
        private static final Pattern pattern = Pattern.compile(IPV4_PATTERN);
        
        public static boolean isValid(final String a) {
            Matcher matcher = pattern.matcher(a);
            return matcher.matches();
        }
    }

    public class GameServer {
        private ServerSocket serverSocket;
        private Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;
    
        public void start(int port) throws IOException  {

            // Setup host
            serverSocket = new ServerSocket(port);
            try{
                String addr = "";
                Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
                while(en.hasMoreElements()){
                    NetworkInterface ni=(NetworkInterface) en.nextElement();
                    Enumeration<InetAddress> ee = ni.getInetAddresses();
                    while(ee.hasMoreElements()) {
                        InetAddress ia= (InetAddress) ee.nextElement();
                        String res = ia.getHostAddress();
                        if (IPv4ValidatorRegex.isValid(res) && !ia.isLoopbackAddress()) {
                            addr = res;
                        }
                    }
                }
                System.out.println("You are now hosting a game on [ " +  addr.trim() + " ]");
            } catch (SocketException a) {
                System.out.println("Connection error, check your internet connection");
            }
            clientSocket = serverSocket.accept();

            // Init streams
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            
            String greeting = in.readLine();
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
    
    public static void main( String[] args )
    {
        Board test1 = new Board();
        App instance = new App();

        // SELECTION
        Scanner input = new Scanner(System.in);
        System.out.println("Welcome to the Battleship game. Please make a choice.");
        System.out.println("(1) Host a LAN game");
        System.out.println("(2) Join a LAN game");
        int choice = 0;
        while(choice != 1 && choice != 2) {
            choice = input.nextInt();  
        }

        // Clear 
        System.out.print("\033[H\033[2J");
        System.out.flush();

        // SOCKET INIT
        switch(choice) {
            case 1:
                // Start the server
                try {
                    GameServer server = instance.new GameServer();
                    server.start(16333);
                } catch(IOException exc) {
                    System.out.println("An error occured");
                    System.exit(1);
                }

                break;
            case 2:
                System.out.println("Please type in the ip adress of the host:");
                String ip = input.next();
                try {
                    Socket clientSocket = new Socket(ip, 16333);
                } catch(IOException exc) {
                    System.out.println("An error occured: " + exc.toString());
                    System.exit(1);
                }
                break;
        }

        input.close();
    }
}

class Board {
    public File myShips;
    public File opponentShips;
    public Board() {
        try {
            myShips = new File("myships.game");
            opponentShips = new File("target/opponentships.game");
            if(opponentShips.createNewFile()) {
                System.out.println("Created new file: " + opponentShips.getName());
            } else {
                System.out.println("the file already exists");
            }
        } catch (IOException e) {
            System.out.println("an error occured");
            e.printStackTrace();
        } 
    }

}
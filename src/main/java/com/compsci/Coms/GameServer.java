package com.compsci.Coms;
import com.compsci.Board;
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
    boolean isValidated;
    boolean isStarted;
    Board gameState;

    // client
    private Socket clientSocket;
    private Socket clientDataTransfer;
    private Config localConfig;
    private Board placement;

    // com
    private PrintWriter out;
    private BufferedReader in;
    private ObjectInputStream is;
    

    // classwide logger
    Logger logger;
       
    public GameServer(int port) throws IOException {
        super(port);
        logger = LoggerFactory.getLogger(GameServer.class);

        TransmissionInit();
        Validate();

        logger.debug("All initial setup completed and ready");
        isInitialized = true; // init flag trigger

        if (isValidated) Greet();
        else System.out.println("Config check or board validation failed. Cannot start; (You can use command 'retry' to retry)");

        EnterCommandLoop();
    }

    void Validate() throws IOException {
        // (re)generate
        localConfig = new Config();
        placement = new Board(localConfig.size);

        // Compatibility check
        try {
            out.println("REQ_CONF");
            Config returnConfig = (Config) is.readUnshared();
            if (returnConfig.compareConfigs(localConfig) ) {
                // After conf loaded, check board 
                out.println("REQ_BOARD");
                Board returnBoard = (Board) is.readUnshared();
                if (localConfig.validate(returnBoard)) {
                    if (localConfig.validate(placement)) {
                        isValidated = true;
                        System.out.println("Validated");
                    } else {
                        System.out.println("Your board doesnt match the config requirements!");
                        out.println("INV_SBOARD");
                    }
                } else {
                    System.out.println("Opponents board doesnt match the config requirements!");
                    out.println("INV_BOARD");
                }
            } else {
                System.out.println("Your configurations do not match! ");
                out.println("INV_CONF");
            }
            
        } catch (EOFException e) { } catch(ClassNotFoundException exc) {
            System.out.println("An error occored with config transfer, was it loaded properly?");
        }
    }

    void TransmissionInit() throws IOException {
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

        clientDataTransfer = accept();
        logger.debug("Second player datastream joined the game.");
        is = new ObjectInputStream(clientDataTransfer.getInputStream());
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
                case "start":
                    break;
                case "finish":
                    break;
                case "retry":
                    Validate();
                    if (isValidated) Greet();
                    break;
                case "exit":
                    input.close();
                    out.println("TERM 1");
                    System.out.println("Goodbye");
                    System.exit(1);
                    break;

            }
        }
    }

    void EnterDataLoop() {

        // After initial check enter permanent reciever thread
        Thread commands = new Thread() {
            public void run() {
                while(true) {
                    try {
                        String data = in.readLine();
                        if (data.equals(null)) continue;

                        // Handle data logic 
                        switch(data) {

                        }

                        System.out.println(data);
                    } catch (IOException e) { }
                }
            }
        };
        commands.start(); 
    }

    void Greet() {
        System.out.println("======================================================");
        System.out.println("||                   Game Is Ready                  ||");
        System.out.println("======================================================");
        System.out.println("When ready please start the game with 'start' command:");

        // Receive
        EnterDataLoop();
    }

    @Override
    public void close() throws IOException {
        in.close();
        out.close();
        clientSocket.close();
        super.close();
    }
}
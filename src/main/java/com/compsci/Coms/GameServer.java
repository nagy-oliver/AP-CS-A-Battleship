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
    private Board placementServer;
    private Board placementClient;

    // com
    private PrintWriter out;
    private BufferedReader in;
    private ObjectInputStream is;
    private ObjectOutputStream os;

    // game
    public Game serverPlayer;
    public Game clientPlayer;
    public Player move = Player.SERVER;
    private boolean okFlag;
    
    public enum Player {
        SERVER(0), CLIENT(1);
    
        private final int value;
        private Player(int value) {
            this.value = value;
        }
    
        public int getValue() {
            return value;
        }
    }


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
        placementServer = new Board(localConfig.size);

        // Compatibility check
        try {
            out.println("REQ_CONF");
            Config returnConfig = (Config) is.readUnshared();
            if (returnConfig.compareConfigs(localConfig) ) {
                // After conf loaded, check board 
                out.println("REQ_BOARD");
                placementClient = (Board) is.readUnshared();
                if (localConfig.validate(placementClient)) {
                    if (localConfig.validate(placementServer)) {
                        isValidated = true;
                        System.out.println("Validated");
                    } else {
                        System.out.println("Your board doesn't match the config requirements!");
                        out.println("INV_SBOARD");
                    }
                } else {
                    System.out.println("Opponent's board doesn't match the config requirements!");
                    out.println("INV_BOARD");
                }
            } else {
                System.out.println("Your configurations do not match! ");
                out.println("INV_CONF");
            }
            
        } catch (EOFException e) { } catch(ClassNotFoundException exc) {
            System.out.println("An error occured with config transfer, was it loaded properly?");
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
        os = new ObjectOutputStream(clientDataTransfer.getOutputStream());
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
                    if(move == Player.SERVER) {
                        Utils.Clear();
                        System.out.println("Moved on " + splitCommand[1] + " " + splitCommand[2]);
                        int response = clientPlayer.move(Integer.parseInt(splitCommand[1]), Integer.parseInt(splitCommand[2]));
                        System.out.println(clientPlayer.board);
                        switch (response) {
                            case 0:
                                System.out.println("You missed!");
                                System.out.println("It's opponents' turn now");
                                out.println("CLEAR");
                                os.writeUnshared(clientPlayer.board); // send state
                                os.flush();
                                move = Player.CLIENT;
                                break;
                            case 1:
                                System.out.println("You hit a ship!");
                                System.out.println("It's your turn");
                                out.println("CLEAR");
                                os.writeUnshared(clientPlayer.board); // send state
                                os.flush();
                                break;
                            case 2:
                                System.out.println("You sunk a ship!");
                                System.out.println("It's opponents' turn now");
                                out.println("CLEAR");
                                os.writeUnshared(clientPlayer.board); // send state
                                os.flush();
                                move = Player.CLIENT;
                                break;
                            case 3:
                                out.println("MSG Game ended! You have won.");
                                System.out.println("Game ended! You have lost.");
                                out.println("TERM 1");
                                System.exit(1);
                                break;
                            case -1:
                                System.out.println("This place was already hit!");
                                System.out.println("Try again");
                                break;
                            case -2:
                                System.out.println("Out of bounds!");
                                System.out.println("Try again");
                                break;
                            default:
                                System.out.println("An error occured");
                                System.exit(1);
                                break;
                        }
                    } else {
                        System.out.println("It is opponent's turn now. Wait...");
                    }
                    break;
                case "start":
                    Utils.Clear();
                    move = ((int) Math.round(Math.random()) == 0 ? Player.SERVER : Player.CLIENT);
                    out.println("START " + move);
                    System.out.println("Starting game! Random selected player to start: " + move);
                    serverPlayer = new Game(localConfig, placementClient);
                    clientPlayer = new Game(localConfig, placementServer);

                    // State management
                    switch(move) {
                        case SERVER: System.out.println(serverPlayer.board.toString()); os.writeUnshared(serverPlayer.board); os.flush(); break;
                        case CLIENT: System.out.println(clientPlayer.board.toString()); os.writeUnshared(clientPlayer.board); os.flush(); break;
                    }
                    
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
                case "ok":
                    System.out.println("ok registered by server");
                    Utils.Clear();
                    if (move == Player.CLIENT) {
                        System.out.println(serverPlayer.board);
                    } else {
                        System.out.println(clientPlayer.board);
                    }
                    break;

            }
        }
    }

    void EnterDataLoop() {

        // After initial check enter permanent reciever thread
        Thread data = new Thread() {
            public void run() {
                while(true) {
                    try {
                        String data = in.readLine();
                        if (data.equals(null)) continue;

                        String[] splitCommand = data.split(" ");
                        // Handle data logic

                            switch(splitCommand[0]) {
                                case "move":
                                    if(move == Player.CLIENT) {
                                        out.println("CLEAR");
                                        out.println("MSG Moved on " + splitCommand[1] + " " + splitCommand[2]);
                                        int response = serverPlayer.move(Integer.parseInt(splitCommand[1]), Integer.parseInt(splitCommand[2]));
                                        os.writeUnshared(serverPlayer.board);
                                        os.flush();
                                        switch (response) {
                                            case 0:
                                                out.println("MSG You missed!");
                                                out.println("MSG It's opponents' turn now");
                                                Utils.Clear();
                                                System.out.println(serverPlayer.board);
                                                move = Player.SERVER;
                                                break;
                                            case 1:
                                                out.println("MSG You hit a ship!");
                                                out.println("MSG It's your turn");
                                                Utils.Clear();
                                                System.out.println(serverPlayer.board);
                                                break;
                                            case 2:
                                                out.println("MSG You sunk a ship!");
                                                out.println("MSG It's opponents' turn now");
                                                Utils.Clear();
                                                System.out.println(serverPlayer.board);
                                                move = Player.SERVER;
                                                break;
                                            case 3:
                                                out.println("MSG Game ended! You have lost.");
                                                System.out.println("Game ended! You have won.");
                                                out.println("TERM 1");
                                                System.exit(1);
                                                break;
                                            case -1:
                                                out.println("MSG This place was already hit!");
                                                out.println("MSG Try again");
                                                break;
                                            case -2:
                                                out.println("MSG The coordinates entered are not valid!");
                                                out.println("MSG Try again");
                                            default:
                                                out.println("MSG An error occured");
                                                System.exit(1);
                                                break;
                                        }
                                    } else {
                                        out.println("MSG It is opponent's turn now. Wait...");
                                    }
                                    break;
                                case "ok":
                                    if (move == Player.SERVER) {
                                        os.writeUnshared(clientPlayer.board);
                                        os.flush();
                                    } else {
                                        os.writeUnshared(serverPlayer.board);
                                        os.flush();
                                    }
                                    break;
                
                            }
                    } catch (IOException e) { }
                }
            }
        };
        data.start(); 
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

class Game {
    Config config;
    public Board board; //either server board or client board
    int[] amountHit;
    public Game(Config config, Board board) {
        this.config = config;
        this.board = board;
        amountHit = new int[config.ships];
    }

    public int move(int x, int y) {
        try {
            if(board.displayShips[y][x].equals("·")) {
                if(board.myShips[y][x] == 0) {
                    board.displayShips[y][x] = "*";
                    return 0; //miss
                } else {
                    if(++amountHit[board.myShips[y][x]-1] == config.unsortedShipSizes[board.myShips[y][x]-1]) {
                        //change every field on the displayship to sunk:
                        for(int i = 0; i < board.displayShips.length; i++) {
                            for(int j = 0; j < board.displayShips[i].length; j++) {
                                if(board.myShips[i][j] == board.myShips[y][x]) {
                                    board.displayShips[i][j] = "█";
                                }
                            }
                        }
                        int sunkCount = 0;
                        for(int i : amountHit) {
                            sunkCount += i;
                        }
                        if (sunkCount == config.ships) {
                            return 3; //game done
                        }
                        return 2; //sunk
                    } else {
                        board.displayShips[y][x] = "X";
                        return 1; //hit
                    }
                }
            } else {
                return -1; //already hit
            }
        } catch (IndexOutOfBoundsException e) {
            return -2; //invalid coords
        }
    }
}
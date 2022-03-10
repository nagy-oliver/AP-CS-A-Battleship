package com.compsci;
import com.compsci.Coms.*;
import java.util.*;
import java.io.*;
import org.json.simple.*;
import org.json.simple.parser.*;

public class App 
{ 
    public static void main( String[] args )
    {
        //Board test1 = new Board();
        Config testConfig = new Config();
        int[] boardSize = {5, 5};
        Board testBoard = new Board(boardSize);
        
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
                    GameServer server = new GameServer(16333);
                } catch(IOException exc) {
                    System.out.println("An error occured");
                    System.exit(1);
                }
                break;
            case 2:
                System.out.println("Please type in the ip adress of the host:");
                String ip = input.next();
                try {
                    GameClient clientSocket = new GameClient(ip, 16333);
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
    public File myShipsFile;
    public int[][] myShips;
    //public File opponentShips;
    public Board(int[] boardSize) {
        //define myShips size:
        myShips = new int[boardSize[1]][boardSize[0]];
        //create a 2d array from the .game file
        try {
            myShipsFile = new File("target/myships.game");
            Scanner fileReader = new Scanner(myShipsFile);
            int line = 0;
            while(fileReader.hasNextLine()) {
                String newLine = fileReader.nextLine();
                if(newLine.length() != boardSize[0]) {
                    System.out.println("Invalid game file");
                    System.exit(1);
                }
                for(int i = 0; i < newLine.length(); i++) {
                    myShips[line][i] = Integer.parseInt(Character.toString(newLine.charAt(i)));
                }
                line++;
            }
        } catch (Exception e) { //most likely in case of invalid dimensions of the file
            System.out.println("Invalid game file");
            System.exit(1);
        }
    }
}

class Config {
    public int[] size = new int[2]; //[width, height]
    public int ships;
    public int[] shipSizes;

    public Config() {
        //reads own JSON and assigns instance variables
        JSONParser parser = new JSONParser();

        try {
            Object obj = parser.parse(new FileReader("target/config.json"));

            JSONObject jsonObject =  (JSONObject) obj;

            JSONArray arr = (JSONArray) jsonObject.get("size");
            for(int i = 0; i<arr.toArray().length; i++) {
                size[i] = ((Long) arr.toArray()[i]).intValue();
            }

            ships = ((Long) jsonObject.get("ships")).intValue();
            shipSizes = new int[ships];

            arr = (JSONArray) jsonObject.get("shipSizes");
            for(int i = 0; i<arr.toArray().length; i++) {
                shipSizes[i] = ((Long) arr.toArray()[i]).intValue();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    
    /*public boolean validate(Board myBoard) {
        //checks whether board is corresponding to the config, otherwise exits
        //check size of the board:

        //check number of ships:

        //check the size of ships:
    }*/

    public boolean compareConfigs(Config clientConfig) {
        return clientConfig.equals(this);
    }
}
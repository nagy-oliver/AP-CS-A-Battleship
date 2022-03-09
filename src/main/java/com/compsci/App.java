package com.compsci;
import com.compsci.Coms.*;
import java.util.*;  
import java.net.*;
import java.io.*;
import org.json.simple.*;
import org.json.simple.parser.*;

public class App 
{ 
    public static void main( String[] args )
    {
        //Board test1 = new Board();
        Config testConfig = new Config();
        
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
    public File myShips;
    public File opponentShips;
    public Board() {
        try {
            myShips = new File("target/myships.game");
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

class Config {
    public int[] size = new int[2];
    public int ships;
    public int[] shipSizes;

    public Config() {
        //reads own JSON and assigns instances values
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

    }*/

    public boolean compareConfigs(Config clientConfig) {
        return clientConfig.equals(this);
    }
}
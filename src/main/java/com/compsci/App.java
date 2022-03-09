package com.compsci;
import com.compsci.Coms.*;
import java.util.*;  
import java.net.*;
import java.io.*;

public class App 
{ 
    public static void main( String[] args )
    {
        Board test1 = new Board();
        
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
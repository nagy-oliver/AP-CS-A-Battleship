package com.compsci;
import com.compsci.Coms.*;
import java.util.*;
import java.io.*;
import org.apache.log4j.BasicConfigurator;

public class App 
{ 
    static GameServer server;
    static GameClient client;

    public static void main( String[] args )
    {
        //Board test1 = new Board();
        Config testConfig = new Config();
        int[] boardSize = testConfig.size;
        Board testBoard = new Board(boardSize);
        
        System.out.println(testConfig.validate(testBoard));
        // System.out.println(testConfig.validate(testBoard));
        // Config test2 = new Config();
        // System.out.println(testConfig.compareConfigs(test2));
        // System.out.println(testBoard);
        // Game testGame = new Game(testConfig, testBoard);
        // System.out.println(testGame.move(0, 0));
        // System.out.println(testGame.board);
        // System.out.println(testGame.move(0, 1));
        // System.out.println(testGame.move(0, 2));
        // System.out.println(testGame.board);        
        
        BasicConfigurator.configure();
        
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
        Utils.Clear();

        // SOCKET INIT
        switch(choice) {
            case 1:
                // Start the server
                try {
                    server = new GameServer(16333);
                } catch(IOException exc) {
                    System.out.println("An error occured: " + exc.toString());
                    exc.printStackTrace();
                    System.exit(1);
                }
                break;
            case 2:
                System.out.println("Please type in the ip adress of the host:");
                String ip = input.next();
                try {
                    client = new GameClient(ip, 16333);
                } catch(IOException exc) {
                    System.out.println("An error occured: " + exc.toString());
                    exc.printStackTrace();
                    System.exit(1);
                } catch (InterruptedException e) {}
                break;
        }
        input.close();
    }
}
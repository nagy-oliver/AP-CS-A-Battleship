package com.compsci;
import java.util.*;
import java.io.*;

public class Board {
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
            fileReader.close();
        } catch (Exception e) { //most likely in case of invalid dimensions of the file
            System.out.println("Invalid game file");
            System.exit(1);
        }
    }
}
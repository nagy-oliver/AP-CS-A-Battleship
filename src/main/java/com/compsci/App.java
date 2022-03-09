package com.compsci;
import io.socket.*;
import java.io.*;
import org.json.*;

public class App 
{
    public static void main( String[] args )
    {
        Board test1 = new Board();
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
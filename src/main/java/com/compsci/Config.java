package com.compsci;
import java.util.*;
import java.io.*;
import org.json.simple.*;
import org.json.simple.parser.*;


public class Config implements Serializable {
    public int[] size = new int[2]; //[width, height]
    public int ships;
    public int[] shipSizes;

    public Config() {
        //reads own JSON and assigns instance variables
        JSONParser parser = new JSONParser();

        try {
            Object obj = parser.parse(new FileReader("target/config.json"));

            JSONObject jsonObject = (JSONObject) obj;

            JSONArray arr = (JSONArray) jsonObject.get("size");
            for(int i = 0; i<arr.toArray().length; i++) {
                size[i] = ((Long) arr.toArray()[i]).intValue();
            }

            ships = ((Long) jsonObject.get("ships")).intValue();
            shipSizes = new int[ships];

            arr = (JSONArray) jsonObject.get("shipSizes");
            for(int i = 0; i<arr.toArray().length; i++) {
                shipSizes[i] = ((Long) arr.toArray()[i]).intValue();
            } Arrays.sort(shipSizes);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    
    public boolean validate(Board myBoard) {
        //checks whether board is corresponding to the config, otherwise exits
        //check size of the board:
        if(size[0] != myBoard.myShips[0].length || size[1] != myBoard.myShips.length) return false;
        //check number of ships:
        int findShipNum = 1;
        while(contains(findShipNum, myBoard)) {
            findShipNum++;
        }
        if(containsLarger(findShipNum, myBoard) || findShipNum != ships) return false;
        //check the size of ships:
        int[] shipsFound = new int[ships];
            //

        Arrays.sort(shipsFound);

        return true;
    }

    boolean contains(int num, Board myBoard) {
        for(int i = 0; i < myBoard.myShips.length; i++) {
            for(int j = 0; j < myBoard.myShips[i].length; j++) {
                if(myBoard.myShips[i][j] == num) {
                    //findShipSize(j, i);
                    return true;
                }
            }
        }
        return false;
    }
    boolean containsLarger(int num, Board myBoard) {
        for(int i = 0; i < myBoard.myShips.length; i++) {
            for(int j = 0; j < myBoard.myShips[i].length; j++) {
                if(myBoard.myShips[i][j] > num) {
                    return true;
                }
            }
        }
        return false;
    }
    /*int findShipSize(int x, int y) {
        //recursive function to find the ship size
    }*/

    public boolean compareConfigs(Config clientConfig) {
        return clientConfig.equals(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Config)) return false;
    
        Config comparedConf = (Config) o;
    
        if (size != comparedConf.size) return false;
        if (ships != comparedConf.ships) return false;
        if (shipSizes != comparedConf.shipSizes) return false;
    
        return true;
    }
}
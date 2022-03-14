package com.compsci;
import java.util.*;
import java.io.*;
import org.json.simple.*;
import org.json.simple.parser.*;


public class Config implements Serializable {
    public int[] size = new int[2]; //[width, height]
    public int ships;
    public int[] shipSizes;
    //needed for Game class:
    public int[] unsortedShipSizes;

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
            unsortedShipSizes = new int[ships];

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
        int findShipNum = 0;
        while(contains(findShipNum+1, myBoard)) {
            findShipNum++;
        }
        if(containsLarger(findShipNum, myBoard) || findShipNum != ships) return false;
        //check the size of ships by comparing 2 sorted arrays:
        ArrayList<Integer> shipsFound = new ArrayList<>();
        int totalShipsFound = 0;
        int temp = 0;
        for(int num = 1; num <= ships; num++) {
            outerloop:
            for(int i = 0; i < myBoard.myShips.length; i++) {
                for(int j = 0; j < myBoard.myShips[i].length; j++) {
                    if(myBoard.myShips[i][j] == num) {
                        temp = findShipSize(i, j, myBoard, 1, num);
                        shipsFound.add(temp);
                        break outerloop;
                    }
                }
            }
            totalShipsFound += temp;
        }

        int totalShips = 0;
        for(int i : shipSizes) {
            totalShips += i;
        }
        if(totalShips != totalShipsFound) return false; //exchange for return false after testing

        for(int i = 0; i < shipsFound.size(); i++) {
            unsortedShipSizes[i] = shipsFound.get(i);
        }

        Collections.sort(shipsFound);
        ArrayList<Integer> shipSizesList = new ArrayList<>();
        for(int i : shipSizes) {
            shipSizesList.add(i);
        }
        if(!shipsFound.equals(shipSizesList)) return false; //exchange for return false after testing
        //if everything was satisfied:
        return true;
    }

    boolean contains(int num, Board myBoard) {
        for(int i = 0; i < myBoard.myShips.length; i++) {
            for(int j = 0; j < myBoard.myShips[i].length; j++) {
                if(myBoard.myShips[i][j] == num) return true;
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

    int findShipSize(int y, int x, Board myBoard, int shipSize, int shipNum) {
        //recursive function to find the ship size
        try {
            if(myBoard.myShips[y+1][x] == shipNum) {
                return findShipSize(y+1, x, myBoard, shipSize+1, shipNum, 0);
            }
        } catch (IndexOutOfBoundsException e) {
            try {
                if(myBoard.myShips[y][x+1] == shipNum) {
                    return findShipSize(y, x+1, myBoard, shipSize+1, shipNum, 1);
                }
            } catch (IndexOutOfBoundsException f) {
                return shipSize;
            }
        } try {
            if(myBoard.myShips[y][x+1] == shipNum) {
                return findShipSize(y, x+1, myBoard, shipSize+1, shipNum, 1);
            }
        } catch (IndexOutOfBoundsException e) {
            return shipSize;
        }
        return shipSize;
    }
    int findShipSize(int y, int x, Board myBoard, int shipSize, int shipNum, int dir) { //dir: 0 = vertical, 1 = horizontal
        //recursive function to find the ship size
        try {
            if(dir == 0 && myBoard.myShips[y+1][x] == shipNum) {
                return findShipSize(y+1, x, myBoard, shipSize+1, shipNum, 0);
            } else if(dir == 1 && myBoard.myShips[y][x+1] == shipNum) {
                return findShipSize(y, x+1, myBoard, shipSize+1, shipNum, 1);
            } else {
                return shipSize;
            }
        } catch (IndexOutOfBoundsException e) {
            return shipSize;
        }
    }

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
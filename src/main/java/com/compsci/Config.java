package com.compsci;
import java.io.*;
import org.json.simple.*;
import org.json.simple.parser.*;

public class Config {
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
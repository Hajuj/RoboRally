package main;

import com.google.gson.Gson;
import game.boardelements.ConveyorBelt;


/**
 * for now this class is used for testing
 */
public class Main {

    public static void main(String[] args) {
        serializeObject();
    }

    public static void serializeObject(){
        //Spam spam = new Spam();
        ConveyorBelt conveyorBelt = new ConveyorBelt();

        Gson gson = new Gson();
        String beltjson = gson.toJson(conveyorBelt);

    }

}

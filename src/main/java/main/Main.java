package main;

import com.google.gson.Gson;
import game.Orientation;
import game.boardelements.ConveyorBelt;
import game.damagecards.Virus;

/**
 * for now this class is used for testing
 */
public class Main {
    public static Gson gson = new Gson();

    public static void main(String[] args) {
        Virus virus = new Virus();
        ConveyorBelt conveyorBelt = new ConveyorBelt();
        conveyorBelt.setOrientation(Orientation.DOWN);
        String virusJSON = serializeObject(virus);
        String belt = serializeObject(conveyorBelt);
        ConveyorBelt conveyorBeltfromJson = (ConveyorBelt) deserializeObject(belt, ConveyorBelt.class);
        Virus virusfromJSON = (Virus) deserializeObject(virusJSON, Virus.class);
    }

    public static String serializeObject(Object object){
        return gson.toJson(object);
    }

    //TODO find out which Class is responsible for the json in general
    //      one method with if else body to check for object Class (?)
    public static Object deserializeObject(String json, Class<?> whichClass){
        return gson.fromJson(json, whichClass);
    }

}

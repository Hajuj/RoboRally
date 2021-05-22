package main;

import com.google.gson.*;
import game.Orientation;
import game.boardelements.ConveyorBelt;
import game.damagecards.Virus;

/**
 * for now this class is used for testing
 */
public class Main {
    public static Gson gson = new Gson();
    public static GsonBuilder gsonBuilder = new GsonBuilder();

    public static void main(String[] args) {
        Virus virus = new Virus();
        ConveyorBelt conveyorBelt = new ConveyorBelt();
        conveyorBelt.setOrientation(Orientation.DOWN);
        String virusJSON = serializeObject(virus);
        String belt = serializeObject(conveyorBelt);
        ConveyorBelt conveyorBeltfromJson = (ConveyorBelt) deserializeObject(belt, ConveyorBelt.class);
        Virus virusfromJSON = (Virus) deserializeObject(virusJSON, Virus.class);

        String virusJSON2 = serializeWithBuilder(virus);
        String beltJSON = serializeWithBuilder(conveyorBelt);

    }

    public static String serializeObject(Object object){
        return gson.toJson(object);
    }

    //TODO find out which Class is responsible for the json in general
    //      one method with if else body to check for object Class (?)
    public static Object deserializeObject(String json, Class<?> whichClass){
        return gson.fromJson(json, whichClass);
    }

    public static String serializeWithBuilder(Object object){
        gsonBuilder.excludeFieldsWithoutExposeAnnotation();
        gson = gsonBuilder.create();
        return gson.toJson(object);
    }

}

class JSONMessage{
    private String messageType;
    private Object messageBody;

    public JSONMessage(String messageType, Object messageBody){
        this.messageType = messageType;
        this.messageBody = messageBody;
    }

    public Object getMessageBody() {
        return messageBody;
    }

    public String getMessageType() {
        return messageType;
    }
}

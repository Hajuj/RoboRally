package json;

import com.google.gson.*;
import json.protocol.ErrorBody;
import json.protocol.HelloClientBody;
import json.protocol.HelloServerBody;
import json.protocol.WelcomeBody;

import java.lang.reflect.Type;
import java.util.logging.Logger;

/**
 * This class is responsible for the deserialization (JSON -> Java) of JSON Messages being in their String representation.
 * It makes use of the Gson library. A customized Gson instance using a TypeAdapter is used to properly parse the
 * messageBody object (can be e.g. of type HelloServerBody, HelloClientBody, etc.) while deserializing.
 *
 * @author Mohamad, Viktoria
 */
public class JSONDeserializer {

    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_RESET = "\u001B[0m";
    private static final Logger logger = Logger.getLogger(MessageHandler.class.getName());

    /**
     * This method deserializes a JSON String into a Java Object. It makes use of a
     * customized Gson instance that decides how to properly parse the messageBody object.
     *
     * @param jsonString The JSON String that needs to be deserialized.
     * @return The {@link JSONMessage} created by deserializing the JSON String.
     */
    public static JSONMessage deserializeJSON(String jsonString) {
        // GsonBuilder allows to set settings before parsing stuff
        GsonBuilder gsonBuilder = new GsonBuilder();

        // Register TypeAdapter so Gson knows how to parse the messageBody (java.lang.Object)
        gsonBuilder.registerTypeAdapter(JSONMessage.class, customDeserializer);
        // After (!) settings, create Gson instance to deserialize
        Gson customGson = gsonBuilder.create();

        // Map the received JSON String message into a JSONMessage object
        JSONMessage messageObj = customGson.fromJson(jsonString, JSONMessage.class);
        return messageObj;
    }

    public static JsonDeserializer<JSONMessage> customDeserializer = new JsonDeserializer<JSONMessage>() {
        @Override
        public JSONMessage deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            // Get the overall JSON String with type and body
            JsonObject jsonMessage = jsonElement.getAsJsonObject();

            // Get only the messageBody part of the JSON String so we can access its variables
            JsonObject messageBody = jsonMessage.get("messageBody").getAsJsonObject();

            // Get the messageType of the JSON String
            String messageType = jsonMessage.get("messageType").getAsString();

            // For parsing JSON Arrays into Java ArrayLists<?>

            if (messageType.equals("HelloClient")) {
                HelloClientBody helloClientBody = new HelloClientBody(
                        messageBody.get("protocol").getAsString()
                );

                return new JSONMessage("HelloClient", helloClientBody);
            } else if (messageType.equals("HelloServer")) {
                HelloServerBody helloServerBody = new HelloServerBody(
                        messageBody.get("group").getAsString(),
                        messageBody.get("isAI").getAsBoolean(),
                        messageBody.get("protocol").getAsString()
                );

                return new JSONMessage("HelloServer", helloServerBody);
            } else if (messageType.equals("Welcome")) {

                WelcomeBody welcomeBody = new WelcomeBody(
                        messageBody.get("playerID").getAsInt()
                );
                return new JSONMessage("Welcome", welcomeBody);
            }
            // Something went wrong, could not deserialize!
            return new JSONMessage("Error", new ErrorBody("Fatal error: Could not resolve message." +
                    "Something went wrong while deserializing."));
        }
    };

}

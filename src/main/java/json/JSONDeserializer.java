package json;

import json.protocol.ErrorBody;

import com.google.gson.*;

import java.lang.reflect.Type;

import org.apache.log4j.Logger;

/**
 * This class is responsible for the deserialization (JSONString -> Java) of JSON Messages being in their String representation.
 * It makes use of the Gson library. A customized Gson instance using a TypeAdapter is used to properly parse the
 * messageBody object (can be e.g. of type HelloServerBody, HelloClientBody, etc.) while deserializing.
 *
 * @author Mohamad, Viktoria, Ilja
 */
public class JSONDeserializer {
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
            // Get the messageType as String
            String messageType = jsonMessage.get("messageType").getAsString();
            // Get only the messageBody as JsonObject so we deserialise it zu einem bestimmten messageBody
            JsonObject messageBody = jsonMessage.get("messageBody").getAsJsonObject();

            // Casting a messageBody class by reflection: Alle mögliche MessageBody aus dem Protokoll
            // befinden sich in package json.protocol. und haben Namen (messageType + Body).
            // Wir haben MessageType und suchen nach dem Klass MessageTypeBody. Wenn wir es finden, cast by reflection zu messageBody

            Class<?> reflection = null;
            try {
                reflection = Class.forName("json.protocol." + messageType + "Body");
            } catch (ClassNotFoundException e) {
                logger.warn("Something went wrong while deserializing.");
                return new JSONMessage("Error", new ErrorBody("Something went wrong while deserializing."));
            }

            Gson gson = new Gson();
            Object messageBodyNEW = reflection.cast(gson.fromJson(messageBody, reflection));
            return new JSONMessage(messageType, messageBodyNEW);
        }
    };
}

package json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * This class is responsible for the serialization (Java -> JSONString) of JSONMessage Objects.
 * It makes use of the Gson library.
 *
 * @author Mohamad, Viktoria
 */
public class JSONSerializer {

    public static String serializeJSON(JSONMessage messageObj) {
        // GsonBuilder allows to set settings before parsing stuff
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        // Convert the object into a JSON String
        String jsonString = gson.toJson(messageObj);

        return jsonString;
    }
}

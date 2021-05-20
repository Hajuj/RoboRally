package client;

import json.JSONDeserializer;
import json.JSONMessage;
import json.MessageHandler;
import json.protocol.ServerMessageAction;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.logging.Logger;

/**
 * @author Mohamad, Viktoria
 */

public class ClientThread extends Thread {

    private Socket socket; //This Client's socket
    private Client client;
    private static final Logger logger = Logger.getLogger(ClientThread.class.getName());
    private MessageHandler messageHandler;


    public ClientThread(Client client, Socket socket) {
        this.client = client;
        this.socket = socket;
        this.messageHandler = client.getMessageDistributer();
    }

    /**
     * The String input of the BufferedReader is in JSON-Format. In order to get the content of it we format it (deserialize it) into a JSONMessage.
     * The type of the JSONMessage is equal to the instruction we are getting from the server (serverInstruction). Its ServerInstructionType then is used to differentiate
     * between different  cases like we would normally do with instructions themselves.
     * The body of the message then contains various content. For more info check out the attributes of the
     */
    @Override
    public void run() {
        try {
            //Reads input stream from server
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String jsonString;
            while ((jsonString = reader.readLine()) != null) {
                // Deserialize the received JSON String into a JSON object
                JSONMessage jsonMessage = JSONDeserializer.deserializeJSON(jsonString);
                logger.info("JSONDeserializer in Client done: " + jsonString + jsonMessage);

                // Cast messagebody dynamically by reflection
                Class<?> reflection = (Class<?>) Class.forName("json.protocol." + jsonMessage.getMessageType() + "Body");
                Object messageBodyObject = reflection.cast(jsonMessage.getMessageBody());

                ServerMessageAction msg = (ServerMessageAction) jsonMessage.getMessageBody();
                msg.triggerAction(client, this, messageBodyObject, messageHandler);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void setPlayerID(Integer playerID) {
    }
}

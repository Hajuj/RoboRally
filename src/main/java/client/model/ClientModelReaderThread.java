package client.model;

import json.JSONDeserializer;
import json.JSONMessage;
import json.protocol.ServerMessageAction;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;

/**
 * ClientModelReaderThread for reading ( only! ) of server messages
 *
 * @author Mohamad, Viktoria
 */
public class ClientModelReaderThread extends Thread {
    private ClientModel client;
    private Socket clientSocket;
    private static Logger logger = Logger.getLogger(ClientModelReaderThread.class.getName());
    private MessageHandler messageHandler;


    public ClientModelReaderThread(ClientModel client, Socket clientSocket) {
        this.client = client;
        this.clientSocket = clientSocket;
        this.messageHandler = client.getMessageHandler();
    }

    /**
     * Dieses Thread liest alle String-Nachrichten von dem Server. Alle Nachrichten sind in ClientHandler decodete JSONMessages.
     * Um die zu encoden brauchen wir ein JSONDeserializer, der gibt uns ein JSONMessage.
     * Danach wird der messageBody-Klasse mit dem cast by reflection gefunden und die Action aus diese Klasse gemacht.
     */
    @Override
    public void run() {
        try {
            //InputStream von dem clientSocket and reader fot it to Read server Messages
            InputStreamReader in = new InputStreamReader(clientSocket.getInputStream());
            BufferedReader reader = new BufferedReader(in);

            String messageString;
            while ((messageString = reader.readLine()) != null) {
                // Deserialize the received JSON String into a JSON object

                JSONMessage jsonMessage = JSONDeserializer.deserializeJSON(messageString);
                logger.info("Incoming StringMessage " + messageString + " was deserialized to " + jsonMessage);

                // Casting a messageBody class by reflection
                // Alle mögliche MessageBody aus dem Protokoll befinden sich in package json.protocol. und
                // haben Namen (messageType + Body).
                // Wir haben MessageType und suchen nach dem Klass MessageTypeBody. Wenn wir es finden,
                // cast by reflection zu messageBody

                Class<?> reflection = Class.forName("json.protocol." + jsonMessage.getMessageType() + "Body");
                Object messageBody = reflection.cast(jsonMessage.getMessageBody());

                ServerMessageAction msg = (ServerMessageAction) jsonMessage.getMessageBody();
                msg.triggerAction(client, messageBody, messageHandler);
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}

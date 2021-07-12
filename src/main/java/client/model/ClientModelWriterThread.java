package client.model;

import json.JSONMessage;
import json.JSONSerializer;
import json.protocol.SendChatBody;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * @author Viktoria
 */
public class ClientModelWriterThread extends Thread {
    private ClientModel client;
    private Socket clientSocket;
    private static Logger logger = Logger.getLogger(ClientModelReaderThread.class.getName());
    private MessageHandler messageHandler;
    private PrintWriter writer;

    public ClientModelWriterThread(ClientModel client, Socket clientSocket) {
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
        //OutputStream for clientSocket und writer for it
        OutputStreamWriter out = null;
        try {
            out = new OutputStreamWriter(clientSocket.getOutputStream());
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        writer = new PrintWriter(out);
    }

    public void sendMessage(JSONMessage jsonMessage) {
        writer.println(JSONSerializer.serializeJSON(jsonMessage));
        writer.flush();
    }

    //TODO check if working
    public void sendChatMessage(String message) {
        sendMessage(new JSONMessage("SendChat", new SendChatBody(message, -1)));
    }

    //TODO check if working
    public void sendDirectMessage(String message, int playerID) {
        sendMessage(new JSONMessage("SendChat", new SendChatBody(message, playerID)));
    }
}
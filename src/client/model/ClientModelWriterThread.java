package client.model;

import json.JSONMessage;
import json.JSONSerializer;
import json.MessageHandler;
import json.protocol.HelloServerBody;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientModelWriterThread extends Thread {
    private final ClientModel client;
    private final Socket clientSocket;
    private static final Logger logger = Logger.getLogger(ClientModelReaderThread.class.getName());
    private final MessageHandler messageHandler;
    private PrintWriter writer;

    public ClientModelWriterThread (ClientModel client, Socket clientSocket, MessageHandler messageHandler) {
        this.client = client;
        this.clientSocket = clientSocket;
        this.messageHandler = messageHandler;
    }

    /**
     * Dieses Thread liest alle String-Nachrichten von dem Server. Alle Nachrichten sind in ClientHandler decodete JSONMessages.
     * Um die zu encoden brauchen wir ein JSONDeserializer, der gibt uns ein JSONMessage.
     * Danach wird der messageBody-Klasse mit dem cast by reflection gefunden und die Action aus diese Classe gemacht.
     */
    @Override
    public void run () {
        //Outputstream for clientsocket und writer for it
        OutputStreamWriter out = null;
        try {
            out = new OutputStreamWriter(clientSocket.getOutputStream());
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        writer = new PrintWriter(out);
    }

    public void sendMessage (JSONMessage jsonMessage) {
        writer.println(JSONSerializer.serializeJSON(jsonMessage));
        writer.flush();
    }
}

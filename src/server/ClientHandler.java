package server;

import json.JSONDeserializer;
import json.JSONSerializer;
import json.JSONMessage;
import json.MessageHandler;
import json.protocol.ClientMessageAction;
import json.protocol.HelloClientBody;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.logging.Logger;

/**
 * @author Mohamad, Viktoria
 */

public class ClientHandler extends Thread {
    private Socket clientSocket;
    private Server server;
    private JSONMessage jsonMessage;
    private String protocolVersion;
    private static final Logger logger = Logger.getLogger(ClientHandler.class.getName());
    private MessageHandler messageHandler;

    public ClientHandler(Socket clientSocket, Server server) {
        this.clientSocket = clientSocket;
        this.server = server;
        this.messageHandler = server.getMessageDistributer();
        this.protocolVersion = server.getProtocolVersion();
    }

    public Socket getClientSocket() {
        return clientSocket;
    }

    private PrintWriter writer;
    private BufferedReader reader;

    @Override
    public void run() {
        try {
            //WRITER:
            writer = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

            //READER:
            reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            //server.Server submits protocol version to client
            jsonMessage = new JSONMessage("HelloClient", new HelloClientBody(protocolVersion));
            writer.println(JSONSerializer.serializeJSON(jsonMessage));
            writer.flush();

            String jsonString;
            while ((jsonString = reader.readLine()) != null) {
                // Deserialize the received JSON String into a JSON object
                jsonMessage = JSONDeserializer.deserializeJSON(jsonString);
                logger.info("JSONDeserializer in Server done: " + jsonString + jsonMessage);

                // Cast message body dynamically by reflection
                Class<?> reflection = Class.forName("json.protocol." + jsonMessage.getMessageType() + "Body");
                Object messageBodyObject = reflection.cast(jsonMessage.getMessageBody());

                ClientMessageAction msg = (ClientMessageAction) jsonMessage.getMessageBody();
                msg.triggerAction(this.server, this, messageBodyObject, messageHandler);
            }
        } catch (SocketException exp) {
            if (exp.getMessage().contains("Socket closed"))
                logger.info("Client at " + clientSocket.getInetAddress().getHostAddress() + " disconnected.");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public PrintWriter getWriter() {
        return writer;
    }
}

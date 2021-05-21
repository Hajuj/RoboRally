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
 * Every Client has its own ClientHandlerThread.
 *
 * @author Mohamad, Viktoria
 */

public class ClientHandler extends Thread {
    private final Server server;
    private final Socket clientSocket;
    private PrintWriter writer;
    private BufferedReader reader;


    private JSONMessage jsonMessage;
    private final String protocolVersion;
    private final MessageHandler messageHandler;


    private static final Logger logger = Logger.getLogger(ClientHandler.class.getName());


    public ClientHandler (Socket clientSocket, Server server, String protocolVersion, MessageHandler messageHandler) {
        this.clientSocket = clientSocket;
        this.server = server;
        this.messageHandler = messageHandler;
        this.protocolVersion = protocolVersion;
    }

    public Socket getClientSocket () {
        return clientSocket;
    }

    @Override
    public void run () {
        try {
            // output and input Streams of the Clientsocket
            OutputStreamWriter out = new OutputStreamWriter(clientSocket.getOutputStream());
            InputStreamReader in = new InputStreamReader(clientSocket.getInputStream());

            //reader and writer for input and output Streams
            writer = new PrintWriter(out);
            reader = new BufferedReader(in);

            //Server schickt dem Client eine HelloClient-Massage. Wenn die Protokollversion passt, wird 
            //die Variable waitingForServer bei dem Client auf false gesetzt == der client wird
            //notified, dass er zu dem Server connected ist.
            jsonMessage = new JSONMessage("HelloClient", new HelloClientBody(protocolVersion));
            writer.println(JSONSerializer.serializeJSON(jsonMessage));
            writer.flush();

            //Lese alle incomming Strings mit dem reader und deserialize sie mit dem JSONDeserializer
            String messageString;
            while ((messageString = reader.readLine()) != null) {

                // Deserialize the received JSON String into a JSON object
                jsonMessage = JSONDeserializer.deserializeJSON(messageString);
                logger.info("Incoming StringMessage " + messageString + " was deserialised to " + jsonMessage);

                // Casting a messageBody class by reflection
                // Alle mögliche MessageBody aus dem Protokoll befinden sich in package json.protocol. und
                // haben Namen (messageType + Body).
                // Wir haben MessageType und suchen nach dem Klass MessageTypeBody. Wenn wir es finden,
                // cast by reflection zu messageBody
                Class<?> reflection = Class.forName("json.protocol." + jsonMessage.getMessageType() + "Body");
                Object messageBody = reflection.cast(jsonMessage.getMessageBody());

                // nachdem die richtige MessageBody gefunden wurde, rufe die triggerAction Methode auf.
                ClientMessageAction msg = (ClientMessageAction) jsonMessage.getMessageBody();
                msg.triggerAction(this.server, this, messageBody, messageHandler);
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

    public PrintWriter getWriter () {
        return writer;
    }
}

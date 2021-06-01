package server;

import game.Player;

import json.JSONDeserializer;
import json.JSONSerializer;
import json.JSONMessage;
import json.MessageHandler;
import json.protocol.ClientMessageAction;
import json.protocol.ConnectionUpdateBody;
import json.protocol.HelloClientBody;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

import json.protocol.SelectMapBody;
import org.apache.log4j.Logger;

/**
 * Every ClientModel has its own ClientHandlerThread.
 *
 * @author Mohamad, Viktoria
 */
public class ClientHandler extends Thread {
    private final Server server;
    private final Socket clientSocket;
    private PrintWriter writer;
    private BufferedReader reader;
    private int player_id;


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

    public int getPlayer_id () {
        return player_id;
    }

    public void setPlayer_id (int player_id) {
        this.player_id = player_id;
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

            //Server schickt dem ClientModel eine HelloClient-Massage. Wenn die Protokollversion passt, wird
            //die Variable waitingForServer bei dem ClientModel auf false gesetzt == der client wird
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
                logger.info("ClientModel at " + clientSocket.getInetAddress().getHostAddress() + " disconnected.");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        logger.warn("Verbindung mit dem Client " + this.getPlayer_id() + " wurde abgebrochen.");
        if (server.getPlayerWithID(this.getPlayer_id()).isReady()) {
            if (this.getPlayer_id() == server.getReadyPlayer().get(0).getPlayerID() && server.getReadyPlayer().size() != 1) {
                Player nextOne = server.getReadyPlayer().get(1);
                JSONMessage selectMapmessage = new JSONMessage("SelectMap", new SelectMapBody(server.getCurrentGame().getAvailableMaps()));
                server.sendMessage(selectMapmessage, server.getConnectionWithID(nextOne.getPlayerID()).getWriter());
            }
        }
        server.getConnections().remove(server.getConnectionWithID(this.getPlayer_id()));
        server.getReadyPlayer().remove(server.getPlayerWithID(this.getPlayer_id()));
        server.getWaitingPlayer().remove(server.getPlayerWithID(this.getPlayer_id()));


        for (Connection connection : server.getConnections()) {
            JSONMessage removeMessage = new JSONMessage("ConnectionUpdate", new ConnectionUpdateBody(this.player_id, false, "remove"));
            server.sendMessage(removeMessage, connection.getWriter());
        }
        try {
            clientSocket.close();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    public PrintWriter getWriter () {
        return writer;
    }
}

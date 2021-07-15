package server;

import game.Player;
import json.JSONDeserializer;
import json.JSONMessage;
import json.JSONSerializer;
import json.protocol.*;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

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


    public ClientHandler(Socket clientSocket, Server server, String protocolVersion, MessageHandler messageHandler) {
        this.clientSocket = clientSocket;
        this.server = server;
        this.messageHandler = messageHandler;
        this.protocolVersion = protocolVersion;
    }

    public int getPlayer_id() {
        return player_id;
    }

    public void setPlayer_id(int player_id) {
        this.player_id = player_id;
    }

    public Socket getClientSocket() {
        return clientSocket;
    }

    @Override
    public void run() {
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

        //The next player can choose a map after the first one disconnects
        if (!server.getPlayerWithID(this.getPlayer_id()).isAI() && server.getConnections().size() > 0) {
            if (server.getPlayerWithID(this.getPlayer_id()).isReady()) {
                if (this.getPlayer_id() == server.readyPlayerWithoutAI().get(0).getPlayerID()) {
                    server.getCurrentGame().setMapName(null);
                }
                if (this.getPlayer_id() == server.getReadyPlayer().get(0).getPlayerID() && server.getReadyPlayer().size() > 2) {
                    Player nextOne = server.getReadyPlayer().get(1);
                    JSONMessage selectMapMessage = new JSONMessage("SelectMap", new SelectMapBody(server.getCurrentGame().getAvailableMaps()));
                    server.sendMessage(selectMapMessage, server.getConnectionWithID(nextOne.getPlayerID()).getWriter());
                }
            }
        }

        if (server.getCurrentGame().isGameOn()) {
            //If there are two players in the game, and one goes out
            if (server.getCurrentGame().getPlayerList().size() <= 2) {
                server.getCurrentGame().getPlayerList().remove(server.getPlayerWithID(this.getPlayer_id()));
                server.getCurrentGame().setGameOn(false);
                for (Player player : server.getCurrentGame().getPlayerList()) {
                    if (player.getPlayerID() != this.getPlayer_id()) {
                        JSONMessage gameFinished = new JSONMessage("GameFinished", new GameFinishedBody(player.getPlayerID()));
                        server.getCurrentGame().sendToAllPlayers(gameFinished);
                        server.getCurrentGame().refreshGame();
                    }
                }
            }
            //More than 2 players, and the current player got out
            else if (server.getCurrentGame().getCurrentPlayer() == this.player_id) {
                //If the player was the last one
                if (server.getCurrentGame().nextPlayerID() == -1) {
                    server.getCurrentGame().setCurrentPlayer(server.getCurrentGame().getPlayerList().get(0).getPlayerID());
                } else {
                    server.getCurrentGame().setCurrentPlayer(server.getCurrentGame().nextPlayerID());
                }
                server.getCurrentGame().getPlayerList().remove(server.getPlayerWithID(this.getPlayer_id()));
            } else {
                //Not the current player disconnects
                server.getCurrentGame().getPlayerList().remove(server.getPlayerWithID(this.getPlayer_id()));
            }
        }
        server.getConnections().remove(server.getConnectionWithID(this.getPlayer_id()));
        server.getReadyPlayer().remove(server.getPlayerWithID(this.getPlayer_id()));
        server.getWaitingPlayer().remove(server.getPlayerWithID(this.getPlayer_id()));

        for (Connection connection : server.getConnections()) {
            JSONMessage removeMessage = new JSONMessage("ConnectionUpdate", new ConnectionUpdateBody(this.player_id, false, "remove"));
            server.sendMessage(removeMessage, connection.getWriter());
        }

        if (server.areAllPlayersReady() && !server.getCurrentGame().isGameOn()) {
            for (Connection connection : server.getConnections()) {
                JSONMessage startMessage = new JSONMessage("GameStarted", new GameStartedBody(server.getCurrentGame().getMap()));
                server.sendMessage(startMessage, connection.getWriter());
            }
            server.getCurrentGame().canStartTheGame();
            logger.info("I CAN START THE GAME");
        }

        try {
            clientSocket.close();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    public PrintWriter getWriter() {
        return writer;
    }

    /**
     * Inner class to establish a connection for the client.
     *
     */
    public static class Connection {
        private PrintWriter writer;
        private Socket socket;
        private int playerID;
        private boolean isConnected;

        public Connection(Socket clientSocket) throws IOException {
            this.socket = clientSocket;
            this.writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
        }

        public void setConnected(boolean connected) {
            isConnected = connected;
        }

        public boolean isConnected() {
            return isConnected;
        }

        public void setPlayerID(int playerID) {
            this.playerID = playerID;
        }

        public int getPlayerID() {
            return playerID;
        }

        public PrintWriter getWriter() {
            return writer;
        }
    }
}

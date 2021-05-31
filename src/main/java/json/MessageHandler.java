package json;

import client.model.ClientModel;
import client.model.ClientModelReaderThread;


import server.Server;
import server.Connection;
import server.ClientHandler;

import game.Player;

import json.protocol.*;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.log4j.Logger;

/**
 * @author Mohamad, Viktoria
 */
public class MessageHandler {
    private static final Logger logger = Logger.getLogger(MessageHandler.class.getName());
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_GREEN = "\u001B[0m";


    /**
     * Wenn Client ein HalloClient Message von Server bekommt, wird die Variable waitingForServer
     * auf false gesetzt und Client kann dem Server Nachrichten schicken.
     *
     * @param clientmodel             The ClientModel
     * @param clientModelReaderThread The ClientModelReaderThread of the ClientModel
     * @param helloClientBody         The message body of the message which is of type HelloClientBody
     */
    public void handleHelloClient(ClientModel clientmodel, ClientModelReaderThread clientModelReaderThread, HelloClientBody helloClientBody) {
        logger.info(ANSI_CYAN + "[MessageHandler]: HalloClient Message received. The ClientModel will be notified" + ANSI_RESET);
        logger.info("Server has protocol " + helloClientBody.getProtocol());
        //TODO change to notify() in class ClientModel
        clientmodel.setWaitingForServer(false);
    }


    /**
     * Wenn der Server ein Message HelloServer bekommt, schickt er ein Welcome-Message zu dem ClientModel mit dem ID
     *
     * @param server          The Server
     * @param clientHandler   The ClientHandler of the Server
     * @param helloServerBody The message body of the message which is of type  HelloServerBody
     */
    public void handleHelloServer(Server server, ClientHandler clientHandler, HelloServerBody helloServerBody) {
        logger.info(ANSI_CYAN + "[MessageHandler]: HalloServer Message received. " + ANSI_RESET);
        try {
            if (helloServerBody.getProtocol().equals(server.getProtocolVersion())) {
                logger.info("Protocol version test succeeded");

                // First, assign the client a playerID
                int actual_id = server.getClientsCounter();
                clientHandler.setPlayer_id(actual_id);

                //welcomeMessage with id
                JSONMessage welcomeMessage = new JSONMessage("Welcome", new WelcomeBody(actual_id));
                server.sendMessage(welcomeMessage, clientHandler.getWriter());

                // Create a Connection to this clientSocket
                Connection connection = new Connection(clientHandler.getClientSocket());
                server.getConnections().add(connection);
                connection.setPlayerID(actual_id);
                connection.setConnected(true);

//                server.sendMessage(new JSONMessage("Alive", new AliveBody()), clientHandler.getWriter());

                Player player = new Player(actual_id);
                server.getWaitingPlayer().add(player);

                for (Player player1 : server.getWaitingPlayer()) {
                    if (player1.getPlayerID() != clientHandler.getPlayer_id()) {
                        JSONMessage jsonMessage1 = new JSONMessage("PlayerAdded", new PlayerAddedBody(player1.getPlayerID(), player1.getName(), player1.getFigure()));
                        server.sendMessage(jsonMessage1, clientHandler.getWriter());
                    }
//                    JSONMessage jsonMessage = new JSONMessage("PlayerStatus", new PlayerStatusBody(player1.getPlayerID(), player1.isReady()));
//                    server.sendMessage(jsonMessage, clientHandler.getWriter());
                }
                // Immer um eins erhöhen für den nächsten client
                server.setClientsCounter(actual_id + 1);

            } else {
                logger.info("Protocol version test failed");
                JSONMessage jsonMessage = new JSONMessage("Error", new ErrorBody("Protocol version test failed. Server hat Protokoll " + server.getProtocolVersion()));
                server.sendMessage(jsonMessage, clientHandler.getWriter());
                clientHandler.getClientSocket().close();
                logger.info("ClientModel connection terminated");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * In der HalloServer method wird ein Welcome-message geschickt, und dann weiss der ClientModel sein id
     *
     * @param clientmodel             The ClientModel itself.
     * @param clientModelReaderThread The ClientModelReaderThread of the ClientModel
     * @param welcomeBody             The message body of the message which is of type {@link WelcomeBody}.
     */
    public void handleWelcome(ClientModel clientmodel, ClientModelReaderThread clientModelReaderThread, WelcomeBody welcomeBody) {
        logger.info(ANSI_CYAN + "[MessageHandler]: Welcome Message received." + ANSI_RESET);
        logger.info("Your PlayerID: " + welcomeBody.getClientID());
        Player player = new Player(welcomeBody.getClientID());
        clientmodel.setPlayer(player);
    }


    /**
     * Wenn es ein Error occured bei deserialization, wird ein Message mit dem ErrorBody geschickt
     *
     * @param clientmodel             The ClientModel itself.
     * @param clientModelReaderThread The ClientModelReaderThread of the ClientModel (Gives access to the PrintWriter).
     * @param errorBody               The message body of the message which is of type {@link ErrorBody}.
     */
    public void handleError(ClientModel clientmodel, ClientModelReaderThread clientModelReaderThread, ErrorBody errorBody) {
        logger.warn(ANSI_CYAN + "[MessageHandler]: Error has occurred! " + ANSI_RESET);
        logger.info("Error has occurred! " + errorBody.getError());
        clientmodel.sendError("Error has occurred! " + errorBody.getError());
    }

    public void handlePlayerValues(Server server, ClientHandler clientHandler, PlayerValuesBody playerValuesBody) {
        String username = playerValuesBody.getName();
        int figure = playerValuesBody.getFigure();

        Player player = server.getWaitingPlayer().get(clientHandler.getPlayer_id() - 1);
        player.pickRobot(figure, username);

        for (Player player1 : server.getWaitingPlayer()) {
            JSONMessage jsonMessage1 = new JSONMessage("PlayerAdded", new PlayerAddedBody(player.getPlayerID(), player.getName(), player.getFigure()));
            server.sendMessage(jsonMessage1, server.getConnectionWithID(player1.getPlayerID()).getWriter());
            JSONMessage jsonMessage2 = new JSONMessage("PlayerStatus", new PlayerStatusBody(player.getPlayerID(), false));
            server.sendMessage(jsonMessage2, server.getConnectionWithID(player1.getPlayerID()).getWriter());
        }
        logger.info("Alles gut, der Spieler mit ID " + clientHandler.getPlayer_id() + " heißt " + username + " und hat figur " + figure);
    }

    public void handleSendChat(Server server, ClientHandler clientHandler, SendChatBody sendChatBody) {
        logger.info(ANSI_CYAN + "[MessageHandler]: SendChat Message received. " + ANSI_RESET);

        int playerID = clientHandler.getPlayer_id();
        String senderName = server.getPlayerWithID(clientHandler.getPlayer_id()).getName();

        // Build new string from client's name and the actual message, to show name in chat
        String actualMessage = sendChatBody.getMessage();
        String message = senderName + " : " + actualMessage;

        int to = sendChatBody.getTo();
        //Send Private message
        if (to != -1) {
            for (Connection connection : server.getConnections()) {
                if (connection.getPlayerID() == to) {
                    server.sendMessage(new JSONMessage("ReceivedChat", new ReceivedChatBody(message, playerID, true)), clientHandler.getWriter());
                    server.sendMessage(new JSONMessage("ReceivedChat", new ReceivedChatBody(message, playerID, true)), connection.getWriter());
                }
            }
        } else { //Send public message
            for (Connection connection : server.getConnections()) {
                if (connection.getPlayerID() != playerID) {
                    server.sendMessage(new JSONMessage("ReceivedChat", new ReceivedChatBody(message, playerID, false)), connection.getWriter());
                }
            }
        }
    }

    public void handleReceivedChat(ClientModel clientModel, ClientModelReaderThread clientModelReaderThread, ReceivedChatBody receivedChatBody) {
        logger.info(ANSI_CYAN + "[MessageHandler]: Chat received. " + ANSI_RESET);
        clientModel.receiveMessage(receivedChatBody.getMessage());
    }

    public void handleGameStarted (ClientModel client, ClientModelReaderThread clientModelReaderThread, GameStartedBody bodyObject) {
        logger.info(ANSI_CYAN + "[MessageHandler]: Game Started received." + ANSI_RESET);
        //TODO implement map controller and use in this method to build the map
    }

    //Server receive this message
    public void handleAlive (Server server, ClientHandler clientHandler, AliveBody aliveBody) {
        try {
            //warten 5 sek
            Thread.sleep(5000);
            //senden ein neues Alive- Message zu Client
            server.sendMessage(new JSONMessage("Alive", new AliveBody()), clientHandler.getWriter());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //Client receive this message
    public void handleAlive (ClientModel clientModel, ClientModelReaderThread clientModelReaderThread, AliveBody aliveBody) {
        //wenn client bekommt ein Alive-Message von Server, schickt er ein "Alive"-Antwort zurück
        clientModel.sendMessage(new JSONMessage("Alive", new AliveBody()));
    }

    public void handlePlayerAdded (ClientModel clientModel, ClientModelReaderThread clientModelReaderThread, PlayerAddedBody playerAddedBody) {
        int clientID = playerAddedBody.getClientID();
        String name = playerAddedBody.getName();
        int figure = playerAddedBody.getFigure();

        // The player himself has been added
        if (clientModel.getPlayer().getPlayerID() == clientID) {
            clientModel.getPlayer().setName(name);
            clientModel.getPlayer().setFigure(figure);
        }
        clientModel.getPlayersNamesMap().put(clientID, name);
        clientModel.getPlayersFigureMap().put(clientID, figure);
        clientModel.getPlayersStatusMap().put(clientID, false);
        logger.info("A new player has been added. Name: " + name + ", ID: " + clientID + ", Figure: " + figure);
    }

    public void handleSetStatus(Server server, ClientHandler clientHandler, SetStatusBody setStatusBody) {
        Player player = server.getPlayerWithID(clientHandler.getPlayer_id());
        player.setReady(setStatusBody.isReady());
        String isReady = setStatusBody.isReady() ? "ready" : "not ready";
        for (Connection connection : server.getConnections()) {
            server.sendMessage(new JSONMessage("PlayerStatus", new PlayerStatusBody(player.getPlayerID(), player.isReady())), connection.getWriter());
        }

        JSONMessage selectMapmessage = new JSONMessage("SelectMap", new SelectMapBody(server.getCurrentGame().getAvailableMaps()));
        server.sendMessage(selectMapmessage, clientHandler.getWriter());
        // logger.info("The player " + clientHandler.getPlayer_id() + " is " + isReady);
    }

    public void handlePlayerStatus (ClientModel clientModel, ClientModelReaderThread clientModelReaderThread, PlayerStatusBody playerStatusBody) {
        clientModel.refreshPlayerStatus(playerStatusBody.getClientID(), playerStatusBody.isReady());
    }

    //diese Methode wird getriggert wenn Client eine SelectMap Message bekommt.
    public void handleSelectMap (ClientModel clientModel, ClientModelReaderThread clientModelReaderThread, SelectMapBody selectMapBody) {
        for (String map : selectMapBody.getAvailableMaps()) {
            clientModel.getAvailableMaps().add(map);
        }
        clientModel.setDoChooseMap(true);
    }

}

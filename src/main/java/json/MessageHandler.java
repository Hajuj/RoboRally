package json;

import client.model.ClientModel;


import server.Server;
import server.Connection;
import server.ClientHandler;

import game.Player;

import json.protocol.*;

import java.io.IOException;

import org.apache.log4j.Logger;

/**
 * @author Mohamad, Viktoria
 */
public class MessageHandler {
    private static final Logger logger = Logger.getLogger(MessageHandler.class.getName());
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_RESET = "\u001B[0m";


    /**
     * Wenn Client ein HalloClient Message von Server bekommt, wird die Variable waitingForServer
     * auf false gesetzt und Client kann dem Server Nachrichten schicken.
     *
     * @param clientmodel             The ClientModel
     * @param helloClientBody         The message body of the message which is of type HelloClientBody
     */
    public void handleHelloClient(ClientModel clientmodel, HelloClientBody helloClientBody) {
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

                server.sendMessage(new JSONMessage("Alive", new AliveBody()), clientHandler.getWriter());

                Player player = new Player(actual_id);
                server.getWaitingPlayer().add(player);

                //informieren den neuen Client über alle anderen clients im chat
                for (Player player1 : server.getWaitingPlayer()) {
                    if (player1.getPlayerID() != clientHandler.getPlayer_id()) {
                        JSONMessage jsonMessage1 = new JSONMessage("PlayerAdded", new PlayerAddedBody(player1.getPlayerID(), player1.getName(), player1.getFigure()));
                        server.sendMessage(jsonMessage1, clientHandler.getWriter());
                        JSONMessage jsonMessage2 = new JSONMessage("PlayerStatus", new PlayerStatusBody(player1.getPlayerID(), player1.isReady()));
                        server.sendMessage(jsonMessage2, clientHandler.getWriter());
                    }
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
     * @param welcomeBody             The message body of the message which is of type {@link WelcomeBody}.
     */
    public void handleWelcome(ClientModel clientmodel, WelcomeBody welcomeBody) {
        logger.info(ANSI_CYAN + "[MessageHandler]: Welcome Message received." + ANSI_RESET);
        logger.info("Your PlayerID: " + welcomeBody.getClientID());
        Player player = new Player(welcomeBody.getClientID());
        clientmodel.setPlayer(player);
    }


    /**
     * Wenn es ein Error occured bei deserialization, wird ein Message mit dem ErrorBody geschickt
     *
     * @param clientmodel             The ClientModel itself.
     * @param errorBody               The message body of the message which is of type {@link ErrorBody}.
     */
    public void handleError(ClientModel clientmodel, ErrorBody errorBody) {
        logger.warn(ANSI_CYAN + "[MessageHandler]: Error has occurred! " + ANSI_RESET);
        logger.info("Error has occurred! " + errorBody.getError());
        clientmodel.sendError("Error has occurred! " + errorBody.getError());
    }

    public void handlePlayerValues(Server server, ClientHandler clientHandler, PlayerValuesBody playerValuesBody) {
        String username = playerValuesBody.getName();
        int figure = playerValuesBody.getFigure();

        Player player = server.getPlayerWithID(clientHandler.getPlayer_id());
        player.pickRobot(figure, username);

        //informiere alle anderen clients über den neu gekommen
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

        String message = sendChatBody.getMessage();
        int to = sendChatBody.getTo();

        //Send Private message
        if (to != -1) {
            for (Connection connection : server.getConnections()) {
                if (connection.getPlayerID() == to) {
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

    public void handleReceivedChat(ClientModel clientModel, ReceivedChatBody receivedChatBody) {
        logger.info(ANSI_CYAN + "[MessageHandler]: Chat received. " + ANSI_RESET);
        clientModel.receiveMessage(receivedChatBody.getMessage());
    }

    public void handleGameStarted(ClientModel client, GameStartedBody bodyObject) {
        logger.info(ANSI_CYAN + "[MessageHandler]: Game Started received." + ANSI_RESET);
        //TODO implement map controller and use in this method to build the map
    }

    //Server receive this message
    public void handleAlive(Server server, ClientHandler clientHandler, AliveBody aliveBody) {
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
    public void handleAlive(ClientModel clientModel, AliveBody aliveBody) {
        //wenn client bekommt ein Alive-Message von Server, schickt er ein "Alive"-Antwort zurück
        clientModel.sendMessage(new JSONMessage("Alive", new AliveBody()));
    }

    public void handlePlayerAdded(ClientModel clientModel, PlayerAddedBody playerAddedBody) {
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

        if (setStatusBody.isReady()) {
            server.getReadyPlayer().add(player);
        } else {
            server.getReadyPlayer().remove(player);
        }
        //String isReady = setStatusBody.isReady() ? "ready" : "not ready";
        for (Connection connection : server.getConnections()) {
            server.sendMessage(new JSONMessage("PlayerStatus", new PlayerStatusBody(player.getPlayerID(), player.isReady())), connection.getWriter());
        }

        if (server.getReadyPlayer().size() == 1) {
            JSONMessage selectMapmessage = new JSONMessage("SelectMap", new SelectMapBody(server.getCurrentGame().getAvailableMaps()));
            server.sendMessage(selectMapmessage, clientHandler.getWriter());
        }
        // logger.info("The player " + clientHandler.getPlayer_id() + " is " + isReady);
    }

    public void handlePlayerStatus(ClientModel clientModel, PlayerStatusBody playerStatusBody) {
        clientModel.refreshPlayerStatus(playerStatusBody.getClientID(), playerStatusBody.isReady());
    }

    //diese Methode wird getriggert wenn Client eine SelectMap Message bekommt.
    public void handleSelectMap(ClientModel clientModel, SelectMapBody selectMapBody) {
        for (String map : selectMapBody.getAvailableMaps()) {
            clientModel.getAvailableMaps().add(map);
        }
        clientModel.setDoChooseMap(true);
    }

    public void handleMapSelected(Server server, ClientHandler clientHandler, MapSelectedBody mapSelectedBody) {
        for (Connection connection : server.getConnections()) {
            server.sendMessage(new JSONMessage("MapSelected", new MapSelectedBody(mapSelectedBody.getMap())), connection.getWriter());
        }
    }

    public void handleMapSelected (ClientModel clientModel, MapSelectedBody mapSelectedBody) {
        clientModel.setSelectedMap("DizzyHighway");
        System.out.println("IM HERE");
    }

    public void handleConnectionUpdate (ClientModel clientmodel, ConnectionUpdateBody connectionUpdateBody) {
        int playerID = connectionUpdateBody.getPlayerID();
        boolean isConnected = connectionUpdateBody.isConnected();
        String action = connectionUpdateBody.getAction();

        if (action.equals("remove") && !isConnected) {
            clientmodel.removePlayer(playerID);
        }
    }

}

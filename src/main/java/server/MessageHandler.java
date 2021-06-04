package server;

import game.Game;
import game.Player;
import game.Robot;
import json.JSONDeserializer;
import json.JSONMessage;
import json.protocol.*;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;

/**
 * @author Mohamad, Viktoria
 */
public class MessageHandler {
    private static final Logger logger = Logger.getLogger(MessageHandler.class.getName());
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_RESET = "\u001B[0m";

    /**
     * Wenn der Server ein Message HelloServer bekommt, schickt er ein Welcome-Message zu dem ClientModel mit dem ID
     *
     * @param server          The Server
     * @param clientHandler   The ClientHandler of the Server
     * @param helloServerBody The message body of the message which is of type  HelloServerBody
     */
    public void handleHelloServer (Server server, ClientHandler clientHandler, HelloServerBody helloServerBody) {
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

                if (server.getCurrentGame().isGameOn()) {
                    JSONMessage errorGameOnMessage = new JSONMessage("Error", new ErrorBody("gameOn"));
                    server.sendMessage(errorGameOnMessage, clientHandler.getWriter());
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


    public void handlePlayerValues (Server server, ClientHandler clientHandler, PlayerValuesBody playerValuesBody) {
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


    public void handleSendChat (Server server, ClientHandler clientHandler, SendChatBody sendChatBody) {
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

    public void handleSetStatus (Server server, ClientHandler clientHandler, SetStatusBody setStatusBody) {
        Player player = server.getPlayerWithID(clientHandler.getPlayer_id());
        boolean ready = setStatusBody.isReady();
        player.setReady(ready);

        if (ready) {
            server.getReadyPlayer().add(player);
            if (server.getReadyPlayer().size() == 1) {
                JSONMessage selectMapMessage = new JSONMessage("SelectMap", new SelectMapBody(server.getCurrentGame().getAvailableMaps()));
                server.sendMessage(selectMapMessage, clientHandler.getWriter());
            }
            if (server.canStartTheGame()) {
                try {
                    server.getCurrentGame().start(server.getReadyPlayer());
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                logger.info("I CAN START THE GAME");
            }

        } else {
            if (player.getPlayerID() == server.getReadyPlayer().get(0).getPlayerID() && server.getReadyPlayer().size() != 1) {
                Player nextOne = server.getReadyPlayer().get(1);
                JSONMessage selectMapMessage = new JSONMessage("SelectMap", new SelectMapBody(server.getCurrentGame().getAvailableMaps()));
                server.sendMessage(selectMapMessage, server.getConnectionWithID(nextOne.getPlayerID()).getWriter());
            }
            server.getReadyPlayer().remove(player);
        }

        for (Connection connection : server.getConnections()) {
            server.sendMessage(new JSONMessage("PlayerStatus", new PlayerStatusBody(player.getPlayerID(), player.isReady())), connection.getWriter());
        }

        String isReady = setStatusBody.isReady() ? "ready" : "not ready";
        logger.info("The player " + player.getName() + " is " + isReady);
    }

    public void handleMapSelected (Server server, ClientHandler clientHandler, MapSelectedBody mapSelectedBody) throws IOException {
        //TODO: SEND NOT ZU DEN SPIELER
        for (Connection connection : server.getConnections()) {
            server.sendMessage(new JSONMessage("MapSelected", new MapSelectedBody(mapSelectedBody.getMap())), connection.getWriter());
        }
        server.getCurrentGame().selectMap(mapSelectedBody.getMap());
    }

    public void handleSetStartingPoint (Server server, ClientHandler clientHandler, SetStartingPointBody bodyObject) {
        //TODO: hier etwas wie "Server speichert die Position von dem Player with ID playerID in der position x,y
        int playerID = clientHandler.getPlayer_id();
        int x = bodyObject.getX();
        int y = bodyObject.getY();

        if (playerID == server.getCurrentGame().getCurrentPlayer()) {
            if (server.getCurrentGame().valideStartingPoint(x, y)) {
                Player player = server.getPlayerWithID(playerID);
                player.setRobot(new Robot(Game.getRobotNames().get(player.getFigure()), x, y));

                server.getCurrentGame().setCurrentPlayer(server.getCurrentGame().nextPlayerID());
                for (Player otherPlayer : server.getReadyPlayer()) {
                    //sage allen wo der Spieler mit playerID started
                    //TODO: nur an player schicken
                    JSONMessage startingPointTakenMessage = new JSONMessage("StartingPointTaken", new StartingPointTakenBody(x, y, playerID));
                    server.sendMessage(startingPointTakenMessage, server.getConnectionWithID(otherPlayer.getPlayerID()).getWriter());
                    if (server.getCurrentGame().getCurrentPlayer() != -1) {
                        JSONMessage currentPlayerMessage = new JSONMessage("CurrentPlayer", new CurrentPlayerBody(server.getCurrentGame().getCurrentPlayer()));
                        server.sendMessage(currentPlayerMessage, server.getConnectionWithID(otherPlayer.getPlayerID()).getWriter());
                    } else {
                        server.getCurrentGame().setActivePhase(2);
                    }
                }
            }
            //create einen neuen Robot auf (x,y) und setRobot zu dem Player
        } else {
            JSONMessage errorNotYourTurn = new JSONMessage("Error", new ErrorBody("It's not your turn!"));
            server.sendMessage(errorNotYourTurn, clientHandler.getWriter());
        }
    }

    public void handleSelectedCard(Server server, ClientHandler clientHandler, SelectedCardBody selectedCardBody) {
        String card = selectedCardBody.getCard();
        int register = selectedCardBody.getRegister();
        System.out.println("HEY I GOT SELECTED");

//        if (server.getPlayerWithID(clientHandler.getPlayer_id()).selectedCard(card)) {

//        }

//        server.getPlayerWithID(clientHandler.getPlayer_id()).getDeckRegister().getDeck().add(register, card);

    }

}

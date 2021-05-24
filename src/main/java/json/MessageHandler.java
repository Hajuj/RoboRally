package json;

import client.model.ClientModel;
import client.model.ClientModelReaderThread;

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
     * Wenn client ein HalloClient Massage von Server bekommt, wird die Variable waitingForServer
     * auf false gesetz und client kann dem Server Nachrichten schicken.
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
                int actual_id = server.getClientsCounter();
                // First, assign the client a playerID
                JSONMessage welcomeMessage = new JSONMessage("Welcome", new WelcomeBody(actual_id));
                clientHandler.getWriter().println(JSONSerializer.serializeJSON(welcomeMessage));
                clientHandler.getWriter().flush();
                clientHandler.setPlayer_id(actual_id);

                // Create a Connection to this clientSocket
                Connection connection = new Connection(clientHandler.getClientSocket());
                server.getConnections().add(connection);

                Player player = new Player(actual_id);
                server.getWaitingPlayer().add(player);

                // Immer um eins erhöhen für den nächsten client
                server.setClientsCounter(actual_id + 1);

            } else {
                logger.info("Protocol version test failed");
                JSONMessage jsonMessage = new JSONMessage("Error", new ErrorBody("Protocol version test failed. Server hat Protokoll " + server.getProtocolVersion()));
                clientHandler.getWriter().println(JSONSerializer.serializeJSON(jsonMessage));
                clientHandler.getWriter().flush();
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
        logger.info("Your PlayerID: " + welcomeBody.getPlayerID());
        Player player = new Player(welcomeBody.getPlayerID());
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
    }

    public void handlePlayerValues(Server server, ClientHandler clientHandler, PlayerValuesBody playerValuesBody) {
        String username = playerValuesBody.getName();
        int figure = playerValuesBody.getFigure();
        boolean usernameCheck = true;
        boolean figurCheck = true;

        //erst mal usernameCheck
        for (Player player : server.getWaitingPlayer()) {
            //wenn nicht dasselbe client
            if (player.getPlayerID() != clientHandler.getPlayer_id()) {
                if (player.getName() != null) {
                    //wenn die namen gleich sind
                    if (player.getName().equals(username)) {
                        //schick ein errorMessage
                        JSONMessage errorMessage = new JSONMessage("Error", new ErrorBody("Please use another name"));
                        clientHandler.getWriter().println(JSONSerializer.serializeJSON(errorMessage));
                        clientHandler.getWriter().flush();
                        logger.info("Alles schlecht, der Spieler mit ID kann nicht username " + username + " auswählen");
                        usernameCheck = false;
                    }
                }
            }
        }
        //wenn name passt
        if (usernameCheck) {
            //mach  ein figure-check
            for (Player player : server.getWaitingPlayer()) {
                //wenn nicht dasselbe client
                if (player.getPlayerID() != clientHandler.getPlayer_id()) {
                    //schau ob die roboter die gleichen sind
                    if (player.getFigure() == figure) {
                        JSONMessage errorMessage = new JSONMessage("Error", new ErrorBody("Please use another robot"));
                        clientHandler.getWriter().println(JSONSerializer.serializeJSON(errorMessage));
                        clientHandler.getWriter().flush();
                        logger.info("Alles schlecht, der Spieler mit ID " + clientHandler.getPlayer_id() + " kann nicht Figure " + figure + " auswählen");
                        figurCheck = false;
                    }
                }
            }
        }

        if (usernameCheck && figurCheck) {
            Player player = server.getWaitingPlayer().get(clientHandler.getPlayer_id() - 1);
            player.pickRobot(figure, username);
            logger.info("Alles gut, der Spieler mit ID" + clientHandler.getPlayer_id() + " heißt " + username + " und dat figur " + figure);
        }
    }

    public void handleSendChat(Server server, ClientHandler clientHandler, SendChatBody sendChatBody) {
        logger.info(ANSI_CYAN + "[MessageHandler]: Send Chat received. " + ANSI_RESET);

        //Send private or public message method to be implemented
    }

    public void handleReceivedChat(ClientModel clientModel, ClientModelReaderThread clientModelReaderThread, ReceivedChatBody receivedChatBody) {
        logger.info(ANSI_CYAN + "[MessageHandler]: Chat received. " + ANSI_RESET);

        //Receive message from user method to be implemented
    }

}

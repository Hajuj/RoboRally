package json;

import client.Client;
import client.ClientThread;
import client.ClientWrapper;
import game.Player;
import json.protocol.ErrorBody;
import json.protocol.HelloClientBody;
import json.protocol.HelloServerBody;
import json.protocol.WelcomeBody;
import server.Server;
import server.ClientHandler;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * @author Mohamad, Viktoria
 */

public class MessageHandler {
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_RESET = "\u001B[0m";
    private static final Logger logger = Logger.getLogger(MessageHandler.class.getName());


    /**
     * This method contains the logic that comes into action when a 'HelloServer' protocol message was received and
     * deserialized.
     *
     * @param server          The server.Server itself.
     * @param task            The ClientHandler of the Server (Gives access to the PrintWriter).
     * @param helloServerBody The message body of the message which is of type {@link HelloServerBody}.
     */
    public void handleHelloServer(Server server, ClientHandler task, HelloServerBody helloServerBody) {
        System.out.println(ANSI_CYAN + "[MessageHandler]: Entered handleHelloServer()" + ANSI_RESET);
        try {
            if (helloServerBody.getProtocol().equals(server.getProtocolVersion())) {
                logger.info("Protocol version test succeeded");

                // First, assign the client a playerID
                JSONMessage welcomeMessage = new JSONMessage("Welcome", new WelcomeBody(server.getCounterPlayerID()));
                task.getWriter().println(JSONSerializer.serializeJSON(welcomeMessage));
                task.getWriter().flush();

                // Server.Server creates his player instance
                Player newPlayer = new Player();
                newPlayer.setPlayerID(server.getCounterPlayerID());
                server.getPlayers().add(newPlayer);

                // Create a ClientWrapper containing the player object in order to keep track and update later on
                ClientWrapper newClientWrapper = new ClientWrapper();
                newClientWrapper.setPlayer(newPlayer);
                newClientWrapper.setClientSocket(task.getClientSocket());
                server.getConnectedClients().add(newClientWrapper);

                logger.info("Server created a player with the player ID: " + newPlayer.getPlayerID());

                // Counter is adjusted for next registration process
                server.setCounterPlayerID(server.getCounterPlayerID() + 1);

            } else {
                logger.info("Protocol version test failed");
                task.getClientSocket().close();
                logger.info("Server connection terminated");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * This method contains the logic that comes into action when a 'HelloClient' protocol message was received and
     * deserialized.
     *
     * @param client          The Client itself.
     * @param task            The ClientThread of the Client (Gives access to the PrintWriter).
     * @param helloClientBody The message body of the message which is of type {@link HelloClientBody}.
     */
    public void handleHelloClient(Client client, ClientThread task, HelloClientBody helloClientBody) {
        System.out.println(ANSI_CYAN + "[MessageHandler]: Entered handleHelloClient()" + ANSI_RESET);
        logger.info("Server has protocol " + helloClientBody.getProtocol());
        //TODO change to notify() in class ClientReaderTask
        client.setWaitingForHelloClient(false);
    }


    /**
     * This method contains the logic that comes into action when a 'Welcome' protocol message was received and
     * deserialized.
     *
     * @param client      The Client itself.
     * @param task        The ClientThread of the Client (Gives access to the PrintWriter).
     * @param welcomeBody The message body of the message which is of type {@link WelcomeBody}.
     */
    public void handleWelcome(Client client, ClientThread task, WelcomeBody welcomeBody) {
        System.out.println(ANSI_CYAN + "[MessageHandler]: Entered handleWelcome()" + ANSI_RESET);
        logger.info("PlayerID: " + welcomeBody.getPlayerID());

        // Client creates his player instance
        Player player = new Player();
        player.setPlayerID(welcomeBody.getPlayerID());
        client.setPlayer(player);

        logger.info("Client created a player with the player ID: " + player.getPlayerID());

        task.setPlayerID(welcomeBody.getPlayerID());

    }


    /**
     * This method contains the logic that comes into action when a 'Error' protocol message was received and
     * deserialized.
     *
     * @param client    The Client itself.
     * @param task      The ClientThread of the Client (Gives access to the PrintWriter).
     * @param errorBody The message body of the message which is of type {@link ErrorBody}.
     */
    public void handleError(Client client, ClientThread task, ErrorBody errorBody) {
        System.out.println(ANSI_CYAN + "[MessageHandler]: Entered handleError()" + ANSI_RESET);

        String errorMessage = errorBody.getError();
        logger.info("Error has occurred! " + errorMessage);
    }


}

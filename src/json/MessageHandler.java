package json;

import client.model.ClientModel;
import client.model.ClientModelReaderThread;
import server.Connection;
import json.protocol.ErrorBody;
import json.protocol.HelloClientBody;
import json.protocol.HelloServerBody;
import json.protocol.WelcomeBody;
import server.Server;
import server.ClientHandler;

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
     * @param clientmodel     The ClientModel
     * @param clientThread    The ClientModelReaderThread of the ClientModel
     * @param helloClientBody The message body of the message which is of type HelloClientBody
     */
    public void handleHelloClient (ClientModel clientmodel, ClientModelReaderThread clientThread, HelloClientBody helloClientBody) {
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
    public void handleHelloServer (Server server, ClientHandler clientHandler, HelloServerBody helloServerBody) {
        logger.info(ANSI_CYAN + "[MessageHandler]: HalloServer Message received. " + ANSI_RESET);
        try {
            if (helloServerBody.getProtocol().equals(server.getProtocolVersion())) {
                logger.info("Protocol version test succeeded");

                // First, assign the client a playerID
                JSONMessage welcomeMessage = new JSONMessage("Welcome", new WelcomeBody(server.getClientsCounter()));
                clientHandler.getWriter().println(JSONSerializer.serializeJSON(welcomeMessage));
                clientHandler.getWriter().flush();

                // Create a Connection to this clientSocket
                Connection connection = new Connection(clientHandler.getClientSocket());
                server.getConnections().add(connection);

                // Immer um eins erhöhen dür den nächsten client
                server.setClientsCounter(server.getClientsCounter() + 1);

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
     * @param clientmodel       The ClientModel itself.
     * @param clientThread The ClientModelReaderThread of the ClientModel
     * @param welcomeBody  The message body of the message which is of type {@link WelcomeBody}.
     */
    public void handleWelcome (ClientModel clientmodel, ClientModelReaderThread clientThread, WelcomeBody welcomeBody) {
        logger.info(ANSI_CYAN + "[MessageHandler]: Welcome Message received." + ANSI_RESET);
        logger.info("Your PlayerID: " + welcomeBody.getPlayerID());
    }


    /**
     * Wenn es ein Error occured bei deserialization, wird ein Message mit dem ErrorBody geschickt
     *
     * @param clientmodel    The ClientModel itself.
     * @param task      The ClientModelReaderThread of the ClientModel (Gives access to the PrintWriter).
     * @param errorBody The message body of the message which is of type {@link ErrorBody}.
     */
    public void handleError (ClientModel clientmodel, ClientModelReaderThread task, ErrorBody errorBody) {
        logger.warn(ANSI_CYAN + "[MessageHandler]: Error has occurred! " + ANSI_RESET);
        logger.info("Error has occurred! " + errorBody.getError());
    }


}

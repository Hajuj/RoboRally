package server;

import game.Game;
import game.Player;
import json.JSONMessage;
import json.JSONSerializer;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;


/**
 * This class implements the server.
 *
 * @author Mohamad, Viktoria
 */
public class Server {
    private static Server instance;
    private final int SERVER_PORT = 500;
    private final int MAX_CLIENT = 50;
    private static final Logger logger = Logger.getLogger(Server.class.getName());
    private MessageHandler messageHandler;
    private final String protocolVersion = "Version 1.0";
    private ArrayList<Player> waitingPlayer = new ArrayList<>();
    private ArrayList<Player> readyPlayer = new ArrayList<>();
    private Game currentGame = new Game(this);

    private int clientsCounter = 1;
    private ArrayList<Connection> connections = new ArrayList<>();

    private Server () {
    }

    public static Server getInstance () {
        if (instance == null) {
            instance = new Server();
        }
        return instance;
    }


    public static void main (String[] args) {
        Server server = Server.getInstance();
        server.messageHandler = new MessageHandler();
        server.start();
    }

    public void start () {
        logger.info("Starting server...");

        // Open socket for incoming connections, if socket already exists start aborts
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(SERVER_PORT);
        } catch (IOException ioException) {
            logger.warn("Exception in opening Serversocket for incoming connections. " + ioException.getMessage());
            System.exit(0);
        }

        logger.info("The server has a port number " + SERVER_PORT + ", and runs on localhost.");

        while (checkMaxClient()) {
            logger.info("Waiting for new client...");

            //serverSocket.accept() waits for new clientSocket
            Socket clientSocket = null;
            try {
                clientSocket = serverSocket.accept();
            } catch (IOException | NullPointerException ioException) {
                logger.warn("Exception in accepting Clientsocket. " + ioException.getMessage());
            }

            logger.info("ClientModel connected from: " + clientSocket.getInetAddress().getHostAddress());

            //Every client has its own ClientHandler, starts in the new Thread
            ClientHandler clientHandler = new ClientHandler(clientSocket, this, protocolVersion, messageHandler);
            Thread clientHandlerThread = new Thread(clientHandler);
            clientHandlerThread.start();

            logger.info("ClientModel handler thread " + clientSocket.getInetAddress().getHostAddress());
        }
        //When server shuts down, log the info and close the socket
        logger.warn("Server shut down. Closing the Serversocket..");
        try {
            serverSocket.close();
        } catch (IOException ioException) {
            logger.warn("Exception in closing Serversocket. " + ioException.getMessage());
        }
    }


    public void sendMessage (JSONMessage jsonMessage, PrintWriter writer) {
        writer.println(JSONSerializer.serializeJSON(jsonMessage));
        writer.flush();
    }

    public boolean areAllPlayersReady() {
        if (getReadyPlayer().size() < 2) return false;
        if (getReadyPlayer().size() == 6) return true;
        return getReadyPlayer().size() == getWaitingPlayer().size();
    }

    public Player getPlayerWithID (int ID) {
        for (Player player : waitingPlayer) {
            if (player.getPlayerID() == ID) {
                return player;
            }
        }
        return null;
    }

    public Connection getConnectionWithID (int ID) {
        for (Connection connection : connections) {
            if (connection.getPlayerID() == ID) {
                return connection;
            }
        }
        return null;
    }

    public ArrayList<Player> readyPlayerWithoutAI() {
        ArrayList<Player> readyPlayerWithoutAI = new ArrayList<>();
        for (Player player : readyPlayer) {
            if (!player.isAI()) {
                readyPlayerWithoutAI.add(player);
            }
        }
        return readyPlayerWithoutAI;
    }

    public boolean onlyAI() {
        for (Player player : readyPlayer) {
            if (!player.isAI()) {
                return false;
            }
        }
        return true;
    }

    public Game getCurrentGame () {
        return currentGame;
    }

    public void setCurrentGame (Game currentGame) {
        this.currentGame = currentGame;
    }

    public ArrayList<Player> getReadyPlayer () {
        return readyPlayer;
    }

    public void setReadyPlayer(ArrayList<Player> readyPlayer) {
        this.readyPlayer = readyPlayer;
    }

    public ArrayList<Player> getWaitingPlayer () {
        return waitingPlayer;
    }

    public boolean checkMaxClient () {
        return connections.size() < MAX_CLIENT;
    }

    public int getClientsCounter () {
        return clientsCounter;
    }

    public void setClientsCounter (int clientsCounter) {
        this.clientsCounter = clientsCounter;
    }

    public ArrayList<Connection> getConnections () {
        return connections;
    }

    public String getProtocolVersion () {
        return protocolVersion;
    }

}

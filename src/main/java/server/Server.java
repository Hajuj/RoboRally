package server;

import client.model.ClientModel;
import game.Player;

import json.JSONMessage;
import json.JSONSerializer;
import json.MessageHandler;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

import org.apache.log4j.Logger;


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
    private final String protocolVersion = "Version 0.1";
    private final ArrayList<Player> waitingPlayer = new ArrayList<>();

    private int clientsCounter = 1;
    private final ArrayList<Connection> connections = new ArrayList<>();

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
        boolean isWaitingForClients = true;

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

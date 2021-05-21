package server;

import client.*;
import game.Player;
import json.MessageHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * This class implements the server.
 *
 * @author Mohamad, Viktoria
 */

public class Server {
    private final int SERVER_PORT = 500;
    private static final Logger logger = Logger.getLogger(Server.class.getName());
    private final MessageHandler messageHandler = new MessageHandler();
    private final String protocolVersion = "Version 0.1";

    private int clientsCounter = 1;
    private ArrayList<Connection> connections = new ArrayList<>();
    private ArrayList<Player> players = new ArrayList<>();


    public static void main (String[] args) {
        Server server = new Server();
        server.start();
    }

    public void start () {
        logger.info("Starting server...");

        // Open socket for incoming connections, if socket already exists start aborts
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(SERVER_PORT);
        } catch (IOException ioException) {
            logger.warning("Exception in opening Serversocket for incoming connections. " + ioException.getMessage());
        }

        logger.info("The server has a port number " + SERVER_PORT + ", and runs on localhost.");
        boolean isWaitingForClients = true;

        // TODO: begrenzte Anzahl von clients
        // jetzt wartet er unendlich auf die neuen clientSockets
        while (isWaitingForClients) {
            logger.info("Waiting for new client...");

            //serverSocket.accept() waits for new clientSocket
            Socket clientSocket = null;
            try {
                clientSocket = serverSocket.accept();
            } catch (IOException | NullPointerException ioException) {
                logger.warning("Exception in accepting Clientsocket. " + ioException.getMessage());
            }

            logger.info("Client connected from: " + clientSocket.getInetAddress().getHostAddress());

            //Every client has its own ClientHandler, starts in the new Thread
            ClientHandler clientHandler = new ClientHandler(clientSocket, this, protocolVersion, messageHandler);
            Thread clientHandlerThread = new Thread(clientHandler);
            clientHandlerThread.start();

            logger.info("Client handler thread " + clientSocket.getInetAddress().getHostAddress());
        }
        //When server shuts down, log the info and close the socket
        logger.warning("Server shut down. Closing the Serversocket..");
        try {
            serverSocket.close();
        } catch (IOException ioException) {
            logger.warning("Exception in closing Serversocket. " + ioException.getMessage());
        }
    }

    public ArrayList<Player> getPlayers () {
        return players;
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

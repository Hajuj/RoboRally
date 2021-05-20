package server;

import client.*;
import game.Player;
import javafx.application.Application;
import javafx.stage.Stage;
import json.MessageHandler;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * This class implements the server.
 * It will communicate with the clients.
 *
 * @author Mohamad, Viktoria
 */

public class Server extends Application {
    private static final Logger logger = Logger.getLogger(Server.class.getName());
    private ArrayList<ClientWrapper> connectedClients;
    private int SERVER_PORT = 500;
    private MessageHandler messageHandler = new MessageHandler();
    private String protocolVersion = "Version 0.1";
    private int counterPlayerID = 1;
    private ArrayList<Player> players;

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public int getCounterPlayerID() {
        return counterPlayerID;
    }

    public void setCounterPlayerID(int counterPlayerID) {
        this.counterPlayerID = counterPlayerID;
    }

    public ArrayList<ClientWrapper> getConnectedClients() {
        return connectedClients;
    }

    public MessageHandler getMessageDistributer() {
        return messageHandler;
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }

    @Override
    public void start(Stage stage) throws Exception {
        logger.info("Starting server...");

        // Open socket for incoming connections, if socket already exists start aborts
        ServerSocket serverSocket = new ServerSocket(SERVER_PORT);

        connectedClients = new ArrayList<>();
        boolean isAcceptingNewClients = true;

        while (isAcceptingNewClients) { // Runs forever at the moment
            logger.info("Waiting for new client...");
            //New client connects: (accept() waits for new client)
            Socket clientSocket = serverSocket.accept();
            logger.info("Client connected from: " + clientSocket.getInetAddress().getHostAddress());

            //ServerReaderTask that reads incoming messages from clients -> Every client has its own Task/Thread
            ClientHandler task = new ClientHandler(clientSocket, this);
            Thread clientHandlerThread = new Thread(task);
            clientHandlerThread.start();
            logger.info("Client handler thread " + clientSocket.getInetAddress().getHostAddress());
        }

        //Server.Server shuts down:
        serverSocket.close();
        logger.info("Server shut down.");


    }


}

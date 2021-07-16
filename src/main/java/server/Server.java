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
import java.util.Date;


/**
 * This class implements the server.
 *
 * @author Mohamad, Viktoria
 */
public class Server {
    private static Server instance;
    private static int serverPort = 500;
    private final int MAX_CLIENT = 50;
    private static final Logger logger = Logger.getLogger(Server.class.getName());
    private MessageHandler messageHandler;
    private final String protocolVersion = "Version 2.1";
    private ArrayList<Player> waitingPlayer = new ArrayList<>();
    private ArrayList<Player> readyPlayer = new ArrayList<>();
    private Game currentGame = new Game(this);
    private static String loggerStamp = "Server";

    private int clientsCounter = 1;
    private ArrayList<ClientHandler.Connection> connections = new ArrayList<>();


    private Server() {
        Date date = new Date();
        String workIt = date.toString().replaceAll("\\s+", "_");
        workIt = workIt.replace(":", "-");
        loggerStamp += workIt;
    }

    public static Server getInstance() {
        if (instance == null) {
            instance = new Server();
        }
        return instance;
    }


    public static void main(String[] args) {
        if (args.length == 0)
            throw new IllegalArgumentException("No arguments provided. Flags: -p Port.");
        if (args.length == 1) {
            if (args[0].charAt(0) == '-') {
                throw new IllegalArgumentException("Expected argument after: " + args[0]);
            } else {
                throw new IllegalArgumentException("Illegal Argument: " + args[0]);
            }
        }

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            String nextArg;
            if (i + 1 == args.length || args[i + 1].charAt(0) == '-') {
                throw new IllegalArgumentException("Expected argument after: " + arg);
            } else {
                nextArg = args[i + 1];
            }
            if ("-p".equals(arg)) {
                int port = Integer.parseInt(nextArg);
                if (port < 500 || port > 65535)
                    throw new IllegalArgumentException("Port number: " + port + " is invalid");
                serverPort = port;
                i++;
            } else {
                throw new IllegalArgumentException("Illegal Argument: " + arg);
            }
        }
        Server server = Server.getInstance();
        server.messageHandler = new MessageHandler();
        server.start();
    }

    public void start() {
        logger.info("Starting server...");

        // Open socket for incoming connections, if socket already exists start aborts
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(serverPort);
        } catch (IOException ioException) {
            logger.warn("Exception in opening Serversocket for incoming connections. " + ioException.getMessage());
            System.exit(0);
        }

        logger.info("The server has a port number " + serverPort + ", and runs on localhost.");

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


    public void sendMessage(JSONMessage jsonMessage, PrintWriter writer) {
        writer.println(JSONSerializer.serializeJSON(jsonMessage));
        writer.flush();
    }

    public boolean areAllPlayersReady() {
        if (getReadyPlayer().size() < 2) return false;
        if (getReadyPlayer().size() == 6) return true;
        return getReadyPlayer().size() == getWaitingPlayer().size();
    }

    public Player getPlayerWithID(int ID) {
        for (Player player : waitingPlayer) {
            if (player.getPlayerID() == ID) {
                return player;
            }
        }
        return null;
    }

    public ClientHandler.Connection getConnectionWithID(int ID) {
        for (ClientHandler.Connection connection : connections) {
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

    public Game getCurrentGame() {
        return currentGame;
    }

    public void setCurrentGame(Game currentGame) {
        this.currentGame = currentGame;
    }

    public ArrayList<Player> getReadyPlayer() {
        return readyPlayer;
    }

    public void setReadyPlayer(ArrayList<Player> readyPlayer) {
        this.readyPlayer = readyPlayer;
    }

    public ArrayList<Player> getWaitingPlayer() {
        return waitingPlayer;
    }

    public boolean checkMaxClient() {
        return connections.size() < MAX_CLIENT;
    }

    public int getClientsCounter() {
        return clientsCounter;
    }

    public void setClientsCounter(int clientsCounter) {
        this.clientsCounter = clientsCounter;
    }

    public ArrayList<ClientHandler.Connection> getConnections() {
        return connections;
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }

}

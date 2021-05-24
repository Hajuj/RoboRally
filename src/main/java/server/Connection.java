package server;

import game.Player;

import java.io.PrintWriter;
import java.net.Socket;

/**
 * Inner class to wrap the information from the client
 *
 * @author Mohamad, Viktoria
 */
public class Connection {
    private Socket socket;
    private ClientHandler clientHandler;
    private Player player;
    private int playerID;
    private String name;

    public Connection(Socket socket, String name, PrintWriter writer, int figure, int playerID, boolean isReady) {
        this.socket = socket;
        this.playerID = playerID;
        this.name = name;
    }

    public Connection(Socket clientSocket) {
        this.socket = clientSocket;
    }

    public boolean hasPlayerIdSocket() {
        return socket.equals(clientHandler.getClientSocket());
    }

    public int getPlayerID() {
        return playerID;
    }

    public String getName() {
        return name;
    }
}

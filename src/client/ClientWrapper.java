package client;

import game.Player;

import java.io.PrintWriter;
import java.net.Socket;

/**
 * Inner class to wrap the information from the client
 *
 * @author Mohamad, Viktoria
 */

public class ClientWrapper {
    private PrintWriter writer;
    private Socket socket;

    private Player player;
    private String name;
    private int playerID;
    private int figure;
    private boolean isReady;

    public ClientWrapper() {
    }

    public ClientWrapper(Socket socket, String name, PrintWriter writer, int figure, int playerID, boolean isReady) {
        this.socket = socket;
        this.name = name;
        this.writer = writer;
        this.figure = figure;
        this.playerID = playerID;
        this.isReady = isReady;

    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void setClientSocket(Socket clientSocket) {
    }
}

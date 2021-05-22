package client;

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
    private Player player;
    private int playerID;


    public Connection (Socket socket) {
        this.socket = socket;
    }

    public Connection (Socket socket, String name, PrintWriter writer, int figure, int playerID, boolean isReady) {
        this.socket = socket;
        this.playerID = playerID;
    }
}

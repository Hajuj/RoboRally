package server;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Inner class to wrap the information from the client
 *
 * @author Mohamad, Viktoria
 */
public class Connection {
    private PrintWriter writer;
    private Socket socket;
    private ClientHandler clientHandler;
    private int playerID;
    private String name;
    private boolean isConnected;

    public Connection(Socket clientSocket) throws IOException {
        this.socket = clientSocket;
        this.writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void setPlayerID(int playerID) {
        this.playerID = playerID;
    }

    public int getPlayerID() {
        return playerID;
    }

    public String getName() {
        return name;
    }

    public PrintWriter getWriter() {
        return writer;
    }
}

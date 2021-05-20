package client;

import game.Player;
import json.JSONSerializer;
import json.JSONMessage;
import json.MessageHandler;
import json.protocol.HelloServerBody;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Logger;

/**
 * @author Mohamad, Viktoria
 */

public class Client {
    private MessageHandler messageHandler = new MessageHandler();
    private String name;
    private String serverIP;
    private int serverPort;
    private String protocolVersion = "Version 0.1";
    private static final Logger logger = Logger.getLogger(Client.class.getName());
    private String group = "BlindeBonbons";
    private Player player;

    private boolean waitingForHelloClient;
    private Socket socket;
    private PrintWriter writer;

    public Client(String serverIP, int serverPort) {
        logger.info("Starting registration process...");
        this.serverIP = serverIP;
        this.serverPort = serverPort;

    }

    /**
     * This method is responsible for connecting the client to the specified server.
     * It uses the {@link client.ClientApplication} to get the IP and Port.
     *
     * @return <code>true</code> if connection could be established.
     */
    public boolean connectClient() {
        try {
            //Create socket to connect to server at serverIP:serverPort
            socket = new Socket(serverIP, serverPort);

            //Start new Thread, that reads incoming messages from server
            ClientThread readerTask = new ClientThread(this, socket);
            Thread readerThread = new Thread(readerTask);
            readerThread.start();

            //TODO change to notify()
            //waiting for server response - waitingForHelloClient is changed by ClientReaderTask
            waitingForHelloClient = true;
            while (waitingForHelloClient) {
                logger.info("Waiting...");
                if (waitingForHelloClient) try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            // Inform the server about group, AI and the clients protocol version
            writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            JSONMessage jsonMessage = new JSONMessage("HelloServer", new HelloServerBody(group, false, protocolVersion));
            writer.println(JSONSerializer.serializeJSON(jsonMessage));
            writer.flush();

        } catch (IOException exp) {
            exp.printStackTrace();
        }
        return false;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void setMessageDistributer(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    public Player getPlayer() {
        return player;
    }

    public MessageHandler getMessageDistributer() {
        return messageHandler;
    }

    public void setWaitingForHelloClient(boolean waitingForHelloClient) {
        this.waitingForHelloClient = waitingForHelloClient;
    }
}

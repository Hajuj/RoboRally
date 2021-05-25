package client.model;

import game.Player;

import javafx.beans.property.StringProperty;
import json.JSONMessage;
import json.MessageHandler;
import json.protocol.HelloServerBody;
import json.protocol.PlayerValuesBody;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

import org.apache.log4j.Logger;

/**
 * @author Mohamad, Viktoria
 */
public class ClientModel {
    private String username;
    private ArrayList<String> usersOnline;
    private String message;
    private Socket socket;
    private String server_ip;
    private int server_port;
    private boolean waitingForServer;
    private ClientModelReaderThread clientModelReaderThread;
    private ClientModelWriterThread clientModelWriterThread;
    private static final Logger logger = Logger.getLogger(ClientModel.class.getName());
    private final String protocolVersion = "Version 0.1";
    private final MessageHandler messageHandler = new MessageHandler();
    private final String group = "BlindeBonbons";
    private Player player;
    private StringProperty chatHistory;

    /**
     * This method is responsible for connecting the client to the specified server.
     * It uses the {@link client.ClientApplication} to get the IP and Port.
     *
     * @return <code>true</code> if connection could be established.
     */
    public boolean connectClient() {
        try {
            //Create socket to connect to server at serverIP:serverPort
            server_ip = "localhost";
            server_port = 500;

            logger.info("Trying to connect to the server on the port " + server_ip + " : " + server_port);
            socket = new Socket(server_ip, server_port);

            //Start new Thread, that reads incoming messages from server
            clientModelReaderThread = new ClientModelReaderThread(this, socket);
            clientModelWriterThread = new ClientModelWriterThread(this, socket, messageHandler);
            Thread readerThread = new Thread(clientModelReaderThread);
            readerThread.start();
            Thread writerTread = new Thread(clientModelWriterThread);
            writerTread.start();

            //TODO kein Busy-Waiting, change to notify()
            //waiting for server response - waitingForHelloClient is changed wenn die clientThread bekommt
            //ein JSONMessage mit dem type HelloClient
            waitingForServer = true;
            while (waitingForServer) {
                logger.info("Waiting for the server answer...");
                if (waitingForServer) try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            sendMessage(new JSONMessage("HelloServer", new HelloServerBody(group, false, protocolVersion)));

        } catch (IOException exp) {
            exp.printStackTrace();
        }
        return false;
    }

    public void sendMessage(JSONMessage message) {
        this.clientModelWriterThread.sendMessage(message);
    }

    //TODO check if working
    public void receiveMessage(String message) {
        String oldHistory = chatHistory.get();
        String newHistory = oldHistory + "\n" + message;
        chatHistory.setValue(newHistory);
    }

    public void sendUsernameAndRobot(String username, int figure) {
        JSONMessage jsonMessage = new JSONMessage("PlayerValues", new PlayerValuesBody(username, figure));
        sendMessage(jsonMessage);
    }


    public void setPlayer(Player player) {
        this.player = player;
    }

    public MessageHandler getMessageHandler() {
        return messageHandler;
    }


    public void setWaitingForServer(boolean waitingForServer) {
        this.waitingForServer = waitingForServer;
    }
}

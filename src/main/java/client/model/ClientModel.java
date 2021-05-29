package client.model;


import game.Player;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import json.JSONMessage;
import json.MessageHandler;
import json.protocol.HelloServerBody;
import json.protocol.PlayerValuesBody;

import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import json.protocol.SendChatBody;
import org.apache.log4j.Logger;
import server.ClientHandler;
import server.Server;

/**
 * @author Mohamad, Viktoria
 * ClientModel realisiert Singelton-Pattern, damit alle ViewModels referenzen auf das gleiche Object von ClientModel Klasse haben
 *
 */
public class ClientModel {
    private static ClientModel instance;
    private String username;
    private ArrayList<String> usersOnline;
    private String message;
    private Socket socket;
    private String server_ip;
    private int server_port;
    private boolean waitingForServer = true;
    private ClientModelReaderThread clientModelReaderThread;
    private ClientModelWriterThread clientModelWriterThread;
    private static final Logger logger = Logger.getLogger(ClientModel.class.getName());
    private final String protocolVersion = "Version 0.1";
    private final MessageHandler messageHandler = new MessageHandler();
    private HashMap<Integer, Boolean> playersStatusMap = new HashMap<Integer, Boolean>();
    private HashMap<Integer, String> playersNamesMap = new HashMap<Integer, String>();
    private final String group = "BlindeBonbons";
    private Player player;
    private String newMessage;
    private StringProperty chatHistory = new SimpleStringProperty("");


    private ClientModel () {
    }

    public static ClientModel getInstance () {
        if (instance == null){
            instance = new ClientModel();
        }
        return instance;
    }

    /**
     * This method is responsible for connecting the client to the specified server.
     * @return true if connection could be established.
     */
    public boolean connectClient(String server_ip ,int server_port) {
        try {
            //Create socket to connect to server at serverIP:serverPort
            logger.info("Trying to connect to the server on the port " + server_ip + " : " + server_port);
            socket = new Socket(server_ip, server_port);

            //Start new Threads for reading/writing messages from/to the server
            clientModelReaderThread = new ClientModelReaderThread(this, socket);
            clientModelWriterThread = new ClientModelWriterThread(this, socket, messageHandler);
            Thread readerThread = new Thread(clientModelReaderThread);
            readerThread.start();
            Thread writerTread = new Thread(clientModelWriterThread);
            writerTread.start();

            //TODO: kann es sein, dass Client sehr lange hier in der While-Schleife wartet ohne dass ConnectException passiert? -> THOMAS
            //TODO kein Busy-Waiting, change to notify()
            //waiting for server response - waitingForHelloClient is changed wenn die clientThread bekommt
            //ein JSONMessage mit dem type HelloClient
            while (waitingForServer) {
                logger.info("Waiting for the server answer...");
                Thread.sleep(1000);
            }
            sendMessage(new JSONMessage("HelloServer", new HelloServerBody(group, false, protocolVersion)));
            return true;
        } catch (ConnectException connectException){
        } catch (IOException | InterruptedException exp) {
            exp.printStackTrace();
        }
        logger.info("Something went wrong..");
        return false;
    }


    public void sendMessage(JSONMessage message) {
        this.clientModelWriterThread.sendMessage(message);
    }

    //TODO: sendMsg(String message)

    public void sendUsernameAndRobot(String username, int figure) {
        JSONMessage jsonMessage = new JSONMessage("PlayerValues", new PlayerValuesBody(username, figure));
        sendMessage(jsonMessage);
    }
    //TODO:checken ob es hier ok zu implementieren oder lieber die methoden aus ClientModelWriterThread.java zu nehemen
    public void sendMsg(String message){
        System.out.println("Debug");
        if (message.charAt(0) == '@') {
            if (message.contains(" ")) {
                int Begin_msg = message.indexOf(" ");
                int playerPrivate = Integer.parseInt(message.substring(1, Begin_msg));
                int End_msg = message.length();
                sendPrivateMsg(message.substring(Begin_msg + 1, End_msg), playerPrivate);
            }
        } else {
            //TODO add bindings
            System.out.println("this is a public Msg");
            clientModelWriterThread.broadcastMessage(message);
        }

    }

    public void sendPrivateMsg(String message, int PlayerId){
        clientModelWriterThread.sendDirectMessage(message,PlayerId);
    }


    public void receiveMessage(String message) {
        chatHistory.setValue(chatHistory.getValue() + message + "\n" );
    }


    public void refreshPlayerStatus(int playerID, boolean newPlayerStatus){
        playersStatusMap.replace(playerID, newPlayerStatus);
        for (Map.Entry<Integer, Boolean> p: playersStatusMap.entrySet()){
            String isReady = p.getValue()? "ready" : "not ready";
            System.out.println("Player " + p.getKey() + " is " + isReady);
        }
    }

    public String getChatHistory () {
        return chatHistory.get();
    }

    public StringProperty chatHistoryProperty () {
        return chatHistory;
    }


    /**
     * Sets new message.
     *
     * @param newMessage the new message
     */
    /*Setter für Nachrichten*/
    public void setNewMessage(String newMessage) {
        this.newMessage = newMessage;
    }



    /**
     * Gets new message.
     *
     * @return the new message
     */
    /*Getter für Nachrichten*/
    public String getNewMessage() {
        return newMessage;
    }

    public Player getPlayer() {
        return player;
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

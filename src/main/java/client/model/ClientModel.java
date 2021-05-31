package client.model;


import game.Player;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import json.JSONMessage;
import json.MessageHandler;
import json.protocol.HelloServerBody;
import json.protocol.PlayerValuesBody;
import json.protocol.SetStatusBody;


import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * @author Mohamad, Viktoria
 * ClientModel realisiert Singelton-Pattern, damit alle ViewModels referenzen auf das gleiche Object von ClientModel Klasse haben
 */
public class ClientModel {
    private static ClientModel instance;

    private Socket socket;
    private ClientModelReaderThread clientModelReaderThread;
    private ClientModelWriterThread clientModelWriterThread;
    private boolean waitingForServer = true;

    private static final Logger logger = Logger.getLogger(ClientModel.class.getName());
    private final String protocolVersion = "Version 0.1";
    private final String group = "BlindeBonbons";
    private final MessageHandler messageHandler = new MessageHandler();

    private HashMap<Integer, Boolean> playersStatusMap = new HashMap<Integer, Boolean>();
    private HashMap<Integer, String> playersNamesMap = new HashMap<Integer, String>();
    private HashMap<Integer, Integer> playersFigureMap = new HashMap<Integer, Integer>();

    private Player player;
    private String newMessage;
    private StringProperty chatHistory = new SimpleStringProperty("");
    private StringProperty playersStatusMapProperty = new SimpleStringProperty("");
    private StringProperty error = new SimpleStringProperty("");

    private BooleanProperty doChooseMap = new SimpleBooleanProperty(false);
    private String selectedMap;
    private ArrayList<String> availableMaps = new ArrayList<>();

    private ClientModel () {
    }

    public static ClientModel getInstance () {
        if (instance == null) {
            instance = new ClientModel();
        }
        return instance;
    }

    /**
     * This method is responsible for connecting the client to the specified server.
     *
     * @return true if connection could be established.
     */
    public boolean connectClient(String server_ip, int server_port) {
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
        } catch (ConnectException connectException) {
        } catch (IOException | InterruptedException exp) {
            exp.printStackTrace();
        }
        logger.info("Something went wrong..");
        return false;
    }


    public void setNewStatus(Boolean newStatus) {
        player.setReady(newStatus);
        JSONMessage statusMessage = new JSONMessage("SetStatus", new SetStatusBody(newStatus));
        sendMessage(statusMessage);
    }


    public void sendMessage(JSONMessage message) {
        this.clientModelWriterThread.sendMessage(message);
    }


    public void sendUsernameAndRobot(String username, int figure) {
        JSONMessage jsonMessage = new JSONMessage("PlayerValues", new PlayerValuesBody(username, figure));
        sendMessage(jsonMessage);
    }

    public int getIDbyUsername(String username) {
        for (Map.Entry<Integer, String> entry : playersNamesMap.entrySet()) {
            if (entry.getValue().equals(username)) {
                return entry.getKey();
            }
        }
        return 0;
    }

    public void sendMsg(String message) {
        if (!message.isBlank()) {
            //schauen ob das eine private Nachricht ist
            if (message.charAt(0) == '@') {
                if (message.contains(" ")) {
                    int beginMsg = message.indexOf(" ");
                    String playerPrivate = message.substring(1, beginMsg);
                    if (getIDbyUsername(playerPrivate) != player.getPlayerID()) {
                        if (getIDbyUsername(playerPrivate) != 0) {
                            clientModelWriterThread.sendDirectMessage(player.getName() + " : " + message, getIDbyUsername(playerPrivate));
                            chatHistory.setValue(chatHistory.getValue() + player.getName() + " : " + message + "\n");
                        } else {
                            this.chatHistory.setValue(chatHistory.getValue() + "No Player with name " + playerPrivate + " found." + "\n");
                        }
                    } else {
                        this.chatHistory.setValue(chatHistory.getValue() + "You can't send yourself a private message!" + "\n");
                    }
                } else {
                    this.chatHistory.setValue(chatHistory.getValue() + "No Player with name " + message.substring(1) + " found." + "\n");
                }
            } else {
                //öffentliche nachricht.
                clientModelWriterThread.sendChatMessage(player.getName() + " : " + message);
                chatHistory.setValue(chatHistory.getValue() + player.getName() + " : " + message + "\n");
            }
        }
    }

    public void receiveMessage(String message) {
        chatHistory.setValue(chatHistory.getValue() + message + "\n");
    }

    public void refreshPlayerStatus(int playerID, boolean newPlayerStatus) {
        playersStatusMap.replace(playerID, newPlayerStatus);
        playersStatusMapProperty.setValue("");
        for (Map.Entry<Integer, Boolean> p : playersStatusMap.entrySet()) {
            String isReady = p.getValue() ? "ready" : "not ready";
            playersStatusMapProperty.setValue(playersStatusMapProperty.getValue() + "Player " + playersNamesMap.get(p.getKey()) + " is " + isReady + "  |   Robot " + playersFigureMap.get(p.getKey()) + "\n");
        }
    }

    public String getChatHistory() {
        return chatHistory.get();
    }

    public StringProperty chatHistoryProperty() {
        return chatHistory;
    }

    public StringProperty playersStatusMapProperty() {
        return playersStatusMapProperty;
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

    public HashMap<Integer, String> getPlayersNamesMap() {
        return playersNamesMap;
    }

    public HashMap<Integer, Integer> getPlayersFigureMap() {
        return playersFigureMap;
    }

    public HashMap<Integer, Boolean> getPlayersStatusMap() {
        return playersStatusMap;
    }

    public void sendError (String s) {
        error.setValue(s);

    }

    public StringProperty errorProperty () {
        return error;
    }

    public String getSelectedMap () {
        return selectedMap;
    }

    public void setSelectedMap (String selectedMap) {
        this.selectedMap = selectedMap;
    }

    public ArrayList<String> getAvailableMaps () {
        return availableMaps;
    }

    public boolean isDoChooseMap () {
        return doChooseMap.get();
    }

    public BooleanProperty doChooseMapProperty () {
        return doChooseMap;
    }

    public void setDoChooseMap (boolean doChooseMap) {
        this.doChooseMap.set(doChooseMap);
    }

    public void setAvailableMaps (ArrayList<String> availableMaps) {
        this.availableMaps = availableMaps;
    }
}

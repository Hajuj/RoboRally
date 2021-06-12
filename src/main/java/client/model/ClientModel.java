package client.model;

import game.Game;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import json.JSONMessage;
import json.protocol.HelloServerBody;
import json.protocol.PlayerValuesBody;
import json.protocol.SetStatusBody;
import org.apache.log4j.Logger;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Mohamad, Viktoria
 * ClientModel realisiert Singelton-Pattern,
 * damit alle ViewModels referenzen auf das gleiche Object von ClientModel Klasse haben
 */

public class ClientModel {
    private static ClientModel instance;
    private static ClientGameModel clientGameModel = ClientGameModel.getInstance();

    private Socket socket;
    private ClientModelReaderThread clientModelReaderThread;
    private ClientModelWriterThread clientModelWriterThread;
    private boolean waitingForServer = true;

    private static Logger logger = Logger.getLogger(ClientModel.class.getName());
    private String protocolVersion = "Version 1.0";
    private String group = "BlindeBonbons";
    private boolean isAI = false;
    private MessageHandler messageHandler = new MessageHandler();

    private StringProperty playersStatusMapProperty = new SimpleStringProperty("");
    private HashMap<Integer, Boolean> playersStatusMap = new HashMap<Integer, Boolean>();
    private HashMap<Integer, String> playersNamesMap = new HashMap<Integer, String>();
    private HashMap<Integer, Integer> playersFigureMap = new HashMap<Integer, Integer>();

    private boolean canPlay = true;

    private StringProperty chatHistory = new SimpleStringProperty("");

    private BooleanProperty doChooseMap = new SimpleBooleanProperty(false);
    private String selectedMap;
    private ArrayList<String> availableMaps = new ArrayList<>();

    private BooleanProperty gameOn = new SimpleBooleanProperty(false);


    private BooleanProperty move = new SimpleBooleanProperty(false);

    private ClientModel () {
    }

    public static ClientModel getInstance () {
        if (instance == null) {
            instance = new ClientModel();
        }
        return instance;
    }


    public int getIDfromRobotName (String name) {
        for (Map.Entry<Integer, Integer> entry : getPlayersFigureMap().entrySet()) {
            if (Game.getRobotNames().get(entry.getValue()).equals(name)) {
                return entry.getKey();
            }
        }
        return -1;
    }

    /**
     * This method is responsible for connecting the client to the specified server.
     *
     * @return true if connection could be established.
     */
    public boolean connectClient (String server_ip, int server_port) {
        try {
            //Create socket to connect to server at serverIP:serverPort
            logger.info("Trying to connect to the server on the port " + server_ip + " : " + server_port);
            socket = new Socket(server_ip, server_port);

            //Start new Threads for reading/writing messages from/to the server
            clientModelReaderThread = new ClientModelReaderThread(this, socket);
            clientModelWriterThread = new ClientModelWriterThread(this, socket);

            Thread readerThread = new Thread(clientModelReaderThread);
            readerThread.start();

            Thread writerTread = new Thread(clientModelWriterThread);
            writerTread.start();

            //waiting for server response
            synchronized (this) {
                while (waitingForServer) {
                    logger.info("Waiting for the server answer...");
                    this.wait();
                }
            }

            sendMessage(new JSONMessage("HelloServer", new HelloServerBody(group, isAI, protocolVersion)));
            return true;

        } catch (ConnectException connectException) {
            return false;
        } catch (IllegalArgumentException connectException) {
            return false;
        } catch (IOException connectException) {
            return false;
        } catch (InterruptedException connectException) {
            return false;
        }
    }


    public void setMessageHandler (MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    public boolean isAI () {
        return isAI;
    }

    public void setAI (boolean AI) {
        isAI = AI;
    }

    public void setNewStatus (Boolean newStatus) {
        clientGameModel.getPlayer().setReady(newStatus);
        JSONMessage statusMessage = new JSONMessage("SetStatus", new SetStatusBody(newStatus));
        sendMessage(statusMessage);
    }

    public void addPlayer (int clientID, String name, int figure) {
        getPlayersNamesMap().put(clientID, name);
        getPlayersFigureMap().put(clientID, figure);
        getPlayersStatusMap().put(clientID, false);
    }


    public void sendMessage (JSONMessage message) {
        this.clientModelWriterThread.sendMessage(message);
    }


    public void sendUsernameAndRobot (String username, int figure) {
        JSONMessage jsonMessage = new JSONMessage("PlayerValues", new PlayerValuesBody(username, figure));
        sendMessage(jsonMessage);
    }

    public int getIDbyUsername (String username) {
        for (Map.Entry<Integer, String> entry : playersNamesMap.entrySet()) {
            if (entry.getValue().equals(username)) {
                return entry.getKey();
            }
        }
        return 0;
    }

    public void sendMsg (String message) {
        if (!message.isBlank()) {
            //schauen ob das eine private Nachricht ist
            if (message.charAt(0) == '@') {
                if (message.contains(" ")) {
                    int beginMsg = message.indexOf(" ");
                    String playerPrivate = message.substring(1, beginMsg);
                    if (getIDbyUsername(playerPrivate) != clientGameModel.getPlayer().getPlayerID()) {
                        if (getIDbyUsername(playerPrivate) != 0) {
                            clientModelWriterThread.sendDirectMessage(clientGameModel.getPlayer().getName() + " : " + message, getIDbyUsername(playerPrivate));
                            chatHistory.setValue(chatHistory.getValue() + clientGameModel.getPlayer().getName() + " : " + message + "\n");
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
                clientModelWriterThread.sendChatMessage(clientGameModel.getPlayer().getName() + " : " + message);
                chatHistory.setValue(chatHistory.getValue() + clientGameModel.getPlayer().getName() + " : " + message + "\n");
            }
        }
    }

    public void receiveMessage (String message) {
        chatHistory.setValue(chatHistory.getValue() + message + "\n");
    }

    public void refreshPlayerStatus (int playerID, boolean newPlayerStatus) {
        playersStatusMap.replace(playerID, newPlayerStatus);
        playersStatusMapProperty.setValue("");
        for (Map.Entry<Integer, Boolean> p : playersStatusMap.entrySet()) {
            String robotName = "**chat only**";
            String isReady = "               ";
            if (playersFigureMap.get(p.getKey()) != -1) {
                robotName = "Robot " + Game.getRobotNames().get(playersFigureMap.get(p.getKey()));
                isReady = p.getValue() ? " is ready" : " is not ready";
            }
            playersStatusMapProperty.setValue(playersStatusMapProperty.getValue() + "Player " + playersNamesMap.get(p.getKey()) + isReady + "  |   " + robotName + "\n");
        }
    }

    public void removePlayer (int playerID) {
        playersStatusMap.remove(playerID);
        playersFigureMap.remove(playerID);
        refreshPlayerStatus(playerID, false);
        chatHistoryProperty().setValue(chatHistoryProperty().getValue() + "Player " + playersNamesMap.get(playerID) + " is disconnected. \n");
        playersNamesMap.remove(playerID);
    }

    public boolean isGameOn () {
        return gameOn.get();
    }

    public BooleanProperty gameOnProperty () {
        return gameOn;
    }

    public void setGameOn (boolean gameOn) {
        this.gameOn.set(gameOn);
    }

    public String getChatHistory () {
        return chatHistory.get();
    }

    public StringProperty chatHistoryProperty () {
        return chatHistory;
    }

    public StringProperty playersStatusMapProperty () {
        return playersStatusMapProperty;
    }

    public boolean isCanPlay () {
        return canPlay;
    }

    public void setCanPlay (boolean canPlay) {
        this.canPlay = canPlay;
    }

    public MessageHandler getMessageHandler () {
        return messageHandler;
    }

    public void setWaitingForServer (boolean waitingForServer) {
        this.waitingForServer = waitingForServer;
    }

    public HashMap<Integer, String> getPlayersNamesMap () {
        return playersNamesMap;
    }

    public HashMap<Integer, Integer> getPlayersFigureMap () {
        return playersFigureMap;
    }

    public HashMap<Integer, Boolean> getPlayersStatusMap () {
        return playersStatusMap;
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

    public ClientGameModel getClientGameModel () {
        return clientGameModel;
    }

    public BooleanProperty moveProperty() {
        return move;
    }

    public void setMove(boolean move) {
        this.move.set(move);
    }



    /*public BooleanProperty getProgrammingPhaseProperty () {
        return programmingPhaseProperty;
    }*/

}

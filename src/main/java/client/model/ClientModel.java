package client.model;

import game.Game;

import json.JSONMessage;
import json.protocol.HelloServerBody;
import json.protocol.PlayerValuesBody;
import json.protocol.SetStatusBody;

import org.apache.log4j.Logger;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.net.ConnectException;
import java.util.ArrayList;
import java.io.IOException;
import java.util.HashMap;
import java.net.Socket;
import java.util.Map;

/**
 * @author Mohamad, Viktoria
 */

public class ClientModel {
    private static ClientModel instance;
    private static ClientGameModel clientGameModel = ClientGameModel.getInstance();

    private static Logger logger = Logger.getLogger(ClientModel.class.getName());
    protected PropertyChangeSupport propertyChangeSupport;

    private Socket socket;
    private ClientModelReaderThread clientModelReaderThread;
    private ClientModelWriterThread clientModelWriterThread;
    private boolean waitingForServer = true;


    private String protocolVersion = "Version 2.1";
    private String group = "BlindeBonbons";
    private boolean isAI = false;

    private MessageHandler messageHandler = new MessageHandler();

    private HashMap<Integer, Boolean> playersStatusMap = new HashMap<Integer, Boolean>();
    private HashMap<Integer, String> playersNamesMap = new HashMap<Integer, String>();
    private HashMap<Integer, Integer> playersFigureMap = new HashMap<Integer, Integer>();

    private boolean doRefreshPlayerDisplay = false;

    private String playersStatus = "";
    private String chatHistory = "";

    private boolean canPlay = true;
    private boolean doChooseMap = false;
    private String selectedMap;

    private ArrayList<String> availableMaps = new ArrayList<>();

    private boolean gameOn = false;
    private boolean gameFinished = false;


    private ClientModel() {
        propertyChangeSupport = new PropertyChangeSupport(this);
    }


    /**
     * ClientModel realisiert Singelton-Pattern,
     * damit alle ViewModels referenzen auf das gleiche Object von ClientModel Klasse haben
     *
     * @return instace of ClientModel Klasse
     */
    public static ClientModel getInstance() {
        if (instance == null) {
            instance = new ClientModel();
        }
        return instance;
    }


    /**
     * This method is responsible for connecting the client to the specified server.
     *
     * @param server_ip
     * @param server_port
     * @return true if connection could be established.
     */
    public boolean connectClient(String server_ip, int server_port) {
        try {
            //Create socket to connect to server at serverIP:serverPort
            logger.info("Trying to connect to the server on the port " + server_ip + " : " + server_port);
            socket = new Socket(server_ip, server_port);

            //Start new Threads for reading/writing messages from/to the server
            clientModelReaderThread = new ClientModelReaderThread(this, socket);
            clientModelWriterThread = new ClientModelWriterThread(this, socket);
            //chatHistory = new SimpleStringProperty("");

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

            //falls Client von dem Server mit einem HalloClient-Message notified wird, schickt er ein HalloServer-Message an den Server
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

    /**
     * Falls Client ein PlayerAdded Message kriegt, tut er den neuen Player in alle Spieler-HashMaps
     *
     * @param clientID - ID des neuen Players
     * @param name     - Name des neuen PLayers
     * @param figure   - Robot, den der neue Player ausgewählt hat.
     */
    public void addPlayer(int clientID, String name, int figure) {
        getPlayersNamesMap().put(clientID, name);
        getPlayersFigureMap().put(clientID, figure);
        getPlayersStatusMap().put(clientID, false);
    }

    /**
     * Schick message in JSON-Format mit dem WriterThread vob clientSocket an den Server.
     *
     * @param message
     */
    public void sendMessage(JSONMessage message) {
        this.clientModelWriterThread.sendMessage(message);
    }


    /**
     * Falls der Spieler erfolgreich ein Nickname und Figur ausgewählt hat, wird ein PlayerValues-Message geschickt.
     *
     * @param username
     * @param figure
     */
    public void sendUsernameAndRobot(String username, int figure) {
        JSONMessage jsonMessage = new JSONMessage("PlayerValues", new PlayerValuesBody(username, figure));
        sendMessage(jsonMessage);
    }


    /**
     * Eine Hilfsmethode, um ID von dem Player mit dem Nickname username zu kriegen.
     * Die wird beim "Private-Nachrichten schicken" benutzt.
     *
     * @param username
     * @return ID der Spielers, falls er in dem HashMap gefunden wurde, oder 0 falls nicht.
     */
    public int getIDbyUsername(String username) {
        for (Map.Entry<Integer, String> entry : playersNamesMap.entrySet()) {
            if (entry.getValue().equals(username)) {
                return entry.getKey();
            }
        }
        return 0;
    }


    /**
     * Methode, um offentliche / private Nachrichten in Chat zu schicken.
     *
     * @param message - Nachricht zu schicken
     */
    public void sendMsg(String message) {
        if (!message.isBlank()) {
            //schauen ob das eine private Nachricht ist
            if (message.charAt(0) == '@') {
                if (message.contains(" ")) {
                    int beginMsg = message.indexOf(" ");
                    String playerPrivate = message.substring(1, beginMsg);
                    if (getIDbyUsername(playerPrivate) != clientGameModel.getPlayer().getPlayerID()) {
                        if (getIDbyUsername(playerPrivate) != 0) {
                            clientModelWriterThread.sendDirectMessage(clientGameModel.getPlayer().getName() + " : " + message, getIDbyUsername(playerPrivate));
                            setChatHistory(chatHistory + clientGameModel.getPlayer().getName() + " : " + message + "\n");
                            setChatHistory(chatHistory + clientGameModel.getPlayer().getName() + " : " + message + "\n");
                        } else {
                            setChatHistory(chatHistory + "No Player with name " + playerPrivate + " found." + "\n");
                        }
                    } else {
                        setChatHistory(chatHistory + "You can't send yourself a private message!" + "\n");
                    }
                } else {
                    setChatHistory(chatHistory + "No Player with name " + message.substring(1) + " found." + "\n");
                }
            } else {
                //öffentliche nachricht.
                clientModelWriterThread.sendChatMessage(clientGameModel.getPlayer().getName() + " : " + message);
                setChatHistory(chatHistory + clientGameModel.getPlayer().getName() + " : " + message + "\n");
            }
        }
    }

    /**
     * Update den Text, der in chatHistory TextArea in dem ViewModell angezeigt wird.
     *
     * @param message - Neue Nachricht in Chat
     */
    public void receiveMessage(String message) {
        setChatHistory(chatHistory + message + "\n");
    }

    /**
     * Update den Text, der in playerStatusDisplay TextArea in dem ViewModell angezeiget wird. Dass passiert jedes mal,
     * wenn der Client ein neues PlayerStatus-Message von dem Server kriegt.
     *
     * @param playerID
     * @param newPlayerStatus
     */
    public void refreshPlayerStatus(int playerID, boolean newPlayerStatus) {
        playersStatusMap.replace(playerID, newPlayerStatus);
        setPlayersStatus("");
        for (Map.Entry<Integer, Boolean> p : playersStatusMap.entrySet()) {
            String robotName = "**chat only**";
            String isReady = "               ";
            if (playersFigureMap.get(p.getKey()) != -1) {
                robotName = "Robot " + Game.getRobotNames().get(playersFigureMap.get(p.getKey()));
                isReady = p.getValue() ? " is ready" : " is not ready";
            }
            setPlayersStatus(playersStatus + "Player " + playersNamesMap.get(p.getKey()) + isReady + "  |   " + robotName + "\n");
        }
        setDoRefreshPlayerDisplay(true);
    }

    /**
     * Sucht nach dem ID des Spielers, der einen Roboter mit dem Namen name hat
     *
     * @param name
     * @return ID des Spielers, falls er gefunden wurde und -1, falls nicht.
     */
    public int getIDfromRobotName(String name) {
        for (Map.Entry<Integer, Integer> entry : getPlayersFigureMap().entrySet()) {
            if (Game.getRobotNames().get(entry.getValue()).equals(name)) {
                return entry.getKey();
            }
        }
        return -1;
    }

    /**
     * Wenn ein Player von dem Server weggeht oder es ein Verbindungsverlust passiert, werden alle anderen Spieler mit
     * einem ConnectionUpdate-Message darüber informiert. Dann entfernen sie diesen User aus allen HashMaps.
     *
     * @param playerID
     */
    public void removePlayer(int playerID) {
        playersStatusMap.remove(playerID);
        playersFigureMap.remove(playerID);
        refreshPlayerStatus(playerID, false);
        setChatHistory(chatHistory + "Player " + playersNamesMap.get(playerID) + " is disconnected. \n");
        playersNamesMap.remove(playerID);
    }

    /**
     * PropertyChangeListener, damit alle ViewModels, die Changes in dem ClientModel observieren, über diese changes notified werden.
     *
     * @param listener
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public boolean isGameOn() {
        return gameOn;
    }

    public void setGameOn(boolean gameOn) {
        boolean oldGameOn = this.gameOn;
        this.gameOn = gameOn;
        if (this.gameOn) {
            propertyChangeSupport.firePropertyChange("gameOn", oldGameOn, true);
        }
    }

    public void setGameFinished(boolean gameFinished) {
        boolean oldGameFinished = this.gameFinished;
        this.gameFinished = gameFinished;
        if (this.gameFinished) {
            propertyChangeSupport.firePropertyChange("gameFinished", oldGameFinished, true);
        }
    }

    /* public void setChatHistory(String chat) {
       String oldChatHistory = chatHistory.get();
       String newChatHistroy= oldChatHistory+ "\n" + chat;
        chatHistory.setValue(newChatHistroy);

    }*/

    /* public StringProperty getChatHistory() {
         return chatHistory;
     }*/
    public void setChatHistory(String chatHistory) {
        String oldChatHistory = this.chatHistory;
        this.chatHistory = chatHistory;
        propertyChangeSupport.firePropertyChange("chatHistory", oldChatHistory, chatHistory);
    }

    public String getChatHistory() {
        return chatHistory;
    }

    public void setPlayersStatus(String playersStatus) {
        String oldPlayerStatus = this.playersStatus;
        this.playersStatus = playersStatus;
        propertyChangeSupport.firePropertyChange("playerStatus", oldPlayerStatus, playersStatus);
    }

    public String getPlayersStatus() {
        return playersStatus;
    }

    public boolean isCanPlay() {
        return canPlay;
    }

    public void setCanPlay(boolean canPlay) {
        this.canPlay = canPlay;
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

    public void setPlayersStatusMap(HashMap<Integer, Boolean> playersStatusMap) {
        this.playersStatusMap = playersStatusMap;
    }

    public String getSelectedMap() {
        return selectedMap;
    }

    public void setSelectedMap(String selectedMap) {
        this.selectedMap = selectedMap;
    }

    public ArrayList<String> getAvailableMaps() {
        return availableMaps;
    }

    public boolean doChooseMap() {
        return doChooseMap;
    }

    public void setDoChooseMap(boolean doChooseMap) {
        boolean oldDoChooseMap = this.doChooseMap;
        this.doChooseMap = doChooseMap;
        if (this.doChooseMap) {
            propertyChangeSupport.firePropertyChange("doChooseMap", oldDoChooseMap, true);
        }
    }

    public void setDoRefreshPlayerDisplay(boolean doRefreshPlayerDisplay) {
        boolean oldsDoRefreshPlayerDisplay = this.doRefreshPlayerDisplay;
        this.doRefreshPlayerDisplay = doRefreshPlayerDisplay;
        if (this.doRefreshPlayerDisplay) {
            propertyChangeSupport.firePropertyChange("doRefreshPlayerDisplay", oldsDoRefreshPlayerDisplay, true);
        }
    }



    public void setAvailableMaps(ArrayList<String> availableMaps) {
        this.availableMaps = availableMaps;
    }

    public ClientGameModel getClientGameModel() {
        return clientGameModel;
    }

    public void setMessageHandler(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    public boolean isAI() {
        return isAI;
    }

    public void setAI(boolean AI) {
        isAI = AI;
    }

    public void setNewStatus(boolean newStatus) {
        clientGameModel.getPlayer().setReady(newStatus);
        JSONMessage statusMessage = new JSONMessage("SetStatus", new SetStatusBody(newStatus));
        sendMessage(statusMessage);
    }


}

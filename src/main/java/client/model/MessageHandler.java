package client.model;

import game.Player;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import json.JSONMessage;
import json.protocol.*;
import org.apache.log4j.Logger;
import server.ClientHandler;
import server.Server;

import java.util.ArrayList;

/**
 * @author Mohamad, Viktoria
 */
public class MessageHandler {
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_RESET = "\u001B[0m";
    private static final Logger logger = Logger.getLogger(MessageHandler.class.getName());

    /**
     * Wenn Client ein HalloClient Message von Server bekommt, wird die Variable waitingForServer
     * auf false gesetzt und Client kann dem Server Nachrichten schicken.
     *
     * @param clientmodel     The ClientModel
     * @param helloClientBody The message body of the message which is of type HelloClientBody
     */
    public void handleHelloClient (ClientModel clientmodel, HelloClientBody helloClientBody) {
        logger.info(ANSI_CYAN + "HalloClient Message received." + ANSI_RESET);
        logger.info("Server has protocol " + helloClientBody.getProtocol());
        //TODO change to notify() in class ClientModel
        clientmodel.setWaitingForServer(false);
    }


    /**
     * In der HalloServer method wird ein Welcome-message geschickt, und dann weiss der ClientModel sein id
     *
     * @param clientmodel The ClientModel itself.
     * @param welcomeBody The message body of the message which is of type {@link WelcomeBody}.
     */
    public void handleWelcome (ClientModel clientmodel, WelcomeBody welcomeBody) {
        logger.info(ANSI_CYAN + "Welcome Message received." + ANSI_RESET);
        logger.info("Your PlayerID: " + welcomeBody.getClientID());
        Player player = new Player(welcomeBody.getClientID());
        clientmodel.getClientGameModel().setPlayer(player);
    }


    /**
     * Wenn es ein Error occured bei deserialization, wird ein Message mit dem ErrorBody geschickt
     *
     * @param clientmodel The ClientModel itself.
     * @param errorBody   The message body of the message which is of type {@link ErrorBody}.
     */
    public void handleError (ClientModel clientmodel, ErrorBody errorBody) {
        logger.warn(ANSI_CYAN + "Error has occurred! " + ANSI_RESET);
        logger.info("Error has occurred! " + errorBody.getError());

        switch (errorBody.getError()) {
            case "gameOn":
                clientmodel.setCanPlay(false);
        }


        Platform.runLater(() -> {
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setContentText(errorBody.getError());
            a.show();
        });
        clientmodel.sendError("Error has occurred! " + errorBody.getError());
    }

    public void handleReceivedChat (ClientModel clientModel, ReceivedChatBody receivedChatBody) {
        logger.info(ANSI_CYAN + "Chat received." + ANSI_RESET);
        clientModel.receiveMessage(receivedChatBody.getMessage());
    }

    public void handleGameStarted (ClientModel client, GameStartedBody bodyObject) {
        logger.info(ANSI_CYAN + "Game Started received." + ANSI_RESET);
        client.getClientGameModel().setMap(bodyObject.getGameMap());
        client.gameOnProperty().setValue(true);
        //TODO implement map controller and use in this method to build the map
    }

    //Client receive this message
    public void handleAlive (ClientModel clientModel, AliveBody aliveBody) {
        //wenn client bekommt ein Alive-Message von Server, schickt er ein "Alive"-Antwort zurück
        clientModel.sendMessage(new JSONMessage("Alive", new AliveBody()));
    }

    public void handlePlayerAdded (ClientModel clientModel, PlayerAddedBody playerAddedBody) {
        int clientID = playerAddedBody.getClientID();
        String name = playerAddedBody.getName();
        int figure = playerAddedBody.getFigure();

        // The player himself has been added
        if (clientModel.getClientGameModel().getPlayer().getPlayerID() == clientID) {
            clientModel.getClientGameModel().getPlayer().setName(name);
            clientModel.getClientGameModel().getPlayer().setFigure(figure);
        }
        // save client info in the Hash Maps
        clientModel.getPlayersNamesMap().put(clientID, name);
        clientModel.getPlayersFigureMap().put(clientID, figure);
        clientModel.getPlayersStatusMap().put(clientID, false);
        logger.info("A new player has been added. Name: " + name + ", ID: " + clientID + ", Figure: " + figure);
    }

    public void handlePlayerStatus (ClientModel clientModel, PlayerStatusBody playerStatusBody) {
        clientModel.refreshPlayerStatus(playerStatusBody.getClientID(), playerStatusBody.isReady());
    }

    //diese Methode wird getriggert wenn Client eine SelectMap Message bekommt.
    public void handleSelectMap (ClientModel clientModel, SelectMapBody selectMapBody) {
        for (String map : selectMapBody.getAvailableMaps()) {
            clientModel.getAvailableMaps().add(map);
        }
        clientModel.setDoChooseMap(true);
    }

    public void handleMapSelected (ClientModel clientModel, MapSelectedBody mapSelectedBody) {
        clientModel.setSelectedMap(mapSelectedBody.getMap());
        System.out.println(mapSelectedBody.getMap().getClass());
        //clientModel.gameOnProperty().setValue(true);
    }

    public void handleConnectionUpdate (ClientModel clientmodel, ConnectionUpdateBody connectionUpdateBody) {
        int playerID = connectionUpdateBody.getPlayerID();
        boolean isConnected = connectionUpdateBody.isConnected();
        String action = connectionUpdateBody.getAction();

        if (action.equals("remove") && !isConnected) {
            clientmodel.removePlayer(playerID);
        }
    }

    public void handleStartingPointTaken (ClientModel clientModel, StartingPointTakenBody startingPointTakenBody) {
        clientModel.getClientGameModel().setX(startingPointTakenBody.getX());
        clientModel.getClientGameModel().setY(startingPointTakenBody.getY());
        clientModel.getClientGameModel().canSetStartingPointProperty().setValue(true);
    }

    public void handleCurrentPlayer (ClientModel clientModel, CurrentPlayerBody currentPlayerBody) {
        int playerID = currentPlayerBody.getClientID();
        clientModel.getClientGameModel().setActualPlayerID(playerID);
        logger.info("Current Player: " + playerID);
    }

    public void handleActivePhase (ClientModel clientModel, ActivePhaseBody activePhaseBody) {
        int phase = activePhaseBody.getPhase();
        clientModel.getClientGameModel().setActualPhase(phase);
        logger.info("Current Active Phase: " + phase);
    }

    public void handleCardSelected(ClientModel clientModel, CardSelectedBody cardSelectedBody) {
        int clientID = cardSelectedBody.getClientID();
        int register = cardSelectedBody.getRegister();

        if (cardSelectedBody.isFilled()) {
            clientModel.receiveMessage("Player with ID: " + clientID + " added a card to register number: " + register);
        } else {
            clientModel.receiveMessage("Player with ID: " + clientID + " removed a card from register number: " + register);
        }
    }

    public void handleYourCards (ClientModel clientModel, YourCardsBody yourCardsBody) {
        //speichere die Cards und refresh the View
        clientModel.getClientGameModel().getCardsInHandObservable().addAll(yourCardsBody.getCardsInHand());
    }

    public void handleNotYourCards (ClientModel clientModel, NotYourCardsBody notYourCardsBody) {
        int clientID = notYourCardsBody.getClientID();
        int amount = notYourCardsBody.getCardsInHand();
        String playerName = clientModel.getPlayersNamesMap().get(clientID);


        //TODO: benachrichtige den Client (schön in View, wie viele Karten derjenige Spieler hat)

        clientModel.receiveMessage("Player " + playerName + " has " + amount + " cards in the hand!");
    }

    public void handleShuffleCoding(ClientModel clientModel, ShuffleCodingBody shuffleCodingBody) {
        int clientID = shuffleCodingBody.getClientID();

        clientModel.receiveMessage("Player with ID: " + clientID + " shuffled the card!");
    }

    public void handleSelectionFinished (ClientModel clientModel, SelectionFinishedBody selectionFinishedBody) {
        int clientID = selectionFinishedBody.getClientID();
        if (clientID != clientModel.getClientGameModel().getPlayer().getPlayerID()) {
                clientModel.receiveMessage("Player with ID: " + clientID + " finished selecting cards!");
        }
    }

    public void handleTimerStarted(ClientModel clientModel, TimerStartedBody timerStartedBody) {
        Platform.runLater(() -> {
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setContentText("Selection Finished! You have 30 Seconds! Hurry up!");
            a.show();
        });
    }

    public void handleTimerEnded(ClientModel clientModel, TimerEndedBody timerEndedBody) {
        ArrayList<Integer> lateClient = timerEndedBody.getClientIDs();

        clientModel.receiveMessage("Late client IDs are: " + lateClient);
    }

    public void handleCardsYouGotNowBody (ClientModel clientModel, CardsYouGotNowBody cardsYouGotNowBody) {
        ArrayList<String> cards = cardsYouGotNowBody.getCards();
        //TODO: put the cards in leere Felder in Register
        for (String card : cards) {
            clientModel.receiveMessage("Your new Card is " + card);
        }
    }

    public void handleCurrentCards(ClientModel clientModel, CurrentCardsBody currentCardsBody) {

    }

    public void handleReplaceCard(ClientModel clientModel, ReplaceCardBody replaceCardBody) {

    }

    public void handleCardPlayed (ClientModel clientModel, CardPlayedBody cardPlayedBody){
        int clientID = cardPlayedBody.getClientID();
        String card = cardPlayedBody.getCard();

    }


}

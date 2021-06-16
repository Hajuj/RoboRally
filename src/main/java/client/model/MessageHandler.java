package client.model;

import game.Game;
import game.Player;
import game.Robot;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.control.Alert;
import json.JSONMessage;
import json.protocol.*;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


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

        synchronized (clientmodel) {
            clientmodel.setWaitingForServer(false);
            clientmodel.notifyAll();
        }
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

        if ("gameOn".equals(errorBody.getError())) {
            clientmodel.setCanPlay(false);
        }


        Platform.runLater(() -> {
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setContentText(errorBody.getError());
            a.show();
        });
    }

    public void handleReceivedChat (ClientModel clientModel, ReceivedChatBody receivedChatBody) {
        logger.info(ANSI_CYAN + "Chat received." + ANSI_RESET);
        clientModel.receiveMessage(receivedChatBody.getMessage());
    }

    public void handleGameStarted (ClientModel client, GameStartedBody bodyObject) {
        logger.info(ANSI_CYAN + "Game Started received." + ANSI_RESET);
        client.getClientGameModel().setMap(bodyObject.getGameMap());
        int mapX = bodyObject.getGameMap().size();
        int mapY = bodyObject.getGameMap().get(0).size();
        client.getClientGameModel().createMapObjects(bodyObject.getGameMap(), mapX, mapY);
        client.setGameOn(true);
        //TODO implement map controller and use in this method to build the map
    }

    //Client receive this message
    public void handleAlive (ClientModel clientModel, AliveBody aliveBody) {
        //wenn client bekommt ein Alive-Message von Server, schickt er ein "Alive"-Antwort zurück
        clientModel.sendMessage(new JSONMessage("Alive", new AliveBody()));
    }

    public void handlePlayerAdded (ClientModel clientModel, PlayerAddedBody playerAddedBody) {
        logger.info(ANSI_CYAN + "PlayerAdded Message received." + ANSI_RESET);
        int clientID = playerAddedBody.getClientID();
        String name = playerAddedBody.getName();
        int figure = playerAddedBody.getFigure();

        // The player himself has been added
        if (clientModel.getClientGameModel().getPlayer().getPlayerID() == clientID) {
            clientModel.getClientGameModel().getPlayer().setName(name);
            clientModel.getClientGameModel().getPlayer().setFigure(figure);
        }
        // save client info in the Hash Maps
        clientModel.addPlayer(clientID, name, figure);
        logger.info("A new player has been added. Name: " + name + ", ID: " + clientID + ", Figure: " + figure);
    }

    public void handlePlayerStatus (ClientModel clientModel, PlayerStatusBody playerStatusBody) {
        logger.info(ANSI_CYAN + "PlayerStatus Message received." + ANSI_RESET);
        clientModel.refreshPlayerStatus(playerStatusBody.getClientID(), playerStatusBody.isReady());
    }

    //diese Methode wird getriggert wenn Client eine SelectMap Message bekommt.
    public void handleSelectMap (ClientModel clientModel, SelectMapBody selectMapBody) {
        logger.info(ANSI_CYAN + "SelectMap Message received." + ANSI_RESET);
        for (String map : selectMapBody.getAvailableMaps()) {
            clientModel.getAvailableMaps().add(map);
        }
        clientModel.setDoChooseMap(true);
    }

    public void handleMapSelected (ClientModel clientModel, MapSelectedBody mapSelectedBody) {
        logger.info(ANSI_CYAN + "MapSelected Message received." + ANSI_RESET);
        clientModel.setSelectedMap(mapSelectedBody.getMap());
    }

    public void handleConnectionUpdate (ClientModel clientmodel, ConnectionUpdateBody connectionUpdateBody) {
        logger.info(ANSI_CYAN + "ConnectionUpdate Message received." + ANSI_RESET);
        int playerID = connectionUpdateBody.getPlayerID();
        boolean isConnected = connectionUpdateBody.isConnected();
        String action = connectionUpdateBody.getAction();

        if (action.equals("remove") && !isConnected) {
            clientmodel.removePlayer(playerID);
        }
    }

    public void handleStartingPointTaken (ClientModel clientModel, StartingPointTakenBody startingPointTakenBody) {
        logger.info(ANSI_CYAN + "StartingPointTaken Message received." + ANSI_RESET);
        int playerID = startingPointTakenBody.getClientID();

        String robotName = Game.getRobotNames().get(clientModel.getPlayersFigureMap().get(playerID));
        Robot robot = new Robot(robotName, startingPointTakenBody.getX(), startingPointTakenBody.getY());

        Point2D position = new Point2D(startingPointTakenBody.getX(), startingPointTakenBody.getY());

        clientModel.getClientGameModel().getStartingPointQueue().put(robot, position);
        clientModel.getClientGameModel().setStartingPoint(true);

        //BraucheIch das noch
        clientModel.getClientGameModel().setProgrammingPhase(true);

    }

    public void handleCurrentPlayer (ClientModel clientModel, CurrentPlayerBody currentPlayerBody) {
        logger.info(ANSI_CYAN + "CurrentPlayer Message received." + ANSI_RESET);
        int playerID = currentPlayerBody.getClientID();
//        try {
//            Thread.sleep(5000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        clientModel.getClientGameModel().setActualPlayerID(playerID);
        clientModel.getClientGameModel().switchPlayer(true);
        logger.info("Current Player: " + playerID);
    }

    public void handleActivePhase (ClientModel clientModel, ActivePhaseBody activePhaseBody) {
        logger.info(ANSI_CYAN + "ActivePhase Message received." + ANSI_RESET);
        int phase = activePhaseBody.getPhase();
//        try {
//            Thread.sleep(3000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        clientModel.getClientGameModel().setActualPhase(phase);
    }

    public void handleCardSelected(ClientModel clientModel, CardSelectedBody cardSelectedBody) {
        logger.info(ANSI_CYAN + "CardSelected Message received." + ANSI_RESET);
        int clientID = cardSelectedBody.getClientID();
        int register = cardSelectedBody.getRegister();

        if (cardSelectedBody.isFilled()) {
            clientModel.receiveMessage("Player with ID: " + clientID + " added a card to register number: " + (register + 1));
        } else {
            clientModel.receiveMessage("Player with ID: " + clientID + " removed a card from register number: " + (register + 1));
        }
    }

    public void handleYourCards (ClientModel clientModel, YourCardsBody yourCardsBody) {
        logger.info(ANSI_CYAN + "YourCards Message received." + ANSI_RESET);
        //speichere die Cards und refresh the View
        clientModel.getClientGameModel().getCardsInHand().clear();
        clientModel.getClientGameModel().setCardsInHand(yourCardsBody.getCardsInHand());
        clientModel.getClientGameModel().setHandCards(true);
    }

    public void handleNotYourCards (ClientModel clientModel, NotYourCardsBody notYourCardsBody) {
        logger.info(ANSI_CYAN + "NotYourCards Message received." + ANSI_RESET);
        int clientID = notYourCardsBody.getClientID();
        int amount = notYourCardsBody.getCardsInHand();
        String playerName = clientModel.getPlayersNamesMap().get(clientID);


        //TODO: benachrichtige den Client (schön in View, wie viele Karten derjenige Spieler hat)

        clientModel.receiveMessage("Player " + playerName + " has " + amount + " cards in the hand!");
    }

    public void handleShuffleCoding (ClientModel clientModel, ShuffleCodingBody shuffleCodingBody) {
        logger.info(ANSI_CYAN + "ShuffleCoding Message received." + ANSI_RESET);
        int clientID = shuffleCodingBody.getClientID();

        clientModel.receiveMessage("Player with ID: " + clientID + " shuffled the card!");
    }


    public void handleSelectionFinished (ClientModel clientModel, SelectionFinishedBody selectionFinishedBody) {
        logger.info(ANSI_CYAN + "SelectionFinished Message received." + ANSI_RESET);
        int clientID = selectionFinishedBody.getClientID();
        if (clientID != clientModel.getClientGameModel().getPlayer().getPlayerID()) {
            clientModel.receiveMessage("Player with ID: " + clientID + " finished selecting cards!");
        }
    }

    public void handleTimerStarted(ClientModel clientModel, TimerStartedBody timerStartedBody) {
        logger.info(ANSI_CYAN + "TimerStarted Message received." + ANSI_RESET);
        Platform.runLater(() -> {
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setContentText("Selection Finished! You have 30 Seconds! Hurry up!");
            a.show();
        });
    }

    public void handleTimerEnded(ClientModel clientModel, TimerEndedBody timerEndedBody) {
        logger.info(ANSI_CYAN + "TimerEnded Message received." + ANSI_RESET);
        ArrayList<Integer> lateClient = timerEndedBody.getClientIDs();

        clientModel.receiveMessage("Late client IDs are: " + lateClient);
    }

    public void handleCardsYouGotNowBody (ClientModel clientModel, CardsYouGotNowBody cardsYouGotNowBody) {
        logger.info(ANSI_CYAN + "CardsYouGotNow Message received." + ANSI_RESET);
        ArrayList<String> cards = cardsYouGotNowBody.getCards();
        //clientModel.getClientGameModel().setLateCards(cards);
        //TODO: put the cards in leere Felder in Register
        for (String card : cards) {
            clientModel.getClientGameModel().setLateCards(card);
            clientModel.getClientGameModel().setLatePlayers(true);
            clientModel.receiveMessage("Your new Card is " + card);
        }
    }

    public void handleCurrentCards(ClientModel clientModel, CurrentCardsBody currentCardsBody) {
        //clientModel.getClientGameModel().actualRegisterPropertyProperty().setValue(clientModel.getClientGameModel().getActualRegister());
        logger.info(ANSI_CYAN + "CurrentCards Message received." + ANSI_RESET);
        ArrayList<Object> currentCards = currentCardsBody.getActiveCards();

            if (clientModel.getClientGameModel().getValueActualRegister() != 4) {
                clientModel.getClientGameModel().setActualRegister(clientModel.getClientGameModel().getValueActualRegister() + 1);
            } else {
                clientModel.getClientGameModel().setActualRegister(0);
            }
            for (Object currentCard : currentCards) {
                String message = currentCard.toString().substring(10, currentCard.toString().length() - 1);
                String userNameDelimiter = ".0, card=";
                String[] split = message.split(userNameDelimiter);
                int playerID = Integer.parseInt(split[0]);
                String card = split[1];
                clientModel.receiveMessage("Player with ID: " + playerID + " has card: " + card + " in register: " + (clientModel.getClientGameModel().getValueActualRegister() + 1));
            }
    }

    public void handleReplaceCard (ClientModel clientModel, ReplaceCardBody replaceCardBody) {
        logger.info(ANSI_CYAN + "ReplaceCard Message received." + ANSI_RESET);
        int register = replaceCardBody.getRegister();
        String newCard = replaceCardBody.getNewCard();
        int clientID = replaceCardBody.getClientID();

        clientModel.receiveMessage("Player with ID: " + clientID + " replaced a card in register: " + (register + 1) + " new card is: " + newCard);
    }

    public void handleCardPlayed (ClientModel clientModel, CardPlayedBody cardPlayedBody) {
        logger.info(ANSI_CYAN + "CardPlayed Message received." + ANSI_RESET);
        int clientID = cardPlayedBody.getClientID();
        String card = cardPlayedBody.getCard();

    }

    public void handlePlayerTurning (ClientModel clientModel, PlayerTurningBody playerTurningBody) {
        int clientID = playerTurningBody.getClientID();
        String rotation = playerTurningBody.getRotation();
        Robot robot = null;
        for (Map.Entry<Robot, Point2D> entry : clientModel.getClientGameModel().getRobotMap().entrySet()) {
            if (entry.getKey().getName().equals(Game.getRobotNames().get(clientModel.getPlayersFigureMap().get(clientID)))) {
                robot = entry.getKey();
            }
        }
        clientModel.getClientGameModel().getTurningQueue().put(robot, rotation);
        clientModel.getClientGameModel().setQueueTurning(true);

        logger.info(ANSI_CYAN + "PlayerTurning Message received." + ANSI_RESET);

    }

    public void handleMovement (ClientModel clientModel, MovementBody movementBody) {
        logger.info(ANSI_CYAN + "Movement Message received." + ANSI_RESET);
        int clientID = movementBody.getClientID();
        int newX = movementBody.getX();
        int newY = movementBody.getY();
        Robot robot = null;

        for (Map.Entry<Robot, Point2D> entry : clientModel.getClientGameModel().getRobotMap().entrySet()) {
            if (entry.getKey().getName().equals(Game.getRobotNames().get(clientModel.getPlayersFigureMap().get(clientID)))) {
                robot = entry.getKey();
            }
        }
        clientModel.getClientGameModel().getMoveQueue().put(robot, new Point2D(newX, newY));
        clientModel.getClientGameModel().setQueueMove(true);

    }

    public void handleAnimation (ClientModel clientModel, AnimationBody animationBody) {
        logger.info(ANSI_CYAN + "Animation Message received." + ANSI_RESET);
        String type = animationBody.getType();
        switch (type) {
            case "BlueConveyorBelt": {

                /*clientModel.getClientGameModel().activateBlueBeltAnime(true);
                clientModel.getClientGameModel().extractData("BlueConveyorBelt");*/
                break;
            }
            case "GreenConveyorBelt": {
                break;
            }
            case "PushPanel": {
                //animation für PushPanel
                break;
            }
            case "Gear": {
                //animation für Gear
                break;
            }
            case "CheckPoint": {
                //animation für CheckPoint
                break;
            }
            case "PlayerShooting": {
                //animation für PlayerShooting
                break;
            }
            case "WallShooting": {
                clientModel.getClientGameModel().setanimationType("WallShooting");
                //animation für WallShooting
                break;
            }
            case "EnergySpace": {
                //animation für EnergySpace
                break;
            }
        }

    }

    public void handleReboot(ClientModel clientModel, RebootBody rebootBody) {

    }

    public void handleRebootDirection(ClientModel clientModel, RebootDirectionBody rebootDirectionBody) {

    }


}


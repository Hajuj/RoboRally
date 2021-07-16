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


/**
 * @author Mohamad, Viktoria
 */
public class MessageHandler {
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_RESET = "\u001B[0m";
    private static final Logger logger = Logger.getLogger(MessageHandler.class.getName());


    /**
     * When a client receives HelloClient from the server, the variable WaitingForServer is changed to false.
     * and now the client can send messages to the server.
     *
     * @param clientmodel     The ClientModel
     * @param helloClientBody The message body of the JSON message
     */
    public void handleHelloClient(ClientModel clientmodel, HelloClientBody helloClientBody) {
        logger.info(ANSI_CYAN + "HalloClient Message received." + ANSI_RESET);
        logger.info("Server has protocol " + helloClientBody.getProtocol());

        synchronized(clientmodel) {
            clientmodel.setWaitingForServer(false);
            clientmodel.notifyAll();
        }
    }


    /**
     * When the Client receives Welcome from the Server, the method creates a playerID for him.
     *
     * @param clientmodel     The ClientModel
     * @param welcomeBody     The message body of the JSON message
     */
    public void handleWelcome(ClientModel clientmodel, WelcomeBody welcomeBody) {
        logger.info(ANSI_CYAN + "Welcome Message received." + ANSI_RESET);
        logger.info("Your PlayerID: " + welcomeBody.getClientID());
        Player player = new Player(welcomeBody.getClientID());
        clientmodel.getClientGameModel().setPlayer(player);
    }


    /**
     * When an Error happens, it is sent after that to the player.
     *
     * @param clientmodel     The ClientModel
     * @param errorBody       The message body of the JSON message
     */
    public void handleError(ClientModel clientmodel, ErrorBody errorBody) {
        logger.warn(ANSI_CYAN + "Error has occurred! " + ANSI_RESET);
        logger.info("Error has occurred! " + errorBody.getError());

        if("gameOn".equals(errorBody.getError())) {
            clientmodel.setCanPlay(false);
        }


        Platform.runLater(() -> {
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setContentText(errorBody.getError());
            a.show();
        });
    }

    /**
     * When a client receives a chat message, it triggers the method receiveMessage in clientModel,
     * so the message can be shown.
     *
     * @param clientModel     The ClientModel
     * @param receivedChatBody The message body of the JSON message
     */
    public void handleReceivedChat(ClientModel clientModel, ReceivedChatBody receivedChatBody) {
        logger.info(ANSI_CYAN + "Chat received." + ANSI_RESET);
        clientModel.receiveMessage(receivedChatBody.getMessage());
    }

    /**
     * When a Game is started, all the players get 5 energy cubes.
     * A method from ClientGameModel is called to create the Map Objects,
     * then it sets the Game to On.
     *
     * @param clientModel     The ClientModel
     * @param gameStartedBody The message body of the JSON message
     */
    public void handleGameStarted(ClientModel clientModel, GameStartedBody gameStartedBody) {
        logger.info(ANSI_CYAN + "Game Started received." + ANSI_RESET);
        clientModel.getClientGameModel().setEnergy(5);
        clientModel.getClientGameModel().setMap(gameStartedBody.getGameMap());
        int mapX = gameStartedBody.getGameMap().size();
        int mapY = gameStartedBody.getGameMap().get(0).size();
        clientModel.getClientGameModel().createMapObjects(gameStartedBody.getGameMap(), mapX, mapY);
        clientModel.setGameOn(true);
    }

    /**
     * Send Alive-Message to the server after the client receives Alive-Message.
     *
     * @param clientModel The ClientModel
     * @param aliveBody   The message body of the JSON message
     */
    public void handleAlive(ClientModel clientModel, AliveBody aliveBody) {
        //wenn client bekommt ein Alive-Message von Server, schickt er ein "Alive"-Antwort zurück
        clientModel.sendMessage(new JSONMessage("Alive", new AliveBody()));
    }

    /**
     * When a player is added the
     *
     * @param clientModel     The ClientModel
     * @param playerAddedBody The message body of the JSON message
     */
    public void handlePlayerAdded(ClientModel clientModel, PlayerAddedBody playerAddedBody) {
        logger.info(ANSI_CYAN + "PlayerAdded Message received." + ANSI_RESET);
        int clientID = playerAddedBody.getClientID();
        String name = playerAddedBody.getName();
        int figure = playerAddedBody.getFigure();

        // The player himself has been added
        if(clientModel.getClientGameModel().getPlayer().getPlayerID() == clientID) {
            clientModel.getClientGameModel().getPlayer().setName(name);
            clientModel.getClientGameModel().getPlayer().setFigure(figure);
        }
        // save client info in the Hash Maps
        clientModel.addPlayer(clientID, name, figure);
        logger.info("A new player has been added. Name: " + name + ", ID: " + clientID + ", Figure: " + figure);
    }

    /**
     * When a player changes his status from ready to ot ready or the other way,
     * The status is refreshed
     *
     * @param clientModel     The ClientModel
     * @param playerStatusBody The message body of the JSON message
     */
    public void handlePlayerStatus(ClientModel clientModel, PlayerStatusBody playerStatusBody) {
        logger.info(ANSI_CYAN + "PlayerStatus Message received." + ANSI_RESET);
        clientModel.refreshPlayerStatus(playerStatusBody.getClientID(), playerStatusBody.isReady());

    }

    /**
     * The first ready player receives a message to select the map for the game.
     *
     * @param clientModel     The ClientModel
     * @param selectMapBody The message body of the JSON message
     */
    public void handleSelectMap(ClientModel clientModel, SelectMapBody selectMapBody) {
        logger.info(ANSI_CYAN + "SelectMap Message received." + ANSI_RESET);
        clientModel.getAvailableMaps().clear();
        for(String map : selectMapBody.getAvailableMaps()) {
            clientModel.getAvailableMaps().add(map);
        }
        clientModel.setDoChooseMap(true);
    }

    /**
     * When a Map is selected, a message is sent with the selected map.
     *
     * @param clientModel     The ClientModel
     * @param mapSelectedBody The message body of the JSON message
     */
    public void handleMapSelected(ClientModel clientModel, MapSelectedBody mapSelectedBody) {
        logger.info(ANSI_CYAN + "MapSelected Message received." + ANSI_RESET);
        clientModel.setSelectedMap(mapSelectedBody.getMap());
    }

    /**
     * When a player disconnects, then he gets removed from the clientModel.
     *
     * @param clientmodel     The ClientModel
     * @param connectionUpdateBody The message body of the JSON message
     */
    public void handleConnectionUpdate(ClientModel clientmodel, ConnectionUpdateBody connectionUpdateBody) {
        logger.info(ANSI_CYAN + "ConnectionUpdate Message received." + ANSI_RESET);
        int playerID = connectionUpdateBody.getPlayerID();
        boolean isConnected = connectionUpdateBody.isConnected();
        String action = connectionUpdateBody.getAction();

        if(action.equals("remove") && !isConnected) {
            clientmodel.removePlayer(playerID);
        }
    }

    /**
     * When a player chooses a starting point, the others should update their views.
     *
     * @param clientModel     The ClientModel
     * @param startingPointTakenBody The message body of the JSON message
     */
    public void handleStartingPointTaken(ClientModel clientModel, StartingPointTakenBody startingPointTakenBody) {
        logger.info(ANSI_CYAN + "StartingPointTaken Message received." + ANSI_RESET);
        int playerID = startingPointTakenBody.getClientID();

        if(playerID == clientModel.getClientGameModel().getPlayer().getPlayerID()) {
            String robotName = Game.getRobotNames().get(clientModel.getPlayersFigureMap().get(playerID));
            Robot myRobot = new Robot(robotName, startingPointTakenBody.getX(), startingPointTakenBody.getY());
            clientModel.getClientGameModel().getPlayer().setRobot(myRobot);
        }

        String robotName = Game.getRobotNames().get(clientModel.getPlayersFigureMap().get(playerID));
        Robot robot = new Robot(robotName, startingPointTakenBody.getX(), startingPointTakenBody.getY());

        Point2D position = new Point2D(startingPointTakenBody.getX(), startingPointTakenBody.getY());

        clientModel.getClientGameModel().getStartingPointQueue().put(robot, position);
        clientModel.getClientGameModel().setStartingPoint(true);

        //Brauche Ich das noch
        clientModel.getClientGameModel().setProgrammingPhase(true);

    }

    /**
     *  Sets the current player to the right ID, and checks if it's the turn of the player himself.
     *
     * @param clientModel     The ClientModel
     * @param currentPlayerBody The message body of the JSON message
     */
    public void handleCurrentPlayer(ClientModel clientModel, CurrentPlayerBody currentPlayerBody) {
        logger.info(ANSI_CYAN + "CurrentPlayer Message received." + ANSI_RESET);
        int playerID = currentPlayerBody.getClientID();
        clientModel.getClientGameModel().setActualPlayerID(playerID);
        clientModel.getClientGameModel().switchPlayer(true);
        if(clientModel.getClientGameModel().getActualPhase() == 1 && clientModel.getClientGameModel().getPlayer().getPlayerID() == playerID) {
            clientModel.getClientGameModel().refillShop(true);
        }
        logger.info("Current Player: " + playerID);

    }

    /**
     * Sets the Active Phase to the right Phase.
     *
     * @param clientModel     The ClientModel
     * @param activePhaseBody The message body of the JSON message
     */
    public void handleActivePhase(ClientModel clientModel, ActivePhaseBody activePhaseBody) {
        logger.info(ANSI_CYAN + "ActivePhase Message received." + ANSI_RESET);
        int phase = activePhaseBody.getPhase();

        clientModel.getClientGameModel().setActualPhase(phase);
        if(phase == 2) {
            clientModel.getClientGameModel().setActualRegister(-1);
        }
    }

    /**
     * The selected card is shown in the chat after the client receives it from the server.
     *
     * @param clientModel     The ClientModel
     * @param cardSelectedBody The message body of the JSON message
     */
    public void handleCardSelected(ClientModel clientModel, CardSelectedBody cardSelectedBody) {
        logger.info(ANSI_CYAN + "CardSelected Message received." + ANSI_RESET);
        int clientID = cardSelectedBody.getClientID();
        int register = cardSelectedBody.getRegister();

        if(cardSelectedBody.isFilled()) {
            clientModel.receiveMessage("Player with ID: " + clientID + " added a card to register number: " + (register + 1));
        } else {
            clientModel.receiveMessage("Player with ID: " + clientID + " removed a card from register number: " + (register + 1));
        }
    }

    /**
     * Send all the selected cards to the player.
     *
     * @param clientModel     The ClientModel
     * @param yourCardsBody The message body of the JSON message
     */
    public void handleYourCards(ClientModel clientModel, YourCardsBody yourCardsBody) {
        logger.info(ANSI_CYAN + "YourCards Message received." + ANSI_RESET);
        //speichere die Cards und refresh the View
        clientModel.getClientGameModel().getCardsInHand().clear();
        clientModel.getClientGameModel().setCardsInHand(yourCardsBody.getCardsInHand());
        clientModel.getClientGameModel().setHandCards(true);
    }

    /**
     * It sends the cards of the other players
     *
     * @param clientModel     The ClientModel
     * @param notYourCardsBody The message body of the JSON message
     */
    public void handleNotYourCards(ClientModel clientModel, NotYourCardsBody notYourCardsBody) {
        logger.info(ANSI_CYAN + "NotYourCards Message received." + ANSI_RESET);
        int clientID = notYourCardsBody.getClientID();
        int amount = notYourCardsBody.getCardsInHand();
        String playerName = clientModel.getPlayersNamesMap().get(clientID);

        clientModel.receiveMessage("Player " + playerName + " has " + amount + " cards in the hand!");
    }

    /**
     * When the deck is shuffled the player gets informed.
     *
     * @param clientModel     The ClientModel
     * @param shuffleCodingBody The message body of the JSON message
     */
    public void handleShuffleCoding(ClientModel clientModel, ShuffleCodingBody shuffleCodingBody) {
        logger.info(ANSI_CYAN + "ShuffleCoding Message received." + ANSI_RESET);
        int clientID = shuffleCodingBody.getClientID();

        clientModel.receiveMessage("Player with ID: " + clientID + " shuffled the card!");
    }

    /**
     * When a player finish the selection of the cards, tell the players the a player is finished
     * choosing cards.
     *
     * @param clientModel     The ClientModel
     * @param selectionFinishedBody The message body of the JSON message
     */
    public void handleSelectionFinished(ClientModel clientModel, SelectionFinishedBody selectionFinishedBody) {
        logger.info(ANSI_CYAN + "SelectionFinished Message received." + ANSI_RESET);
        int clientID = selectionFinishedBody.getClientID();
        if(clientID != clientModel.getClientGameModel().getPlayer().getPlayerID()) {
            clientModel.receiveMessage("Player with ID: " + clientID + " finished selecting cards!");
        }
    }

    /**
     * When the timer is started all the players get informed.
     *
     * @param clientModel     The ClientModel
     * @param timerStartedBody The message body of the JSON message
     */
    public void handleTimerStarted(ClientModel clientModel, TimerStartedBody timerStartedBody) {
        logger.info(ANSI_CYAN + "TimerStarted Message received." + ANSI_RESET);
        Platform.runLater(() -> {
            clientModel.getClientGameModel().setTimer(true);
        });
    }

    /**
     * When the timer is ended, the players get informed about the late players.
     *
     * @param clientModel     The ClientModel
     * @param timerEndedBody The message body of the JSON message
     */
    public void handleTimerEnded(ClientModel clientModel, TimerEndedBody timerEndedBody) {
        logger.info(ANSI_CYAN + "TimerEnded Message received." + ANSI_RESET);
        ArrayList<Integer> lateClient = timerEndedBody.getClientIDs();

        clientModel.receiveMessage("Late client IDs are: " + lateClient);
    }

    /**
     * The cards that a late player gets, are shown to him here.
     *
     * @param clientModel     The ClientModel
     * @param cardsYouGotNowBody The message body of the JSON message
     */
    public void handleCardsYouGotNowBody(ClientModel clientModel, CardsYouGotNowBody cardsYouGotNowBody) {
        logger.info(ANSI_CYAN + "CardsYouGotNow Message received." + ANSI_RESET);
        ArrayList<String> cards = cardsYouGotNowBody.getCards();
        clientModel.getClientGameModel().setLateCards(cards);
        clientModel.getClientGameModel().setLatePlayers(true);
    }

    /**
     * When a player plays a card in his register, the other players get informed about it,
     * The ID and the card of the player are sent.
     *
     * @param clientModel     The ClientModel
     * @param currentCardsBody The message body of the JSON message
     */
    public void handleCurrentCards(ClientModel clientModel, CurrentCardsBody currentCardsBody) {
        //clientModel.getClientGameModel().actualRegisterPropertyProperty().setValue(clientModel.getClientGameModel().getActualRegister());
        logger.info(ANSI_CYAN + "CurrentCards Message received." + ANSI_RESET);
        ArrayList<Object> currentCards = currentCardsBody.getActiveCards();

        if(clientModel.getClientGameModel().getValueActualRegister() != 4) {
            clientModel.getClientGameModel().setActualRegister(clientModel.getClientGameModel().getValueActualRegister() + 1);
        } else {
            clientModel.getClientGameModel().setActualRegister(0);
        }
        for(Object currentCard : currentCards) {
            String message = currentCard.toString().substring(10, currentCard.toString().length() - 1);
            String userNameDelimiter = ".0, card=";
            String[] split = message.split(userNameDelimiter);
            int playerID = Integer.parseInt(split[0]);
            String card = split[1];
            clientModel.receiveMessage("Player with ID: " + playerID + " has card: " + card + " in register: " + (clientModel.getClientGameModel().getValueActualRegister() + 1));
        }
    }

    /**
     * When a player plays a spam card, he gets notified about it in this message.
     *
     * @param clientModel     The ClientModel
     * @param replaceCardBody The message body of the JSON message
     */
    public void handleReplaceCard(ClientModel clientModel, ReplaceCardBody replaceCardBody) {
        logger.info(ANSI_CYAN + "ReplaceCard Message received." + ANSI_RESET);
        int register = replaceCardBody.getRegister();
        String newCard = replaceCardBody.getNewCard();
        int clientID = replaceCardBody.getClientID();

        clientModel.receiveMessage("Player with ID: " + clientID + " replaced a card in register: " + (register + 1) + " new card is: " + newCard);
    }

    /**
     * The played card.
     *
     * @param clientModel     The ClientModel
     * @param cardPlayedBody The message body of the JSON message
     */
    public void handleCardPlayed(ClientModel clientModel, CardPlayedBody cardPlayedBody) {
        logger.info(ANSI_CYAN + "CardPlayed Message received." + ANSI_RESET);

    }

    /**
     * When a player turns the Views for all gets updated.
     *
     * @param clientModel     The ClientModel
     * @param playerTurningBody The message body of the JSON message
     */
    public void handlePlayerTurning(ClientModel clientModel, PlayerTurningBody playerTurningBody) {
        int clientID = playerTurningBody.getClientID();
        String rotation = playerTurningBody.getRotation();

        ClientGameModel.TurnTask turnTask = new ClientGameModel.TurnTask(clientID, rotation);
        clientModel.getClientGameModel().getTurningQueue().add(turnTask);
        clientModel.getClientGameModel().setQueueTurning(true);

        logger.info(ANSI_CYAN + "PlayerTurning Message received." + ANSI_RESET);

    }

    /**
     * When a player moves the Views for all gets updated.
     *
     * @param clientModel     The ClientModel
     * @param movementBody The message body of the JSON message
     */
    public void handleMovement(ClientModel clientModel, MovementBody movementBody) {
        logger.info(ANSI_CYAN + "Movement Message received." + ANSI_RESET);
        int clientID = movementBody.getClientID();
        int newX = movementBody.getX();
        int newY = movementBody.getY();

        ClientGameModel.MoveTask moveTask = new ClientGameModel.MoveTask(clientID, new Point2D(newX, newY));

        clientModel.getClientGameModel().getMoveQueue().add(moveTask);
        clientModel.getClientGameModel().setQueueMove(true);

    }

    /**
     * The Animations in the map of the game.
     *
     * @param clientModel     The ClientModel
     * @param animationBody The message body of the JSON message
     */
    public void handleAnimation(ClientModel clientModel, AnimationBody animationBody) {
        logger.info(ANSI_CYAN + "Animation Message received." + ANSI_RESET);
        String type = animationBody.getType();
        switch(type) {
            case "BlueConveyorBelt": {
                clientModel.getClientGameModel().setAnimateBelts(true);
                break;
            }
            case "GreenConveyorBelt": {
                // clientModel.getClientGameModel().setAnimateBelts(true);
                break;
            }
            case "PushPanel": {
                //animation für PushPanel
                break;
            }
            case "Gear": {
                clientModel.getClientGameModel().setAnimateGears(true);
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
                //animation für WallShooting
                break;
            }
            case "EnergySpace": {
                clientModel.getClientGameModel().setAnimateEnergySpaces(true);
                break;
            }
        }
    }

    /**
     * The energy of the player is sent to all others.
     *
     * @param clientModel     The ClientModel
     * @param energyBody The message body of the JSON message
     */
    public void handleEnergy(ClientModel clientModel, EnergyBody energyBody) {
        logger.info(ANSI_CYAN + "Energy Message received." + ANSI_RESET);
        clientModel.receiveMessage("The Energy from Player " + energyBody.getClientID() + " is " + energyBody.getCount() + " now!");
        if(clientModel.getClientGameModel().getPlayer().getPlayerID() == energyBody.getClientID()) {
            clientModel.getClientGameModel().setEnergy(energyBody.getCount());
        }
    }

    /**
     * When a player reboots the Views are updated so he can choose a reboot direction.
     *
     * @param clientModel     The ClientModel
     * @param rebootBody The message body of the JSON message
     */
    public void handleReboot(ClientModel clientModel, RebootBody rebootBody) {
        logger.info(ANSI_CYAN + "Reboot Message received." + ANSI_RESET);

        int clientID = rebootBody.getClientID();
        if(clientID == clientModel.getClientGameModel().getPlayer().getPlayerID()) {
            clientModel.getClientGameModel().setChooseRebootDirection(true);
        }

    }

    /**
     * When a player reaches a checkpoint an alert is shown to all players.
     *
     * @param clientModel     The ClientModel
     * @param checkPointReachedBody The message body of the JSON message
     */
    public void handleCheckPointReachedBody(ClientModel clientModel, CheckPointReachedBody checkPointReachedBody) {
        logger.info(ANSI_CYAN + "CheckPointReached Message received." + ANSI_RESET);
        clientModel.receiveMessage("Player " + checkPointReachedBody.getClientID() + " is on the " + checkPointReachedBody.getNumber() + " Checkpoint now!");
        if(clientModel.getClientGameModel().getPlayer().getPlayerID() == checkPointReachedBody.getClientID()) {
            clientModel.receiveMessage("YOU ARE AWESOME");
        }
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Player " + checkPointReachedBody.getClientID() + " is on the " + checkPointReachedBody.getNumber() + " Checkpoint now!");
            alert.show();
        });
    }

    /**
     * When the game finished all the players get informed about it.
     *
     * @param clientModel     The ClientModel
     * @param gameFinishedBody The message body of the JSON message
     */
    public void handleGameFinished(ClientModel clientModel, GameFinishedBody gameFinishedBody) {
        logger.info(ANSI_CYAN + "GameFinished Message received." + ANSI_RESET);
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Game finished! The Player with ID " + gameFinishedBody.getClientID() + " is the best");
            alert.show();
        });
        clientModel.setGameFinished(true);
        clientModel.getClientGameModel().refreshModel();
        clientModel.getClientGameModel().setGameFinished(true);
    }

    /**
     * When a damage is drawn to a player, he gets notified.
     *
     * @param clientModel     The ClientModel
     * @param drawDamageBody The message body of the JSON message
     */
    public void handleDrawDamage(ClientModel clientModel, DrawDamageBody drawDamageBody) {
        logger.info(ANSI_CYAN + "DrawDamage Message received." + ANSI_RESET);
        ArrayList<String> cards = drawDamageBody.getCards();
        int clientID = drawDamageBody.getClientID();
        String cardsString = "";
        for(String one : cards) {
            cardsString = cardsString + " " + one;
        }
        clientModel.receiveMessage("Player " + clientID + " has " + cardsString);
    }

    /**
     * When a player picks a damage, gets a window with the amount of damage needed to be picked.
     *
     * @param clientModel     The ClientModel
     * @param pickDamageBody The message body of the JSON message
     */
    public void handlePickDamage(ClientModel clientModel, PickDamageBody pickDamageBody) {
        logger.info(ANSI_CYAN + "PickDamage Message received." + ANSI_RESET);
        clientModel.getClientGameModel().setDamageCount(pickDamageBody.getCount());
    }

    /**
     * When the upgrade shop gets refilled
     *
     * @param clientModel     The ClientModel
     * @param refillShopBody The message body of the JSON message
     */
    public void handleRefillShop(ClientModel clientModel, RefillShopBody refillShopBody) {
        logger.info(ANSI_CYAN + "RefillShop Message received." + ANSI_RESET);
        ArrayList<String> cards = refillShopBody.getCards();

        //clientModel.getClientGameModel ().setUpgradeCards (cards);
        //clientModel.getClientGameModel ().refillShop ( true );

        for(String card : cards) {
            clientModel.getClientGameModel().getUpgradeCards().add(card);
        }
    }

    /**
     * When the upgrade shop gets exchanged when no one buys upgrade card.
     *
     * @param clientModel     The ClientModel
     * @param exchangeShopBody The message body of the JSON message
     */
    public void handleExchangeShop(ClientModel clientModel, ExchangeShopBody exchangeShopBody) {
        logger.info(ANSI_CYAN + "ExchangeShop Message received." + ANSI_RESET);
        ArrayList<String> cards = exchangeShopBody.getCards();

        clientModel.getClientGameModel().setUpgradeCards(exchangeShopBody.getCards());
    }

    /**
     * When a a check point moves (Twister map)
     *
     * @param clientModel     The ClientModel
     * @param checkpointMovedBody The message body of the JSON message
     */
    public void handleCheckpointMovedBody(ClientModel clientModel, CheckpointMovedBody checkpointMovedBody) {
        logger.info(ANSI_CYAN + "CheckPointMoved Message received." + ANSI_RESET);
        int numCP = checkpointMovedBody.getCheckpointID();
        int x = checkpointMovedBody.getX();
        int y = checkpointMovedBody.getY();


        ClientGameModel.MoveCPTask moveCPTask = new ClientGameModel.MoveCPTask(numCP, new Point2D(x, y));
        clientModel.getClientGameModel().getMoveCPQueue().add(moveCPTask);
        clientModel.getClientGameModel().setQueueCPMove(true);

    }

    /**
     * When a player who is Admin Privilege chooses a register.
     *
     * @param clientModel     The ClientModel
     * @param registerChosenBody The message body of the JSON message
     */
    public void handleRegisterChosen(ClientModel clientModel, RegisterChosenBody registerChosenBody) {
        logger.info(ANSI_CYAN + "RegisterChosen Message received." + ANSI_RESET);
        int id = registerChosenBody.getClientID();
        int register = registerChosenBody.getRegister();
        String newAdmitMessage = "Player " + id + " is Admin in " + register + " register!";
        clientModel.receiveMessage(newAdmitMessage);
    }

    /**
     * When a player buys an upgrade card.
     *
     * @param clientModel     The ClientModel
     * @param upgradeBoughtBody The message body of the JSON message
     */
    public void handleUpgradeBought(ClientModel clientModel, UpgradeBoughtBody upgradeBoughtBody) {
        logger.info(ANSI_CYAN + "UpgradeBought Message received." + ANSI_RESET);
        int clientID = upgradeBoughtBody.getClientID();
        String card = upgradeBoughtBody.getCard();

        for(String upgradeCard : clientModel.getClientGameModel().getUpgradeCards()) {
            if(upgradeCard.equals(card)) {
                clientModel.getClientGameModel().getUpgradeCards().remove(upgradeCard);
                break;
            }

        }
        clientModel.receiveMessage("Player " + clientID + " has bought " + card);
    }

}

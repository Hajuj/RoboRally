package client.KI_Zeug;

import client.model.ClientModel;
import client.model.MessageHandler;
import json.protocol.*;
import org.apache.log4j.Logger;

import java.util.Map;

/**
 * @author Viktoria
 * This is a message handler for the AI that overrides some of the methods from client MessageHandler.
 * The current stand of the AI is that it chooses random cards, doesn't buy upgrade cards,
 * doesn't choose a reboot direction.
 */
public class MessageHandlerAI extends client.model.MessageHandler {
    private static SimpleAIModel simpleAIModel = SimpleAIModel.getInstance();
    private static final Logger logger = Logger.getLogger(MessageHandler.class.getName());


    @Override
    public void handleWelcome(ClientModel clientmodel, WelcomeBody welcomeBody) {
        super.handleWelcome(clientmodel, welcomeBody);
        simpleAIModel.setName("SimpleAI_" + welcomeBody.getClientID());
        clientmodel.sendUsernameAndRobot(simpleAIModel.getName(), simpleAIModel.getFigureCounter());
    }

    @Override
    public void handlePlayerAdded(ClientModel clientModel, PlayerAddedBody playerAddedBody) {
        super.handlePlayerAdded(clientModel, playerAddedBody);
        if (simpleAIModel.isHasPlayerValues()) {
            clientModel.sendMsg("Hi " + playerAddedBody.getName() + "! I'm Simple AI and I love you!");
        } else if (playerAddedBody.getClientID() == clientModel.getClientGameModel().getPlayer().getPlayerID()) {
            simpleAIModel.setHasPlayerValues(true);
            //AI ist immer ready
            clientModel.setNewStatus(true);
            for (Map.Entry<Integer, String> entry : clientModel.getPlayersNamesMap().entrySet()) {
                //say hi zu alle ausser dich selber
                if (entry.getKey() != clientModel.getClientGameModel().getPlayer().getPlayerID()) {
                    clientModel.sendMsg("Hi " + entry.getValue() + "! I'm Simple AI and I love you!");
                }
            }
        }
    }

    //Wegen Alert-Fenster
    @Override
    public void handleError(ClientModel clientmodel, ErrorBody errorBody) {
        //f??r die Automatische Figur Ausw??hlen
        if (errorBody.getError().equals("Figure is already taken") && !simpleAIModel.isHasPlayerValues()) {
            simpleAIModel.chooseRobotRoutine();
        } else if (errorBody.getError().equals("Another robot is on this tile!")) {
            simpleAIModel.setStartingPointCounter(simpleAIModel.getStartingPointCounter() + 1);
            simpleAIModel.setStartingPointRoutine();
        }
    }

    @Override
    public void handleCheckPointReachedBody (ClientModel clientModel, CheckPointReachedBody checkPointReachedBody) {
        logger.info(ANSI_CYAN + "CheckPointReached Message received." + ANSI_RESET);
        clientModel.receiveMessage("Player " + checkPointReachedBody.getClientID() + " is on the " + checkPointReachedBody.getNumber() + " Checkpoint now!");
        if (clientModel.getClientGameModel().getPlayer().getPlayerID() == checkPointReachedBody.getClientID()) {
            clientModel.receiveMessage("YOU ARE AWESOME");
        }
    }

    @Override
    public void handleCurrentPlayer (ClientModel clientModel, CurrentPlayerBody currentPlayerBody) {
        super.handleCurrentPlayer(clientModel, currentPlayerBody);
        if (currentPlayerBody.getClientID() == clientModel.getClientGameModel().getPlayer().getPlayerID()) {
            if (clientModel.getClientGameModel().getActualPhase() == 0) {
                simpleAIModel.setStartingPointRoutine();
            } else if (clientModel.getClientGameModel().getActualPhase() == 3) {
                simpleAIModel.playCardRoutine(clientModel.getClientGameModel().getValueActualRegister());
            } else if (clientModel.getClientGameModel().getActualPhase() == 1) {
                clientModel.getClientGameModel().buyUpgradeCard("Null");
            }
        }
    }

    @Override
    public void handleYourCards(ClientModel clientModel, YourCardsBody yourCardsBody) {
        super.handleYourCards(clientModel, yourCardsBody);
        simpleAIModel.setMyHandCards(yourCardsBody.getCardsInHand());
    }

    //wegen Timer Alert
    @Override
    public void handleTimerStarted(ClientModel clientModel, TimerStartedBody timerStartedBody) {
        logger.info(ANSI_CYAN + "TimerStarted Message received." + ANSI_RESET);
        for (int i = 0; i < 5; i++) {
            SimpleAIModel.getCardsInRegister().replace(i, null);
        }
        if (clientModel.getClientGameModel().getActualPhase() == 2) {
            simpleAIModel.chooseCardsRoutine();
        }
    }

    @Override
    public void handleGameFinished(ClientModel clientModel, GameFinishedBody gameFinishedBody) {
    }

    @Override
    public void handlePickDamage(ClientModel clientModel, PickDamageBody pickDamageBody) {
        super.handlePickDamage(clientModel, pickDamageBody);
        simpleAIModel.pickDamageRoutine();
    }

    @Override
    public void handleReboot(ClientModel clientModel, RebootBody rebootBody) {

    }
}
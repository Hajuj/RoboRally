package client.KI_Zeug;

import client.model.ClientModel;
import json.protocol.*;

import java.util.Map;

public class MessageHandlerAI extends client.model.MessageHandler {
    private static SimpleAIModel simpleAIModel = SimpleAIModel.getInstance();


    @Override
    public void handleWelcome (ClientModel clientmodel, WelcomeBody welcomeBody) {
        super.handleWelcome(clientmodel, welcomeBody);
        simpleAIModel.setName("SimpleAI_" + welcomeBody.getClientID());
        clientmodel.sendUsernameAndRobot(simpleAIModel.getName(), simpleAIModel.getFigureCounter());
    }

    @Override
    public void handlePlayerAdded (ClientModel clientModel, PlayerAddedBody playerAddedBody) {
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
    public void handleError (ClientModel clientmodel, ErrorBody errorBody) {
        System.out.println(errorBody.getError());
        //für die Automatische Figur Auswählen
        if (errorBody.getError().equals("Figure is already taken") && !simpleAIModel.isHasPlayerValues()) {
            simpleAIModel.chooseRobotRoutine();
        } else if (errorBody.getError().equals("Another robot is on this tile!")) {
            simpleAIModel.setStartingPointCounter(simpleAIModel.getStartingPointCounter() + 1);
            simpleAIModel.setStartingPointRoutine();
        }
    }

    @Override
    public void handleCurrentPlayer (ClientModel clientModel, CurrentPlayerBody currentPlayerBody) {
        super.handleCurrentPlayer(clientModel, currentPlayerBody);
        if (currentPlayerBody.getClientID() == clientModel.getClientGameModel().getPlayer().getPlayerID()) {
            if (clientModel.getClientGameModel().getActualPhase() == 0) {
                simpleAIModel.setStartingPointRoutine();
            } else if (clientModel.getClientGameModel().getActualPhase() == 3) {
                simpleAIModel.playCardRoutine(clientModel.getClientGameModel().getActualRegister());
            }
        }
    }

    @Override
    public void handleYourCards (ClientModel clientModel, YourCardsBody yourCardsBody) {
        super.handleYourCards(clientModel, yourCardsBody);
        for (int i = 0; i < 5; i++) {
            simpleAIModel.getCardsInRegister().replace(i, null);
        }
        if (clientModel.getClientGameModel().getActualPhase() == 2) {
            simpleAIModel.chooseCardsRoutine();
        }
    }

    //wegen Timer Alert
    @Override
    public void handleTimerStarted (ClientModel clientModel, TimerStartedBody timerStartedBody) {
        System.out.println("You are awesome AI Baby");
    }
}
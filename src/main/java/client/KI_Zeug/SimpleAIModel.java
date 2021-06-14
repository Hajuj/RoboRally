package client.KI_Zeug;

import client.model.ClientModel;
import game.boardelements.StartPoint;
import javafx.geometry.Point2D;

import java.util.HashMap;
import java.util.Map;

public class SimpleAIModel {
    private static SimpleAIModel instance;
    private static ClientModel clientModel = ClientModel.getInstance();

    private boolean hasPlayerValues = false;
    private int figureCounter = 0;
    private int startingPointCounter = 0;

    private static HashMap<Integer, String> cardsInRegister = new HashMap<Integer, String>();

    private String name;

    private SimpleAIModel () {
    }

    public static SimpleAIModel getInstance () {
        if (instance == null) {
            instance = new SimpleAIModel();
        }
        return instance;
    }


    public static void main (String[] args) {
        clientModel.setMessageHandler(new MessageHandlerAI());
        clientModel.setAI(true);
        for (int i = 0; i < 5; i++) {
            cardsInRegister.put(i, null);
        }
        clientModel.connectClient("sep21.dbs.ifi.lmu.de", 52020);
    }

    public void chooseRobotRoutine () {
        if (figureCounter != 5) {
            figureCounter++;
            clientModel.sendUsernameAndRobot("SimpleAIModel", figureCounter);
        } else {
            System.out.println("No available figure, srry");
            System.exit(0);
        }
    }

    public void setStartingPointRoutine () {
        int i = 0;
        if (startingPointCounter != clientModel.getClientGameModel().getStartPointMap().size() - 1) {
            for (Map.Entry<Point2D, StartPoint> entry : clientModel.getClientGameModel().getStartPointMap().entrySet()) {
                if (i == startingPointCounter) {
                    int x = (int) entry.getKey().getX();
                    int y = (int) entry.getKey().getY();
                    clientModel.getClientGameModel().sendStartingPoint(x, y);
                    break;
                } else {
                    i++;
                }
            }
        } else {
            System.out.println("No starting points, srry");
            System.exit(0);
        }
    }

    public void findTheRouteToWin () {
         clientModel.getClientGameModel().getCheckPointMap();
         clientModel.getClientGameModel().getStartPointMap();
    }


    public void chooseCardsRoutine () {
        for (int i = 0; i < 5; i++) {
            /**String cardName = clientModel.getClientGameModel().getCardsInHand().get(i);
            clientModel.getClientGameModel().sendSelectedCards(i, cardName);
            cardsInRegister.replace(i, cardName);
             */
        }
    }

    public void playCardRoutine (int currentRegiser) {
        clientModel.getClientGameModel().sendPlayCard(cardsInRegister.get(currentRegiser));
    }

    public void setStartingPointCounter (int startingPointCounter) {
        this.startingPointCounter = startingPointCounter;
    }

    public int getStartingPointCounter () {
        return startingPointCounter;
    }

    public boolean isHasPlayerValues () {
        return hasPlayerValues;
    }

    public void setHasPlayerValues (boolean hasPlayerValues) {
        this.hasPlayerValues = hasPlayerValues;
    }

    public int getFigureCounter () {
        return figureCounter;
    }

    public void setFigureCounter (int figureCounter) {
        this.figureCounter = figureCounter;
    }

    public String getName () {
        return name;
    }

    public void setName (String name) {
        this.name = name;
    }
}

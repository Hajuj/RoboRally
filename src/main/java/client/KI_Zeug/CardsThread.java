package client.KI_Zeug;

import client.model.ClientModel;
import game.Card;
import game.Game;
import game.Player;
import json.JSONMessage;
import json.protocol.EnergyBody;
import json.protocol.PlayCardBody;
import json.protocol.PlayerTurningBody;
import json.protocol.ReplaceCardBody;
import org.apache.log4j.Logger;

import java.util.ArrayList;

public class CardsThread extends Thread {
    private SimpleAIModel simpleAIModel;
    private int cards;
    private ArrayList<String> myCards = new ArrayList<>();
    private static Logger logger = Logger.getLogger(CardsThread.class.getName());

    public CardsThread (SimpleAIModel baby, int cards) {
        this.cards = cards;
        this.simpleAIModel = baby;
    }

    @Override
    public void run () {
        for (int i = 0; i < 5; i++) {
            myCards.add(i, null);
        }
        decodeCards();
        if (againInZeroRegister()) {
            simpleAIModel.getMyBabyList().remove(this);
            this.interrupt();
        } else if (spamInRegister()) {
            simpleAIModel.getMyBabyList().remove(this);
        } else {
            logger.warn(myCards);
        }
    }

    public boolean spamInRegister(){
        for (int i = 0; i <5; i++){
            if()
        }
    }


    public boolean againInZeroRegister () {
        if (myCards.get(0).equals("Again")) return true;
        return false;
    }


    public void decodeCards () {
        int i = cards;
        int reg0 = i / 10000;
        int reg1 = (i - 10000 * reg0) / 1000;
        int reg2 = (i - 10000 * reg0 - 1000 * reg1) / 100;
        int reg3 = (i - 10000 * reg0 - 1000 * reg1 - 100 * reg2) / 10;
        int reg4 = (i - 10000 * reg0 - 1000 * reg1 - 100 * reg2 - 10 * reg3);
        myCards.set(0, simpleAIModel.getMyHandCards().get(reg0 - 1));
        myCards.set(1, simpleAIModel.getMyHandCards().get(reg1 - 1));
        myCards.set(2, simpleAIModel.getMyHandCards().get(reg2 - 1));
        myCards.set(3, simpleAIModel.getMyHandCards().get(reg3 - 1));
        myCards.set(4, simpleAIModel.getMyHandCards().get(reg4 - 1));
        System.out.println("39  " + myCards);
    }

}





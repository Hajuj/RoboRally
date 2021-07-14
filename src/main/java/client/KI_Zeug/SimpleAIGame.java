package client.KI_Zeug;

import game.Card;
import game.Game;
import game.Player;

import java.util.ArrayList;

/**public class SimpleAIGame extends Game {

    public void activateCardEffect(String card) {
        int indexCurrentPlayer = playerList.indexOf(server.getPlayerWithID(currentPlayer));
        String robotOrientation = playerList.get(indexCurrentPlayer).getRobot().getOrientation();

        switch (card) {
            case "Again" -> {
                Card lastCard = playerList.get(indexCurrentPlayer).getDeckRegister().getDeck().get((currentRegister - 1));
                if (lastCard.cardName.equals("Again")) {
                    Card veryLastCard = playerList.get(indexCurrentPlayer).getDeckRegister().getDeck().get((currentRegister - 2));
                    activateCardEffect(veryLastCard.cardName);
                } else {
                    activateCardEffect(lastCard.cardName);
                }

            }
            case "BackUp" -> {
                switch (robotOrientation) {
                    case "top" -> {
                        moveRobot(playerList.get(indexCurrentPlayer).getRobot(), "bottom", 1);
                    }
                    case "bottom" -> {
                        moveRobot(playerList.get(indexCurrentPlayer).getRobot(), "top", 1);
                    }
                    case "left" -> {
                        moveRobot(playerList.get(indexCurrentPlayer).getRobot(), "right", 1);
                    }
                    case "right" -> {
                        moveRobot(playerList.get(indexCurrentPlayer).getRobot(), "left", 1);
                    }
                }
            }
            case "MoveI" -> {
                moveRobot(playerList.get(indexCurrentPlayer).getRobot(), robotOrientation, 1);
            }
            case "MoveII" -> {
                moveRobot(playerList.get(indexCurrentPlayer).getRobot(), robotOrientation, 2);
            }
            case "MoveIII" -> {
                moveRobot(playerList.get(indexCurrentPlayer).getRobot(), robotOrientation, 3);
            }
            case "PowerUp" -> {
                playerList.get(indexCurrentPlayer).increaseEnergy(1);

            }
            case "TurnLeft" -> {
                changeOrientation(playerList.get(indexCurrentPlayer).getRobot(), "left");


            }
            case "TurnRight" -> {
                game.changeOrientation(SimpleAIModel.getClientModel().getClientGameModel().getPlayer().getRobot().setOrientation(), "right");


            }
            case "UTurn" -> {
                game.changeOrientation(.playerList.get(indexCurrentPlayer).getRobot(), "uturn");


                //TODO: Repair Move and Turning Queues.
                if (IS_LAZY) {
                    try {
                        Thread.sleep(80);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
            case "Spam" -> {
                Card spam = playerList.get(indexCurrentPlayer).getDeckRegister().getDeck().get(currentRegister);
                deckSpam.getDeck().add(spam);
                //TODO implement a method and call by the other cards (similar to drawBlind in Player -> return Card instead of ArrayList)

                //TODO test if-statement for consistency when deck is empty
                if (!(playerList.get(indexCurrentPlayer).getDeckProgramming().getDeck().size() > 0)) {
                    playerList.get(indexCurrentPlayer).shuffleDiscardIntoProgramming();
                }
                Card top = playerList.get(indexCurrentPlayer).getDeckProgramming().getTopCard();
                if (currentRegister == 0 && top.cardName.equals("Again")) {
                    playerList.get(indexCurrentPlayer).getDeckDiscard().getDeck().add(top);
                    playerList.get(indexCurrentPlayer).getDeckProgramming().getDeck().remove(top);
                    activateCardEffect("Spam");
                    break;
                }
                playerList.get(indexCurrentPlayer).getDeckRegister().getDeck().set(currentRegister, top);
                playerList.get(indexCurrentPlayer).getDeckProgramming().getDeck().remove(top);


            }
            case "Trojan" -> {
                game.drawSpam(SimpleAIModel.getClientModel().getClientGameModel().getPlayer(), 2);
                //TODO access current register and play top card from deckProgramming
                //     JSON Messages senden

            }
            case "Virus" -> {
                ArrayList<Player> playersWithinRadius = game.getPlayersInRadius();
                for (Player player : playersWithinRadius) {
                    game.drawSpam(player, 1);
                }
                //TODO access current register and play top card from deckProgramming
                //     JSON Messages senden
            }
            case "Worm" -> game.rebootRobot(SimpleAIModel.getClientModel().getClientGameModel().getPlayer());

        }
    }
}*/

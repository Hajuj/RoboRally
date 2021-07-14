package server;

import game.*;
import game.boardelements.Antenna;
import game.upgradecards.AdminPrivilege;
import game.upgradecards.MemorySwap;
import game.upgradecards.RearLaser;
import game.upgradecards.SpamBlocker;
import javafx.geometry.Point2D;
import json.JSONMessage;
import json.protocol.*;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

/**
 * @author Mohamad, Viktoria
 */
public class MessageHandler {
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_RESET = "\u001B[0m";
    private static final Logger logger = Logger.getLogger(MessageHandler.class.getName());

    /**
     * Wenn der Server ein Message HelloServer bekommt, schickt er ein Welcome-Message zu dem ClientModel mit dem ID
     *
     * @param server          The Server
     * @param clientHandler   The ClientHandler of the Server
     * @param helloServerBody The message body of the message which is of type  HelloServerBody
     */
    public void handleHelloServer(Server server, ClientHandler clientHandler, HelloServerBody helloServerBody) {
        logger.info(ANSI_CYAN + "HalloServer Message received." + ANSI_RESET);
        try {
            if (helloServerBody.getProtocol().equals(server.getProtocolVersion())) {
                logger.info("Protocol version test succeeded");

                // First, assign the client a playerID
                int actual_id = server.getClientsCounter();
                clientHandler.setPlayer_id(actual_id);

                //welcomeMessage with id
                JSONMessage welcomeMessage = new JSONMessage("Welcome", new WelcomeBody(actual_id));
                server.sendMessage(welcomeMessage, clientHandler.getWriter());

                // Create a Connection to this clientSocket
                Connection connection = new Connection(clientHandler.getClientSocket());
                server.getConnections().add(connection);
                connection.setPlayerID(actual_id);

                server.sendMessage(new JSONMessage("Alive", new AliveBody()), clientHandler.getWriter());

                Player player = new Player(actual_id);
                server.getWaitingPlayer().add(player);
                player.setAI(helloServerBody.isAI());

                //informieren den neuen Client über alle anderen clients im chat
                for (Player player1 : server.getWaitingPlayer()) {
                    if (player1.getPlayerID() != clientHandler.getPlayer_id()) {
                        JSONMessage jsonMessage1 = new JSONMessage("PlayerAdded", new PlayerAddedBody(player1.getPlayerID(), player1.getName(), player1.getFigure()));
                        server.sendMessage(jsonMessage1, clientHandler.getWriter());
                        JSONMessage jsonMessage2 = new JSONMessage("PlayerStatus", new PlayerStatusBody(player1.getPlayerID(), player1.isReady()));
                        server.sendMessage(jsonMessage2, clientHandler.getWriter());
                    }
                }

                if (server.getCurrentGame().isGameOn()) {
                    JSONMessage errorGameOnMessage = new JSONMessage("Error", new ErrorBody("gameOn"));
                    server.sendMessage(errorGameOnMessage, clientHandler.getWriter());
                }

                // Immer um eins erhöhen für den nächsten client
                server.setClientsCounter(actual_id + 1);

            } else {
                logger.info("Protocol version test failed");
                JSONMessage jsonMessage = new JSONMessage("Error", new ErrorBody("Protocol version test failed. Server hat Protokoll " + server.getProtocolVersion()));
                server.sendMessage(jsonMessage, clientHandler.getWriter());
                clientHandler.getClientSocket().close();
                logger.info("ClientModel connection terminated");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void handlePlayerValues(Server server, ClientHandler clientHandler, PlayerValuesBody playerValuesBody) {
        logger.info(ANSI_CYAN + "PlayerValues Message received." + ANSI_RESET);
        String username = playerValuesBody.getName();
        int figure = playerValuesBody.getFigure();
        boolean accept = true;

        for (Player player1 : server.getWaitingPlayer()) {
            if (player1.getPlayerID() != clientHandler.getPlayer_id()) {
                if (player1.getFigure() == figure) {
                    JSONMessage jsonMessage = new JSONMessage("Error", new ErrorBody("Figure is already taken"));
                    server.sendMessage(jsonMessage, server.getConnectionWithID(clientHandler.getPlayer_id()).getWriter());
                    accept = false;
                }
            }
        }

        if (accept) {
            Player player = server.getPlayerWithID(clientHandler.getPlayer_id());
            player.pickRobot(figure, username);

            //informiere alle anderen clients über den neu gekommen
            for (Player player1 : server.getWaitingPlayer()) {
                JSONMessage jsonMessage1 = new JSONMessage("PlayerAdded", new PlayerAddedBody(player.getPlayerID(), player.getName(), player.getFigure()));
                server.sendMessage(jsonMessage1, server.getConnectionWithID(player1.getPlayerID()).getWriter());
                JSONMessage jsonMessage2 = new JSONMessage("PlayerStatus", new PlayerStatusBody(player.getPlayerID(), false));
                server.sendMessage(jsonMessage2, server.getConnectionWithID(player1.getPlayerID()).getWriter());
            }
            logger.info("Alles gut, der Spieler mit ID " + clientHandler.getPlayer_id() + " heißt " + username + " und hat figur " + figure);
        }
    }


    public void handleSendChat(Server server, ClientHandler clientHandler, SendChatBody sendChatBody) {
        logger.info(ANSI_CYAN + "SendChat Message received." + ANSI_RESET);

        int playerID = clientHandler.getPlayer_id();
        String message = sendChatBody.getMessage();
        int to = sendChatBody.getTo();

        //Send Private message
        if (to != -1) {
            for (Connection connection : server.getConnections()) {
                if (connection.getPlayerID() == to) {
                    server.sendMessage(new JSONMessage("ReceivedChat", new ReceivedChatBody(message, playerID, true)), connection.getWriter());
                }
            }
        } else { //Send public message
            for (Connection connection : server.getConnections()) {
                if (connection.getPlayerID() != playerID) {
                    server.sendMessage(new JSONMessage("ReceivedChat", new ReceivedChatBody(message, playerID, false)), connection.getWriter());
                }
            }
        }
    }

    //Server receive this message
    public void handleAlive(Server server, ClientHandler clientHandler, AliveBody aliveBody) {
        try {
            //warten 5 sek
            Thread.sleep(5000);
            //senden ein neues Alive- Message zu Client
            server.sendMessage(new JSONMessage("Alive", new AliveBody()), clientHandler.getWriter());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void handleSetStatus(Server server, ClientHandler clientHandler, SetStatusBody setStatusBody) {
        logger.info(ANSI_CYAN + "SetStatus Message received." + ANSI_RESET);
        Player player = server.getPlayerWithID(clientHandler.getPlayer_id());
        boolean ready = setStatusBody.isReady();
        player.setReady(ready);

        for (Connection connection : server.getConnections()) {
            server.sendMessage(new JSONMessage("PlayerStatus", new PlayerStatusBody(player.getPlayerID(), player.isReady())), connection.getWriter());
        }
        if (ready) {
            server.getReadyPlayer().add(player);
            if (server.readyPlayerWithoutAI().size() == 1 && !player.isAI()) {
                JSONMessage selectMapMessage = new JSONMessage("SelectMap", new SelectMapBody(server.getCurrentGame().getAvailableMaps()));
                server.sendMessage(selectMapMessage, clientHandler.getWriter());
            }
            server.getCurrentGame().canStartTheGame();
        } else {
            if (player.getPlayerID() == server.getReadyPlayer().get(0).getPlayerID() && server.getReadyPlayer().size() != 1) {
                Player nextOne = server.getReadyPlayer().get(1);
                JSONMessage selectMapMessage = new JSONMessage("SelectMap", new SelectMapBody(server.getCurrentGame().getAvailableMaps()));
                server.sendMessage(selectMapMessage, server.getConnectionWithID(nextOne.getPlayerID()).getWriter());
            }
            server.getReadyPlayer().remove(player);
        }

        String isReady = setStatusBody.isReady() ? "ready" : "not ready";
        logger.info("Player " + player.getName() + " is " + isReady);
    }

    public void handleMapSelected(Server server, ClientHandler clientHandler, MapSelectedBody mapSelectedBody) throws IOException {
        logger.info(ANSI_CYAN + "MapSelected Message received." + ANSI_RESET);
        String mapName = mapSelectedBody.getMap();
        server.getCurrentGame().selectMap(mapName);
        //Inform all other players about the selected map
        for (Connection connection : server.getConnections()) {
            server.sendMessage(new JSONMessage("MapSelected", new MapSelectedBody(mapName)), connection.getWriter());
        }
        server.getCurrentGame().canStartTheGame();
    }

    public void handleSetStartingPoint(Server server, ClientHandler clientHandler, SetStartingPointBody bodyObject) {
        logger.info(ANSI_CYAN + "SetStartingPoint Message received." + ANSI_RESET);
        //TODO: hier etwas wie "Server speichert die Position von dem Player with ID playerID in der position x,y
        int playerID = clientHandler.getPlayer_id();
        int x = bodyObject.getX();
        int y = bodyObject.getY();

        if (playerID == server.getCurrentGame().getCurrentPlayer()) {
            if (server.getCurrentGame().valideStartingPoint(x, y)) {
                Player player = server.getPlayerWithID(playerID);
                player.setRobot(new Robot(Game.getRobotNames().get(player.getFigure()), x, y));

                //Set orientation of the robots depends on the Antenna
                for (Map.Entry<Point2D, Antenna> entry : server.getCurrentGame().getAntennaMap().entrySet()) {
                    if (entry.getValue().getOrientations().contains("left")) {
                        player.getRobot().setOrientation("left");
                    } else if (entry.getValue().getOrientations().contains("right")) {
                        player.getRobot().setOrientation("right");
                    }
                    break;
                }

                server.getCurrentGame().getStartingPointMap().put(player.getRobot(), new Point2D(x, y));

                //sage allen wo der Spieler mit playerID started
                JSONMessage startingPointTakenMessage = new JSONMessage("StartingPointTaken", new StartingPointTakenBody(x, y, playerID));
                server.getCurrentGame().sendToAllPlayers(startingPointTakenMessage);

                server.getCurrentGame().setCurrentPlayer(server.getCurrentGame().nextPlayerID());
                if (server.getCurrentGame().getCurrentPlayer() != -1) {
                    JSONMessage currentPlayerMessage = new JSONMessage("CurrentPlayer", new CurrentPlayerBody(server.getCurrentGame().getCurrentPlayer()));
                    server.getCurrentGame().sendToAllPlayers(currentPlayerMessage);
                } else { //All players have chose a starting point
                    server.getCurrentGame().setActivePhase(1);
                }
            }

        } else {
            JSONMessage errorNotYourTurn = new JSONMessage("Error", new ErrorBody("It is not your turn!"));
            server.sendMessage(errorNotYourTurn, clientHandler.getWriter());
        }
    }

    public void handleSelectedCard(Server server, ClientHandler clientHandler, SelectedCardBody selectedCardBody) {
        String card = selectedCardBody.getCard();
        logger.info(ANSI_CYAN + "SelectedCard Message received. " + card + ANSI_RESET);
        int register = selectedCardBody.getRegister() - 1;

        Player currentPlayer = server.getPlayerWithID(clientHandler.getPlayer_id());

        //Remove card from register deck
        if (card.equals("Null")) {
            Card currentCard = currentPlayer.getDeckRegister().getDeck().get(register);
            currentPlayer.getDeckHand().getDeck().add(currentCard);
            currentPlayer.getDeckRegister().getDeck().set(register, null);
            JSONMessage removeCard = new JSONMessage("CardSelected", new CardSelectedBody(currentPlayer.getPlayerID(), register, false));
            server.getCurrentGame().sendToAllPlayers(removeCard);
            logger.info(currentPlayer.getName() + " removed a card from the register!");
        } else {
            //Add card to register deck
            if (card.equals("Again") && register == 0) {
                Card againCard = currentPlayer.getDeckRegister().getDeck().get(0);
                currentPlayer.getDeckHand().getDeck().add(againCard);
                currentPlayer.getDeckRegister().getDeck().set(0, null);
                JSONMessage errorNotYourTurn = new JSONMessage("Error", new ErrorBody("Again card can not be in the first register!"));
                server.sendMessage(errorNotYourTurn, clientHandler.getWriter());
            } else {
                Card currentCard = currentPlayer.removeSelectedCard(card);
                currentPlayer.getDeckRegister().getDeck().set(register, currentCard);

                JSONMessage addCard = new JSONMessage("CardSelected", new CardSelectedBody(currentPlayer.getPlayerID(), register, true));
                server.getCurrentGame().sendToAllPlayers(addCard);
                logger.info(currentPlayer.getName() + " added a card to the register!");

                if (currentPlayer.isRegisterFull()) {
                    JSONMessage selectionFinished = new JSONMessage("SelectionFinished", new SelectionFinishedBody(currentPlayer.getPlayerID()));
                    server.getCurrentGame().sendToAllPlayers(selectionFinished);
                }

                synchronized (server.getCurrentGame().getGameTimer()) {
                    if (currentPlayer.isRegisterFull() && !server.getCurrentGame().getTimerOn()) {
                        server.getCurrentGame().getGameTimer().startTimer();
                    } else if (currentPlayer.isRegisterFull() && server.getCurrentGame().getTimerOn()) {
                        if (server.getCurrentGame().tooLateClients().size() == 0) {
                            server.getCurrentGame().getGameTimer().timerEnded();
                        }
                    }
                }
            }
        }
    }

    public void handlePlayCard(Server server, ClientHandler clientHandler, PlayCardBody playCardBody) {
        logger.info(ANSI_CYAN + "PlayCard Message received." + ANSI_RESET);
        String card = playCardBody.getCard();
        boolean canStartNewRound = true;
        if (card.equals("SpamBlocker")) {
            server.getCurrentGame().activatSpamCard(server.getPlayerWithID(clientHandler.getPlayer_id()));
        } else if (card.equals("MemorySwap")) {
            server.getCurrentGame().activatMemorySwapCard(server.getPlayerWithID(clientHandler.getPlayer_id()));
        } else {
            //When it's the turn of the player himself
            if (clientHandler.getPlayer_id() == server.getCurrentGame().getCurrentPlayer()) {
                if (card.equals(server.getPlayerWithID(clientHandler.getPlayer_id()).getDeckRegister().getDeck().get(server.getCurrentGame().getCurrentRegister()).getCardName())) {
                    for (Player player : server.getCurrentGame().getPlayerList()) {
                        if (player.getPlayerID() != clientHandler.getPlayer_id()) {
                            JSONMessage cardPlayed = new JSONMessage("CardPlayed", new CardPlayedBody(clientHandler.getPlayer_id(), card));
                            server.sendMessage(cardPlayed, server.getConnectionWithID(player.getPlayerID()).getWriter());
                            //TODO send also all Movement and Animations
                        }
                    }
                    server.getCurrentGame().activateCardEffect(card);

                    //inform everyone about next player
                    int nextPlayer = server.getCurrentGame().nextPlayerID();
                    if (nextPlayer != -1) {
                        server.getCurrentGame().setCurrentPlayer(nextPlayer);
                        JSONMessage jsonMessage = new JSONMessage("CurrentPlayer", new CurrentPlayerBody(nextPlayer));
                        server.getCurrentGame().sendToAllPlayers(jsonMessage);
                    } else {
                        //Get new register
                        server.getCurrentGame().activateBoardElements();
                        int newRegister = server.getCurrentGame().getCurrentRegister() + 1;
                        server.getCurrentGame().setCurrentRegister(newRegister);

                        if (server.getCurrentGame().getCurrentRegister() != 5) {
                            canStartNewRound = false;
                            server.getCurrentGame().getPlayerList().sort(server.getCurrentGame().getComparator());
                            int nextPlayer1 = server.getCurrentGame().getPlayerList().get(0).getPlayerID();
                            server.getCurrentGame().setCurrentPlayer(nextPlayer);
                            if (server.getCurrentGame().getDeadRobotsIDs().contains(nextPlayer1)) {
                                nextPlayer1 = server.getCurrentGame().nextPlayerID();
                                if (nextPlayer1 == -1) {
                                    canStartNewRound = true;
                                }
                            }
                            server.getCurrentGame().setCurrentPlayer(nextPlayer1);
                            if (!canStartNewRound) {
                                server.getCurrentGame().sendCurrentCards(server.getCurrentGame().getCurrentRegister());
                                server.getCurrentGame().informAboutCurrentPlayer();
                            }
                        }
                        if (canStartNewRound) {
                            //New Round
                            server.getCurrentGame().setRebootDirection();
                            server.getCurrentGame().setNewRoundCounter();
                            for (Player player : server.getCurrentGame().getPlayerList()) {
                                player.discardHandCards();
                                player.discardRegisterCards();
                            }
                            server.getCurrentGame().setActivePhaseOn(false);
                            server.getCurrentGame().setActivePhase(1);
                        }
                    }
                } else {
                    JSONMessage errorNotYourTurn = new JSONMessage("Error", new ErrorBody("Card " + card + " is not in your " + (server.getCurrentGame().getCurrentRegister() + 1) + " register!"));
                    server.sendMessage(errorNotYourTurn, clientHandler.getWriter());
                }
            } else {
                JSONMessage errorNotYourTurn = new JSONMessage("Error", new ErrorBody("It is not your turn!"));
                server.sendMessage(errorNotYourTurn, clientHandler.getWriter());
            }
        }
    }

    public void handleRebootDirection(Server server, ClientHandler clientHandler, RebootDirectionBody rebootDirectionBody) {
        logger.info(ANSI_CYAN + "RebootDirection Message received." + ANSI_RESET);
        String direction = rebootDirectionBody.getDirection();

        Player player = server.getPlayerWithID(clientHandler.getPlayer_id());

        //Add the player to the rebootDirection HashMap
        server.getCurrentGame().getRobotsRebootDirection().put(player, direction);
    }

    public void handleSelectedDamage(Server server, ClientHandler clientHandler, SelectedDamageBody selectedDamageBody) {
        logger.info(ANSI_CYAN + "SelectedDamage Message received." + ANSI_RESET);
        ArrayList<String> cards = selectedDamageBody.getCards();

        Player currentPlayer = server.getPlayerWithID(clientHandler.getPlayer_id());

        int leftCards = 0;
        String unavailableCard = "";

        //Count of the requested damage cards
        int countTrojan = Collections.frequency(cards, "Trojan");
        int countVirus = Collections.frequency(cards, "Virus");
        int countWorm = Collections.frequency(cards, "Worm");

        //Size of the damage decks
        int deckTrojanSize = server.getCurrentGame().getDeckTrojan().getDeck().size();
        int deckVirusSize = server.getCurrentGame().getDeckVirus().getDeck().size();
        int deckWormSize = server.getCurrentGame().getDeckWorm().getDeck().size();

        //Requested cards are more than the available cards
        if (deckTrojanSize < countTrojan) {
            leftCards = leftCards + (countTrojan - deckTrojanSize);
            for (int i = 0; i < deckTrojanSize; i++) {
                currentPlayer.getDeckDiscard().getDeck().add(server.getCurrentGame().getDeckTrojan().getTopCard());
                server.getCurrentGame().getDeckTrojan().removeTopCard();
            }
            unavailableCard += "Trojan ";
        } //Enough available cards
        else if (deckTrojanSize >= countTrojan) {
            for (int i = 0; i < countTrojan; i++) {
                currentPlayer.getDeckDiscard().getDeck().add(server.getCurrentGame().getDeckTrojan().getTopCard());
                server.getCurrentGame().getDeckTrojan().removeTopCard();
            }
        }

        //Requested cards are more than the available cards
        if (deckVirusSize < countVirus) {
            leftCards = leftCards + (countVirus - deckVirusSize);
            for (int i = 0; i < deckVirusSize; i++) {
                currentPlayer.getDeckDiscard().getDeck().add(server.getCurrentGame().getDeckVirus().getTopCard());
                server.getCurrentGame().getDeckVirus().removeTopCard();
            }
            unavailableCard += "Virus ";
        } //Enough available cards
        else if (deckVirusSize >= countVirus) {
            for (int i = 0; i < countVirus; i++) {
                currentPlayer.getDeckDiscard().getDeck().add(server.getCurrentGame().getDeckVirus().getTopCard());
                server.getCurrentGame().getDeckVirus().removeTopCard();
            }
        }

        //Requested cards are more than the available cards
        if (deckWormSize < countWorm) {
            leftCards = leftCards + (countWorm - deckWormSize);
            for (int i = 0; i < deckWormSize; i++) {
                currentPlayer.getDeckDiscard().getDeck().add(server.getCurrentGame().getDeckWorm().getTopCard());
                server.getCurrentGame().getDeckWorm().removeTopCard();
            }
            unavailableCard += "Worm ";
        } //Enough available cards
        else if (deckWormSize >= countWorm) {
            for (int i = 0; i < countWorm; i++) {
                currentPlayer.getDeckDiscard().getDeck().add(server.getCurrentGame().getDeckWorm().getTopCard());
                server.getCurrentGame().getDeckWorm().removeTopCard();
            }
        }

        //When there is no enough cards available -> send PickDamage
        if (leftCards > 0) {
            JSONMessage jsonMessage = new JSONMessage("Error", new ErrorBody("The cards: " + unavailableCard + " are unavailable!"));
            server.sendMessage(jsonMessage, server.getConnectionWithID(currentPlayer.getPlayerID()).getWriter());

            if (deckTrojanSize + deckWormSize + deckVirusSize != 0) {
                JSONMessage jsonMessage1 = new JSONMessage("PickDamage", new PickDamageBody(leftCards));
                server.sendMessage(jsonMessage1, server.getConnectionWithID(currentPlayer.getPlayerID()).getWriter());
            }
        } else {
            JSONMessage jsonMessage = new JSONMessage("Error", new ErrorBody("All damage cards are picked!"));
            server.sendMessage(jsonMessage, server.getConnectionWithID(currentPlayer.getPlayerID()).getWriter());
        }
    }

    public void handleBuyUpgrade(Server server, ClientHandler clientHandler, BuyUpgradeBody buyUpgradeBody) {
        logger.info(ANSI_CYAN + "BuyUpgrade Message received." + ANSI_RESET);
        String cardName = buyUpgradeBody.getCard();
        Player player = server.getPlayerWithID(clientHandler.getPlayer_id());
        boolean allowToBuy = true;
        String errorMessage = "";
        // ob der dran ist
        if (server.getCurrentGame().getCurrentPlayer() != clientHandler.getPlayer_id()) {
            allowToBuy = false;
            errorMessage = "Its not your turn!";
        }
        // ob er noch nicht 3 und 3 karten hat
        if (cardName.equals("Null")) {
            allowToBuy = false;
            errorMessage = "Du hast nichts gekauft, okay!";
        } else {
            if (server.getCurrentGame().isPermanent(cardName) && player.getInstalledPermanentUpgrades().size() == 3) {
                allowToBuy = false;
                errorMessage = "Du hast schon 3 permanenten Karten!";
            } else if ((!server.getCurrentGame().isPermanent(cardName)) && player.getTemporaryUpgrades().size() == 3) {
                allowToBuy = false;
                errorMessage = "Du hast schon 3 temporären Karten!";
            }
        }

        int energyCost = server.getCurrentGame().getUpgradeCost(cardName);
        if (player.getEnergy() < energyCost) {
            allowToBuy = false;
            errorMessage = "Du hast nicht genug Energy Cubes!";
        }

        //sage allen wo der Spieler mit playerID started
        if (buyUpgradeBody.isBuying() && allowToBuy) {
            player.increaseEnergy(-energyCost);
            JSONMessage cardBoughtMessage = new JSONMessage("UpgradeBought", new UpgradeBoughtBody(clientHandler.getPlayer_id(), buyUpgradeBody.getCard()));
            server.getCurrentGame().sendToAllPlayers(cardBoughtMessage);

            if (server.getCurrentGame().isPermanent(cardName)) {
                if (cardName.equals("AdminPrivilege")) {
                    player.getInstalledPermanentUpgrades().add(new AdminPrivilege());
                    player.setNumberOfAdminPrivilege(player.getNumberOfAdminPrivilege() + 1);
                } else {
                    player.getInstalledPermanentUpgrades().add(new RearLaser());
                    server.getCurrentGame().getRearLasers().add(player);
                }
            } else {
                if (cardName.equals("MemorySwap")) {
                    player.getTemporaryUpgrades().add(new MemorySwap());
                } else {
                    player.getTemporaryUpgrades().add(new SpamBlocker());
                }
            }
        }

        server.getCurrentGame().setCurrentPlayer(server.getCurrentGame().nextPlayerID());
        System.out.println("player id " + server.getCurrentGame().getCurrentPlayer());
        if (server.getCurrentGame().getCurrentPlayer() != -1) {
            JSONMessage currentPlayerMessage = new JSONMessage("CurrentPlayer", new CurrentPlayerBody(server.getCurrentGame().getCurrentPlayer()));
            server.getCurrentGame().sendToAllPlayers(currentPlayerMessage);
        } else { //All players have chose a starting point
            System.out.println("new phase");
            server.getCurrentGame().setActivePhaseOn(false);
            server.getCurrentGame().setActivePhase(2);
            server.getCurrentGame().setCurrentRegister(0);
        }
    }


    public void handleChooseRegister(Server server, ClientHandler clientHandler, ChooseRegisterBody chooseRegisterBody) {
        logger.info(ANSI_CYAN + "ChooseRegister Message received." + ANSI_RESET);
        //schauen ob dieser spieler echt AdminPrivilege hat
        Player player = server.getPlayerWithID(clientHandler.getPlayer_id());
        // if (player.checkAdmin()) {
        int register = chooseRegisterBody.getRegister();
        server.getCurrentGame().getAdminPriorityMap().put(register, player);
        JSONMessage adminMessage = new JSONMessage("RegisterChosen", new RegisterChosenBody(player.getPlayerID(), chooseRegisterBody.getRegister()));
        server.getCurrentGame().sendToAllPlayers(adminMessage);
        //  }


    }

    public void handleReturnCards(Server server, ClientHandler clientHandler, ReturnCardsBody returnCardsBody) {
        logger.info(ANSI_CYAN + "ReturnCards Message received." + ANSI_RESET);
        ArrayList<String> returnedCards = returnCardsBody.getCards();

        Player player = server.getPlayerWithID(clientHandler.getPlayer_id());

        //Remove the returned cards from the deck Hand of the player
        for (String card : returnedCards) {
            for (Card card1 : player.getDeckHand().getDeck()) {
                if (card1.getCardName().equals(card)) {
                    player.getDeckHand().getDeck().remove(card1);
                    player.getDeckProgramming().getDeck().add(card1);
                    break;
                }
            }
        }
        JSONMessage jsonMessage = new JSONMessage("YourCards", new YourCardsBody(player.getDeckHand().toArrayList()));
        server.sendMessage(jsonMessage, server.getConnectionWithID(player.getPlayerID()).getWriter());
    }

}

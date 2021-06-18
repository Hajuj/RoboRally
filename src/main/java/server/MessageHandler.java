package server;

import client.model.ClientModel;
import game.*;
import javafx.geometry.Point2D;
import json.JSONMessage;
import json.protocol.*;
import org.apache.log4j.Logger;

import java.io.IOException;

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
            if (server.getReadyPlayer().size() == 1) {
                JSONMessage selectMapMessage = new JSONMessage("SelectMap", new SelectMapBody(server.getCurrentGame().getAvailableMaps()));
                server.sendMessage(selectMapMessage, clientHandler.getWriter());
            }
            if (server.canStartTheGame()) {
                try {
                    server.getCurrentGame().setGameOn(true);
                    server.getCurrentGame().startGame(server.getReadyPlayer());
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                logger.info("I CAN START THE GAME");
            }

        } else {
            if (player.getPlayerID() == server.getReadyPlayer().get(0).getPlayerID() && server.getReadyPlayer().size() != 1) {
                Player nextOne = server.getReadyPlayer().get(1);
                JSONMessage selectMapMessage = new JSONMessage("SelectMap", new SelectMapBody(server.getCurrentGame().getAvailableMaps()));
                server.sendMessage(selectMapMessage, server.getConnectionWithID(nextOne.getPlayerID()).getWriter());
            }
            server.getReadyPlayer().remove(player);
        }


        String isReady = setStatusBody.isReady() ? "ready" : "not ready";
        logger.info("The player " + player.getName() + " is " + isReady);
    }

    public void handleMapSelected(Server server, ClientHandler clientHandler, MapSelectedBody mapSelectedBody) throws IOException {
        logger.info(ANSI_CYAN + "MapSelected Message received." + ANSI_RESET);
        //TODO: SEND NOT ZU DEN SPIELER
        for (Connection connection : server.getConnections()) {
            server.sendMessage(new JSONMessage("MapSelected", new MapSelectedBody(mapSelectedBody.getMap())), connection.getWriter());
        }
        server.getCurrentGame().selectMap(mapSelectedBody.getMap());
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
                server.getCurrentGame().getStartingPointMap().put(player.getRobot(), new Point2D(x, y));

                //sage allen wo der Spieler mit playerID started
                JSONMessage startingPointTakenMessage = new JSONMessage("StartingPointTaken", new StartingPointTakenBody(x, y, playerID));
                server.getCurrentGame().sendToAllPlayers(startingPointTakenMessage);

                server.getCurrentGame().setCurrentPlayer(server.getCurrentGame().nextPlayerID());
                if (server.getCurrentGame().getCurrentPlayer() != -1) {
                    JSONMessage currentPlayerMessage = new JSONMessage("CurrentPlayer", new CurrentPlayerBody(server.getCurrentGame().getCurrentPlayer()));
                    server.getCurrentGame().sendToAllPlayers(currentPlayerMessage);
                } else {
                    server.getCurrentGame().setActivePhase(2);
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
        System.out.println("HEY I GOT SELECTED");

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
                System.out.println(currentCard);

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
                System.out.println("NEXT PLAYER IS " + nextPlayer);
                if (nextPlayer != -1) {
                    server.getCurrentGame().setCurrentPlayer(nextPlayer);
                    JSONMessage jsonMessage = new JSONMessage("CurrentPlayer", new CurrentPlayerBody(nextPlayer));
                    server.getCurrentGame().sendToAllPlayers(jsonMessage);
                } else {
                    //Get new register
                    System.out.println("Dead players" + server.getCurrentGame().getDeadRobotsIDs());
                    server.getCurrentGame().activateBoardElements();
                    int newRegister = server.getCurrentGame().getCurrentRegister() + 1;
                    server.getCurrentGame().setCurrentRegister(newRegister);

                    if (server.getCurrentGame().getCurrentRegister() != 5) {
                        canStartNewRound = false;
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
                        server.getCurrentGame().getDeadRobotsIDs().clear();
                        System.out.println("Dead players" + server.getCurrentGame().getDeadRobotsIDs());
                        server.getCurrentGame().setNewRoundCounter();
                        for (Player player : server.getCurrentGame().getPlayerList()) {
                            player.discardHandCards();
                            player.discardRegisterCards();
                            //TODO test if the cards get really discarded
                        }
                        server.getCurrentGame().setActivePhaseOn(false);
                        server.getCurrentGame().setActivePhase(2);
                        server.getCurrentGame().setCurrentRegister(0);
                        //TODO when does the game stops? -> Ilja
                    }
                }
                logger.info("IM PLAYING MY CARD LOL");
            } else {
                JSONMessage errorNotYourTurn = new JSONMessage("Error", new ErrorBody("Card " + card + " is not in your " + (server.getCurrentGame().getCurrentRegister() + 1) + " register!"));
                server.sendMessage(errorNotYourTurn, clientHandler.getWriter());
            }
        } else {
            JSONMessage errorNotYourTurn = new JSONMessage("Error", new ErrorBody("It is not your turn!"));
            server.sendMessage(errorNotYourTurn, clientHandler.getWriter());
        }
    }

    public void handleRebootDirection(Server server, ClientHandler clientHandler, RebootDirectionBody rebootDirectionBody) {

    }

}
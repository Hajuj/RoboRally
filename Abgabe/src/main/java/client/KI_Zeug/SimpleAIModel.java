package client.KI_Zeug;

import client.model.ClientModel;
import game.Game;
import game.boardelements.StartPoint;
import javafx.geometry.Point2D;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * The type Simple ai model.
 *
 * @author Viktoria
 */
public class SimpleAIModel {
    private static SimpleAIModel instance;
    private static ClientModel clientModel = ClientModel.getInstance();
    private final boolean IS_LAZY = true;

    private static Game game;

    private static String serverIP = "127.0.0.1";
    private static int serverPort = 500;

    private boolean hasPlayerValues = false;
    private int figureCounter = 0;
    private int startingPointCounter = 0;

    private ArrayList<CardsThread> myBabyList = new ArrayList<>();
    private ArrayList<String> myHandCards = new ArrayList<>();

    private static HashMap<Integer, String> cardsInRegister = new HashMap<Integer, String>();

    private String name;

    private SimpleAIModel() {
    }

    /**
     * AI Instance because it's singleton.
     *
     * @return it return the instance of the AI.
     */
    public static SimpleAIModel getInstance() {
        if(instance == null) {
            instance = new SimpleAIModel();
        }
        return instance;
    }


    /**
     * AI Main method, with the serverPort and IP as arguments.
     *
     * @param args the arguments that the server takes.
     */
    public static void main(String[] args) {
        if (args.length == 0)
            throw new IllegalArgumentException("No arguments provided. Flags: -h IP -p Port.");
        if (args.length == 1) {
            if (args[0].charAt(0) == '-')
                throw new IllegalArgumentException("Expected argument after: " + args[0]);
            else
                throw new IllegalArgumentException("Illegal Argument: " + args[0]);

        }
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            String nextArg;
            if (i + 1 == args.length || args[i + 1].charAt(0) == '-') {
                throw new IllegalArgumentException("Expected argument after: " + arg);
            } else {
                nextArg = args[i + 1];
            }
            switch (arg) {
                case "-h" -> {
                    serverIP = nextArg;
                    i++;
                }
                case "-p" -> {
                    int port = Integer.parseInt(nextArg);
                    if (port < 500 || port > 65535)
                        throw new IllegalArgumentException("Port number " + port + " invalid");
                    serverPort = port;
                    i++;
                }
                default -> throw new IllegalArgumentException("Illegal Argument: " + arg);
            }
        }
        clientModel.setMessageHandler(new MessageHandlerAI());
        clientModel.setAI(true);
        for(int i = 0; i < 5; i++) {
            cardsInRegister.put(i, null);
        }
        clientModel.connectClient(instance.serverIP, instance.serverPort);
        //clientModel.connectClient("sep21.dbs.ifi.lmu.de", 52021);
    }

    /**
     * Chooses the free figure
     */
    public void chooseRobotRoutine() {
        if(figureCounter != 5) {
            figureCounter++;
            clientModel.sendUsernameAndRobot("SimpleAIModel", figureCounter);
        } else {
            System.exit(0);
        }
    }

    /**
     * Sets a starting point
     */
    public void setStartingPointRoutine() {
        int i = 0;
        if(startingPointCounter != clientModel.getClientGameModel().getStartPointMap().size() - 1) {
            for(Map.Entry<Point2D, StartPoint> entry : clientModel.getClientGameModel().getStartPointMap().entrySet()) {
                if(i == startingPointCounter) {
                    int x = (int) entry.getKey().getX();
                    int y = (int) entry.getKey().getY();
                    clientModel.getClientGameModel().sendStartingPoint(x, y);
                    break;
                } else {
                    i++;
                }
            }
        } else {
            System.exit(0);
        }
    }

    /**
     * Check doubl and zeros boolean.
     *
     * @param reg0 the reg 0
     * @param reg1 the reg 1
     * @param reg2 the reg 2
     * @param reg3 the reg 3
     * @param reg4 the reg 4
     * @return the boolean
     */
    public static boolean checkDoublAndZeros(int reg0, int reg1, int reg2, int reg3, int reg4) {
        if(reg0 == 0 || reg1 == 0 || reg2 == 0 || reg3 == 0 || reg4 == 0) return true;
        if((reg0 == reg1) || reg0 == reg2 || reg0 == reg3 || reg0 == reg4) return true;
        if(reg1 == reg2 || reg1 == reg3 || reg1 == reg4) return true;
        if(reg2 == reg3 || reg2 == reg4) return true;
        if(reg3 == reg4) return true;
        return false;
    }

    /**
     * Generate arrays array list.
     *
     * @return the array list
     */
    public static ArrayList<Integer> generateArrays() {
        ArrayList<Integer> nubArr = new ArrayList<>();
        for(int i = 11111; i < 100000; i++) {
            int reg0 = i / 10000;
            int reg1 = (i - 10000 * reg0) / 1000;
            int reg2 = (i - 10000 * reg0 - 1000 * reg1) / 100;
            int reg3 = (i - 10000 * reg0 - 1000 * reg1 - 100 * reg2) / 10;
            int reg4 = (i - 10000 * reg0 - 1000 * reg1 - 100 * reg2 - 10 * reg3);
            if(!checkDoublAndZeros(reg0, reg1, reg2, reg3, reg4)) {
                nubArr.add(i);
            }
        }
        return nubArr;
    }

    /**
     * Create threads.
     */
    public void createThreads() {
        ArrayList<Integer> numArr = generateArrays();
        for(int i = 0; i < numArr.size(); i++) {
            int cards = numArr.get(i);
            CardsThread cardsThread = new CardsThread(this, cards);
            myBabyList.add(cardsThread);
            cardsThread.start();
        }
    }


    /**
     * Choode cards with threads routine.
     */
    public void choodeCardsWithThreadsRoutine() {
        createThreads();
    }

    /**
     * Choose random cards from the hand to the register
     */
    public void chooseCardsRoutine() {
        for(int j = 5; j < 9; j++) {
            if(!clientModel.getClientGameModel().getCardsInHand().get(j).equals("Again")) {
                clientModel.getClientGameModel().sendSelectedCards(0, clientModel.getClientGameModel().getCardsInHand().get(j));
                cardsInRegister.replace(0, clientModel.getClientGameModel().getCardsInHand().get(j));
                break;
            }
        }


        for(int i = 0; i < 4; i++) {
            String cardName = clientModel.getClientGameModel().getCardsInHand().get(i);
            clientModel.getClientGameModel().sendSelectedCards(i + 1, cardName);
            cardsInRegister.replace(i + 1, cardName);
        }
    }


    /**
     * Play automatically the card in the register
     *
     * @param currentRegister the current register
     */
    public void playCardRoutine(int currentRegister) {
        if(IS_LAZY) {
            try {
                Thread.sleep(1000);
            } catch(InterruptedException e) {
                e.printStackTrace();
            }
        }
        clientModel.getClientGameModel().sendPlayCard(cardsInRegister.get(currentRegister));
    }


    /**
     * Picks random damage.
     */
    public void pickDamageRoutine() {
        Random random = new Random();
        ArrayList<String> pickedDamage = new ArrayList();
        ArrayList<String> availableDamage = new ArrayList<>();
        availableDamage.add("Virus");
        availableDamage.add("Trojan");
        availableDamage.add("Worm");
        for(int i = 0; i < clientModel.getClientGameModel().getDamageCount(); i++) {
            int cardInx = random.nextInt(3);
            pickedDamage.add(availableDamage.get(cardInx));
        }
        clientModel.getClientGameModel().sendSelectedDamage(pickedDamage);
    }

    /**
     * Gets my hand cards.
     *
     * @return the my hand cards
     */
    public ArrayList<String> getMyHandCards() {
        return myHandCards;
    }

    /**
     * Sets my hand cards.
     *
     * @param myHandCards the my hand cards
     */
    public void setMyHandCards(ArrayList<String> myHandCards) {
        this.myHandCards = myHandCards;
    }

    /**
     * Gets cards in register.
     *
     * @return the cards in register
     */
    public static HashMap<Integer, String> getCardsInRegister() {
        return cardsInRegister;
    }

    /**
     * Sets starting point counter.
     *
     * @param startingPointCounter the starting point counter
     */
    public void setStartingPointCounter(int startingPointCounter) {
        this.startingPointCounter = startingPointCounter;
    }

    /**
     * Gets starting point counter.
     *
     * @return the starting point counter
     */
    public int getStartingPointCounter() {
        return startingPointCounter;
    }

    /**
     * Is has player values boolean.
     *
     * @return the boolean
     */
    public boolean isHasPlayerValues() {
        return hasPlayerValues;
    }

    /**
     * Sets has player values.
     *
     * @param hasPlayerValues the has player values
     */
    public void setHasPlayerValues(boolean hasPlayerValues) {
        this.hasPlayerValues = hasPlayerValues;
    }


    /**
     * Gets figure counter.
     *
     * @return the figure counter
     */
    public int getFigureCounter() {
        return figureCounter;
    }

    /**
     * Gets my baby list.
     *
     * @return the my baby list
     */
    public ArrayList<CardsThread> getMyBabyList() {
        return myBabyList;
    }

    /**
     * Sets my baby list.
     *
     * @param myBabyList the my baby list
     */
    public void setMyBabyList(ArrayList<CardsThread> myBabyList) {
        this.myBabyList = myBabyList;
    }

    /**
     * Sets figure counter.
     *
     * @param figureCounter the figure counter
     */
    public void setFigureCounter(int figureCounter) {
        this.figureCounter = figureCounter;
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets name.
     *
     * @param name the name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets client model.
     *
     * @return the client model
     */
    public static ClientModel getClientModel() {
        return clientModel;
    }

    /**
     * Sets client model.
     *
     * @param clientModel the client model
     */
    public static void setClientModel(ClientModel clientModel) {
        SimpleAIModel.clientModel = clientModel;
    }

    /**
     * Gets game.
     *
     * @return the game
     */
    public Game getGame() {
        return game;
    }

    /**
     * Sets game.
     *
     * @param game the game
     */
    public void setGame(Game game) {
        this.game = game;
    }
}

package client.viewModel;

import client.model.ClientGameModel;
import client.model.ClientModel;

import game.programmingcards.Again;
import javafx.animation.KeyFrame;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Lilas
 */
public class GameViewModel implements Initializable, PropertyChangeListener {


    @FXML
    public ImageView card_0;
    @FXML
    public ImageView card_1;
    @FXML
    public ImageView card_2;
    @FXML
    public ImageView card_3;
    @FXML
    public ImageView card_4;
    @FXML
    public ImageView card_5;
    @FXML
    public ImageView card_6;
    @FXML
    public ImageView card_7;
    @FXML
    public ImageView card_8;


    @FXML
    public Button dummesButton;

    @FXML
    public BorderPane pane;

    @FXML
    public BorderPane chat;

    @FXML
    public ImageView reg_0;
    @FXML
    public ImageView reg_1;
    @FXML
    public ImageView reg_2;
    @FXML
    public ImageView reg_3;
    @FXML
    public ImageView reg_4;
    @FXML
    public ImageView yourRobot;
    @FXML
    public AnchorPane paneA;
    @FXML
    public Text Playerinfo;
    @FXML
    public Text popUpText = null;


    public ClientModel model = ClientModel.getInstance();
    public ClientGameModel clientGameModel = ClientGameModel.getInstance();
    public String cardName;
    public String register;

    public HashMap<Integer, String> regToCard = new HashMap<>();
    public BorderPane right_Side;
    public ImageView readyButton;
    public ImageView chatON;
    public TextArea readyDisplay;
    public ImageView imageView;

    public ImageView upgradeCard_1;
    public ImageView upgradeCard_2;
    public ImageView upgradeCard_3;
    public ImageView upgradeCard_4;
    public ImageView upgradeCard_5;
    public ImageView upgradeCard_6;
    public HBox Timer;
    public Label timerLable;
    private final Integer startTime=30;
    private Integer seconds = startTime;


    ObservableList<ImageView> cards;
    ObservableList<ImageView> upgradeCards;
    ObservableList<ImageView> registers;
    Dragboard dbImage = null;
    ImageView returnSource;
    int count = 0;

    public BooleanProperty laserShootProperty;

    public BooleanProperty gameOn = new SimpleBooleanProperty(false);

    /**
     * Sets the values of the buttons,properties and panes.
     *
     * @param url
     * @param resourceBundle
     */

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Timer.setVisible ( false );
        pane.setMinSize(0, 0);
        imageView.fitHeightProperty().bind(pane.heightProperty());
        imageView.fitWidthProperty().bind(pane.widthProperty());
        right_Side.setCenter(null);
        model.addPropertyChangeListener(this);
        clientGameModel.addPropertyChangeListener(this);
        dummesButton.setDisable(true);
        dummesButton.setText(Integer.toString(1));
        readyDisplay.setText(model.getPlayersStatus());
        readyDisplay.setEditable(false);

        model.refreshPlayerStatus(model.getClientGameModel().getPlayer().getPlayerID(), false);
        model.setDoRefreshPlayerDisplay(false);
        if(model.getClientGameModel().getPlayer().getFigure() == -1) {
            readyButton.setVisible(false);
        }


        registers = FXCollections.observableArrayList(reg_0, reg_1, reg_2, reg_3, reg_4);
        Platform.runLater(() -> {
            yourRobot.setImage(yourRobot());
            yourRobot.setId(String.valueOf(clientGameModel.getPlayer().getFigure()));
        });
    }

    public void showPopup(String popText) throws IOException, InterruptedException {
        Text text = new Text ( popText );

        StackPane root = new StackPane ( );
        root.getChildren ( ).addAll ( text );
        root.setStyle ( "-fx-background-image: url('/images/Gui/phase.gif');" );
        Scene scene = new Scene ( root, 600, 350 );
        Stage not = new Stage ( );
        not.setTitle ( "Player Notification" );
        not.setScene ( scene );
        ScaleTransition scaleTransition = new ScaleTransition ( );
        scaleTransition.setDuration ( Duration.seconds ( 2 ) );
        scaleTransition.setNode ( text );
        scaleTransition.setByY ( 1.0 );
        scaleTransition.setByX ( 1.0 );
        scaleTransition.setCycleCount ( -1 );
        scaleTransition.setAutoReverse ( true );
        scaleTransition.play ( );
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor ( );
        executor.submit ( () -> Platform.runLater ( not::show ) );
        executor.schedule (
                () -> Platform.runLater ( () -> not.close ())
                , 2
                , TimeUnit.SECONDS );

    }

    /**
     * Your robot image.
     * Gets the players values and sets its robot.Figure gets called.
     *
     * @return the image
     */

    public Image yourRobot() {

        int figure = clientGameModel.getPlayer().getFigure();
        Image image;
        if (figure != -1) {
            image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/Robots/robot" + figure + ".png")));
        } else {
            image = null;
        }
        return image;
    }

    /**
     * Load image image.
     * Loads the required image.
     *
     * @param cardName the card name
     * @return the image
     * @throws FileNotFoundException the file not found exception
     */

    public Image loadImage(String cardName) throws FileNotFoundException {
        Image image;
        if (cardName.equals("ready")) {
            image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/Gui/steampunk-on.png")));

        } else if (cardName.equals("notReady")) {
            image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/Gui/steampunk-off.png")));

        } else if (cardName.equals("chatON")) {
            image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/Gui/chatON.png")));

        } else if (cardName.equals("chatOff")) {
            image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/Gui/chatOff.png")));
        } else {
            image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/ProgrammingCards/" + cardName + ".png")));
        }
        return image;

    }

    /**
     * Handles the cards put in the register.
     *
     * @param event the event
     */

    public void handle(MouseEvent event) {
        ImageView source = (ImageView) event.getSource();
        returnSource = source;
        if (source.getId().equals(reg_0.getId()) || source.getId().equals(reg_1.getId())
                || source.getId().equals(reg_2.getId()) || source.getId().equals(reg_3.getId()) || source.getId().equals(reg_4.getId())) {
            this.cardName = "Null";
            int reg = Integer.parseInt(String.valueOf(source.getId().charAt(4)));
            regToCard.replace(reg, null);
            collectingCards();
        } else {
            this.cardName = source.getId();
        }
        event.consume();
        handleSource(source);
    }

    /**
     * Handles the draged cards.
     *
     * @param event the event
     */

    public void handleTarget(DragEvent event) {
        if (event.getDragboard().hasImage()) {
            event.acceptTransferModes(TransferMode.MOVE);
        }
    }

    /**
     * Handles the source of the draged image.
     *
     * @param source the source
     */

    public void handleSource(ImageView source) {
        Dragboard db = source.startDragAndDrop(TransferMode.MOVE);
        dbImage = source.startDragAndDrop(TransferMode.MOVE);
        ClipboardContent content = new ClipboardContent();
        content.putImage(source.getImage());
        source.setImage(null);
        db.setContent(content);
        dbImage.setContent(content);

    }

    /**
     * Handles the dropped image.
     * This image is the card.
     *
     * @param dragEvent the drag event
     */

    public void handledropped(DragEvent dragEvent) {
        Image image = dragEvent.getDragboard ( ).getImage ( );
        ImageView target = (ImageView) dragEvent.getTarget ( );
        //TODO 2 Karten auf einem Register
        //TODO TargetId nehemn und überprüfen
        this.register = target.getId ( );
        if (target.getImage ( ) != null) {

            returnSource.setImage ( target.getImage ( ) );
            target.setImage ( dbImage.getImage ( ) );

        } if (target.getId().equals(card_0.getId()) || target.getId().equals(card_1.getId())
                || target.getId().equals(card_3.getId()) || target.getId().equals(card_4.getId()) || target.getId().equals(card_5.getId())
                ||target.getId().equals(card_6.getId())|| target.getId().equals(card_7.getId())|| target.getId().equals(card_8.getId())){
            handlewithdraw ( target,image );
        }else {

            handlewithdraw ( target, image );
            collectingCards ( );
        }
    }

         /*else if(dragEvent.getGestureTarget ().equals ( card_0 )||dragEvent.getGestureTarget ().equals ( card_1 )||
                dragEvent.getGestureTarget ().equals ( card_2 )||dragEvent.getGestureTarget ().equals ( card_3 )||dragEvent.getGestureTarget ().equals ( card_4 )||
                dragEvent.getGestureTarget ().equals ( card_5 )||dragEvent.getGestureTarget ().equals ( card_6 )||dragEvent.getGestureTarget ().equals ( card_7 )||
                dragEvent.getGestureTarget ().equals ( card_8 )){
            handlewithdraw ( target, image );
            */




    public void handlewithdraw(ImageView target, Image image) {
        target.setImage(image);

    }

    /**
     * Sends the selected cards to be played to the server
     */

    public void collectingCards() {
        int registerNum = Integer.parseInt(String.valueOf(this.register.charAt(4)));
        if (!cardName.equals("Null")) {
            if (clientGameModel.getCardsInHand ().get(Integer.parseInt ( cardName )).equals ( "Again" ) && registerNum==0) {
                registers.get ( 0 ).setImage ( null );
                try {
                    returnSource.setImage ( loadImage ( "Again" ) );
                } catch (FileNotFoundException e) {
                    e.printStackTrace ( );
                }
            }else{
                    regToCard.replace ( registerNum, clientGameModel.getCardsInHand ( ).get ( Integer.parseInt ( cardName ) ) );
                    clientGameModel.sendSelectedCards ( registerNum, clientGameModel.getCardsInHand ( ).get ( Integer.parseInt ( cardName ) ) );
                }
        }else {
            clientGameModel.sendSelectedCards(registerNum, "Null");
        }
    }

    /**
     * Puts the selected card in the selected register
     */

    public void playCard() {
        int currentRegister = clientGameModel.getValueActualRegister();
        //TODO:  java.lang.reflect.InvocationTargetException?
        try {
            //TODO Lilas hier ist ein Nullpointerexception
            String card = regToCard.get(currentRegister);
            clientGameModel.sendPlayCard(card);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets card name.
     *
     * @param cardName the card name
     */

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    /**
     * Gets card name.
     *
     * @return the card name
     */

    public String getCardName() {
        return cardName;
    }

    /**
     * Drag exited.
     *
     * @param dragEvent the drag event
     */

    public void dragExited(DragEvent dragEvent) {
        if (dragEvent.getTarget() == null || dragEvent.getGestureTarget() == null) {
            returnSource.setImage(dbImage.getImage());
        }
    }


    /**
     * Gets next available register.
     *
     * @return the next available register
     */

    public int getNextAvailableRegister () {
        int regNumber = 0;
        for (ImageView register : registers) {
            if (register.getImage() == null) {
                regNumber = Integer.parseInt(String.valueOf(register.getId().charAt(4)));
                break;
            }
        }
        return regNumber;
    }

    /**
     * disables all registers
     */
    private void disableAllRegisters(boolean b) {
        for (ImageView register:registers) {
            register.setDisable(b);
        }
    }

    /**
     * disables the hand
     */
    private void disableHand(boolean b) {
        for (ImageView hand : cards) {
            hand.setDisable(b);
        }
    }

    /**
     * Sets damage count.
     */

    public void setCount() {
        this.count = clientGameModel.getDamageCount();
    }

    /**
     * Loads the window for avaible maps
     */
    public void showMaps() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/AvailableMaps.fxml"));
        Parent root1 = fxmlLoader.load();
        Stage newStage = new Stage();
        newStage.setResizable ( false );
        newStage.setTitle("Available Maps");
        newStage.setScene(new Scene(root1));
        newStage.show();
    }

    /**
     * sends the ready or not ready stauts of a player
     */
    public void sendReadyStatus(MouseEvent mouseEvent) throws FileNotFoundException {
        if (readyButton.getId ().equals ( "readyButton" )) {
            readyButton.setImage ( loadImage ( "ready" ) );
            readyButton.setId ( "notReady" );
            model.setNewStatus ( true );
        }else if (readyButton.getId ().equals ( "notReady" )){
            readyButton.setImage ( loadImage ( "notReady" ) );
            readyButton.setId ( "readyButton" );
            model.setNewStatus(false);
            model.setDoChooseMap(false);
        }
    }

    /**
     * Goes to the game guide and loads the required images
     */
    public void goToGameGuide(MouseEvent event) throws IOException {
        Platform.runLater ( () -> {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/GameGuide.fxml"));
        Parent root1 = null;

            try {
                root1 = fxmlLoader.load();
            } catch (IOException e) {
                e.printStackTrace ( );
            }
            Stage newStage = new Stage();
            newStage.setResizable ( false );
            newStage.setTitle("Game Guide");
            newStage.setScene(new Scene(root1));
            newStage.show();
        } );
    }

    /**
     * Handles the property changes and their values
     * Loads the map
     * Registers are set to null as they are empty
     * Card images are set.
     * It's your turn label is set when it is player's turn.
     * Shows pop ups for the actual phase.
     * Sets the damage and robot direction.
     *
     * @param evt
     */

    @Override
    public void propertyChange (PropertyChangeEvent evt) {
        if (evt.getPropertyName ( ).equals ( "gameOn" )) {
            Platform.runLater ( () -> {
                try {
                    FXMLLoader fxmlLoader = new FXMLLoader ( getClass ( ).getResource ( "/view/Map.fxml" ) );
                    pane.setCenter ( fxmlLoader.load ( ) );
                    readyButton.setDisable ( true );
                    Playerinfo.setText ( null );
                    model.setGameOn ( false );
                } catch (IOException e) {
                    e.printStackTrace ( );
                }
            } );
        }


        if (evt.getPropertyName ( ).equals ( "gameFinished" )) {
            if (cards != null) {
                for (ImageView cards : cards) {
                    cards.setImage ( null );
                }
            }
            if (registers != null) {
                for (ImageView register : registers) {
                    register.setImage ( null );
                }
            }
            if(upgradeCards !=null){
                for (ImageView upgradCard: upgradeCards) {
                    upgradCard.setImage ( null );
                }
            }
            Platform.runLater ( () -> {
                pane.setCenter ( null );
                readyButton.setDisable ( false );
                model.setGameFinished ( false );
                try {
                    readyButton.setImage(loadImage("notReady"));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                readyButton.setId("readyButton");
                model.setNewStatus(false);
                model.setDoChooseMap(false);

            } );
        }

        if (evt.getPropertyName ( ).equals ( "handCards" )) {
            clientGameModel.setHandCards ( false );
            regToCard.put ( 0, null );
            regToCard.put ( 1, null );
            regToCard.put ( 2, null );
            regToCard.put ( 3, null );
            regToCard.put ( 4, null );
            if (clientGameModel.getActualPhase ()==2) {
                for (ImageView register : registers) {
                    register.setImage ( null );
                }
            }
            cards = FXCollections.observableArrayList ( card_0, card_1, card_2, card_3, card_4, card_5,
                    card_6, card_7, card_8 );
            Platform.runLater ( () -> {
                try {
                    for (int j = 0; j < cards.size ( ); j++) {
                        cardName = clientGameModel.getCardsInHand ( ).get ( j );
                        cards.get ( j ).setImage ( loadImage ( cardName ) );
                        cards.get ( j ).setId ( Integer.toString ( j ) );

                    }

                } catch (ArrayIndexOutOfBoundsException | FileNotFoundException e) {
                    e.printStackTrace ( );
                }
                Playerinfo.setText ( "Please choose your programming cards" );
            } );
            disableHand ( false );
        }
        if (evt.getPropertyName ( ).equals ( "currentRegister" )) {
            Platform.runLater ( () -> {

                dummesButton.setDisable ( false );
                dummesButton.setText ( Integer.toString ( 1 + clientGameModel.getValueActualRegister ( ) ) );
            } );
        }
        if (evt.getPropertyName ( ).equals ( "Losers" )) {
            for (int i = 0; i < clientGameModel.getLateCards ( ).size ( ); i++) {
                int regNum = getNextAvailableRegister ( );
                ImageView register = registers.get ( regNum );
                try {
                    register.setImage ( loadImage ( clientGameModel.getLateCards ( ).get ( i ) ) );
                } catch (FileNotFoundException e) {
                    e.printStackTrace ( );
                }
                regToCard.put ( regNum, clientGameModel.getLateCards ( ).get ( i ) );
            }
            clientGameModel.setLatePlayers ( false );
        }
        if (evt.getPropertyName ( ).equals ( "yourTurn" )) {
            Platform.runLater ( () -> {
                if (Integer.parseInt ( yourRobot.getId ( ) ) == model.getPlayersFigureMap ( ).get ( clientGameModel.getActualPlayerID ( ) )) {
                    Playerinfo.setText ( null );
                    if(clientGameModel.getActualPhase ()==0){
                        Playerinfo.setFont ( Font.font ( "Yu Gothic", FontWeight.BOLD,16 ) );
                        Playerinfo.setText ( "Please choose your Starting Point" );
                    }else {
                        Playerinfo.setText ( "It's  your  turn :)" );
                        yourRobot.setEffect ( new DropShadow ( 10.0, Color.GREEN ) );
                    }
                }
                clientGameModel.switchPlayer ( false );
            } );
            Playerinfo.setText ( null );
            yourRobot.setEffect ( new DropShadow ( 0.0, Color.GREEN ) );
        }
        if (evt.getPropertyName ( ).equals ( "ActualPhase" )) {
            Platform.runLater ( () -> {
                if (evt.getNewValue ( ).equals ( 2 )) {
                    disableAllRegisters ( false );
                    try {
                        showPopup ( "Programming Phase has begun" );
                        //Timer.setVisible ( true );



                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace ( );
                    }
                }
                if (evt.getNewValue ( ).equals ( 3 )) {
                    disableHand ( true );
                    try {
                        showPopup ( "Activation Phase has begun" );
                        Timer.setVisible ( false );
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace ( );
                    }
                    disableAllRegisters ( true );
                }
            } );
        }
        if (evt.getPropertyName ( ).equals ( "PickDamage" )) {
            Platform.runLater(() -> {
                setCount();
                try {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/PickDamage.fxml"));
                    right_Side.setCenter(fxmlLoader.load());
                } catch(IOException e) {
                    e.printStackTrace();
                }
            });
        }

        if(evt.getPropertyName().equals("doRefreshPlayerDisplay")) {
            model.setDoRefreshPlayerDisplay(false);

            readyDisplay.setText(model.getPlayersStatus());

        }
        if(evt.getPropertyName().equals("doChooseMap")) {
            model.setDoChooseMap(false);
            Platform.runLater(() -> {
                try {
                    showMaps();
                } catch(IOException ioException) {
                    ioException.printStackTrace();
                }
            });
        }
        if(evt.getPropertyName().equals("RebootDirection")) {
            clientGameModel.setChooseRebootDirection(false);
            Platform.runLater(() -> {
                setCount ( );
                try {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource ( "/view/RebootDirection.fxml" ) );
                    right_Side.setCenter ( fxmlLoader.load ( ) );
                } catch (IOException e) {
                    e.printStackTrace ( );
                }
            } );
        }
        if (evt.getPropertyName ( ).equals ( ( "rebootFinished" ) )) {
            right_Side.setCenter ( null );
        }
        if (evt.getPropertyName ( ).equals ( "refillShop" )) {
            clientGameModel.refillShop ( false );
            Platform.runLater(() -> {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/UpgradeShop.fxml"));
                Parent root1 = null;
                try {
                    showPopup("Upgrade Phase has begun");
                   // seconds = 30;
                   //clientGameModel.getUpgradBoughtCards ().clear ();

                    enableUpgradeCards();
                    root1 = fxmlLoader.load();
                    Stage newStage = new Stage();
                    newStage.setResizable ( false );
                    newStage.setTitle("UpgradeShop");
                    newStage.setScene(new Scene(root1));
                    newStage.setOnCloseRequest ( event ->clientGameModel.buyUpgradeCard ( "Null" ));
                    newStage.show();
                } catch (IOException | InterruptedException ioException) {
                    ioException.printStackTrace ( );
                }

            } );
        }
        if (evt.getPropertyName ( ).equals ( "buyingCardFinished" )) {
            clientGameModel.finishBuyCard ( false );

            upgradeCards = FXCollections.observableArrayList ( upgradeCard_1, upgradeCard_2, upgradeCard_3, upgradeCard_4, upgradeCard_5, upgradeCard_6 );

            Platform.runLater ( () -> {
                try {
                    for (int j = 0; j < clientGameModel.getBoughtCards().size(); j++) {
                        Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/UpgradeCards/" + clientGameModel.getBoughtCards().get(j) + ".png")));
                        String cardName = clientGameModel.getBoughtCards().get(j);
                        upgradeCards.get(j).setImage(image);
                        upgradeCards.get(j).setId(cardName);
                    }
                    for (int j = clientGameModel.getBoughtCards ( ).size ( ); j < upgradeCards.size ( ); j++) {
                        upgradeCards.get ( j ).setImage ( null );
                        upgradeCards.get ( j ).setId ( "Null" );
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    e.printStackTrace();
                }
            } );
        }
        if (evt.getPropertyName().equals("returningFinished")) {
            Platform.runLater(() -> {

                cards = FXCollections.observableArrayList(card_0, card_1, card_2, card_3, card_4, card_5,
                        card_6, card_7, card_8);
                for (ImageView card : cards) {
                    if (clientGameModel.getReturnedCards().contains(card.getId())) {
                        card.setImage(null);
                    }
                }
            });

        }
        if (evt.getPropertyName ().equals ( "TimerStarted" )){
            clientGameModel.setTimer ( false );
            seconds = 30;
            Timer.setVisible ( true );
            doTimer();
        }

    }

    /**
     * Timer for the truns
     */

    private void doTimer() {
        Timeline time = new Timeline (  );
        time.setCycleCount ( Timeline.INDEFINITE );
        if (time!=null){
            time.stop ();
        }
        KeyFrame frame = new KeyFrame ( Duration.seconds ( 1 ), new EventHandler<ActionEvent> ( ) {
            @Override
            public void handle(ActionEvent event) {
                seconds --;
                timerLable.setText ( "CountDown: " +seconds.toString () );
                if (seconds <= 0 ){
                    time.stop ();

                }
            }
        } );
        time.getKeyFrames ().add ( frame );
        time.playFromStart ();


    }

    /**
     * enables the event of buying upgrade cards
     */

    private void enableUpgradeCards() {
        upgradeCards = FXCollections.observableArrayList(upgradeCard_1, upgradeCard_2, upgradeCard_3, upgradeCard_4, upgradeCard_5, upgradeCard_6);

        for (ImageView card:upgradeCards) {
            card.setDisable ( false );
        }
    }

    /**
     * cool method of opening and closing the chat
     */
    public void open_chat(MouseEvent mouseEvent) {
        Platform.runLater ( () -> {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/newChat.fxml"));
            if (chatON.getId ().equals ( "chatON" )){
                chatON.setImage ( loadImage ( "chatOff" ) );
                chatON.setId ( "chatOff" );
                right_Side.setCenter ( fxmlLoader.load ( ) );

            }else if (chatON.getId ().equals ( "chatOff" )) {
                chatON.setImage ( loadImage ( "chatON") );
                chatON.setId ( "chatON" );
                right_Side.setCenter ( null );
            }
        } catch (IOException e) {
            e.printStackTrace ( );
        }});
    }


    public void handleUpgradeCard (MouseEvent mouseEvent) throws IOException {
        ImageView source = (ImageView) mouseEvent.getSource();
        if (source.getId().equals("AdminPrivilege")) {
            source.setDisable(true);
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/AdminPrivilegeEffekt.fxml"));
            Parent root1 = fxmlLoader.load();
            Stage newStage = new Stage();
            newStage.setScene(new Scene(root1));
            newStage.show();
        } else if (source.getId().equals("RearLaser")) {
            source.setDisable ( true );
            clientGameModel.canBackShooting(true);
        }
        else if (source.getId ().equals ( "MemorySwap" )){
            if (clientGameModel.getActualPhase ()==2) {
                source.setImage ( null );
                clientGameModel.getBoughtCards ().remove ( "MemorySwap" );
                clientGameModel.playMemorySwap(true);
                FXMLLoader fxmlLoader = new FXMLLoader ( getClass ( ).getResource ( "/view/MemorySwapEffekt.fxml" ) );
                Parent root1 = fxmlLoader.load ( );
                Stage newStage = new Stage ( );
                newStage.setScene ( new Scene ( root1 ) );
                newStage.show ( );
            }else{
                Alert a = new Alert ( Alert.AlertType.ERROR );
                a.setContentText ( "you cant play this CArd in Activation Phase" );
            }
        }
        else if (source.getId ().equals ( "SpamBlocker" )){
            if (clientGameModel.getActualPhase ()== 2) {
                source.setImage ( null );
                clientGameModel.getBoughtCards ().remove ( "SpamBlocker" );
              cards = FXCollections.observableArrayList ( card_0, card_1, card_2, card_3, card_4, card_5,
                        card_6, card_7, card_8 );
                for (ImageView card : cards) {
                    card.setImage ( null );
                }
                clientGameModel.activateSpamBlocker();

            }else {
                Alert a = new Alert ( Alert.AlertType.ERROR );
                a.setContentText ( "you cant play this CArd in Activation Phase" );
            }
        }
    }
}





package client.viewModel;

import client.model.ClientGameModel;
import client.model.ClientModel;
import client.viewModel.MapViewModel;

import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.*;

import json.JSONMessage;
import json.protocol.PlayCardBody;

//TODO: hier sollte noch die Stage für die beiden Chat und Spiel implementiert werden

/**
 * The type Game view model.
 */
public class GameViewModel implements Initializable, PropertyChangeListener {


    /**
     * The Card 0.
     */
    @FXML
    public ImageView card_0;
    /**
     * The Card 1.
     */
    @FXML
    public ImageView card_1;
    /**
     * The Card 2.
     */
    @FXML
    public ImageView card_2;
    /**
     * The Card 3.
     */
    @FXML
    public ImageView card_3;
    /**
     * The Card 4.
     */
    @FXML
    public ImageView card_4;
    /**
     * The Card 5.
     */
    @FXML
    public ImageView card_5;
    /**
     * The Card 6.
     */
    @FXML
    public ImageView card_6;
    /**
     * The Card 7.
     */
    @FXML
    public ImageView card_7;
    /**
     * The Card 8.
     */
    @FXML
    public ImageView card_8;


    /**
     * The Dummes button.
     */
    @FXML
    public Button dummesButton;

    /**
     * The Pane.
     */
    @FXML
    public BorderPane pane;

    /**
     * The Chat.
     */
    @FXML
    public BorderPane chat;

    /**
     * The Reg 0.
     */
    @FXML
    public ImageView reg_0;
    /**
     * The Reg 1.
     */
    @FXML
    public ImageView reg_1;
    /**
     * The Reg 2.
     */
    @FXML
    public ImageView reg_2;
    /**
     * The Reg 3.
     */
    @FXML
    public ImageView reg_3;
    /**
     * The Reg 4.
     */
    @FXML
    public ImageView reg_4;
    /**
     * The Your robot.
     */
    @FXML
    public ImageView yourRobot;
    /**
     * The Pane a.
     */
    @FXML
    public AnchorPane paneA;
    /**
     * The Playerinfo.
     */
    @FXML
    public Text Playerinfo;


    /**
     * The Model.
     */
    public ClientModel model = ClientModel.getInstance();
    /**
     * The Client game model.
     */
    public ClientGameModel clientGameModel = ClientGameModel.getInstance();
    /**
     * The Card name.
     */
    public String cardName;
    /**
     * The Register.
     */
    public String register;

    /**
     * The Reg to card.
     */
    public HashMap<Integer, String> regToCard = new HashMap<>();


    /**
     * The Cards.
     */
    ObservableList<ImageView> cards;
    /**
     * The Registers.
     */
    ObservableList<ImageView> registers;
    /**
     * The Damages.
     */
    ObservableList<ImageView> damages;
    /**
     * The Db image.
     */
    Dragboard dbImage = null;
    /**
     * The Return source.
     */
    ImageView returnSource;
    /**
     * The Count.
     */
    int count = 0;

    /**
     * The Laser shoot property.
     */
    public BooleanProperty laserShootProperty;

    /**
     * The Game on.
     */
    public BooleanProperty gameOn = new SimpleBooleanProperty(false);

    /**
     * Sets the values of the buttons,properties and panes.
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        model.addPropertyChangeListener(this);
        clientGameModel.addPropertyChangeListener(this);
        dummesButton.setDisable(true);
        dummesButton.setText(Integer.toString(1));

        paneA.prefHeightProperty().bind(pane.heightProperty());
        paneA.prefWidthProperty().bind(pane.widthProperty());
        registers = FXCollections.observableArrayList(reg_0, reg_1, reg_2, reg_3, reg_4);
        Platform.runLater(() -> {
            yourRobot.setImage(yourRobot());
            yourRobot.setId(String.valueOf(clientGameModel.getPlayer().getFigure()));

        });


    }

    /**
     * Show popup.
     * Initiates a new Popup
     *
     * @param popupText the popup text
     */
    public void showPopup(String popupText) {
        Text text = new Text(popupText);
        text.setFill(Color.RED);
        text.setStroke(Color.BLACK);
        ScaleTransition scaleTransition = new ScaleTransition();
        scaleTransition.setDuration(Duration.seconds(2));
        scaleTransition.setNode(text);
        scaleTransition.setByY(1.0);
        scaleTransition.setByX(1.0);
        scaleTransition.setCycleCount(-1);
        scaleTransition.setAutoReverse(true);
        scaleTransition.play();
        StackPane root = new StackPane();
        root.getChildren().addAll(text);
        Scene scene = new Scene(root, 300, 200);
        Stage not = new Stage();
        scene.setFill(Color.DARKGRAY);
        not.setTitle("Player Notification");
        not.setScene(scene);
        not.show();


    }

    /**
     * Your robot image.
     * Gets the players values and sets its robot.Figure gets called.
     *
     * @return the image
     */
    public Image yourRobot() {

        int figure = clientGameModel.getPlayer().getFigure();
        FileInputStream input = null;
        Image image;
        //TODO: FIGURE -1, hat keine Figur
        try {
            input = new FileInputStream((Objects.requireNonNull(getClass().getClassLoader().getResource("Robots/YourRobots/robot" + figure + ".png"))).getFile());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        image = new Image(input);
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
        FileInputStream path = null;
        Image image;
        try {
            path = new FileInputStream((Objects.requireNonNull(getClass().getClassLoader().getResource("images/ProgrammingCards/" + cardName + ".png")).getFile()));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        image = new Image(path);
        return image;

    }


    /**
     * Handles the cards put in the register.
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
        Image image = dragEvent.getDragboard().getImage();
        ImageView target = (ImageView) dragEvent.getTarget();

        this.register = target.getId();
        if (target.getImage() != null) {
            returnSource.setImage(target.getImage());
            target.setImage(dbImage.getImage());

        } else {
            handlewithdraw(target, image);
            collectingCards();
        }
    }

    /**
     * Handlewithdraw.
     *
     * @param target the target
     * @param image  the image
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
            regToCard.replace(registerNum, clientGameModel.getCardsInHand().get(Integer.parseInt(cardName)));
            clientGameModel.sendSelectedCards(registerNum, clientGameModel.getCardsInHand().get(Integer.parseInt(cardName)));
        } else {
            clientGameModel.sendSelectedCards(registerNum, "Null");
        }
    }

    /**
     * Puts the selected card in the selected register
     */
    public void playCard() {
        int currentRegister = clientGameModel.getValueActualRegister();

        try {

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
     * Clear registers.
     */
    public void clearRegisters () {
        for (ImageView register : registers) {
            register.setImage(null);

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
    private void disableAllRegisters(boolean b) {
        for (ImageView register:registers) {
            register.setDisable(b);
        }
    }

    private void disableHand(boolean b) {
        for (ImageView hand : cards) {
            hand.setDisable(b);
        }
    }

    /**
     * Set count.
     */
    public void setCount (){
        this.count = clientGameModel.getDamageCount();
    }

    /**
     * Handles the property changes and their values
     * Loads the map
     * Registers are set to null as they are empty
     * Card images are set.
     * It's your turn label is set when it is player's turn.
     * Shows pop ups for the actual phase.
     * Sets the damage and robot direction.
     * @param evt
     */
    @Override
    public void propertyChange (PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("gameOn")) {
            Platform.runLater(() -> {
                try {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/Map.fxml"));
                    pane.setCenter(fxmlLoader.load());
                    model.setGameOn(false);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            Playerinfo.setText(null);
            Playerinfo.setText("Please choose your Starting Point, click on the shown Points ");
        }

        if (evt.getPropertyName().equals("gameFinished")) {
            if (cards != null) {
                for (ImageView cards : cards) {
                    cards.setImage(null);
                }
            }
            if (register != null) {
                for (ImageView register : registers) {
                    register.setImage(null);
                }
            }
            Platform.runLater(() -> {
                pane.setCenter(null);
                model.setGameFinished(false);
            });
        }

        if (evt.getPropertyName().equals("handCards")) {
            clientGameModel.setHandCards(false);
            regToCard.put(0, null);
            regToCard.put(1, null);
            regToCard.put(2, null);
            regToCard.put(3, null);
            regToCard.put(4, null);
            for (ImageView register : registers) {
                register.setImage(null);
            }
            cards = FXCollections.observableArrayList(card_0, card_1, card_2, card_3, card_4, card_5,
                    card_6, card_7, card_8);
            Platform.runLater(() -> {
                try {
                    for (int j = 0; j < cards.size(); j++) {
                        cardName = clientGameModel.getCardsInHand().get(j);
                        cards.get(j).setImage(loadImage(cardName));
                        cards.get(j).setId(Integer.toString(j));
                    }
                } catch (ArrayIndexOutOfBoundsException | FileNotFoundException e) {
                    e.printStackTrace();
                }

                Playerinfo.setText("Please choose your programming cards");
            });
            disableHand(false);
        }
        if (evt.getPropertyName().equals("currentRegister")) {
            Platform.runLater(() -> {

                dummesButton.setDisable(false);
                dummesButton.setText(Integer.toString(1 + clientGameModel.getValueActualRegister()));
            });
        }
        if (evt.getPropertyName().equals("Losers")) {
            for (int i = 0; i < clientGameModel.getLateCards().size(); i++) {
                int regNum = getNextAvailableRegister();
                ImageView register = registers.get(regNum);
                try {
                    register.setImage(loadImage(clientGameModel.getLateCards().get(i)));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                regToCard.put(regNum, clientGameModel.getLateCards().get(i));
            }
            clientGameModel.setLatePlayers(false);
        }
        if (evt.getPropertyName().equals("yourTurn")) {

            Platform.runLater(() -> {

                if (Integer.parseInt(yourRobot.getId()) == model.getPlayersFigureMap().get(clientGameModel.getActualPlayerID())) {
                    Playerinfo.setText(null);
                    Playerinfo.setText("Its your turn :)");
                    yourRobot.setEffect(new DropShadow(10.0, Color.GREEN));
                }
                clientGameModel.switchPlayer(false);
            });
            Playerinfo.setText(null);
            yourRobot.setEffect(new DropShadow(0.0, Color.GREEN));
        }
        if (evt.getPropertyName().equals("ActualPhase")) {
            Platform.runLater(() -> {
                if (evt.getNewValue().equals(2)) {
                    disableAllRegisters(false);
                    showPopup("Programming Phase has begin");
                }
                if(evt.getNewValue().equals(3)){
                    disableHand(true);
                    showPopup("Activation Phase has begun");
                    disableAllRegisters(true);
                }
            });
        }
       if (evt.getPropertyName().equals("PickDamage")) {
           Platform.runLater(() -> {
               setCount();
               FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/PickDamage.fxml"));
               Parent root1 = null;
               try {
                   root1 = fxmlLoader.load();
               } catch (IOException ioException) {
                   ioException.printStackTrace();
               }
               Stage newStage = new Stage();
               newStage.setTitle("Damage");
               newStage.setScene(new Scene(root1));
               newStage.show();
           });
       }
        if (evt.getPropertyName().equals("RebootDirection")) {
            clientGameModel.setChooseRebootDirection(false);
            Platform.runLater(() -> {
                setCount();
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/RebootDirection.fxml"));
                Parent root1 = null;
                try {
                    root1 = fxmlLoader.load();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                Stage newStage = new Stage();
                newStage.setTitle("Choose Reboot Direction");
                newStage.setScene(new Scene(root1));
                newStage.show();
            });
        }
    }
}













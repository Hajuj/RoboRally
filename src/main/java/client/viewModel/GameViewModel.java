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
    public Label playerInfo;


    public ClientModel model = ClientModel.getInstance();
    public ClientGameModel clientGameModel = ClientGameModel.getInstance();
    public String cardName;
    public String register;

    public HashMap<Integer, String> regToCard = new HashMap<>();

    ObservableList<ImageView> cards;
    ObservableList<ImageView> registers;
    Dragboard dbImage = null;
    ImageView returnSource;
    public BooleanProperty laserShootProperty;

    public BooleanProperty gameOn = new SimpleBooleanProperty(false);


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        model.addPropertyChangeListener(this);
        clientGameModel.addPropertyChangeListener(this);
        dummesButton.setText(Integer.toString(1));

        registers = FXCollections.observableArrayList(reg_0, reg_1, reg_2, reg_3, reg_4);
        Platform.runLater(() -> {
            yourRobot.setImage(yourRobot());
            yourRobot.setId(String.valueOf(clientGameModel.getPlayer().getFigure()));
            // yourRobot.setImage(yourRobot(clientGameModel.getActualPlayerID()));
        });

      /*  clientGameModel.actualPlayerTurnProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                    if(clientGameModel.actualPlayerTurnProperty().getValue().equals())
                    yourRobot.setEffect(new DropShadow(10.0, Color.GREEN));

            }
        });

*/

    }

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
     /*   try {
            not.wait(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        not.close();*/

    }

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

    /*private void loadScene(String scene) throws IOException {
        if (scene.equals("Map")) {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/Map.fxml"));

            pane.setCenter(fxmlLoader.load());

        }
    }*/


    /**
     * drag was detected, start a drag-and-drop gesture
     * /* allow any transfer mode
     **/
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
            System.out.println(this.cardName);
        }
        event.consume();
        handleSource(source);
    }

    /**
     * data is dragged over the target
     * /* accept it only if it is not dragged from the same node
     * and if it has a image data
     * /* allow for moving
     **/
    public void handleTarget(DragEvent event) {

        if (event.getDragboard().hasImage()) {
            event.acceptTransferModes(TransferMode.MOVE);
            // System.out.println(event.getTarget());
        }

    }

    public void handleSource(ImageView source) {
        Dragboard db = source.startDragAndDrop(TransferMode.MOVE);
        dbImage = source.startDragAndDrop(TransferMode.MOVE);
        ClipboardContent content = new ClipboardContent();
        content.putImage(source.getImage());
        source.setImage(null);
        db.setContent(content);
        dbImage.setContent(content);

    }

    public void handledropped(DragEvent dragEvent) {
        Image image = dragEvent.getDragboard().getImage();
        ImageView target = (ImageView) dragEvent.getTarget();
        //TODO 2 Karten auf einem Register
        //TODO TargetId nehemn und überprüfen
        this.register = target.getId();
        if (target.getImage() != null) {
            returnSource.setImage(target.getImage());
            target.setImage(dbImage.getImage());

        } else {
            handlewithdraw(target, image);
            collectingCards();
        }
    }

    public void handlewithdraw(ImageView target, Image image) {
        target.setImage(image);
    }

    public void collectingCards() {
        int registerNum = Integer.parseInt(String.valueOf(this.register.charAt(4)));
        if (!cardName.equals("Null")) {
            regToCard.replace(registerNum, clientGameModel.getCardsInHand().get(Integer.parseInt(cardName)));
            clientGameModel.sendSelectedCards(registerNum, clientGameModel.getCardsInHand().get(Integer.parseInt(cardName)));
        } else {
            clientGameModel.sendSelectedCards(registerNum, "Null");
        }
    }

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

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public String getCardName() {
        return cardName;
    }


    public void dragExited(DragEvent dragEvent) {
        if (dragEvent.getTarget() == null || dragEvent.getGestureTarget() == null) {
            returnSource.setImage(dbImage.getImage());
        }
    }

    public void clearRegisters () {
        for (ImageView register : registers) {
            register.setImage(null);

        }
    }

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

    @Override
    public void propertyChange (PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("gameOn")) {
            Platform.runLater(() -> {
                try {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/Map.fxml"));
                    pane.setCenter(fxmlLoader.load());
                } catch (IOException e) {
                    e.printStackTrace();
                }
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
                //showPopup("Programming Phase has begin");
                //playerInfo.setText("Please choose your programming cards");
            });
        }
        if (evt.getPropertyName().equals("currentRegister")) {
            Platform.runLater(() -> {
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
    }
}
        /*if (evt.getPropertyName().equals("yourTurn")){
            Platform.runLater(() -> {
                //int playerRobot =model.getPlayersFigureMap().get(clientGameModel.getActualPlayerID())
                if(model.getPlayersFigureMap().get(clientGameModel.getActualPlayerID()).equals(yourRobot.getId())){
                    playerInfo.setText("Its your turn :)");
                    yourRobot.setEffect(new DropShadow(10.0, Color.GREEN));
                }
            });
        }*/






    /*    if (evt.getPropertyName().equals("yourTurn")){
            playerInfo.setText("it's Your turn");
            yourRobot.setEffect(new DropShadow(15.0, Color.GREEN));
            try {
                wait(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            playerInfo.setText(null);

        }
    }
        if (evt.getPropertyName().equals("ActualRegister")){
            int currentRegister = (int) evt.getNewValue();
            for (ImageView register: registers) {
                if (String.valueOf(currentRegister).equals(String.valueOf(register.getId().charAt(4)))) {
                    register.setDisable(true);
                }
            }

        }*/




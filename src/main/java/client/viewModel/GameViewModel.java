package client.viewModel;

import client.model.ClientGameModel;
import client.model.ClientModel;
import game.boardelements.Gear;
import game.programmingcards.BackUp;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import json.JSONMessage;
import json.protocol.AnimationBody;
import json.protocol.PlayCardBody;

import java.awt.dnd.DropTargetListener;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.*;

//TODO: hier sollte noch die Stage für die beiden Chat und Spiel implementiert werden

public class GameViewModel implements Initializable {


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
    public Button testButton;

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
    public HBox hand;
    public ImageView yourRobot;


    private ClientModel model = ClientModel.getInstance();
    private ClientGameModel clientGameModel = ClientGameModel.getInstance();
    private String cardName;
    private String register;
    private LinkedHashMap<Point2D, Group> fieldMap = new LinkedHashMap<>();

    private HashMap<Integer, Integer> regToCard = new HashMap<>();

    ObservableList<ImageView> cards;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        regToCard.put(0, null);
        regToCard.put(1, null);
        regToCard.put(2, null);
        regToCard.put(3, null);
        regToCard.put(4, null);
        dummesButton.setText(Integer.toString(1));

        Platform.runLater(() -> {
            yourRobot.setImage(yourRobot());
            // yourRobot.setImage(yourRobot(clientGameModel.getActualPlayerID()));
        });

        clientGameModel.actualRegisterPropertyProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                Platform.runLater(() -> {
                    dummesButton.setText(Integer.toString(1 + clientGameModel.getActualRegisterProperty()));
                });
            }
        });

      /*  clientGameModel.actualPlayerTurnProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                    if(clientGameModel.actualPlayerTurnProperty().getValue().equals())
                    yourRobot.setEffect(new DropShadow(10.0, Color.GREEN));

            }
        });

*/

        clientGameModel.getProgrammingPhaseProperty().addListener(new ChangeListener<Boolean>() {
            //TODO:Boolean Checkk dass es auf True gesetzt ist
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                Platform.runLater(() -> {
                    clientGameModel.getCardsInHandObservable().addListener(new ListChangeListener() {
                        @Override
                        public void onChanged(Change change) {
                            cards = FXCollections.observableArrayList(card_0, card_1, card_2, card_3, card_4, card_5,
                                    card_6, card_7, card_8);
                            Platform.runLater(() -> {
                                try {
                                    for (int j = 0; j < cards.size(); j++) {
                                        cardName = (String) clientGameModel.getCardsInHandObservable().get(j);
                                        cards.get(j).setImage(loadImage(cardName));
                                        cards.get(j).setId(Integer.toString(j));
                                    }
                                } catch (ArrayIndexOutOfBoundsException | FileNotFoundException e) {
                                    e.printStackTrace();
                                }
                            });
                        }
                    });
                });
            }
        });


        model.gameOnProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) {
                Platform.runLater(() -> {
                    try {
                        loadScene("Map");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        });
    }

    public Image yourRobot() {

        //int player = model.getPlayersFigureMap().get(playerId);

        int figure = clientGameModel.getPlayer().getFigure();
        FileInputStream input = null;
        Image image;
        //TODO FIGURE -1, hat keine Figur
        try {
            input = new FileInputStream((Objects.requireNonNull(getClass().getClassLoader().getResource("Robots/YourRobots/robot" + figure + ".png"))).getFile());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        image = new Image(input);
        return image;

    }

    private Image loadImage(String cardName) throws FileNotFoundException {
        FileInputStream path = null;
        Image image;
        path = new FileInputStream((Objects.requireNonNull(getClass().getClassLoader().getResource("images/ProgrammingCards/" + cardName + ".png")).getFile()));
        image = new Image(path);
        return image;

    }

    private void loadScene(String scene) throws IOException {
        if (scene.equals("Map")) {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/Map.fxml"));

            pane.setCenter(fxmlLoader.load());

        }
        if (scene.equals("PlayerMat")) {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/PlayerMat.fxml"));
            pane.setLeft(fxmlLoader.load());
        }
    }


    /**
     * drag was detected, start a drag-and-drop gesture
     * /* allow any transfer mode
     **/
    public void handle(MouseEvent event) {
        ImageView source = (ImageView) event.getSource();
        if (source.getId().equals(reg_0.getId()) || source.getId().equals(reg_1.getId())
                || source.getId().equals(reg_2.getId()) || source.getId().equals(reg_3.getId()) || source.getId().equals(reg_0.getId())) {
            this.cardName = "Null";
            int reg = Integer.parseInt(String.valueOf(this.register.charAt(4)));
            regToCard.replace(reg, null);
            collectingCards();
        } else {
            this.cardName = source.getId();
        }
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

        }

    }

    private void handleSource(ImageView source) {
        Dragboard db = source.startDragAndDrop(TransferMode.ANY);
        ClipboardContent content = new ClipboardContent();
        content.putImage(source.getImage());
        source.setImage(null);
        db.setContent(content);
    }

    public void handledropped(DragEvent dragEvent) {
        Image image = dragEvent.getDragboard().getImage();
        ImageView target = (ImageView) dragEvent.getTarget();
        //TODO TargetId nehemn und überprüfen
        //TODO 2 Karten auf einem Register
        //if (((ImageView) dragEvent.getTarget()).getImage(null))
        this.register = target.getId();
        handlewithdraw(target, image);
        collectingCards();

    }

    public void handlewithdraw(ImageView target, Image image) {
        target.setImage(image);
    }

    public void collectingCards() {
        int registerNum = Integer.parseInt(String.valueOf(this.register.charAt(4)));
        if (!cardName.equals("Null")) {
            regToCard.replace(registerNum, Integer.parseInt(cardName));
            clientGameModel.sendSelectedCards(registerNum, (String) clientGameModel.getCardsInHandObservable().get(Integer.parseInt(cardName)));
        } else {
            clientGameModel.sendSelectedCards(registerNum, "Null");
        }

    }

    public void playCard() {
        int currentRegister = clientGameModel.getActualRegister();
        //TODO:  java.lang.reflect.InvocationTargetException?
        String card = clientGameModel.getCardsInHand().get(regToCard.get(currentRegister));
        clientGameModel.sendPlayCard(card);

    }

    public void activateTestButton(ActionEvent event){
        //JSONMessage testGearAnimation = new JSONMessage("Gear",new AnimationBody("Gear"));
        //ClientModel.getInstance().sendMessage(testGearAnimation);
        Platform.runLater(()->{handleGearAnimation();});

    }


    public void handleGearAnimation() {

        //switch (gear.getColour()) {
            //case "green": {
        for (Map.Entry<Point2D, Gear> entry: clientGameModel.getGearMap().entrySet()){
            ImageView gear = (ImageView) fieldMap.get(entry.getKey()).getChildren().get(fieldMap.get(entry.getKey()).getChildren().size()-1);

            RotateTransition rotateTransition = new RotateTransition(Duration.millis(3000), gear);
            rotateTransition.setByAngle(270);
            rotateTransition.setCycleCount(Animation.INDEFINITE);
            rotateTransition.setInterpolator(Interpolator.LINEAR);
            rotateTransition.play();

        }
                //}

            }
            /**break;
            case "red": {
                Group imageGroup = fieldMap.get((Point2D) clientGameModel.getGearMap().keySet());
                ImageView image = (ImageView) imageGroup.getChildren().get(imageGroup.getChildren().size() - 1);
                for (Map.Entry<Point2D, Gear> entry : clientGameModel.getGearMap().entrySet()) {
                    gear = entry.getValue();
                    Duration duration = Duration.INDEFINITE;
                    RotateTransition rotateTransition = new RotateTransition(duration, image);
                    rotateTransition.setByAngle(-90);
                    rotateTransition.play();

                }*/




        public void setCardName (String cardName){
            this.cardName = cardName;
        }

        public String getCardName () {
            return cardName;
        }
    }
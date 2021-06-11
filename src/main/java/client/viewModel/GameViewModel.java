package client.viewModel;

import client.model.ClientGameModel;
import client.model.ClientModel;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import json.JSONMessage;
import json.protocol.PlayCardBody;

import java.awt.*;
import java.awt.dnd.DropTargetEvent;
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


    ObservableList<ImageView> cards ;
    ObservableList<ImageView> registers ;
    Dragboard db = null;
    ImageView returnSource;
    boolean paneDropped=false;



    @Override
    public void initialize (URL url, ResourceBundle resourceBundle) {
        registers = FXCollections.observableArrayList(reg_0,reg_1,reg_2,reg_3,reg_4);
        Platform.runLater(() -> {
            yourRobot.setImage(yourRobot());
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

        clientGameModel.getProgrammingPhaseProperty().addListener(new ChangeListener<Boolean>() {
            //TODO:Boolean Checkk dass es auf True gesetzt ist
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {

                Platform.runLater(() -> {

                        showPopup("Programming Phase has begin","please choose your Programming Cards");

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
                                        cards.get(j).setId(cardName);
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
            public void changed (ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) {
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

    private void showPopup(String popupText, String contentText) {
        Text text = new Text (popupText);
        Text content = new Text(contentText);
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
        root.getChildren().addAll(content);
        Scene scene = new Scene(root, 400, 200);
        Stage not = new Stage();
        scene.setFill(Color.DARKGRAY);
        not.setTitle("Player Notification");
        not.setScene(scene);
        not.show();
        //not.wait(2);
        not.close();

}

    public Image yourRobot (){

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
        if(scene.equals("PlayerMat")){
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/PlayerMat.fxml"));
            pane.setLeft(fxmlLoader.load());
        }
    }


    /** drag was detected, start a drag-and-drop gesture
     /* allow any transfer mode **/
    public void handle(MouseEvent event) {
        ImageView source = (ImageView) event.getSource();
        returnSource = source;
        if(source.getId().equals(reg_0.getId())||source.getId().equals(reg_1.getId())
                ||source.getId().equals(reg_2.getId())||source.getId().equals(reg_3.getId()) || source.getId().equals(reg_0.getId())){
            this.cardName = "Null";
            System.out.println(this.cardName);
            collectingCards();
        }else {
            this.cardName = source.getId();
        }

        event.consume();
        handleSource(source);


    }
    /** data is dragged over the target
    /* accept it only if it is not dragged from the same node
     * and if it has a image data
    /* allow for moving **/
    public void handleTarget(DragEvent event) {

        if (event.getDragboard().hasImage()) {
            event.acceptTransferModes(TransferMode.MOVE);
           // System.out.println(event.getTarget());

        }
        event.consume();

    }

    private void handleSource(ImageView source) {
         db = source.startDragAndDrop(TransferMode.ANY);
        ClipboardContent content = new ClipboardContent();
        content.putImage(source.getImage());
        source.setImage(null);
        db.setContent(content);
    }

    public void handledropped(DragEvent dragEvent) {
        Image image = dragEvent.getDragboard().getImage();
        ImageView target = (ImageView) dragEvent.getTarget();
        //TODO TargetId nehemn und überprüfen

/*
        if (dragEvent.getGestureTarget().equals(reg_0)||dragEvent.getGestureTarget().equals(reg_1)||
                dragEvent.getGestureTarget().equals(reg_2)||dragEvent.getGestureTarget().equals(reg_3)||dragEvent.getGestureTarget().equals(reg_4)||target.equals(null)){*/
 //           if (paneDropped==false) {
                this.register = target.getId();
                handlewithdraw(target, image);
                collectingCards();
  //          }




//        } else {
           /* System.out.println("NOT ACCEPTED TARGET");
            ImageView source = (ImageView) dragEvent.getSource();
            System.out.println(dragEvent.getSource());
            source.setImage(image);
            System.out.println(dragEvent.getDragboard().getImage());
//        }*/
        //TODO 2 Karten auf einem Register
        //if (((ImageView) dragEvent.getTarget()).getImage(null))

    }


    public void handlewithdraw(ImageView target, Image image) {
        target.setImage(image);
   }

    public void collectingCards(){

        int registerNum = Integer.parseInt(String.valueOf(this.register.charAt(4)));
        clientGameModel.sendSelectedCards(registerNum,cardName);

    }

    public void playCard () {

        JSONMessage playCard = new JSONMessage("PlayCard", new PlayCardBody("MoveI"));
        model.sendMessage(playCard);

    }
    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public String getCardName() {
        return cardName;
    }

    public void dragDroppedPane(DragEvent dragEvent) {
        System.out.println("you dropped on me Pane");
    }


    public void drgExited(DragEvent dragEvent) {
        System.out.println("you exited on me Pane");
       returnSource.setImage(db.getImage());
    }

/*   public void drgExited(DragEvent dragEvent) {
     this.paneDropped=true;
        returnCard();
        dragEvent.consume();
   }

   public void returnCard (){
       returnSource.setImage(db.getImage());
       this.paneDropped=false;

   }*/

}

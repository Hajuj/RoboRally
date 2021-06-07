package client.viewModel;

import client.model.ClientGameModel;
import client.model.ClientModel;
import javafx.application.Platform;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class PlayerMatViewModel implements Initializable {


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
    /*@FXML
    public VBox cards;*/

    private String cardName;
    private Integer register;

    ObservableList<ImageView> cards ;


    private ClientModel model = ClientModel.getInstance();
    private ClientGameModel clientGameModel = ClientGameModel.getInstance();

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        clientGameModel.getCardsInHandObservable().addListener(new ListChangeListener() {
            @Override
            public void onChanged(Change change) {
                cards = FXCollections.observableArrayList(card_0, card_1, card_2, card_3, card_4, card_5,
                        card_6, card_7, card_8);
                Platform.runLater(() -> {
                    try {
                            //card_0.setImage(loadImage("MoveI"));
                            int i = 0;
                            for (int j = 0; j <= cards.size(); j++) {

                                //for (int i = 0; i < clientGameModel.getCardsInHandObservable().size(); i++) {
                                cardName = (String) clientGameModel.getCardsInHandObservable().get(i);
                                cards.get(j).setImage(loadImage(cardName));
                                cards.get(j).setId(cardName);
                                i++;

                            }
                        } catch (ArrayIndexOutOfBoundsException | FileNotFoundException e) {
                        e.printStackTrace();
                    }
                });
            }
        });

    }

    private void handoutCards() throws FileNotFoundException {
     /*   System.out.println("HIIIIIII33333333333");
        cards = FXCollections.observableArrayList(card_0, card_1, card_2, card_3, card_4, card_5,
                card_6, card_7, card_8);
        //card_0.setImage(loadImage("MoveI"));
        for (int i = 0; i <= clientGameModel.getCardsInHandObservable().size(); i++) {
            System.out.println(clientGameModel.getCardsInHandObservable());
            for (int j = 0; j <= cards.size(); j++) {
                cardName = (String) clientGameModel.getCardsInHandObservable().get(i);
                System.out.println(cardName);
                System.out.println(cards.get(0));
                cards.get(j).setImage(loadImage(cardName));

                               *//* imv.setImage(loadImage(cardName));
                                imv.setId(cardName);*//*
            }

        }*/
    }

    public void handle(MouseEvent event) {
        /* drag was detected, start a drag-and-drop gesture*/
        /* allow any transfer mode */

        ImageView source = (ImageView) event.getSource();
        this.cardName = source.getId();
        handleSource(source);
        System.out.println(source.getId());
    }

    public void handleTarget(DragEvent event) {
        /* data is dragged over the target */
        /* accept it only if it is not dragged from the same node
         * and if it has a string data */
        if (event.getDragboard().hasImage()) {
            /* allow for moving */
            event.acceptTransferModes(TransferMode.MOVE);
        }
    }

    public void handledropped(DragEvent dragEvent) {
        Image image = dragEvent.getDragboard().getImage();
        ImageView target = (ImageView) dragEvent.getTarget();

        handlewithdraw(target, image);
        System.out.println(target.getId()+","+cardName);
        //Object targetTest =dragEvent.getGestureTarget();
        //System.out.println(this.cardName+","+targetTest);

        //register = Integer.parseInt(target.getId());

        //System.out.println(target.getImage().getUrl());
        //System.out.println(register);
        //String cardName= image.getUrl();
        //System.out.println(cardName);

    }


    private void handleSource(ImageView source) {
        Dragboard db = source.startDragAndDrop(TransferMode.ANY);
        ClipboardContent content = new ClipboardContent();
        content.putImage(source.getImage());
        source.setImage(null);
        db.setContent(content);
        //event.consume();

    }

    public void handlewithdraw(ImageView target, Image image) {
        target.setImage(image);


        //dragEvent.getTarget();
    }

    private Image loadImage(String cardName) throws FileNotFoundException {
        FileInputStream path = null;
        Image image;
        path = new FileInputStream((Objects.requireNonNull(getClass().getClassLoader().getResource("images/ProgrammingCards/" + cardName + ".png")).getFile()));
        image = new Image(path);
        return image;

    }
}

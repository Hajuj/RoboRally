package client.viewModel;

import client.model.ClientGameModel;
import client.model.ClientModel;
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
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.ResourceBundle;




//TODO: hier sollte noch die Stage für die beiden Chat und Spiel implementiert werden

public class GameViewModel implements Initializable {




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


    private ClientModel model = ClientModel.getInstance();
    private ClientGameModel clientGameModel = ClientGameModel.getInstance();
    private String cardName;
    private Integer register;


    @Override
    public void initialize (URL url, ResourceBundle resourceBundle) {
        //TODO:ADD LISTENER TO PROGRAMMINGPHASE BOOLEAN
        clientGameModel.getProgrammingPhaseProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                Platform.runLater(() -> {
                    try {
                        loadScene("PlayerMat");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        });
       /* clientGameModel.canSetStartingPointProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                Platform.runLater(() -> {
                try {
                    loadScene("PlayerMat");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                });
            }
        });*/



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


        register = Integer.parseInt(target.getId());

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
        System.out.println(this.cardName+","+this.register);

        //dragEvent.getTarget();
   }





}

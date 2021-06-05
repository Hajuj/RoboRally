package client.viewModel;

import client.model.ClientGameModel;
import client.model.ClientModel;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

//TODO: hier sollte noch die Stage für die beiden Chat und Spiel implementiert werden

public class GameViewModel implements Initializable {

   @FXML
    public BorderPane pane;
   @FXML
    public BorderPane chat;
    @FXML
    public ImageView move1;
    @FXML
    public ImageView move2;
    @FXML
    public ImageView move3;
    @FXML
    public ImageView move4;
    @FXML
    public ImageView move5;
    @FXML
    public ImageView move6;
    @FXML
    public ImageView move7;
    @FXML
    public ImageView move8;
    @FXML
    public ImageView move9;
    @FXML
    public VBox cards;
    @FXML
    public ImageView register1;
    @FXML
    public ImageView register2;
    @FXML
    public ImageView register3;
    @FXML
    public ImageView register4;
    @FXML
    public ImageView register5;





    private ClientModel model = ClientModel.getInstance();
    private ClientGameModel clientGameModel = ClientGameModel.getInstance();

    @Override
    public void initialize (URL url, ResourceBundle resourceBundle) {

            //cards.setVisible(false);


        model.gameOnProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed (ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) {

                Platform.runLater(() -> {
                    try {
                        loadGameScene();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

            }
        });
    }

    private void loadGameScene() throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/Map.fxml"));
        pane.setCenter(fxmlLoader.load());
    }


    public void handle(MouseEvent event) {
        /* drag was detected, start a drag-and-drop gesture*/
        /* allow any transfer mode */

        ImageView source = (ImageView) event.getSource();
        handleSource(source);
    }


    private void handleSource(ImageView source) {
        Dragboard db = source.startDragAndDrop(TransferMode.ANY);
        ClipboardContent content = new ClipboardContent();
        content.putImage(source.getImage());
        source.setImage(null);
        db.setContent(content);
        //event.consume();

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
        String register = target.getId();
        //System.out.println(target.getImage().getUrl());
        System.out.println(register);
        //String cardName= image.getUrl();
        //System.out.println(cardName);

    }




    /*
     if ( dragEvent.getTarget().equals(register1)){
        register1.setImage(image);
    }
        if ( dragEvent.getTarget().equals(register2)) {
        register2.setImage(image);
    }
        if ( dragEvent.getTarget().equals(register3)) {
        register3.setImage(image);
    }
        if ( dragEvent.getTarget().equals(register4)) {
        register4.setImage(image);
    }
        if ( dragEvent.getTarget().equals(register5)) {
        register5.setImage(image);
    }*/
    public void handlewithdraw(ImageView target, Image image) {

        target.setImage(image);



        /*dragEvent.getTarget();


        if ( dragEvent.getTarget().equals(register1)){
            register1.setImage(image);
        }
        if ( dragEvent.getTarget().equals(register2)) {
            register2.setImage(image);
        }
        if ( dragEvent.getTarget().equals(register3)) {
            register3.setImage(image);
        }
        if ( dragEvent.getTarget().equals(register4)) {
            register4.setImage(image);
        }
        if ( dragEvent.getTarget().equals(register5)) {
            register5.setImage(image);
        }*/

    }



}

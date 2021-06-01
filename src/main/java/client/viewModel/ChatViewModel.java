package client.viewModel;


import client.model.ClientModel;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.effect.ColorInput;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ChatViewModel implements Initializable {

    ClientModel model = ClientModel.getInstance();

    @FXML
    private TextArea readyDisplay = new TextArea("");
    @FXML
    private Button readyButton;
    @FXML
    private Button gameGuideBtn;
    @FXML
    private TextArea chatField = new TextArea("");
    @FXML
    private TextField messageField;
    @FXML
    private Button sendButton;
    @FXML
    private Button notReadyBtn;

    private String message;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        //TODO check how to do it with observable pattern instead of addListener
        model.doChooseMapProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed (ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) {
                //TODO: Hier (wenn möglich) kein Platform.runLater()
                if (model.doChooseMapProperty().getValue() == true) {
                    Platform.runLater(() -> {
                        try {
                            showMaps();
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                        }
                    });
                }
            }
        });
        //chatField = new TextArea("");
        model.chatHistoryProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed (ObservableValue<? extends String> observableValue, String s, String t1) {
                //System.out.println("VALUE CHANGED");
                chatField.setText(t1);
            }
        });
        model.playersStatusMapProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed (ObservableValue<? extends String> observableValue, String s1, String s2) {
                readyDisplay.setText(s2);
            }
        });


        model.refreshPlayerStatus(model.getPlayer().getPlayerID(), false);
        chatField.setEditable(false);
        readyDisplay.setEditable(false);
        if (model.getPlayer().getFigure() == -1) {
            readyButton.setVisible(false);
            notReadyBtn.setVisible(false);
        }
        notReadyBtn.setDisable(true);
    }

    public void sendMessageButton(ActionEvent event) {
       //System.out.println("HI");
            message = messageField.getText();
            model.sendMsg(message);
            messageField.clear();
    }

    public void goToGameGuide(ActionEvent event) throws IOException {
        Stage rootStage = new Stage();
        Parent root;
        root = FXMLLoader.load(getClass().getResource("/view/GameGuide.fxml"));
        rootStage.setScene(new Scene(root));
        rootStage.setTitle("Game Guide");
        rootStage.show();

    }

    public void sendReadyStatus (ActionEvent event) {
        model.setNewStatus(true);
        readyButton.setDisable(true);
        notReadyBtn.setDisable(false);
    }

    public void showMaps () throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/AvailableMaps.fxml"));
        Parent root1 = (Parent) fxmlLoader.load();
        Stage newStage = new Stage();
        newStage.setTitle("Available Maps");
        newStage.setScene(new Scene(root1));
        newStage.show();
    }


    public void changeStatusButton (ActionEvent event) {
        model.setNewStatus(false);
        notReadyBtn.setDisable(true);
        readyButton.setDisable(false);
        model.doChooseMapProperty().setValue(false);
    }

}

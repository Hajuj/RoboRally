package client.viewModel;


import client.model.ClientModel;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.media.AudioClip;

import java.net.URL;
import java.util.ResourceBundle;

public class ChatViewModel implements Initializable {
    ClientModel model = ClientModel.getInstance();
    @FXML
    private TextArea chatField;
    @FXML
    private TextField messageField;
    @FXML
    private Button sendButton;

    AudioClip sound;
    private String message;
    private StringProperty chatHistory;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        chatHistory = new SimpleStringProperty();
        model.chatHistoryProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed (ObservableValue<? extends String> observableValue, String s, String t1) {
                System.out.println("VALUE CHANGED");
                Platform.runLater(() -> {
                    chatField.setText(model.getChatHistory());
                });
            }
        });
    }


    //TODO: die Implentierung der MEthoden sendPrivateMsg(String msg, int senderId, int receiverID) und SendMsgAllPlayers(String msg)

    public void sendMessageButton(ActionEvent event) {
       //System.out.println("HI");
                message = messageField.getText();
                 model.sendMsg(message);
                 messageField.clear();

    }

}

package client.viewModel;


import client.model.ClientModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.media.AudioClip;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ChatViewModel implements Initializable {
    ClientModel model = ClientModel.getInstance();
    @FXML
    private TextArea ChatField;
    @FXML
    private TextField messageField;
    @FXML
    private Button sendButton;



    AudioClip sound;

    private StringProperty messageInput;
    private StringProperty chatText;
    private String message;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        messageInput = new SimpleStringProperty();
        chatText = new SimpleStringProperty();

       /* sendButton.setOnMouseClicked(event -> {

        });
        */

    }


    //TODO: die Implentierung der MEthoden sendPrivateMsg(String msg, int senderId, int receiverID) und SendMsgAllPlayers(String msg)
    public void sendMessageButton(ActionEvent event) {
       //messageInput.addListener((observableValue, oldValue, newValue) -> {
                message = messageField.getText();
                if (message.charAt(0) == '@') {
                    if (message.contains(" ")) {
                        int Begin_msg = message.indexOf(" ");
                        int playerPrivate = Integer.parseInt(message.substring(1, Begin_msg));
                        int End_msg = message.length();
                        model.sendPrivateMsg(message.substring(Begin_msg + 1, End_msg), playerPrivate);
                        System.out.println("this is a private message");
                        messageField.setText("");
                        messageProperty().setValue(messageField.getText());
                        //model.sendPrivateMsg((message.substring(Begin_msg + 1,End_msg), int playerSenderID);
                    }
                } else {
                    model.sendMsg(message);
                    System.out.println("this is a public Msg");
                    messageField.setText("");
                    //model.sendMsgAllPlayers(message,-1);
                    messageProperty().setValue(messageField.getText());
                }
           // });

    }


    public StringProperty messageProperty() {return messageInput;}

    public StringProperty chatText() {return chatText;}


}

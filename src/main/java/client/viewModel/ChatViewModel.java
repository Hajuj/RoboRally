package client.viewModel;


import client.model.ClientModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.css.StyleableBooleanProperty;
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


    private String message;
    private StringProperty chatHistory;
    private StringProperty clientChatOutput;
    private StringProperty msg;

    @Override
    public void initialize(URL location, ResourceBundle resources) {


        clientChatOutput = new SimpleStringProperty();
        chatHistory = new SimpleStringProperty();
       /* //clientChatOutput = new SimpleStringProperty();
       *//* sendButton.setOnMouseClicked(event -> {

        });
        *//*
        clientChatOutput.addListener((observable, oldValue, newValue) -> {
            clientChatOutput.bind(getChatHistoryProperty());
            ChatField.textProperty().bind(clientChatOutputProperty());

        });*/
    }


    //TODO: die Implentierung der MEthoden sendPrivateMsg(String msg, int senderId, int receiverID) und SendMsgAllPlayers(String msg)

    public void sendMessageButton(ActionEvent event) {
        System.out.println("HI");

                message = messageField.getText();
                //ChatField.textProperty().bind(chatTextProperty());

                if (message.charAt(0) == '@') {
                    if (message.contains(" ")) {
                        System.out.println("ACHTUNG this is a private message");

                        int Begin_msg = message.indexOf(" ");
                        int playerPrivate = Integer.parseInt(message.substring(1, Begin_msg));
                        int End_msg = message.length();
                        model.sendPrivateMsg(message.substring(Begin_msg + 1, End_msg), playerPrivate);
                        System.out.println("this is a private message");
                        messageField.setText("");
                        //messageProperty().setValue(messageField.getText()); war
                        //ChatField.textProperty().bind(chatTextProperty());war
                        //model.sendPrivateMsg((message.substring(Begin_msg + 1,End_msg), int playerSenderID);
                    }
                } else {
                    //TODO add bindings
                    System.out.println("this is a public Msg");

                    model.sendMsg(message);
                    refreshMessages();
                    handleReceiveMessage(model.getNewMessage()) ;
                    ChatField.textProperty().bind(getChatHistoryProperty());
                    messageField.clear();
                    //ChatField.setText(message); hier
                }
                clientChatOutput.bind(getChatHistoryProperty());
                ChatField.textProperty().bind(clientChatOutputProperty());



    }

    public StringProperty clientChatOutputProperty() {

        return clientChatOutput;
    }
    /**
     * Refresh messages.
     */
    public synchronized void refreshMessages() {
        ChatField.appendText(model.getNewMessage() + "\n");

    }


    public void handleReceiveMessage(String message) {
        String oldHistory = chatHistory.get();
        String newHistory = oldHistory + "\n" + message;
        chatHistory.setValue(newHistory);
    }

    //public StringProperty messageProperty() {return messageInput;}

    public StringProperty getChatHistoryProperty() {return chatHistory;}

    //public StringProperty chatTextProperty() {return chatText;}

   /* public void setChatText(String chatText) {
        this.chatText.set(chatText);

    }*/

    public void setChatHistory(String chatHistory) {
        this.chatHistory.set(chatHistory);
    }


}

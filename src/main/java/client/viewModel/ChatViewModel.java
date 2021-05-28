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

//TODO: refactoren the Name. Warum heißt FXML View Robochat und ViewModel ChatViewModell?
public class ChatViewModel implements Initializable {
    ClientModel model = ClientModel.getInstance();
    @FXML
    //TODO: REFACTOREN, das ist doch eine variable
    private TextArea ChatField;
    @FXML
    private TextField messageField;
    @FXML
    private Button sendButton;

    AudioClip sound;

    private StringProperty messageInput;
    private StringProperty chatText;
    private String message;
    private StringProperty chatHistory;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        messageInput = new SimpleStringProperty();
        chatText = new SimpleStringProperty();
        chatHistory = new SimpleStringProperty();

       /* sendButton.setOnMouseClicked(event -> {

        });
        */

    }


    //TODO: die Implentierung der MEthoden sendPrivateMsg(String msg, int senderId, int receiverID) und SendMsgAllPlayers(String msg)

    public void sendMessageButton(ActionEvent event) {
       //messageInput.addListener((observableValue, oldValue, newValue) -> {
                message = messageField.getText();
                //ChatField.textProperty().bind(chatTextProperty());

                if (message.charAt(0) == '@') {
                    if (message.contains(" ")) {
                        System.out.println("ACHTUNG this is a private message");

                        int Begin_msg = message.indexOf(" ");
                        //TODO: @viki, not @5. in ClientModel befindet sich ein HashMap mit allen Namen und IDs
                        int playerPrivate = Integer.parseInt(message.substring(1, Begin_msg));
                        int End_msg = message.length();
                        model.sendPrivateMsg(message.substring(Begin_msg + 1, End_msg), playerPrivate);
                        System.out.println("this is a private message");
                        messageField.setText("");
                        messageProperty().setValue(messageField.getText());
                        ChatField.textProperty().bind(chatTextProperty());
                        //model.sendPrivateMsg((message.substring(Begin_msg + 1,End_msg), int playerSenderID);
                    }
                } else {
                    //TODO add bindings
                    System.out.println("this is a public Msg");
                    model.sendMsg(message);
                   // chatText.bind(model.getChatHistoryProperty());
                    //setChatHistory(model.getNewHistory());
                    setChatHistory(model.getNewHistory());

                    //chatText.bind(chatHistory);
                    //processIncomingMessage(message);
                    messageField.setText("");
                    //TODO: was passiert hier überhauot :((((
                    //ChatField.setText(message);
                    ChatField.appendText(model.getNewMessage() + "\n");
                    //messageProperty().setValue(messageField.getText());
                }
           // });

    }
    /**
     * Refresh messages.
     */
    public synchronized void refreshMessages() {
        ChatField.appendText(model.getNewMessage() + "\n");
    }
    /**
     * Processes the message.
     * @param message the message
     */

    protected void processIncomingMessage(String message) {
        model.setNewMessage(message);
        refreshMessages();
    }


    public StringProperty messageProperty() {return messageInput;}

    public StringProperty chatTextProperty() {return chatText;}

   /* public void setChatText(String chatText) {
        this.chatText.set(chatText);

    }*/

    public void setChatHistory(String chatHistory) {
        this.chatHistory.set(chatHistory);
    }


}

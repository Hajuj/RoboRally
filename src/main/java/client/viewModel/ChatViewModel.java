package client.viewModel;

import client.model.ClientModel;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;
import java.io.IOException;
import java.util.ResourceBundle;

/**
 * The type Chat view model.
 */

public class ChatViewModel implements Initializable, PropertyChangeListener {
    /**
     * The Model.
     */

    ClientModel model = ClientModel.getInstance();
    /**
     * The Ready display.
     */

    @FXML
    public TextArea readyDisplay = new TextArea("");
    /**
     * The Ready button.
     */

    @FXML
    public Button readyButton;
    /**
     * The Game guide btn.
     */

    @FXML
    public Button gameGuideBtn;
    /**
     * The Chat field.
     */

    @FXML
    public TextArea chatField = new TextArea("");
    /**
     * The Message field.
     */

    @FXML
    public TextField messageField;
    /**
     * The Send button.
     */

    @FXML
    public Button sendButton;
    /**
     * The Not ready btn.
     */

    @FXML
    public Button notReadyBtn;

    private String message;
    /**
     * The Chat output.
     */

    public StringProperty chatOutput;

    /**
     * Sets the properties to their values.
     *
     * @param location
     * @param resources
     */


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        chatOutput = new SimpleStringProperty();
        model.addPropertyChangeListener(this);
        chatField.setText(model.getChatHistory());
        messageField.requestFocus();
        sendButton.defaultButtonProperty();

        chatField.setEditable(false);
        //readyDisplay.setText(model.getPlayersStatus());
        //readyDisplay.setEditable(false);
        //if (model.getClientGameModel().getPlayer().getFigure() == -1) {
        //  readyButton.setVisible(false);
        //notReadyBtn.setVisible(false);
        // }

        /*messageField.textProperty().addListener(((observableValue, oldValue, newValue) -> {
        chatOutput.bind(model.getChatHistory());
        chatField.textProperty().bind(chatOutputProperty());
        }));*/

    }

    /**
     * Chat output property string property.
     *
     * @return the string property
     */

    public StringProperty chatOutputProperty() {
        return chatOutput;
    }

    /**
     * Send message button.
     *
     * @param event the event
     */

    public void sendMessageButton(ActionEvent event) {
        message = messageField.getText();
        model.sendMsg(message);
        messageField.clear();
    }

    /**
     * Send ready status.
     *
     * @param event the event
     */

    public void sendReadyStatus(ActionEvent event) {
        model.setNewStatus(true);
        readyButton.setDisable(true);
        notReadyBtn.setDisable(false);
    }

    /**
     * Change status button.
     *
     * @param event the event
     */

    public void changeStatusButton(ActionEvent event) {
        model.setNewStatus(false);
        notReadyBtn.setDisable(true);
        readyButton.setDisable(false);
        model.setDoChooseMap(false);
    }

    /**
     * Changes the properties of chat elements.
     *
     * @param evt
     */


    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if(evt.getPropertyName().equals("chatHistory")) {
            Platform.runLater(() -> {
                chatField.setText(evt.getNewValue().toString());
                chatField.appendText("");
                chatField.setScrollTop(Double.MAX_VALUE);
            });
        }
        if(evt.getPropertyName().equals("playerStatus")) {
            Platform.runLater(() -> {
                readyDisplay.setText(evt.getNewValue().toString());
            });
        }
    }
}

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

public class ChatViewModel implements Initializable, PropertyChangeListener {

    ClientModel model = ClientModel.getInstance();

    @FXML
    public TextArea readyDisplay = new TextArea("");
    @FXML
    public Button readyButton;
    @FXML
    public Button gameGuideBtn;
    @FXML
    public TextArea chatField = new TextArea("");
    @FXML
    public TextField messageField;
    @FXML
    public Button sendButton;
    @FXML
    public Button notReadyBtn;

    private String message;

    public StringProperty chatOutput;


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

    public StringProperty chatOutputProperty() {
        return chatOutput;
    }

    public void sendMessageButton(ActionEvent event) {
        message = messageField.getText();
        model.sendMsg(message);
        messageField.clear();
    }


    public void sendReadyStatus(ActionEvent event) {
        model.setNewStatus(true);
        readyButton.setDisable(true);
        notReadyBtn.setDisable(false);
    }

    public void changeStatusButton(ActionEvent event) {
        model.setNewStatus(false);
        notReadyBtn.setDisable(true);
        readyButton.setDisable(false);
        model.setDoChooseMap(false);
    }

    public void loadGameScene() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/Map.fxml"));
        Parent root1 = fxmlLoader.load();
        Stage newStage = new Stage();
        newStage.setTitle("GAME");
        newStage.setScene(new Scene(root1));
        newStage.show();

        FXMLLoader fxmlLoader2 = new FXMLLoader(getClass().getResource("/view/YourCards.fxml"));
        Parent root2 = fxmlLoader2.load();
        Stage newStage2 = new Stage();
        newStage2.setTitle("CARDS");
        newStage2.setScene(new Scene(root2));
        newStage2.show();
    }


    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("chatHistory")) {
            Platform.runLater(() -> {
                chatField.setText(evt.getNewValue().toString());
                chatField.appendText("");
                chatField.setScrollTop(Double.MAX_VALUE);
            });
        }
        if (evt.getPropertyName().equals("playerStatus")) {
            Platform.runLater(() -> {
                readyDisplay.setText(evt.getNewValue().toString());
            });
        }
    }
}

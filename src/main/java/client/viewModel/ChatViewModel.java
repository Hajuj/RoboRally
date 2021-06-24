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
import java.util.Objects;
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
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        chatOutput = new SimpleStringProperty();
        model.addPropertyChangeListener(this);
        chatField.setText(model.getChatHistory());
        model.refreshPlayerStatus(model.getClientGameModel().getPlayer().getPlayerID(), false);
        readyDisplay.setText(model.getPlayersStatus());
        chatField.setEditable(false);
        readyDisplay.setEditable(false);
        sendButton.setDefaultButton(true);
        messageField.requestFocus();
        if (model.getClientGameModel().getPlayer().getFigure() == -1) {
            readyButton.setVisible(false);
            notReadyBtn.setVisible(false);
        }
        notReadyBtn.setDisable(true);

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
     * Go to game guide.Opens the game guide when the button is clicked
     *
     * @param event the event
     * @throws IOException the io exception
     */
    public void goToGameGuide(ActionEvent event) throws IOException {
        Stage rootStage = new Stage();
        Parent root;
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/GameGuide.fxml")));
        rootStage.setScene(new Scene(root));
        rootStage.setTitle("Game Guide");
        rootStage.show();
    }

    /**
     * Send ready status.
     *
     * @param event the event
     */
    public void sendReadyStatus (ActionEvent event) {
        model.setNewStatus(true);
        readyButton.setDisable(true);
        notReadyBtn.setDisable(false);
    }

    /**
     * Shows maps.
     *
     * @throws IOException the io exception
     */
    public void showMaps () throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/AvailableMaps.fxml"));
        Parent root1 = fxmlLoader.load();
        Stage newStage = new Stage();
        newStage.setTitle("Available Maps");
        newStage.setScene(new Scene(root1));
        newStage.show();
    }

    /**
     * Change status button.
     *
     * @param event the event
     */
    public void changeStatusButton (ActionEvent event) {
        model.setNewStatus(false);
        notReadyBtn.setDisable(true);
        readyButton.setDisable(false);
        model.setDoChooseMap(false);
    }

    /**
     * Load game scene.Opens the whole screen for game and your cards.
     *
     * @throws IOException the io exception
     */
    public void loadGameScene () throws IOException {


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

    /**
     * Changes the properties of chat elements.
     * @param evt
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("chatHistory")) {
            chatField.setText(evt.getNewValue().toString());
            chatField.appendText("");
            chatField.setScrollTop(Double.MAX_VALUE);
        }
        if (evt.getPropertyName().equals("playerStatus")) {
            readyDisplay.setText(evt.getNewValue().toString());
        }
        if (evt.getPropertyName().equals("doChooseMap")) {
            Platform.runLater(() -> {
                try {
                    showMaps();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            });
        }
    }
}

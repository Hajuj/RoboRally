package client.viewModel;

import client.model.ClientModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.IOException;

/**
 * The type client.viewModel.ClientModel gui controller.
 *
 * @author Vorprojekt
 */
public class ClientViewModel {
    ClientModel model = new ClientModel();

    @FXML
    private TextArea messages;
    @FXML
    private Button loginButton;
    @FXML
    private Button startButton;
    @FXML
    private TextField nameField;
    @FXML
    private TextArea users;
    @FXML
    private Label yourNameLabel;
    @FXML
    private Label usersOnlineLabel;
    @FXML
    private TextField messageField;
    @FXML
    private Button sendButton;
    @FXML
    private Label errorLabel;
    @FXML
    private Label chatLabel;

    /**
     * Instantiates a new chat.ClientModel.
     *
     * @throws IOException the io exception
     */
    public ClientViewModel () throws IOException {
    }


    /**
     * Instantiates a new chat.ClientModel gui controller.
     */
    /*Konstruktor für GUI Controller*/
    public void initialize () {
        messageField.setDisable(true);
        messages.setEditable(false);
        users.setEditable(false);
        nameField.setDisable(true);
    }

    /**
     * Refresh messages.
     */
    public synchronized void refreshMessages () {

    }

    /**
     * Send message button.
     *
     * @param event the event
     */
    @FXML
    public void sendMessageButton (ActionEvent event) {

    }

    /**
     * Login button.
     *
     * @param event the event
     */
    @FXML
    public void loginButton (ActionEvent event) {
        model.connectClient();
        loginButton.setDisable(true);
    }


    /**
     * Start bot client button.
     *
     * @param event the event
     */
    @FXML
    public void startBotClientButton (ActionEvent event) {
    }


    /**
     * Refresh users.
     */
    /*Funktion um die Benutzer kontinuierlich zu aktualisieren*/
    public synchronized void refreshUsers () {

    }


    /**
     * Notify connection status changed.
     *
     * @param clientConnected the client connected
     */
    /*Funktion um zu überprüfen ob die Verbindung weiterhin besteht*/
    public synchronized void notifyConnectionStatusChanged (boolean clientConnected) {

    }

}
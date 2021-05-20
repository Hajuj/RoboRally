package viewModel;

import client.Client;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.IOException;

/**
 * The type viewModel.Client gui controller.
 *
 * @author Vorprojekt
 */
public class ClientGuiController {
    private String userName;

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
     * Instantiates a new chat.Client.
     *
     * @throws IOException the io exception
     */
    public ClientGuiController() throws IOException {
    }


    /**
     * Instantiates a new chat.Client gui controller.
     */
    /*Konstruktor für GUI Controller*/
    public void initialize() {
        messageField.setDisable(true);
        messages.setEditable(false);
        users.setEditable(false);
        nameField.setDisable(true);
    }


    /**
     * Refresh messages.
     */
    public synchronized void refreshMessages() {

    }


    /**
     * Send message button.
     *
     * @param event the event
     */
    @FXML
    public void sendMessageButton(ActionEvent event) {

    }


    /**
     * Login button.
     *
     * @param event the event
     */
    @FXML
    public void loginButton(ActionEvent event) {
        String serverIP = "localhost";
        int serverPort = 500;
        Client client = new Client(serverIP, serverPort);
        client.connectClient();
        loginButton.setDisable(true);
    }


    /**
     * Start bot client button.
     *
     * @param event the event
     */
    @FXML
    public void startBotClientButton(ActionEvent event) {
    }


    /**
     * Refresh users.
     */
    /*Funktion um die Benutzer kontinuierlich zu aktualisieren*/
    public synchronized void refreshUsers() {

    }


    /**
     * Notify connection status changed.
     *
     * @param clientConnected the client connected
     */
    /*Funktion um zu überprüfen ob die Verbindung weiterhin besteht*/
    public synchronized void notifyConnectionStatusChanged(boolean clientConnected) {

    }


    /**
     * Gets user name.
     *
     * @return the user name
     */
    /*Getter für Username*/
    public String getUserName() {
        return userName;
    }

    /*run Methode für Thread*/
    public void run() {
    }

}
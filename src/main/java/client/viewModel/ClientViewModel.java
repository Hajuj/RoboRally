package client.viewModel;

import client.model.ClientModel;

//import com.sun.javafx.charts.Legend;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
//this class could also be considered as our sec. Window
/**
 * The type client.viewModel.ClientModel gui controller.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *                                  NO MORE ACTUAL
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 * @author Vorprojekt
 */
public class ClientViewModel implements Initializable{
    ClientModel model = ClientModel.getInstance();
    public IntegerProperty figure;
    private StringProperty message;
    private StringProperty serverAddress;

    @FXML
    private TextArea messages;
    @FXML
    private Button ConnectButton;
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


    private int serverPort;
    private String serverIPAdress;
    @FXML
    private TextField serverAddressField;
    @FXML
    private TextField PortField;
    private StringProperty chat;

    /**
     * Instantiates a new chat.ClientModel.
     *
     * @throws IOException the io exception
     */
    public ClientViewModel () throws IOException {
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        message = new SimpleStringProperty();
        serverAddress = new SimpleStringProperty();


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
        //messageField.textProperty().bind(model.sendMessage(messageField.getText());

    }







 /*   @FXML
    public void goToStartScreen(ActionEvent event){
        try {
            FXMLLoader fxmlLoader= new FXMLLoader(getClass().getResource("/view/Startscreen.fxml"));
            Parent root1 = (Parent) fxmlLoader.load();
            Stage stage= new Stage();
            stage.setTitle("RoboRally");
            stage.setScene(new Scene(root1));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }*/


    /**
     * Start bot client button.
     *
     * @param event the event
     */
 /*   @FXML
    public voidstartBotClientButton (ActionEvent event) {
    }
*/

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


    public void setPlayerReady(String name, int intValue) {
    }

    public StringProperty getChatText() {
        return chat;
    }
}
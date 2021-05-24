package View;


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
import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class ChatController implements Initializable {

    @FXML
    private TextField fieldName;
    @FXML
    private TextField fieldServer;
    @FXML
    private TextArea chatOutput;
    @FXML
    private TextArea chatInput;
    @FXML
    private Button buttonWiki;
    @FXML
    private Button buttonReady;
    @FXML
    private TextField messageField;
    @FXML
    private  TextArea messages;
    public String userName;


    public void chatController() throws IOException {
    }

    /**
     * Design of the Stage including the set of the scene with fxml-File and CSS-File
     *
     * @param stage the window of the application
     */

    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("RoboChat.fxml")));
        stage.setTitle("RoboRally Chat");
        Scene scene = new Scene(root);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("Chat.css")).toString());
        stage.setScene(scene);
        stage.show();
        System.out.println("start!");
    }

    @FXML
    public void sendMessageButton(ActionEvent event) {
        //sendTextMessage(chatLabel.getText());
        messageField.textProperty().bind(messages.textProperty());

    }

    public synchronized void refreshMessages() {
    }

    public void readyForTheGame(){

    }


    @FXML
    public void loginButton(ActionEvent event) {
    }

    @FXML
    public void readyButton(ActionEvent event) {
    }

    @FXML
    public void playButton(ActionEvent event) {

    }

    public synchronized void refreshUsers() {
    }

    public synchronized void notifyConnectionStatusChanged(boolean clientConnected) {
    }

    public String getUserName() {
        return userName;
    }


    public void sendTextMessage(String text) {
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}

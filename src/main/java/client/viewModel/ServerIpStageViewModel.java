package client.viewModel;

import client.model.ClientModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

/**
 * The type Server ip stage view model.
 */
public class ServerIpStageViewModel implements Initializable {
    /**
     * The Model.
     */
    ClientModel model = ClientModel.getInstance();

    private int serverPort;
    private String serverIP;
    private StringProperty serverAddress;

    /**
     * The Server address field.
     */
    @FXML
    public TextField serverAddressField;
    /**
     * The Server port field.
     */
    @FXML
    public TextField serverPortField;
    /**
     * The Connect button.
     */
    @FXML
    public Button connectButton;
    /**
     * The Logo.
     */
    @FXML
    public ImageView logo;
    /**
     * The Lmu button.
     */
    @FXML
    public Button LMUButton;
    /**
     * The B button.
     */
    @FXML
    public Button BButton;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        serverAddress = new SimpleStringProperty();
        LMUButton = new Button();
        BButton = new Button();
        connectButton.setDefaultButton(true);
    }

    /**
     * Find path file.
     *
     * @param fileName the file name
     * @return the file
     */
    public File findPath(String fileName) {
        ClassLoader classLoader = getClass().getClassLoader();
        return new File(Objects.requireNonNull(classLoader.getResource(fileName)).getFile());
    }

    /**
     * Server address property string property.
     *
     * @return the string property
     */
    public StringProperty serverAddressProperty() {
        return serverAddress;
    }

    /**
     * Connect button action.
     * Handles the action when the connect button is clicked.
     * Takes the given IP and Port info and tries to connect
     * Opens the chooserobot window if the connect is succesful
     *
     * @param event the event
     */
    @FXML
    public void connectButtonAction(ActionEvent event) {
        try {
            serverIP = serverAddressField.getText();
            //Numberformatexception, wenn nicht checken, ob valide ist
            serverPort = Integer.parseInt(serverPortField.getText());
//            if (validateIpAdress(serverIP, serverPort)) {
            if (model.connectClient(serverIP, serverPort)) {
                Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
                stage.close();
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/ChooseRobot.fxml"));
                Parent root1 = fxmlLoader.load();
                Stage newStage = new Stage();
                newStage.setTitle("RoboRally");
                newStage.setScene(new Scene(root1));
                newStage.show();
            } else {
                Alert a = new Alert(Alert.AlertType.NONE);
                a.setAlertType(Alert.AlertType.ERROR);
                a.setContentText("I can't find a game-server at " + serverIP + " : " + serverPort);
                a.show();
                serverAddressField.clear();
                serverPortField.clear();
            }
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    private boolean validateIpAdress(String IP, int port) {
        String IP_REGEX = "(([0-1]?[0-9]{1,2}\\.)|(2[0-4][0-9]\\.)|(25[0-5]\\.)){3}(([0-1]?[0-9]{1,2})|(2[0-4][0-9])|(25[0-5]))";
        Pattern IP_PATTERN = Pattern.compile(IP_REGEX);
        return IP_PATTERN.matcher(IP).matches();
    }

    /**
     * Lmu button action.
     * Sets the IP and the Port to our beloved LMU's server
     *
     * @param event the event
     */
    @FXML
    public void LMUButtonAction(ActionEvent event) {
        serverAddressField.setText("sep21.dbs.ifi.lmu.de");
        serverPortField.setText("52021");
    }

    /**
     * Bb button action.
     * Sets the IP and the Port to our beloved bb server
     *
     * @param event the event
     */
    @FXML
    public void BBButtonAction(ActionEvent event) {
        serverAddressField.setText("127.0.0.1");
        serverPortField.setText("500");
    }


}

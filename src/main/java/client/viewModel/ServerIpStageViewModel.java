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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class ServerIpStageViewModel implements Initializable {
    ClientModel model = ClientModel.getInstance();

    private int serverPort;
    private String serverIP;
    private StringProperty serverAddress;

    @FXML
    public TextField serverAddressField;
    @FXML
    public TextField serverPortField;
    @FXML
    public Button connectButton;
    @FXML
    public ImageView logo;
    @FXML
    public Button LMUButton;
    @FXML
    public Button BButton;


    @Override
    public void initialize (URL url, ResourceBundle resourceBundle) {
        connectButton.setDefaultButton(true);
        serverAddress = new SimpleStringProperty();
        LMUButton = new Button();
        BButton = new Button();
        Image image;
        FileInputStream input = null;
        try {
            input = new FileInputStream(findPath("robo_rally_logo.png"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        image = new Image(input);
        logo = new ImageView();
        logo.setImage(image);
        logo.setVisible(true);
    }

    public File findPath (String fileName) {
        ClassLoader classLoader = getClass().getClassLoader();
        return new File(Objects.requireNonNull(classLoader.getResource(fileName)).getFile());
    }

    public StringProperty serverAddressProperty () {
        return serverAddress;
    }

    @FXML
    public void connectButtonAction (ActionEvent event) {
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
//            } else {
//                serverAddressField.clear();
//                serverPortField.clear();
//                Alert a = new Alert(Alert.AlertType.NONE);
//                a.setAlertType(Alert.AlertType.ERROR);
//                a.setContentText("Non-valide-Daten, srry");
//                a.show();
//            }
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    private boolean validateIpAdress (String IP, int port) {
        String IP_REGEX = "(([0-1]?[0-9]{1,2}\\.)|(2[0-4][0-9]\\.)|(25[0-5]\\.)){3}(([0-1]?[0-9]{1,2})|(2[0-4][0-9])|(25[0-5]))";
        Pattern IP_PATTERN = Pattern.compile(IP_REGEX);
        return IP_PATTERN.matcher(IP).matches();
    }

    @FXML
    public void LMUButtonAction (ActionEvent event) {
        serverAddressField.setText("sep21.dbs.ifi.lmu.de");
        serverPortField.setText("52021");
        LMUButton.setStyle("-fx-background-radius: 180; -fx-background-color: chartreuse;");
        BButton.setStyle("-fx-background-radius: 180; -fx-background-color: gray;");
    }

    @FXML
    public void BBButtonAction (ActionEvent event) {
        serverAddressField.setText("127.0.0.1");
        serverPortField.setText("500");
        BButton.setStyle("-fx-background-radius: 180; -fx-background-color: chartreuse;");
        LMUButton.setStyle("-fx-background-radius: 180; -fx-background-color: gray;");
        // LMUButton.setStyle("-fx-background-radius: 180; -fx-background-color: gray;");
    }


}

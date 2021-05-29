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
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.regex.*;

public class ServerIpStageViewModel implements Initializable {
    ClientModel model = ClientModel.getInstance();
    private int serverPort;
    private String serverIP;
    @FXML
    private TextField serverAddressField;
    @FXML
    private TextField serverPortField;

    private StringProperty serverAddress;

    @FXML
    private Button connectButton;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        serverAddress = new SimpleStringProperty();
    }

    public StringProperty serverAddressProperty() {
        return serverAddress;
    }

    @FXML
    public void connectButtonAction(ActionEvent event) {
        try {
            //TODO: statt diese if() eine Methode mit boolean-Wert-zurück. Da auch die valide/not-valide IP&Port checken
            if (!serverAddressField.getText().equals("") && !serverPortField.getText().equals("")){
                serverIP = serverAddressField.getText();
                //Numberformatexception, wenn nicht checken, ob valide ist
                serverPort = Integer.parseInt(serverPortField.getText());
                if (model.connectClient(serverIP, serverPort)){
                    Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
                    stage.close();
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/chooseRobot.fxml"));
                    Parent root1 = (Parent) fxmlLoader.load();
                    Stage newStage = new Stage();
                    newStage.setTitle("RoboRally");
                    newStage.setScene(new Scene(root1));
                    newStage.show();
                } else {
                    serverAddressField.clear();
                    serverPortField.clear();
                }
            }
             else{
                serverAddressField.clear();
                serverPortField.clear();
            }
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    //TODO: diese Methode zu implementieren um zu checken ob die eingegebene Port und IP stimmen
    private boolean validateIpAdress(String IP, int Port) {
       //String PATTERN = "^((0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)\\.){3}(0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)$";
        String IP_REGEX = "(([0-1]?[0-9]{1,2}\\.)|(2[0-4][0-9]\\.)|(25[0-5]\\.)){3}(([0-1]?[0-9]{1,2})|(2[0-4][0-9])|(25[0-5]))";
        Pattern IP_PATTERN = Pattern.compile(IP_REGEX);
       return IP_PATTERN.matcher(IP).matches();
        //for port  (portString.length() > 4 || portString.length() == 0)
    }



}

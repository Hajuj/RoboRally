package client.viewModel;

import client.model.ClientModel;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;

public class ChooseRobotController {
    public TextField nameField;
    public Button playButton;
    private StringProperty username;
    public IntegerProperty figure;

    
    @FXML
    private ImageView robot1;
    @FXML
    private ImageView robot2;
    @FXML
    private ImageView robot3;
    @FXML
    private ImageView robot4;
    @FXML
    private ImageView robot5;
    @FXML
    private ImageView robot6;

    ClientViewModel model= new ClientViewModel();

    public ChooseRobotController() throws IOException {
    }

    public void setRobot1() throws IOException {
        ClientViewModel Client = new ClientViewModel();
        Client.figure.setValue(1);

    }
    public void setRobot2() throws IOException {
        ClientViewModel Client = new ClientViewModel();
        Client.figure.setValue(2);
    }
    public void setRobot3() throws IOException {
        ClientViewModel Client = new ClientViewModel();
        Client.figure.setValue(3);

    }
    public void setRobot4() throws IOException {
        ClientViewModel Client = new ClientViewModel();
        Client.figure.setValue(4);

    }
    public void setRobot5() throws IOException {
        ClientViewModel Client = new ClientViewModel();
        Client.figure.setValue(5);
    }
    public void setRobot6() throws IOException {
        ClientViewModel Client = new ClientViewModel();
        Client.figure.setValue(6);

    }

    public void playButtonClicked() {
        try {

            username.setValue(nameField.getText());
            nameProperty().setValue(nameField.getText());
            model.setPlayerReady(username.get(), figure.getValue().intValue());

            FXMLLoader fxmlLoader= new FXMLLoader(getClass().getResource("/view/chat.fxml"));
            Parent root1 = (Parent) fxmlLoader.load();
            Stage stage= new Stage();
            stage.setTitle("RoboRally");
            stage.setScene(new Scene(root1));
            stage.show();

        }catch (IOException e) {
            e.printStackTrace();
            }
    }

    public StringProperty nameProperty() {
        return username;
    }
}

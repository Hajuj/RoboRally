package client.viewModel;

import client.model.ClientModel;

import javafx.fxml.FXML;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ChooseRobotViewModel implements Initializable {

    public Button playButton;
    public TextField nameField;
    private String username;
    public int figure;
    ClientModel model = ClientModel.getInstance();


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


    @Override
    public void initialize (URL location, ResourceBundle resources) {

    }

    public void setRobot1 () {
        System.out.println("robot1 is been set");
        figure = 1;
    }

    public void setRobot2 () {
        System.out.println("robot2 is been set");
        figure = 2;
    }

    public void setRobot3 () {

        System.out.println("robot3 is been set");
        figure = 3;
    }

    public void setRobot4 () {
        System.out.println("robot4 is been set");
        figure = 4;

    }

    public void setRobot5 () {
        System.out.println("robot5 is been set");
        figure = 5;
    }

    public void setRobot6 () {
        System.out.println("robot6 is been set");
        figure = 6;

    }

    public void playButtonClicked () {
        try {
            username = nameField.getText();
            model.sendUsernameAndRobot(username, figure);
            System.out.println(username + " " + figure);
            Parent root = FXMLLoader.load(getClass().getResource("/view/RoboChat.fxml"));
            Stage window = (Stage) playButton.getScene().getWindow();
            window.setScene(new Scene(root, 800, 800));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public ImageView getRobot1() {
        return robot1;
    }

    public ImageView getRobot2() {
        return robot2;
    }

    public ImageView getRobot3() {
        return robot3;
    }

    public ImageView getRobot4() {
        return robot4;
    }

    public ImageView getRobot5() {
        return robot5;
    }

    public ImageView getRobot6() {
        return robot6;
    }

}

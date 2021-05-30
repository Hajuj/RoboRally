package client.viewModel;

import client.model.ClientModel;

import javafx.fxml.FXML;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.effect.ColorInput;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.effect.MotionBlur;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
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


    //TODO disable robots in the hash map
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        GaussianBlur blur = new GaussianBlur(10);
        for (Map.Entry<Integer, Integer> entry : model.getPlayersFigureMap().entrySet()) {
            switch (entry.getValue()) {
                case 1 : robot1.setDisable(true);
                         robot1.setEffect(blur);
                         break;
                case 2 : robot2.setDisable(true);
                          robot2.setEffect(blur);
                          break;
                case 3 : robot3.setDisable(true);
                          robot3.setEffect(blur);
                          break;
                case 4 : robot4.setDisable(true);
                        robot4.setEffect(blur);
                        break;
                case 5 : robot5.setDisable(true);
                    robot5.setEffect(blur);
                    break;
                case 6 : robot6.setDisable(true);
                    robot6.setEffect(blur);
                    break;
            }
        }
    }


        public void setRobot1()   {
        System.out.println("robot1 is been set");
        robot1.setEffect(new DropShadow(20.0, Color.BLACK));
        figure =1;
    }
    public void setRobot2()  {
        System.out.println("robot2 is been set");
        robot2.setEffect(new DropShadow(20.0, Color.BLACK));
        figure =2;
    }
    public void setRobot3()   {

        System.out.println("robot3 is been set");
        robot3.setEffect(new DropShadow(20.0, Color.BLACK));
        figure=3;
    }
    public void setRobot4() {
        System.out.println("robot4 is been set");
        robot4.setEffect(new DropShadow(20.0, Color.BLACK));
        figure=4;

    }
    public void setRobot5( )  {
        System.out.println("robot5 is been set");
        robot5.setEffect(new DropShadow(20.0, Color.BLACK));
        figure=5;
    }
    public void setRobot6() {
        System.out.println("robot6 is been set");
        robot6.setEffect(new DropShadow(20.0, Color.BLACK));
        figure=6;

    }

    public void playButtonClicked() {
        try {
            username= nameField.getText();
            if (username.isEmpty()) {
                model.sendUsernameAndRobot(username, figure);
                System.out.println(username + " " + figure);
                Parent root = FXMLLoader.load(getClass().getResource("/view/RoboChat.fxml"));
                Stage window = (Stage) playButton.getScene().getWindow();
                window.setScene(new Scene(root, 800, 800));
            }else{
                Alert a = new Alert(Alert.AlertType.NONE);
                a.setAlertType(Alert.AlertType.ERROR);
                a.setContentText("Please enter a valid name");
                a.show();
            }
        }catch (IOException e) {
            e.printStackTrace();
        }

    }

}

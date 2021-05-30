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


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        disableUsedRobots();
    }

    public void disableUsedRobots() {
        GaussianBlur blur = new GaussianBlur(10);
        for (Map.Entry<Integer, Integer> entry : model.getPlayersFigureMap().entrySet()) {
            switch (entry.getValue()) {
                case 0 -> {
                    robot1.setDisable(true);
                    robot1.setEffect(blur);
                }
                case 1 -> {
                    robot2.setDisable(true);
                    robot2.setEffect(blur);
                }
                case 2 -> {
                    robot3.setDisable(true);
                    robot3.setEffect(blur);
                }
                case 3 -> {
                    robot4.setDisable(true);
                    robot4.setEffect(blur);
                }
                case 4 -> {
                    robot5.setDisable(true);
                    robot5.setEffect(blur);
                }
                case 5 -> {
                    robot6.setDisable(true);
                    robot6.setEffect(blur);
                }
            }
        }
    }

    public void refreshShadow() {
        robot1.setEffect(new DropShadow(0.0, Color.RED));
        robot2.setEffect(new DropShadow(0.0, Color.RED));
        robot3.setEffect(new DropShadow(0.0, Color.RED));
        robot4.setEffect(new DropShadow(0.0, Color.RED));
        robot5.setEffect(new DropShadow(0.0, Color.RED));
        robot6.setEffect(new DropShadow(0.0, Color.RED));
        disableUsedRobots();
    }


    public void setRobot1() {
        refreshShadow();
        System.out.println("robot1 has been set");
        robot1.setEffect(new DropShadow(20.0, Color.RED));
        figure = 0;
    }

    public void setRobot2() {
        refreshShadow();
        System.out.println("robot2 has been set");
        robot2.setEffect(new DropShadow(20.0, Color.RED));
        figure = 1;
    }

    public void setRobot3() {
        refreshShadow();
        System.out.println("robot3 has been set");
        robot3.setEffect(new DropShadow(20.0, Color.RED));
        figure = 2;
    }

    public void setRobot4() {
        refreshShadow();
        System.out.println("robot4 has been set");
        robot4.setEffect(new DropShadow(20.0, Color.RED));
        figure = 3;

    }

    public void setRobot5() {
        refreshShadow();
        System.out.println("robot5 has been set");
        robot5.setEffect(new DropShadow(20.0, Color.RED));
        figure = 4;
    }

    public void setRobot6() {
        refreshShadow();
        System.out.println("robot6 has been set");
        robot6.setEffect(new DropShadow(20.0, Color.RED));
        figure = 5;

    }

    public void playButtonClicked() {
        try {
            username = nameField.getText();
            if (!username.isEmpty()) {
                model.getPlayer().setName(username);
                model.getPlayer().setFigure(figure);
                model.sendUsernameAndRobot(username, figure);
                System.out.println(username + " " + figure);
                Parent root = FXMLLoader.load(getClass().getResource("/view/RoboChat.fxml"));
                Stage window = (Stage) playButton.getScene().getWindow();
                window.setScene(new Scene(root, 800, 800));
            } else {
                //TODO you can't choose a new name after you press on the play button
                Alert a = new Alert(Alert.AlertType.NONE);
                a.setAlertType(Alert.AlertType.ERROR);
                a.setContentText("Please enter a valid name");
                a.show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}

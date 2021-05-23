package client.viewModel;


import javafx.fxml.FXML;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.media.AudioClip;
import javafx.stage.Stage;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;



public class StartScreenController {
    AudioClip sound;
    @FXML
    private Button StartGameButton;
   @FXML
    private Button GameGuidButton;
    @FXML
    private Button ExitButton;

    public StartScreenController() throws IOException {
    }


    @FXML
    public void chooseRobotScreen(ActionEvent event){

    }
    public void initialize(URL url, ResourceBundle resourceBundle) {
        sound = new AudioClip(this.getClass().getResource("/src/images/sounds/walking-dead.mp3").toExternalForm());
        sound.setCycleCount(AudioClip.INDEFINITE);
        sound.play();
    }
    @FXML
    public void setExitButton(ActionEvent event){
        ExitButton.setCancelButton(false);
    }
    @FXML
    public void openGameGuid(ActionEvent event) throws IOException{
        Stage rootStage;
        Parent root2;

        if (event.getSource() == GameGuidButton) {

            root2 = FXMLLoader.load(getClass().getResource("/view/GameGuide.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(root2));
            stage.setTitle("Game Guide");
            stage.show();


        }
        if (event.getSource() == StartGameButton) {

            root2 = FXMLLoader.load(getClass().getResource("/view/chooseRobot.fxml"));

            Stage stage2 = null;
            stage2.setScene(new Scene(root2));
            stage2.setTitle("Robot Choice");
            stage2.show();


        }


    }



}

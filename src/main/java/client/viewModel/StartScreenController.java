package client.viewModel;



import javafx.fxml.FXML;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.AudioClip;
import javafx.stage.Stage;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;



public class StartScreenController {

    @FXML
    private Button StartGameButton;
   @FXML
    private Button GameGuidButton;
    @FXML
    private Button ExitButton;

    @FXML
    private AnchorPane StartscreenPane;


        @FXML
        public void chooseRobotScreen (ActionEvent event){

        }

        @FXML
        public void setExitButton (ActionEvent event){
            ExitButton.setCancelButton(true);
        }
        @FXML
        public void openGameGuid (ActionEvent event) throws IOException {
            Stage rootStage = new Stage();
            Parent root2;

            if (event.getSource() == GameGuidButton) {

                root2 = FXMLLoader.load(getClass().getResource("/view/GameGuide.fxml"));
                rootStage.setScene(new Scene(root2));
                rootStage.setTitle("Game Guide");
                rootStage.show();


            }
            if (event.getSource() == StartGameButton) {
                root2 = FXMLLoader.load(getClass().getResource("/view/ServerIpStage.fxml"));
                rootStage.setScene(new Scene(root2));
                rootStage.setTitle("Lobby");
                rootStage.show();


                // StartscreenPane.getChildren().setAll(pane);
                //  root2 = FXMLLoader.load(getClass().getResource("/view/ServerIpStage.fxml"));

                //Stage stage2 = new Stage();
                //stage2.setScene(new Scene(root2));
                //stage2.setTitle("Robot Choice");
                //stage2.show();
            }
        }

        /* for Audio_Music
        *  @Override
    public void initialize(URL location, ResourceBundle resources) {
        sound = new AudioClip(this.getClass().getResource("sounds/walking-dead.mp3").toExternalForm());
        sound.setCycleCount(AudioClip.INDEFINITE);
        sound.play();

    }*/



}

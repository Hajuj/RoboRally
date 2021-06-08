package client.viewModel;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.AudioClip;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;



public class StartScreenViewModel implements Initializable {

    @FXML
    public Button StartGameButton;
    @FXML
    public Button GameGuidButton;
    @FXML
    public Button ExitButton;
    @FXML
    public AnchorPane StartscreenPane;
    @FXML
    public AudioClip sound;


    @FXML
        public void chooseRobotScreen (ActionEvent event){

        }
          //for Audio_Music
          @Override
    public void initialize(URL location, ResourceBundle resources) {
//        sound = new AudioClip(this.getClass().getResource("/sounds/walking-dead.mp3").toExternalForm());
//        sound.setCycleCount(AudioClip.INDEFINITE);
//        sound.play();
              StartGameButton.setDefaultButton(true);
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
            //TODO: close the StartScreen before go to the ServerIPStage??
            if (event.getSource() == StartGameButton) {
                Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
                stage.close();
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/ServerIpStage.fxml"));
                Parent root1 = fxmlLoader.load();
                Stage newStage = new Stage();
                newStage.setTitle("Connect");
                newStage.setScene(new Scene(root1));
                newStage.show();
            }
            /*if (event.getSource() == StartGameButton) {
                root2 = FXMLLoader.load(getClass().getResource("/view/ServerIpStage.fxml"));
                rootStage.setScene(new Scene(root2));
                rootStage.setTitle("Lobby");
                rootStage.show();
*/
        }





}

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


/**
 * The type Start screen view model.
 */
public class StartScreenViewModel implements Initializable {

    /**
     * The Start game button.
     */
    @FXML
    public Button StartGameButton;
    /**
     * The Game guid button.
     */
    @FXML
    public Button GameGuidButton;
    /**
     * The Exit button.
     */
    @FXML
    public Button ExitButton;
    /**
     * The Startscreen pane.
     */
    @FXML
    public AnchorPane StartscreenPane;
    /**
     * The Sound.
     */
    @FXML
    public AudioClip sound;


    @Override
    public void initialize (URL location, ResourceBundle resources) {
        //sound = new AudioClip(this.getClass().getResource("/sounds/walking-dead.mp3").toExternalForm());
        //sound.setCycleCount(AudioClip.INDEFINITE);
        //sound.play();
    }

    /**
     * Loads the ServerIpStage window to start the game
     *
     * @param event the event
     * @throws IOException the io exception
     */
    @FXML
    public void startGame (ActionEvent event) throws IOException {
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.close();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/ServerIpStage.fxml"));
        Parent root1 = fxmlLoader.load();
        Stage newStage = new Stage();
        newStage.setTitle("Connect");
        newStage.setScene(new Scene(root1));
        newStage.show();
    }

    /**
     * Opens the game guide.
     *
     * @param event the event
     * @throws IOException the io exception
     */
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
    }

    /**
     * Sets exit button.
     * Handles the event of clicking on the exit button
     * @param event the event
     */
    @FXML
    public void setExitButton (ActionEvent event) {
        ExitButton.setCancelButton(true);
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.close();
        System.exit(0);
    }


}

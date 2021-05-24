package viewModel;

import javafx.fxml.FXML;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.stage.Stage;

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

    public StartScreenController() throws IOException {

    }


    @FXML
    public void chooseRobotScreen(ActionEvent event){

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
            // Set Stage icon and title
            stage.setTitle("Game Guide");
            //stage.getIcons().add(new Image("/resources/images/others/wiki-icon.png"));
            stage.show();
            stage.setResizable(false);

        }
    }



    public void initialize() {

    }
}

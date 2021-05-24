package View;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

import java.net.URL;
import java.util.ResourceBundle;

public class ServerIpStageController {

    @FXML private Button connect;



        }

        @FXML
        public void connectButtonAction  (ActionEvent event){

            try {
                clientModel.connect(serverIP, serverPort);

                FXMLLoader fxmlLoader= new FXMLLoader(getClass().getResource("/view/ChooseRobot.fxml"));
                Parent root1 = (Parent) fxmlLoader.load();
                Stage stage= new Stage();
                stage.setTitle("RoboRally");
                stage.setScene(new Scene(root1));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }




}

package client.viewModel;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class AvailableMapsViewModel implements Initializable {

    @FXML
    private Button DizzyHighwayButton;

    @Override
    public void initialize (URL url, ResourceBundle resourceBundle) {

    }

    public void selectDizzyHighway (Event event) {
        System.out.println("Ich will Dizzy Highway!");
        //TODO: hier muss ein message MapSelected geschickt
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.close();
    }
}

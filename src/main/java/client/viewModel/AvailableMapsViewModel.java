package client.viewModel;

import client.model.ClientModel;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.stage.Stage;
import json.JSONMessage;
import json.protocol.MapSelectedBody;

import java.net.URL;
import java.util.ResourceBundle;

public class AvailableMapsViewModel implements Initializable {

    ClientModel model = ClientModel.getInstance();

    @FXML
    private Button DizzyHighwayButton;

    @FXML
    ChoiceBox choiseBox;

    @Override
    public void initialize (URL url, ResourceBundle resourceBundle) {
        for (String m : model.getAvailableMaps()) {
            choiseBox.getItems().add(m);
        }
    }

    //TODO it should be in ClientModel
    public void selectDizzyHighway (Event event) {
        System.out.println("Ich will Dizzy Highway!");
        String map = "Dizzy Highway";
        JSONMessage jsonMessage = new JSONMessage("MapSelected", new MapSelectedBody(map));
        model.sendMessage(jsonMessage);
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.close();
    }

}

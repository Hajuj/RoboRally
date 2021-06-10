package client.viewModel;

import client.model.ClientModel;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
    public Button DizzyHighwayButton;
    @FXML
    public ChoiceBox choiseBox;

    @Override
    public void initialize (URL url, ResourceBundle resourceBundle) {
        for (String m : model.getAvailableMaps()) {
            choiseBox.getItems().add(m);
        }

        choiseBox.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed (ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                //TODO:
            }
        });
    }

    //TODO it should be in ClientModel
    public void selectDizzyHighway (Event event) {
        System.out.println("Ich will Dizzy Highway!");
        String map = "Death Trap";
        JSONMessage jsonMessage = new JSONMessage("MapSelected", new MapSelectedBody(map));
        model.sendMessage(jsonMessage);
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.close();
    }

}

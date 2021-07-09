package client.viewModel;

import client.model.ClientModel;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;
public class AvailableMapsViewModel implements Initializable {

    ClientModel model = ClientModel.getInstance();

    @FXML
    public ChoiceBox choiceBox;

    @Override
    public void initialize (URL url, ResourceBundle resourceBundle) {
        choiceBox.setStyle("-fx-font: 23px \"Serif\";");
        Platform.runLater(() -> {
            for (String m : model.getAvailableMaps()) {
                choiceBox.getItems().add(m);
            }
        });

        choiceBox.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed (ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                Platform.runLater(() -> {
                    String mapName = model.getAvailableMaps().get(t1.intValue());
                    model.getClientGameModel().chooseMap(mapName);
                    Stage stage = (Stage) choiceBox.getScene().getWindow();
                    stage.setResizable ( false );
                    stage.close();
                });
            }
        });
    }

}

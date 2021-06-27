package client.viewModel;

import client.model.ClientModel;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.awt.*;

public class RebootDirectionViewModel {
    ClientModel clientModel = ClientModel.getInstance();

    @FXML
    public Button top;

    @FXML
    public Button bottom;
    @FXML
    public Button right;
    @FXML
    public Button left;

    @FXML
    public void chooseTop () {
        clientModel.getClientGameModel().sendRebootDirection("top");
        Stage stage = (Stage) top.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void chooseBottom () {
        clientModel.getClientGameModel().sendRebootDirection("bottom");
        Stage stage = (Stage) bottom.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void chooseLeft () {
        clientModel.getClientGameModel().sendRebootDirection("left");
        Stage stage = (Stage) left.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void chooseRight () {
        clientModel.getClientGameModel().sendRebootDirection("right");
        Stage stage = (Stage) right.getScene().getWindow();
        stage.close();
    }
}

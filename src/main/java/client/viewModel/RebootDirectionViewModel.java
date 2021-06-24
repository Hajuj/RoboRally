package client.viewModel;

import client.model.ClientModel;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.awt.*;

/**
 * The type Reboot direction view model.
 */
public class RebootDirectionViewModel {
    /**
     * The Client model.
     */
    ClientModel clientModel = ClientModel.getInstance();

    /**
     * The Top.
     */
    @FXML
    public Button top;

    /**
     * The Bottom.
     */
    @FXML
    public Button bottom;
    /**
     * The Right.
     */
    @FXML
    public Button right;
    /**
     * The Left.
     */
    @FXML
    public Button left;

    /**
     * Choose top.
     */
    @FXML
    public void chooseTop () {
        clientModel.getClientGameModel().sendRebootDirection("top");
        Stage stage = (Stage) top.getScene().getWindow();
        stage.close();
    }

    /**
     * Choose bottom.
     */
    @FXML
    public void chooseBottom () {
        clientModel.getClientGameModel().sendRebootDirection("bottom");
        Stage stage = (Stage) bottom.getScene().getWindow();
        stage.close();
    }

    /**
     * Choose left.
     */
    @FXML
    public void chooseLeft () {
        clientModel.getClientGameModel().sendRebootDirection("left");
        Stage stage = (Stage) left.getScene().getWindow();
        stage.close();
    }

    /**
     * Choose right.
     */
    @FXML
    public void chooseRight () {
        clientModel.getClientGameModel().sendRebootDirection("right");
        Stage stage = (Stage) right.getScene().getWindow();
        stage.close();
    }
}

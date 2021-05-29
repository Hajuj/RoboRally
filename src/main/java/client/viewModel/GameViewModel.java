package client.viewModel;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.util.ResourceBundle;

//TODO: hier sollte noch die Stage für die beiden Chat und Spiel implementiert werden

public class GameViewModel implements Initializable {
    @FXML
    private GridPane playerMat;
    @FXML
    private GridPane startScreen;
    @FXML
    private GridPane chooseRobot;

    @FXML
    private ClientViewModel chatController;
    @FXML
    private ChooseRobotViewModel chooseRobotViewModel;
    @FXML
    private PlayerMatViewModel playerMatViewModel;

    @Override
    public void initialize (URL url, ResourceBundle resourceBundle) {
    }


    public GridPane getStartScreen () {
        return startScreen;
    }

    public GridPane getChooseRobot () {
        return chooseRobot;
    }

    public GridPane getPlayerMat () {
        return playerMat;
    }


}

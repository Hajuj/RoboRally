package client.viewModel;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.util.ResourceBundle;

//TODO: hier sollte noch die Stage für die beiden Chat und Spiel implementiert werden

public class GameViewModel implements Initializable {

   @FXML
    public BorderPane pane;
    @FXML
    public GridPane gameSection;
    @FXML
    public GridPane chatSection;
    @FXML
    public HBox registerSection;

    private GridPane playerMat;

    private GridPane startScreen;

    private GridPane chooseRobot;

    private ChooseRobotViewModel chooseRobotViewModel;

    private PlayerMatViewModel playerMatViewModel;

    @Override
    public void initialize (URL url, ResourceBundle resourceBundle) {

        FxmlLoader screen =new FxmlLoader();
        Pane view = screen.getPage("Map");
        pane.setCenter(view);
        FxmlLoader screen2 = new FxmlLoader();
        Pane view2 = screen.getPage("RoboChat");
        pane.setRight(view2);
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

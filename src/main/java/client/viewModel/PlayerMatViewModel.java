package client.viewModel;

import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

public class PlayerMatViewModel {
    //5 Registern für die jeweiligen ausgewählten ProgrammierKarten
    @FXML
    public HBox playerRegister;
    @FXML
    public ImageView register1;
    @FXML
    public ImageView register2;
    @FXML
    public ImageView register3;
    @FXML
    public ImageView register4;
    @FXML
    public ImageView register5;


    public HBox getPlayerRegister () {
        return playerRegister;
    }

    public void initialize () {
    }
}

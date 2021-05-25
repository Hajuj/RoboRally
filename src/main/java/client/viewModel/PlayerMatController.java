 package client.viewModel;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

public class PlayerMatController {
    //5 Registern für die jeweiligen ausgewählten ProgrammierKarten
    @FXML
    private HBox playerRegister;
    @FXML
    private ImageView register1;
    @FXML
    private ImageView register2;
    @FXML
    private ImageView register3;
    @FXML
    private ImageView register4;
    @FXML
    private ImageView register5;


    public HBox getPlayerRegister() {
        return playerRegister;
    }
    public void initialize() {}
}
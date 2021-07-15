package client.viewModel;

import client.model.ClientGameModel;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * The type Choose damage view model.
 */
public class ChooseDamageViewModel implements Initializable {
    /**
     * The Count display.
     */
    public Text countDisplay;
    /**
     * The Client game model.
     */
    ClientGameModel clientGameModel = ClientGameModel.getInstance();


    /**
     * The Trojan.
     */
    @FXML
    public ImageView Trojan;
    /**
     * The Virus.
     */
    @FXML
    public ImageView Virus;
    /**
     * The Worm.
     */
    @FXML
    public ImageView Worm;

    private int count = 0;
    private int tempCount = 0;
    private ArrayList<String> choosenDamageCards = new ArrayList<>();


    /**
     * Choose damage cards.
     * Method to choose a damage card when all spam cards are used
     *
     * @param event the event
     */
    public void chooseDamageCards(MouseEvent event) {
        ImageView imageView = (ImageView) event.getSource();
        String card = imageView.getId();
        // damages = FXCollections.observableArrayList(TrojanHorse,Virus,Worm);
        if (tempCount < count) {
            choosenDamageCards.add(card);
            tempCount++;
            Platform.runLater(() -> {
                countDisplay.setText(tempCount + "  /  " + count);
            });
            if (tempCount == count) {
                clientGameModel.sendSelectedDamage(choosenDamageCards);
                Stage stage = (Stage) countDisplay.getScene().getWindow();
                stage.setScene(null);
            }
        }
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.count = clientGameModel.getDamageCount();
        this.tempCount = 0;

        Platform.runLater(() -> {
            countDisplay.setText(tempCount + "  /  " + count);
        });
        clientGameModel.setDamageCount(0);
    }
}

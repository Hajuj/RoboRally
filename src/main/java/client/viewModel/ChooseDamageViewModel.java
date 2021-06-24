package client.viewModel;

import client.model.ClientGameModel;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * The type Choose damage view model.
 */
public class ChooseDamageViewModel implements Initializable {
    /**
     * The Client game model.
     */
    ClientGameModel clientGameModel = ClientGameModel.getInstance();

    /**
     * The Count button.
     */
    @FXML
    public Button countButton;
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
     * Adds damage cards
     *
     * @param event the event
     */
    public void chooseDamageCards (MouseEvent event) {
        ImageView imageView = (ImageView) event.getSource();
        String card = imageView.getId();
        System.out.println(tempCount + "  /  " + count);
        if (tempCount < count) {
            choosenDamageCards.add(card);
            tempCount++;
            Platform.runLater(() -> {
                countButton.setText(tempCount + "  /  " + count);
            });
            if (tempCount == count) {
                clientGameModel.sendSelectedDamage(choosenDamageCards);
                Stage stage = (Stage) countButton.getScene().getWindow();
                stage.close();
            }
        }
    }

    /**
     * Initializes count ,tempcount and sets damage count to zero.
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize (URL url, ResourceBundle resourceBundle) {
        this.count = clientGameModel.getDamageCount();
        this.tempCount = 0;

        Platform.runLater(() -> {
            countButton.setText(tempCount + "  /  " + count);
        });
        clientGameModel.setDamageCount(0);
    }
}

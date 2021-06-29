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
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ChooseDamageViewModel implements Initializable {
    public Text countDisplay;
    ClientGameModel clientGameModel = ClientGameModel.getInstance();


    @FXML
    public ImageView Trojan;
    @FXML
    public ImageView Virus;
    @FXML
    public ImageView Worm;

    private int count = 0;
    private int tempCount = 0;
    private ArrayList<String> choosenDamageCards = new ArrayList<>();


    public void chooseDamageCards (MouseEvent event) {
        ImageView imageView = (ImageView) event.getSource();
        String card = imageView.getId();
        // damages = FXCollections.observableArrayList(TrojanHorse,Virus,Worm);
        System.out.println(tempCount + "  /  " + count);
        if (tempCount < count) {
            choosenDamageCards.add(card);
            tempCount++;
            Platform.runLater(() -> {
                countDisplay.setText(tempCount + "  /  " + count);
            });
            if (tempCount == count) {
                clientGameModel.sendSelectedDamage(choosenDamageCards);
                Stage stage = (Stage) countDisplay.getScene().getWindow();
                stage.setScene ( null );
                //stage.close();
            }
        }
    }


    @Override
    public void initialize (URL url, ResourceBundle resourceBundle) {
        this.count = clientGameModel.getDamageCount();
        this.tempCount = 0;

        Platform.runLater(() -> {
            countDisplay.setText(tempCount + "  /  " + count);
        });
        clientGameModel.setDamageCount(0);
    }
}

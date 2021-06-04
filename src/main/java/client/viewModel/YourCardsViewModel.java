package client.viewModel;

import client.model.ClientGameModel;
import client.model.ClientModel;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import json.JSONMessage;
import json.protocol.SelectedCardBody;

import java.net.URL;
import java.util.ResourceBundle;

public class YourCardsViewModel implements Initializable {

    ClientModel model = ClientModel.getInstance();
    ClientGameModel clientGameModel = ClientGameModel.getInstance();

    @FXML
    public Button card0;
    @FXML
    public Button card1;
    @FXML
    public Button card2;
    @FXML
    public Button card3;
    @FXML
    public Button card4;
    @FXML
    public Button card5;
    @FXML
    public Button card6;
    @FXML
    public Button card7;
    @FXML
    public Button card8;

    public void selectCard0() {
        JSONMessage jsonMessage = new JSONMessage("SelectedCard", new SelectedCardBody(card0.getText(), 2));
        model.sendMessage(jsonMessage);
    }

    @Override
    public void initialize (URL url, ResourceBundle resourceBundle) {
        clientGameModel.getCardsInHandObservable().addListener(new ListChangeListener() {
            @Override
            public void onChanged (Change change) {
                Platform.runLater(() -> {
                    for (int i = 0; i < clientGameModel.getCardsInHandObservable().size(); i++) {
                        switch (i) {
                            case 0: {
                                card0.setText(clientGameModel.getCardsInHandObservable().get(i).toString());
                            }
                            case 1: {
                                card1.setText(clientGameModel.getCardsInHandObservable().get(i).toString());
                            }
                            case 2: {
                                card2.setText(clientGameModel.getCardsInHandObservable().get(i).toString());
                            }
                            case 3: {
                                card3.setText(clientGameModel.getCardsInHandObservable().get(i).toString());
                            }
                            case 4: {
                                card4.setText(clientGameModel.getCardsInHandObservable().get(i).toString());
                            }
                            case 5: {
                                card5.setText(clientGameModel.getCardsInHandObservable().get(i).toString());
                            }
                            case 6: {
                                card6.setText(clientGameModel.getCardsInHandObservable().get(i).toString());
                            }
                            case 7: {
                                card7.setText(clientGameModel.getCardsInHandObservable().get(i).toString());
                            }
                            case 8: {
                                card8.setText(clientGameModel.getCardsInHandObservable().get(i).toString());
                            }
                        }
                    }
                });
            }
        });
    }
}

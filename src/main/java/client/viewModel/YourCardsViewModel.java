package client.viewModel;

import client.model.ClientGameModel;
import client.model.ClientModel;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import json.JSONMessage;
import json.protocol.PlayCardBody;
import json.protocol.SelectedCardBody;


import java.net.URL;
import java.util.HashMap;
import java.util.Map;
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
    @FXML
    public Button reg0;
    @FXML
    public Button reg1;
    @FXML
    public Button reg2;
    @FXML
    public Button reg3;
    @FXML
    public Button reg4;
    @FXML
    public Button send;


    public HashMap<Integer, String> registerMap = new HashMap<>();
    public HashMap<Integer, Integer> regiterToCard = new HashMap<>();

    public int firstAvailableRegister () {
        for (Map.Entry<Integer, String> entry : registerMap.entrySet()) {
            if (entry.getValue().equals("Null")) {
                System.out.println(entry.getKey());
                return entry.getKey();
            }
        }
        return -1;
    }

    public Button getRegisterButton (int i) {
        Button event = null;
        switch (i) {
            case 0: {
                event = reg0;
                break;
            }
            case 1: {
                event = reg1;
                break;
            }
            case 2: {
                event = reg2;
                break;
            }
            case 3: {
                event = reg3;
                break;
            }
            case 4: {
                event = reg4;
                break;
            }

        }
        return event;
    }

    public Button getCardButton (int card) {
        Button event = null;
        switch (card) {
            case 0: {
                event = card0;
                break;
            }
            case 1: {
                event = card1;
                break;
            }
            case 2: {
                event = card2;
                break;
            }
            case 3: {
                event = card3;
                break;
            }
            case 4: {
                event = card4;
                break;
            }
            case 5: {
                event = card5;
                break;
            }
            case 6: {
                event = card6;
                break;
            }
            case 7: {
                event = card7;
                break;
            }
            case 8: {
                event = card8;
                break;
            }
        }
        return event;
    }

    public void playCard () {
        JSONMessage playCard = new JSONMessage("PlayCard", new PlayCardBody(reg0.getText()));
        model.sendMessage(playCard);
    }

    //TODO:WICHTIG
    public void chooseCard (int card) {
        int register = firstAvailableRegister();
        Button event = null;
        if (register != -1) {
            event = getCardButton(card);
            event.setDisable(true);
            String cardText = event.getText();
            getRegisterButton(register).setText(cardText);
            registerMap.replace(register, cardText);
            regiterToCard.put(register, card);
            JSONMessage jsonMessage = new JSONMessage("SelectedCard", new SelectedCardBody(cardText, register + 1));
            model.sendMessage(jsonMessage);
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Register is full!");
            alert.show();
        }
    }
    //


    public void chooseReg (int register) {
        int card = regiterToCard.get(register);
        Button registerButton = getRegisterButton(register);
        String cardText = registerButton.getText();
        registerButton.setText("reg" + register);
        Button cardButton = getCardButton(card);
        cardButton.setText(cardText);
        registerMap.replace(register, "Null");
        cardButton.setDisable(false);
        JSONMessage jsonMessage = new JSONMessage("SelectedCard", new SelectedCardBody("Null", register + 1));
        model.sendMessage(jsonMessage);
    }


    public void chooseReg0 () {
        chooseReg(0);
    }

    public void chooseReg1 () {
        chooseReg(1);
    }

    public void chooseReg2 () {
        chooseReg(2);
    }

    public void chooseReg3 () {
        chooseReg(3);
    }

    public void chooseReg4 () {
        chooseReg(4);
    }


    public void chooseCard0 () {
        chooseCard(0);
    }

    public void chooseCard1 () {
        chooseCard(1);
    }

    public void chooseCard2 () {
        chooseCard(2);
    }

    public void chooseCard3 () {
        chooseCard(3);
    }

    public void chooseCard4 () {
        chooseCard(4);
    }

    public void chooseCard5 () {
        chooseCard(5);
    }

    public void chooseCard6 () {
        chooseCard(6);
    }

    public void chooseCard7 () {
        chooseCard(7);
    }

    public void chooseCard8 () {
        chooseCard(8);
    }


    @Override
    public void initialize (URL url, ResourceBundle resourceBundle) {
        registerMap.put(0, "Null");
        registerMap.put(1, "Null");
        registerMap.put(2, "Null");
        registerMap.put(3, "Null");
        registerMap.put(4, "Null");
        //ARRAY FOR PROGRAMMING CARDS
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

package client.viewModel;

import client.model.ClientGameModel;
import client.model.ClientModel;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.ResourceBundle;

public class MemorySwapEffekt implements Initializable {


    public HBox returnedCards;
    public ImageView returnCard1;
    public ImageView returnCard2;
    public ImageView returnCard3;
    public ImageView OkButton;

    ObservableList<ImageView> cards;
    ObservableList<ImageView> allReturnedCards;

    public ClientModel model = ClientModel.getInstance();
    public ClientGameModel clientGameModel = ClientGameModel.getInstance();
    public String cardName;
    ArrayList<String> allReturnedCardsL = new ArrayList<>();

    @FXML
    public ImageView card_0;
    @FXML
    public ImageView card_1;
    @FXML
    public ImageView card_2;
    @FXML
    public ImageView card_3;
    @FXML
    public ImageView card_4;
    @FXML
    public ImageView card_5;
    @FXML
    public ImageView card_6;
    @FXML
    public ImageView card_7;
    @FXML
    public ImageView card_8;
    @FXML
    public ImageView card_9;
    @FXML
    public ImageView card_10;
    @FXML
    public ImageView card_11;


    int j = 0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cards = FXCollections.observableArrayList(card_0, card_1, card_2, card_3, card_4, card_5,
                card_6, card_7, card_8, card_9, card_10, card_11);
        Platform.runLater(() -> {
            try {
                for(int j = 0; j < cards.size(); j++) {
                    cardName = clientGameModel.getCardsInHand().get(j);
                    cards.get(j).setImage(loadImage(cardName));
                    cards.get(j).setId(cardName);
                }
                for(int j = clientGameModel.getCardsInHand().size(); j < cards.size(); j++) {
                    cards.get(j).setImage(null);
                    cards.get(j).setId("Null");
                }
            } catch(ArrayIndexOutOfBoundsException | FileNotFoundException e) {
                e.printStackTrace();
            }
        });
    }


    private Image loadImage(String cardName) throws FileNotFoundException {
        return new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/ProgrammingCards/" + cardName + ".png")));
    }

    /**
     * Choose return card.
     *
     * @param mouseEvent the mouse event
     */
    public void chooseReturnCard(MouseEvent mouseEvent) {
        ImageView returnCard = (ImageView) mouseEvent.getSource();
        String cardName = returnCard.getId();
        allReturnedCardsL.add(cardName);
        allReturnedCards = FXCollections.observableArrayList(returnCard1, returnCard2, returnCard3);
        try {
            for(int i = 0; i < allReturnedCards.size(); i++) {
                allReturnedCards.get(j).setImage(returnCard.getImage());
                j++;
                break;
            }
            returnCard.setImage(null);
        } catch(ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setContentText("you may choose only 3 Cards");
        }
    }

    /**
     * Return cards.
     *
     * @param mouseEvent the mouse event
     */
    public void returnCards(MouseEvent mouseEvent) {
        clientGameModel.sendRetrunCards(allReturnedCardsL);
        // clientGameModel.finishRetunrCard ( true );
        Stage stage = (Stage) OkButton.getScene().getWindow();
        stage.close();
    }
}


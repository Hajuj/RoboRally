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

/**
 * The type Memory swap effekt.
 */
public class MemorySwapEffekt implements Initializable {


    /**
     * The Returned cards.
     */
    public HBox returnedCards;
    /**
     * The Return card 1.
     */
    public ImageView returnCard1;
    /**
     * The Return card 2.
     */
    public ImageView returnCard2;
    /**
     * The Return card 3.
     */
    public ImageView returnCard3;
    /**
     * The Ok button.
     */
    public ImageView OkButton;

    /**
     * The Cards.
     */
    ObservableList<ImageView> cards;
    /**
     * The All returned cards.
     */
    ObservableList<ImageView> allReturnedCards;

    /**
     * The Model.
     */
    public ClientModel model = ClientModel.getInstance();
    /**
     * The Client game model.
     */
    public ClientGameModel clientGameModel = ClientGameModel.getInstance();
    /**
     * The Card name.
     */
    public String cardName;
    /**
     * The All returned cards l.
     */
    ArrayList<String> allReturnedCardsL = new ArrayList<>();

    /**
     * The Card 0.
     */
    @FXML
    public ImageView card_0;
    /**
     * The Card 1.
     */
    @FXML
    public ImageView card_1;
    /**
     * The Card 2.
     */
    @FXML
    public ImageView card_2;
    /**
     * The Card 3.
     */
    @FXML
    public ImageView card_3;
    /**
     * The Card 4.
     */
    @FXML
    public ImageView card_4;
    /**
     * The Card 5.
     */
    @FXML
    public ImageView card_5;
    /**
     * The Card 6.
     */
    @FXML
    public ImageView card_6;
    /**
     * The Card 7.
     */
    @FXML
    public ImageView card_7;
    /**
     * The Card 8.
     */
    @FXML
    public ImageView card_8;
    /**
     * The Card 9.
     */
    @FXML
    public ImageView card_9;
    /**
     * The Card 10.
     */
    @FXML
    public ImageView card_10;
    /**
     * The Card 11.
     */
    @FXML
    public ImageView card_11;


    /**
     * The J.
     */
    int j = 0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cards = FXCollections.observableArrayList(card_0, card_1, card_2, card_3, card_4, card_5,
                card_6, card_7, card_8, card_9,card_10,card_11);
        Platform.runLater(() -> {
            try {
                for (int j = 0; j < cards.size(); j++) {
                    cardName = clientGameModel.getCardsInHand().get(j);
                    cards.get(j).setImage(loadImage(cardName));
                    cards.get(j).setId(cardName);
                }
                for (int j = clientGameModel.getCardsInHand().size(); j < cards.size(); j++) {
                    cards.get(j).setImage(null);
                    cards.get(j).setId("Null");
                }
            } catch (ArrayIndexOutOfBoundsException | FileNotFoundException e) {
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
            for (int i = 0; i < allReturnedCards.size(); i++) {
                allReturnedCards.get(j).setImage(returnCard.getImage());
                j++;
                break;
            }
            returnCard.setImage(null);
        } catch (ArrayIndexOutOfBoundsException e) {
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
       /* for (String card:allReturnedCardsL) {
            if (clientGameModel.getCardsInHand ().contains ( card )){

            }
        }*/
        clientGameModel.sendRetrunCards(allReturnedCardsL);
        // clientGameModel.finishRetunrCard ( true );
        Stage stage = (Stage) OkButton.getScene().getWindow();
        stage.close();
    }
}


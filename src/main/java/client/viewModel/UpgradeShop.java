package client.viewModel;

import client.model.ClientGameModel;
import client.model.ClientModel;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.beans.PropertyChangeListener;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.*;

/**
 * @author Lilas
 */

public class UpgradeShop implements Initializable {
    public Tooltip toolTip1;
    public Text cubesNum;

    @FXML
    public ImageView card_1;
    public ImageView card_2;
    public ImageView card_3;
    public ImageView card_4;
    public ImageView card_5;
    public ImageView card_6;

    public ImageView buyButton;
    public ImageView buyNothing;
    public Text info;
    String cardName;

    ObservableList<ImageView> upgradeCards;
    public ClientModel model = ClientModel.getInstance();
    public ClientGameModel clientGameModel = ClientGameModel.getInstance();
    private StringProperty choosenUpgradeCard = new SimpleStringProperty ( "" );

    /**
     * in the following initialize methode cubesNum will show the value of the the player's
     * energyCubes he possesses
     * upgradeCards is an observableArrayList that observes the value of the showen upgradeCards
     * **/

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        buyButton.setDisable ( true );
        buyButton.setVisible ( false );
        cubesNum.setText ( String.valueOf ( clientGameModel.getEnergy () ) );
        upgradeCards = FXCollections.observableArrayList ( card_1, card_2, card_3, card_4, card_5, card_6);
        Platform.runLater ( () -> {
            try {
                for (int j = 0; j < clientGameModel.getUpgradeCards ( ).size ( ); j++) {
                    cardName = clientGameModel.getUpgradeCards ( ).get ( j );
                    upgradeCards.get ( j ).setImage ( loadImage ( cardName ) );

                    upgradeCards.get ( j ).setId ( cardName );

                }
                for (int j = clientGameModel.getUpgradeCards ( ).size ( ); j < upgradeCards.size ( ); j++) {
                        upgradeCards.get ( j ).setImage ( null );
                        upgradeCards.get ( j ).setId ( "null" );

                }
            } catch (ArrayIndexOutOfBoundsException | FileNotFoundException e) {
                e.printStackTrace ( );
            }

        } );


    }



    public void showDescription(MouseEvent mouseEvent) {
        ImageView source  = (ImageView) mouseEvent.getSource ();
        switch (source.getId ()){
            case "AdminPrivilege"-> {
                toolTip1.setText ( "give your robot priority for one register." );
            }
            case "MemorySwap"-> {
                toolTip1.setText ( "Draw three cards. Then choose three from your hand to put on top of your deck." );
            }
            case "SpamBlocker"-> {
                toolTip1.setText ( "Replace each SPAM damage card in your hand with a card from the top of your deck." );
            }
            case "RearLaser"-> {
                toolTip1.setText ( "shoot backward as well as forward." );
            }
        }
    }

    public void buyCard(MouseEvent mouseEvent) {
        Stage stage =(Stage) buyNothing.getScene ().getWindow ();
        if (mouseEvent.getSource ().equals ( buyButton )){
                clientGameModel.buyUpgradeCard ( getChoosenUpgradeCard ( ) );
                clientGameModel.finishBuyCard ( true );
                info.setText ( "You dont have enough Energy Cubes" );

        }else if (mouseEvent.getSource ().equals ( buyNothing )) {
            clientGameModel.buyUpgradeCard ( "Null" );
            stage.close ();
        }
        stage.close ();
    }

    public void chooseUpgradeCard(MouseEvent mouseEvent) {
        ImageView choosenCard = (ImageView) mouseEvent.getSource ();
        if (choosenCard.getId ().equals ( null )||choosenCard.getId ().equals ( "null" )){
            buyButton.setDisable ( true);
            buyButton.setVisible ( false );
        }else {
            buyButton.setDisable ( false );
            buyButton.setVisible ( true );
            refreshShadow ( );
            choosenCard.setEffect ( new DropShadow ( 20.0, Color.RED ) );

            setChoosenUpgradeCard ( choosenCard.getId ( ) );
        }
    }
    public void refreshShadow () {
        for (ImageView upgradeCard:upgradeCards) {
            upgradeCard.setEffect ( new DropShadow ( 0.0,Color.RED ) );
        }
    }

    private Image loadImage(String cardName) throws FileNotFoundException {
        return new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/UpgradeCards/" + cardName + ".png")));
    }

    public void setChoosenUpgradeCard (String cardName) {
        this.choosenUpgradeCard.set(cardName);
    }

    public String getChoosenUpgradeCard() {
        return this.choosenUpgradeCard.get ( );
    }

}
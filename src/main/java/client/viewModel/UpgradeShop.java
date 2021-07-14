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
    String cardName;

    ObservableList<ImageView> upgradeCards;
    public ClientModel model = ClientModel.getInstance();
    public ClientGameModel clientGameModel = ClientGameModel.getInstance();
    private StringProperty choosenUpgradeCard = new SimpleStringProperty ( "" );



    @Override
    public void initialize(URL location, ResourceBundle resources) {
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
                       // upgradeCards.get ( j ).setId ( "Null" );
                }
                disableChoosenCards();
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
        if (mouseEvent.getSource ().equals ( buyButton )){
            clientGameModel.buyUpgradeCard(getChoosenUpgradeCard ());
            clientGameModel.finishBuyCard(true);

        }else if (mouseEvent.getSource ().equals ( buyNothing )) {
            clientGameModel.buyUpgradeCard ( "Null" );
            Stage stage =(Stage) buyNothing.getScene ().getWindow ();
            stage.close ();
        }

        Stage stage = (Stage) buyButton.getScene ().getWindow ();
        stage.close ();
    }

    public void chooseUpgradeCard(MouseEvent mouseEvent) {
        ImageView choosenCard = (ImageView) mouseEvent.getSource ();
        if (choosenCard.equals ( null )){
            buyButton.setDisable ( true );
            buyButton.setVisible ( false );
        }else {
            refreshShadow ( );
            choosenCard.setEffect ( new DropShadow ( 20.0, Color.RED ) );

            setChoosenUpgradeCard ( choosenCard.getId ( ) );
        }
    }
    public void refreshShadow () {
        for (ImageView upgradeCard:upgradeCards) {
            upgradeCard.setEffect ( new DropShadow ( 0.0,Color.RED ) );
        }
        //disableChoosenCards ();
    }
    public void disableChoosenCards () {
        GaussianBlur blur = new GaussianBlur(10);
        for (ImageView boughtCard : upgradeCards) {
            if (clientGameModel.getUpgradBoughtCards ().contains ( boughtCard.getId () )){
                boughtCard.setDisable ( true );
                boughtCard.setEffect ( blur );
            }
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
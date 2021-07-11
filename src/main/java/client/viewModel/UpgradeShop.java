package client.viewModel;

import client.model.ClientGameModel;
import client.model.ClientModel;
import game.upgradecards.SpamBlocker;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.*;


public class UpgradeShop implements Initializable, PropertyChangeListener {
    public Tooltip toolTip1;
    public Text cubesNum;

    @FXML
    public ImageView card_1;
    public ImageView card_2;
    public ImageView card_3;
    public ImageView card_4;
    public ImageView card_5;
    public ImageView card_6;
    public ImageView card_7;
    public ImageView card_8;
    public ImageView card_9;
    public ImageView card_10;
    public ImageView buyButton;
    public ImageView buyNothing;
    String cardName;

    ObservableList<ImageView> upgradeCards;
    public ClientModel model = ClientModel.getInstance();
    public ClientGameModel clientGameModel = ClientGameModel.getInstance();
    private StringProperty choosenUpgradeCard = new SimpleStringProperty ( "" );
    //private IntegerProperty energyCount = new SimpleIntegerProperty ( 0 );


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        //energyCount.bind ( energyCountProperty() );
        //cubesNum.setText ( energyCount.toString () );
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
                        upgradeCards.get ( j ).setId ( "Null" );
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
        if (mouseEvent.getSource ().equals ( buyButton )){
            clientGameModel.buyUpgradeCard(getChoosenUpgradeCard ());

        }else if (mouseEvent.getSource ().equals ( buyNothing )) {
            clientGameModel.buyUpgradeCard ( "Null" );
        }
        clientGameModel.finishBuyCard(true);
        Stage stage = (Stage) buyButton.getScene ().getWindow ();
        stage.close ();
    }

    public void chooseUpgradeCard(MouseEvent mouseEvent) {
        ImageView choosenCard = (ImageView) mouseEvent.getSource ();
        if (choosenCard.equals ( null )){
            buyButton.setDisable ( true );
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
        disableChoosenCards();
    }
    public void disableChoosenCards () {
        GaussianBlur blur = new GaussianBlur(10);

    }

    public void refillUpgradeShop(){}

    @Override
    public void propertyChange (PropertyChangeEvent evt) {
/*
        if (evt.getPropertyName ().equals ( "refillShop" )) {
            System.out.println ( "muss shop füllen " );
            clientGameModel.refillShop ( false );
        }*/
    }

    private Image loadImage(String cardName) throws FileNotFoundException {
        Image image;
        FileInputStream path = new FileInputStream(( Objects.requireNonNull(getClass().getClassLoader().getResource( "images/UpgradeCards/" + cardName + ".png" )).getFile()));
        image = new Image(path);
        return image;
    }

    public void setChoosenUpgradeCard (String cardName) {
        this.choosenUpgradeCard.set(cardName);
    }

    public String getChoosenUpgradeCard() {
        return this.choosenUpgradeCard.get ( );
    }

  /*  public IntegerProperty energyCountProperty() {
        this.energyCount.set ( clientGameModel.getEnergy () );
        return this.energyCount;
    }*/
/*
    public void setEnergyCount(final int energyCount) {
        this.energyCount.set ( energyCount );
    }*/
}

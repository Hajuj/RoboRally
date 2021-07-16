package client.viewModel;

import client.model.ClientModel;
import client.model.ClientGameModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
/**
 * @author Lilas
 */
public class RebootDirectionViewModel implements Initializable {
    public ImageView robotAvatar;
    public ImageView robotOrientation;
    ClientModel clientModel = ClientModel.getInstance();
    ClientGameModel clientGameModel = ClientGameModel.getInstance();

    @FXML
    public Button top;
    @FXML
    public Button bottom;
    @FXML
    public Button right;
    @FXML
    public Button left;

    /**
     * Chooses top as orientation.
     */
    @FXML
    public void chooseTop() {
        clientModel.getClientGameModel().sendRebootDirection("top");
    }

    /**
     * Chooses bottom as orientation.
     */
    @FXML
    public void chooseBottom() {
        clientModel.getClientGameModel().sendRebootDirection("bottom");
    }

    /**
     * Chooses left as orientation.
     */
    @FXML
    public void chooseLeft() {
        clientModel.getClientGameModel().sendRebootDirection("left");
    }

    /**
     * Chooses right as orientation.
     */
    @FXML
    public void chooseRight() {
        clientModel.getClientGameModel().sendRebootDirection("right");
    }

    /**
     * Your robot image.
     *
     * @return the image
     */
    public Image yourRobot() {

        int figure = clientGameModel.getPlayer().getFigure();
        return new Image(Objects.requireNonNull(getClass().getResourceAsStream("/Robots/robot" + figure + ".png")));

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        robotAvatar.setImage(yourRobot());
    }

    /**
     * Show orientation.
     * Sets the image in the place according to its orientation
     *
     * @param mouseEvent the mouse event
     */
    public void showOrientation(MouseEvent mouseEvent) {
        Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/TransparentElements/RobotDirectionArrowHUGE.png")));
        if(mouseEvent.getSource().equals(top)) {
            robotOrientation.setRotate(180);
            robotOrientation.setImage(image);
        }
        if(mouseEvent.getSource().equals(bottom)) {
            robotOrientation.setRotate(0);
            robotOrientation.setImage(image);
        }
        if(mouseEvent.getSource().equals(left)) {
            robotOrientation.setRotate(90);
            robotOrientation.setImage(image);
        }
        if(mouseEvent.getSource().equals(right)) {
            robotOrientation.setRotate(-90);
            robotOrientation.setImage(image);
        }
    }
}

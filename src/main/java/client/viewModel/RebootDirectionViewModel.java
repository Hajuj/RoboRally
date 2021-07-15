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
 * The type Reboot direction view model.
 */
public class RebootDirectionViewModel implements Initializable {
    /**
     * The Robot avatar.
     */
    public ImageView robotAvatar;
    /**
     * The Robot orientation.
     */
    public ImageView robotOrientation;
    /**
     * The Client model.
     */
    ClientModel clientModel = ClientModel.getInstance();
    /**
     * The Client game model.
     */
    ClientGameModel clientGameModel = ClientGameModel.getInstance();

    /**
     * The Top.
     */
    @FXML
    public Button top;
    /**
     * The Bottom.
     */
    @FXML
    public Button bottom;
    /**
     * The Right.
     */
    @FXML
    public Button right;
    /**
     * The Left.
     */
    @FXML
    public Button left;


    /**
     * Choose top.
     */
    @FXML
    public void chooseTop() {
        clientModel.getClientGameModel().sendRebootDirection("top");
    }

    /**
     * Choose bottom.
     */
    @FXML
    public void chooseBottom() {
        clientModel.getClientGameModel().sendRebootDirection("bottom");
    }

    /**
     * Choose left.
     */
    @FXML
    public void chooseLeft() {
        clientModel.getClientGameModel().sendRebootDirection("left");
    }

    /**
     * Choose right.
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
        if (mouseEvent.getSource().equals(top)) {
            robotOrientation.setRotate(180);
            robotOrientation.setImage(image);
        }
        if (mouseEvent.getSource().equals(bottom)) {
            robotOrientation.setRotate(0);
            robotOrientation.setImage(image);
        }
        if (mouseEvent.getSource().equals(left)) {
            robotOrientation.setRotate(90);
            robotOrientation.setImage(image);
        }
        if (mouseEvent.getSource().equals(right)) {
            robotOrientation.setRotate(-90);
            robotOrientation.setImage(image);
        }
    }
}

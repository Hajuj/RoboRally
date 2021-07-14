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


    @FXML
    public void chooseTop() {
        clientModel.getClientGameModel().sendRebootDirection("top");
    }

    @FXML
    public void chooseBottom() {
        clientModel.getClientGameModel().sendRebootDirection("bottom");
    }

    @FXML
    public void chooseLeft() {
        clientModel.getClientGameModel().sendRebootDirection("left");
    }

    @FXML
    public void chooseRight() {
        clientModel.getClientGameModel().sendRebootDirection("right");
    }

    public Image yourRobot() {

        int figure = clientGameModel.getPlayer().getFigure();
        return new Image(Objects.requireNonNull(getClass().getResourceAsStream("/Robots/robot" + figure + ".png")));

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        robotAvatar.setImage(yourRobot());
    }


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

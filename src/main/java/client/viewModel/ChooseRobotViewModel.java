package client.viewModel;

import client.model.ClientModel;
import game.Game;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * The type Choose robot view model.
 */
public class ChooseRobotViewModel implements Initializable {
    /**
     * The Model.
     */
    ClientModel model = ClientModel.getInstance();

    /**
     * The Play button.
     */
    public Button playButton;
    /**
     * The Name field.
     */
    public TextField nameField = new TextField("");
    private String username;
    private IntegerProperty figureProperty = new SimpleIntegerProperty(-1);
    private static Logger logger = Logger.getLogger(ChooseRobotViewModel.class.getName());

    /**
     * The Robot 0.
     */
    @FXML
    public ImageView robot0;
    /**
     * The Robot 1.
     */
    @FXML
    public ImageView robot1;
    /**
     * The Robot 2.
     */
    @FXML
    public ImageView robot2;
    /**
     * The Robot 3.
     */
    @FXML
    public ImageView robot3;
    /**
     * The Robot 4.
     */
    @FXML
    public ImageView robot4;
    /**
     * The Robot 5.
     */
    @FXML
    public ImageView robot5;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        playButton.setDisable(true);
        refreshShadow();

        nameField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                if (!isValideUsername(t1)) {
                    playButton.setDisable(true);
                } else if (figureProperty.getValue() != -1) {
                    playButton.setDisable(false);
                } else if (isGameOn()) {
                    playButton.setDefaultButton(true);
                    playButton.setDisable(false);
                }
            }
        });

        figureProperty.addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                if (figureProperty.getValue() == -1) {
                    playButton.setDisable(true);
                } else if (isValideUsername(nameField.getText())) {
                    playButton.setDisable(false);
                }
            }
        });

        disableUsedRobots();

        //TODO: SCHAUEN WIE WIR DAS ANDERS machen
        //  if (isGameOn() || !model.isCanPlay()) {
        //     playButton.setText("Chat!");
        //     playButton.setDisable(false);
        //       disableAllRobots();
        //   }

    }

    /**
     * Disable used robots.
     * Blurs a robot if it is chosen by someone else
     */
    public void disableUsedRobots() {
        GaussianBlur blur = new GaussianBlur(10);
        for (Map.Entry<Integer, Integer> entry : model.getPlayersFigureMap().entrySet()) {
            switch (entry.getValue()) {
                case 0 -> {
                    robot0.setDisable(true);
                    robot0.setEffect(blur);
                }
                case 1 -> {
                    robot1.setDisable(true);
                    robot1.setEffect(blur);
                }
                case 2 -> {
                    robot2.setDisable(true);
                    robot2.setEffect(blur);
                }
                case 3 -> {
                    robot3.setDisable(true);
                    robot3.setEffect(blur);
                }
                case 4 -> {
                    robot4.setDisable(true);
                    robot4.setEffect(blur);
                }
                case 5 -> {
                    robot5.setDisable(true);
                    robot5.setEffect(blur);
                }
            }
        }
    }

    /**
     * Disable all robots.
     * blurs all robots
     */
    public void disableAllRobots() {
        GaussianBlur blur = new GaussianBlur(10);

        robot0.setDisable(true);
        robot0.setEffect(blur);

        robot1.setDisable(true);
        robot1.setEffect(blur);

        robot2.setDisable(true);
        robot2.setEffect(blur);

        robot3.setDisable(true);
        robot3.setEffect(blur);

        robot4.setDisable(true);
        robot4.setEffect(blur);

        robot5.setDisable(true);
        robot5.setEffect(blur);

    }

    /**
     * Refresh shadow.
     * Cool looking thing around the robots.
     * Really cool.
     */
    public void refreshShadow() {
        robot0.setEffect(new DropShadow(0.0, Color.RED));
        robot1.setEffect(new DropShadow(0.0, Color.RED));
        robot2.setEffect(new DropShadow(0.0, Color.RED));
        robot3.setEffect(new DropShadow(0.0, Color.RED));
        robot4.setEffect(new DropShadow(0.0, Color.RED));
        robot5.setEffect(new DropShadow(0.0, Color.RED));

        robot0.setEffect(new DropShadow(20.0, Color.WHITE));
        robot1.setEffect(new DropShadow(20.0, Color.WHITE));
        robot2.setEffect(new DropShadow(20.0, Color.WHITE));
        robot3.setEffect(new DropShadow(20.0, Color.WHITE));
        robot4.setEffect(new DropShadow(20.0, Color.WHITE));
        robot5.setEffect(new DropShadow(20.0, Color.WHITE));

        disableUsedRobots();
    }


    /**
     * Sets robot 0.
     */
    public void setRobot0() {
        refreshShadow();
        robot0.setEffect(new DropShadow(20.0, Color.RED));
        setFigureProperty(0);
        logger.info("Robot " + Game.getRobotNames().get(figureProperty.getValue()) + " has been set.");
    }

    /**
     * Sets robot 1.
     */
    public void setRobot1() {
        refreshShadow();
        robot1.setEffect(new DropShadow(20.0, Color.RED));
        setFigureProperty(1);
        logger.info("Robot " + Game.getRobotNames().get(figureProperty.getValue()) + " has been set.");
    }

    /**
     * Sets robot 2.
     */
    public void setRobot2() {
        refreshShadow();
        robot2.setEffect(new DropShadow(20.0, Color.RED));
        setFigureProperty(2);
        logger.info("Robot " + Game.getRobotNames().get(figureProperty.getValue()) + " has been set.");
    }

    /**
     * Sets robot 3.
     */
    public void setRobot3() {
        refreshShadow();
        robot3.setEffect(new DropShadow(20.0, Color.RED));
        setFigureProperty(3);
        logger.info("Robot " + Game.getRobotNames().get(figureProperty.getValue()) + " has been set.");
    }

    /**
     * Sets robot 4.
     */
    public void setRobot4() {
        refreshShadow();
        robot4.setEffect(new DropShadow(20.0, Color.RED));
        setFigureProperty(4);
        logger.info("Robot " + Game.getRobotNames().get(figureProperty.getValue()) + " has been set.");
    }

    /**
     * Sets robot 5.
     */
    public void setRobot5() {
        refreshShadow();
        robot5.setEffect(new DropShadow(20.0, Color.RED));
        setFigureProperty(5);
        setFigureProperty(5);
        logger.info("Robot " + Game.getRobotNames().get(figureProperty.getValue()) + " has been set.");
    }

    /**
     * Play button clicked.
     * Gets the players values and figure
     * Opens the GameStage accordingly
     */
    public void playButtonClicked() {
        try {
            username = nameField.getText();
            model.getClientGameModel().getPlayer().setName(username);
            model.getClientGameModel().getPlayer().setFigure(figureProperty.getValue());
            logger.info("Username " + username + " has been set.");
            model.sendUsernameAndRobot(username, figureProperty.getValue());
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/GameStage.fxml")));
            Stage window = (Stage) playButton.getScene().getWindow();
            window.setScene(new Scene(root, 1223, 665));

            //window.setResizable(false);
            //window.maxHeightProperty().bind(window.getScene().heightProperty());
            // window.sizeToScene();
            /*window.minHeightProperty().bind(window.widthProperty());
            window.maxHeightProperty().bind(window.getScene().widthProperty());*/
        } catch (
                IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Is valide username boolean.
     *
     * @param username the username
     * @return the boolean
     */
    public boolean isValideUsername(String username) {
        if (username.isBlank()) return false;
        if (username.contains(" ")) return false;
        return !username.contains("@");
    }

    /**
     * Is game on boolean.
     *
     * @return the boolean
     */
    public boolean isGameOn() {
        return (model.getPlayersFigureMap().size() >= 6);
    }

    /**
     * Gets figure property.
     *
     * @return the figure property
     */
    public int getFigureProperty() {
        return figureProperty.get();
    }

    /**
     * Figure property property integer property.
     *
     * @return the integer property
     */
    public IntegerProperty figurePropertyProperty() {
        return figureProperty;
    }

    /**
     * Sets figure property.
     *
     * @param figureProperty the figure property
     */
    public void setFigureProperty(int figureProperty) {
        this.figureProperty.set(figureProperty);
    }
}

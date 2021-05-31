package client.viewModel;

import client.model.ClientModel;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.apache.log4j.Logger;
import server.Server;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

public class ChooseRobotViewModel implements Initializable {

    public Button playButton;
    public TextField nameField;
    private String username;
    ClientModel model = ClientModel.getInstance();
    private IntegerProperty figureProperty = new SimpleIntegerProperty(-1);
    private static final Logger logger = Logger.getLogger(ChooseRobotViewModel.class.getName());


    @FXML
    private ImageView robot0;
    @FXML
    private ImageView robot1;
    @FXML
    private ImageView robot2;
    @FXML
    private ImageView robot4;
    @FXML
    private ImageView robot5;
    @FXML
    private ImageView robot6;


    @Override
    public void initialize (URL location, ResourceBundle resources) {
        playButton.setDisable(true);

        nameField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed (ObservableValue<? extends String> observableValue, String s, String t1) {
                if (!isValideUsername(t1)) {
                    playButton.setDisable(true);
                } else {
                    playButton.setDisable(false);
                }
            }
        });

        figureProperty.addListener(new ChangeListener<Number>() {
            @Override
            public void changed (ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                if (figureProperty.getValue() == -1) {
                    playButton.setDisable(true);
                }
            }
        });

        disableUsedRobots();
    }

    public void disableUsedRobots () {
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
                    robot4.setDisable(true);
                    robot4.setEffect(blur);
                }
                case 4 -> {
                    robot5.setDisable(true);
                    robot5.setEffect(blur);
                }
                case 5 -> {
                    robot6.setDisable(true);
                    robot6.setEffect(blur);
                }
            }
        }
    }

    public void refreshShadow () {
        robot0.setEffect(new DropShadow(0.0, Color.RED));
        robot1.setEffect(new DropShadow(0.0, Color.RED));
        robot2.setEffect(new DropShadow(0.0, Color.RED));
        robot4.setEffect(new DropShadow(0.0, Color.RED));
        robot5.setEffect(new DropShadow(0.0, Color.RED));
        robot6.setEffect(new DropShadow(0.0, Color.RED));
        disableUsedRobots();
    }


    public void setRobot0 () {
        refreshShadow();
        logger.info("Robot 0 has been set.");
        robot0.setEffect(new DropShadow(20.0, Color.RED));
        setFigureProperty(0);
    }

    public void setRobot1 () {
        refreshShadow();
        logger.info("Robot 1 has been set.");
        robot1.setEffect(new DropShadow(20.0, Color.RED));
        setFigureProperty(1);
    }

    public void setRobot2 () {
        refreshShadow();
        logger.info("Robot 2 has been set.");
        robot2.setEffect(new DropShadow(20.0, Color.RED));
        setFigureProperty(2);
    }

    public void setRobot3 () {
        refreshShadow();
        logger.info("Robot 3 has been set.");
        robot4.setEffect(new DropShadow(20.0, Color.RED));
        setFigureProperty(3);

    }

    public void setRobot4 () {
        refreshShadow();
        logger.info("Robot 4 has been set.");
        robot5.setEffect(new DropShadow(20.0, Color.RED));
        setFigureProperty(4);
    }

    public void setRobot5 () {
        refreshShadow();
        logger.info("Robot 5 has been set.");
        robot6.setEffect(new DropShadow(20.0, Color.RED));
        setFigureProperty(5);

    }

    public void playButtonClicked () {
        try {
            username = nameField.getText();
            model.getPlayer().setName(username);
            model.getPlayer().setFigure(figureProperty.getValue());
            model.sendUsernameAndRobot(username, figureProperty.getValue());
            Parent root = FXMLLoader.load(getClass().getResource("/view/RoboChat.fxml"));
            Stage window = (Stage) playButton.getScene().getWindow();
            window.setScene(new Scene(root, 800, 800));

        } catch (
                IOException e) {
            e.printStackTrace();
        }

    }

    public boolean isValideUsername (String username) {
        if (username.isBlank()) return false;
        if (username.contains(" ")) return false;
        if (username.contains("@")) return false;
        return true;
    }

    public int getFigureProperty () {
        return figureProperty.get();
    }

    public IntegerProperty figurePropertyProperty () {
        return figureProperty;
    }

    public void setFigureProperty (int figureProperty) {
        this.figureProperty.set(figureProperty);
    }
}

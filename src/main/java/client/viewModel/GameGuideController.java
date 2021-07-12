package client.viewModel;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class GameGuideController {
    public Stage rootStage;

    @FXML
    public Button buttonCards;
    @FXML
    public Button buttonRobots;
    @FXML
    public Button buttonRules;
    @FXML
    public Button buttonCourses;


    @FXML
    public void buttonClicked(ActionEvent event) throws IOException {
        Parent root;

        if (event.getSource() == buttonCards) {
            this.rootStage = (Stage) buttonCards.getScene().getWindow();
            root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/CardsInfo.fxml")));
            Scene scene = new Scene(root);
            this.rootStage.setScene(scene);
            this.rootStage.show();
            rootStage.setResizable(false);
        }
        if (event.getSource() == buttonRobots) {
            this.rootStage = (Stage) buttonRobots.getScene().getWindow();
            root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/RobotsInfo.fxml")));
            Scene scene = new Scene(root);
            this.rootStage.setScene(scene);
            this.rootStage.show();
            rootStage.setResizable(false);

        }
        if (event.getSource() == buttonRules) {
            this.rootStage = (Stage) buttonRules.getScene().getWindow();
            root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/RulesInfo.fxml")));
            Scene scene = new Scene(root);
            this.rootStage.setScene(scene);
            this.rootStage.show();
            rootStage.setResizable(false);

        }
        if (event.getSource() == buttonCourses) {
            this.rootStage = (Stage) buttonCourses.getScene().getWindow();
            root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/CoursesInfo.fxml")));
            Scene scene = new Scene(root);
            this.rootStage.setScene(scene);
            rootStage.setResizable(false);
            this.rootStage.show();

        }


    }
}

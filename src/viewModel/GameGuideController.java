package viewModel;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class GameGuideController {
    private Stage rootStage;

    @FXML
    private Button buttonCards;
    @FXML
    private Button buttonRobots;
    @FXML
    private Button buttonRules;
    @FXML
    private Button buttonCourses;

    @FXML
    private Button buttonCard;
    @FXML
    private Button buttonRobot;
    @FXML
    private Button buttonRule;
    @FXML
    private Button buttonCourse;

    @FXML
    public void buttonClicked(ActionEvent event ) throws IOException {
        Parent root;

        if (event.getSource() == buttonCards) {
            this.rootStage = (Stage) buttonCards.getScene().getWindow();
            root = FXMLLoader.load(getClass().getResource("/views/WikiCards.fxml"));
            Scene scene = new Scene(root);
            this.rootStage.setScene(scene);
            this.rootStage.show();
        }
        if (event.getSource() == buttonRobots) {
            this.rootStage = (Stage) buttonRobots.getScene().getWindow();
            root = FXMLLoader.load(getClass().getResource("/views/WikiRobots.fxml"));
            Scene scene = new Scene(root);
            this.rootStage.setScene(scene);
            this.rootStage.show();
        }
        if (event.getSource() == buttonRules) {
            this.rootStage = (Stage) buttonRules.getScene().getWindow();
            root = FXMLLoader.load(getClass().getResource("/views/WikiRules.fxml"));
            Scene scene = new Scene(root);
            this.rootStage.setScene(scene);
            this.rootStage.show();
        }
        if (event.getSource() == buttonCourses) {
            this.rootStage = (Stage) buttonCourses.getScene().getWindow();
            root = FXMLLoader.load(getClass().getResource("/views/WikiCourses.fxml"));
            Scene scene = new Scene(root);
            this.rootStage.setScene(scene);
            this.rootStage.show();
        }
        if (event.getSource() == buttonCard) {
            this.rootStage = (Stage) buttonCard.getScene().getWindow();
            root = FXMLLoader.load(getClass().getResource("/views/WikiCards.fxml"));
            Scene scene = new Scene(root);
            this.rootStage.setScene(scene);
            this.rootStage.show();
        }
        if (event.getSource() == buttonRobot) {
            this.rootStage = (Stage) buttonRobot.getScene().getWindow();
            root = FXMLLoader.load(getClass().getResource("/views/WikiRobots.fxml"));
            Scene scene = new Scene(root);
            this.rootStage.setScene(scene);
            this.rootStage.show();
        }
        if (event.getSource() == buttonRule) {
            this.rootStage = (Stage) buttonRule.getScene().getWindow();
            root = FXMLLoader.load(getClass().getResource("/views/WikiRules.fxml"));
            Scene scene = new Scene(root);
            this.rootStage.setScene(scene);
            this.rootStage.show();
        }
        if (event.getSource() == buttonCourse) {
            this.rootStage = (Stage) buttonCourse.getScene().getWindow();
            root = FXMLLoader.load(getClass().getResource("/views/WikiCourses.fxml"));
            Scene scene = new Scene(root);
            this.rootStage.setScene(scene);
            this.rootStage.show();
        }

    }
}

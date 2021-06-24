package client.viewModel;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * The type Game guide controller.
 */
public class GameGuideController {
    /**
     * The Root stage.
     */
    public Stage rootStage;

    /**
     * The Button cards.
     */
    @FXML
    public Button buttonCards;
    /**
     * The Button robots.
     */
    @FXML
    public Button buttonRobots;
    /**
     * The Button rules.
     */
    @FXML
    public Button buttonRules;
    /**
     * The Button courses.
     */
    @FXML
    public Button buttonCourses;


    /**
     * Button clicked.
     * If the related button is clicked ,this method opens the page of that button.
     *
     * @param event the event
     * @throws IOException the io exception
     */
    @FXML
    public void buttonClicked (ActionEvent event) throws IOException {
        Parent root;

        if (event.getSource() == buttonCards) {
            this.rootStage = (Stage) buttonCards.getScene().getWindow();
            root = FXMLLoader.load(getClass().getResource("/view/CardsInfo.fxml"));
            Scene scene = new Scene(root);
            this.rootStage.setScene(scene);
            this.rootStage.show();
        }
        if (event.getSource() == buttonRobots) {
            this.rootStage = (Stage) buttonRobots.getScene().getWindow();
            root = FXMLLoader.load(getClass().getResource("/view/RobotsInfo.fxml"));
            Scene scene = new Scene(root);
            this.rootStage.setScene(scene);
            this.rootStage.show();
        }
        if (event.getSource() == buttonRules) {
            this.rootStage = (Stage) buttonRules.getScene().getWindow();
            root = FXMLLoader.load(getClass().getResource("/view/RulesInfo.fxml"));
            Scene scene = new Scene(root);
            this.rootStage.setScene(scene);
            this.rootStage.show();
        }
        if (event.getSource() == buttonCourses) {
            this.rootStage = (Stage) buttonCourses.getScene().getWindow();
            root = FXMLLoader.load(getClass().getResource("/view/CoursesInfo.fxml"));
            Scene scene = new Scene(root);
            this.rootStage.setScene(scene);
            this.rootStage.show();
        }


    }
}

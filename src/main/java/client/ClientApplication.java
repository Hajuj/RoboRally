package client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

import org.apache.log4j.Logger;

/**
 * Class client.ClientApplication to start a client.
 *
 * @author Vorprojekt
 */
public class ClientApplication extends Application {

    private static final Logger logger = Logger.getLogger(ClientApplication.class.getName());

    /**
     * starts before the method start - Lifecycle of JavaFX
     */
    @Override
    public void init () {
        logger.info("Init!");
    }

    /**
     * starts before the application will close - Lifecycle of JavaFX
     */
    @Override
    public void stop () {
        logger.info("Stage is closing..");
        System.exit(0);
    }

    /**
     * Design of the Stage including the set of the scene with fxml-File
     *
     * @param stage the window of the application
     */
    @Override
    public void start (Stage stage) throws Exception {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/Startscreen.fxml")));
        stage.setTitle("RoboRally Menu");
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        logger.info("Start!");
    }
}

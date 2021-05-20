package client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

/**
 * Class client.ClientApplication to start a client.
 *
 * @author Vorprojekt
 */
public class ClientApplication extends Application {

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * starts before the method start - Lifecycle of JavaFX
     */
    @Override
    public void init() {
        System.out.println("init!");
    }

    /*Design der Stage inklusive der Platzierung aller Elemente*/

    /**
     * starts before the application will close - Lifecycle of JavaFX
     */
    @Override
    public void stop() {
        System.out.println("Stage is closing");
        System.exit(0);
    }

    /**
     * Design of the Stage including the set of the scene with fxml-File and CSS-File
     *
     * @param stage the window of the application
     */
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/Chat.fxml")));
        stage.setTitle("RoboRally Chat Login");
        Scene scene = new Scene(root);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/view/Chat.fxml")).toString());
        stage.setScene(scene);
        stage.show();
        System.out.println("start!");
    }
}

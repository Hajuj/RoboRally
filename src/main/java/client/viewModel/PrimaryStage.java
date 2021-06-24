package client.viewModel;

    import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class PrimaryStage  extends Application{

        public static void main(String[] args) {
            launch(args);
        }


        @Override
        public void start(Stage primaryStage) {
            // +++++++++++++++++++++++++++++++++++++++++++++
            // Center
            // +++++++++++++++++++++++++++++++++++++++++++++

            // Layout
            GridPane grid = new GridPane();
            grid.setAlignment(Pos.CENTER);
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(25, 25, 25, 25));

            // Ueberschrift
            Text scenetitle = new Text("Hallo AxxG-Leser");
            scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
            grid.add(scenetitle, 0, 0, 2, 1);

            // Szene
            Scene scene = new Scene(grid);
            // +++++++++++++++++++++++++++++++++++++++++++++
            // Stage konfigurieren
            // +++++++++++++++++++++++++++++++++++++++++++++

            // groessenanpassung erlauben
            primaryStage.setResizable(false);

            // hoehe und breite der Stage festlegen
            primaryStage.setWidth(800);
            primaryStage.setHeight(600);
            // oder min/max festlegen
            primaryStage.setMaxWidth(800);
            primaryStage.setMaxHeight(600);
            primaryStage.setMinWidth(800);
            primaryStage.setMinHeight(600);

            // miniicon setzen
            primaryStage.getIcons().add(new Image(PrimaryStage.class.getResourceAsStream("logo.jpg")));

            // Vollbildmodus ausschalten (default aus)
            primaryStage.setFullScreen(false);

            // Titel setzen
            primaryStage.setTitle("AxxG - Stage Beispiel");

            // Szene setzen
            primaryStage.setScene(scene);
            primaryStage.sizeToScene();

            // Stage anzeigen
            primaryStage.show();


        }


    }



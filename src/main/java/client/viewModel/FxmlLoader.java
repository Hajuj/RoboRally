/*package client.viewModel;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

public class FxmlLoader {
    private Pane view;

    public Pane getPage(String fileName) {
        try {
            URL fileUrl = FxmlLoader.class.getResource("resources/view/" + fileName + ".fxml");
            if (fileUrl == null) {
                throw new java.io.FileNotFoundException("File Fxml was not found");
            }
            view = new FXMLLoader().load(fileUrl);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return view;
    }*/
//}

package client.viewModel;

import client.model.ClientModel;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class ChatViewModel implements Initializable {

    ClientModel model = ClientModel.getInstance();

    @FXML
    public TextArea readyDisplay = new TextArea("");
    @FXML
    public Button readyButton;
    @FXML
    public Button gameGuideBtn;
    @FXML
    public TextArea chatField = new TextArea("");
    @FXML
    public TextField messageField;
    @FXML
    public Button sendButton;
    @FXML
    public Button notReadyBtn;

    private String message;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        model.refreshPlayerStatus(model.getClientGameModel().getPlayer().getPlayerID(), false);
        readyDisplay.setText(model.playersStatusMapProperty().getValue());
        chatField.setEditable(false);
        readyDisplay.setEditable(false);
        if (model.getClientGameModel().getPlayer().getFigure() == -1) {
            readyButton.setVisible(false);
            notReadyBtn.setVisible(false);
        }
        notReadyBtn.setDisable(true);

        //TODO check how to do it with observable pattern instead of addListener
        model.doChooseMapProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed (ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) {
                //TODO: Hier (wenn möglich) kein Platform.runLater()
                if (model.doChooseMapProperty().getValue() == true) {
                    Platform.runLater(() -> {
                        try {
                            showMaps();
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                        }
                    });
                }
            }
        });
        //chatField = new TextArea("");
        model.chatHistoryProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed (ObservableValue<? extends String> observableValue, String s, String t1) {
                //System.out.println("VALUE CHANGED");
                chatField.setText(t1);
                chatField.appendText("");
            }
        });

            model.playersStatusMapProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observableValue, String s1, String s2) {
                    //TODO try to implement it in ClientModel
                    //     check if synchronized block working
                    //     which means no -> java.lang.ArrayIndexOutOfBoundsException: Index 66 out of bounds for length 66
                    //     arraycopy: last destination index 78 out of bounds for byte[66]
                    //     IndexOutOfBoundsException: Index 2 out of bounds for length 2
                    //     SYNCHRONIZED IS NOT WORKING LOL
                    //     Cannot read field "glyphs" because "this.layoutCache" is null
                    readyDisplay.setText(s2);
                }
            });
        //Automatic scroll for the ChatField
        chatField.textProperty().addListener(new ChangeListener<Object>() {
            @Override
            public void changed(ObservableValue<?> observable, Object oldValue,
                                Object newValue) {
                chatField.setScrollTop(Double.MAX_VALUE); //this will scroll to the bottom

            }
        });

        //TODO close the chat window when the game starts and make the chat as a button in the game window
        /*model.gameOnProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed (ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) {

                Platform.runLater(() -> {
                    try {
                        loadGameScene();
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                });

            }
        });
*/

    }

    public void sendMessageButton(ActionEvent event) {
       //System.out.println("HI");
            message = messageField.getText();
            model.sendMsg(message);
            messageField.clear();
    }

    public void goToGameGuide(ActionEvent event) throws IOException {
        Stage rootStage = new Stage();
        Parent root;
        root = FXMLLoader.load(getClass().getResource("/view/GameGuide.fxml"));
        rootStage.setScene(new Scene(root));
        rootStage.setTitle("Game Guide");
        rootStage.show();
    }

    public void sendReadyStatus (ActionEvent event) {
        model.setNewStatus(true);
        readyButton.setDisable(true);
        notReadyBtn.setDisable(false);
    }

    public void showMaps () throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/AvailableMaps.fxml"));
        Parent root1 = fxmlLoader.load();
        Stage newStage = new Stage();
        newStage.setTitle("Available Maps");
        newStage.setScene(new Scene(root1));
        newStage.show();
    }

    public void changeStatusButton (ActionEvent event) {
        model.setNewStatus(false);
        notReadyBtn.setDisable(true);
        readyButton.setDisable(false);
        model.doChooseMapProperty().setValue(false);
    }

    public void loadGameScene () throws IOException {
       /* ClassLoader classLoader = getClass().getClassLoader();
        InputStream is = getClass().getClassLoader().getResourceAsStream("/view/Map.fxml");
        System.out.println(is+"True File exited");*/


        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/Map.fxml"));
        Parent root1 = fxmlLoader.load();
        Stage newStage = new Stage();
        newStage.setTitle("GAME");
        newStage.setScene(new Scene(root1));
        newStage.show();

        FXMLLoader fxmlLoader2 = new FXMLLoader(getClass().getResource("/view/YourCards.fxml"));
        Parent root2 = fxmlLoader2.load();
        Stage newStage2 = new Stage();
        newStage2.setTitle("CARDS");
        newStage2.setScene(new Scene(root2));
        newStage2.show();
    }
}

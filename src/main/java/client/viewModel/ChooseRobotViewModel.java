package client.viewModel;

import client.model.ClientModel;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;

public class ChooseRobotViewModel {
    public TextField nameField;
    public Button playButton;
    private StringProperty username;
    public IntegerProperty figure;
    ClientModel model = ClientModel.getInstance();





    @FXML
    private ImageView robot1;
    @FXML
    private ImageView robot2;
    @FXML
    private ImageView robot3;
    @FXML
    private ImageView robot4;
    @FXML
    private ImageView robot5;
    @FXML
    private ImageView robot6;



    public ChooseRobotViewModel() throws IOException {
    }

    public void setRobot1() throws IOException {
        ClientViewModel Client = new ClientViewModel();
        Client.figure.setValue(1);
        System.out.println("robot1 is been set");


    }
    public void setRobot2() throws IOException {
        ClientViewModel Client = new ClientViewModel();
        Client.figure.setValue(2);
        System.out.println("robot2 is been set");
    }
    public void setRobot3() throws IOException {
        ClientViewModel Client = new ClientViewModel();
        Client.figure.setValue(3);
        System.out.println("robot3 is been set");
    }
    public void setRobot4() throws IOException {
        ClientViewModel Client = new ClientViewModel();
        Client.figure.setValue(4);
        System.out.println("robot4 is been set");

    }
    public void setRobot5() throws IOException {
        ClientViewModel Client = new ClientViewModel();
        Client.figure.setValue(5);
        System.out.println("robot5 is been set");
    }
    public void setRobot6() throws IOException {
        ClientViewModel Client = new ClientViewModel();
        Client.figure.setValue(6);
        System.out.println("robot6 is been set");

    }
    //TODO:die Implementierung der Methode setPlayerReady
    //TODO:die Methoden funktionieren noch nicht

    public void playButtonClicked() {
        try {

            username.setValue(nameField.getText());
           // getNameProperty().setValue(nameField.getText());

            model.sendUsernameAndRobot(username.getName(), figure.getValue().intValue());


            System.out.println(username.get() +" "+ figure.getValue().intValue());

            Parent root= FXMLLoader.load(getClass().getResource("/view/chat.fxml"));
            Stage window = (Stage) playButton.getScene().getWindow();
            window.setScene(new Scene(root, 800, 800));
            //Parent root1 = (Parent) fxmlLoader.load();
            //Stage stage= new Stage();
            //stage.setTitle("RoboRally");
            //stage.setScene(new Scene(root1));
            //stage.show();
        }catch (IOException e) {
            e.printStackTrace();
            }

    }

    public ImageView getRobot1() {
        return robot1;
    }

    public void setRobot1(ImageView robot1) {
        this.robot1 = robot1;
    }
   /* public StringProperty getNameProperty() {
        return username;
    }*/
}

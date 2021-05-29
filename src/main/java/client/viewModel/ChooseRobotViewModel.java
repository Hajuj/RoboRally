package client.viewModel;

import client.model.ClientModel;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.scene.input.MouseEvent;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ChooseRobotViewModel implements Initializable {
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




    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }



    public void setRobot1(ActionEvent event) throws IOException {
        ClientViewModel Client = new ClientViewModel();
        Client.figure.setValue(1);
        System.out.println("robot1 is been set");


    }
    public void setRobot2(ActionEvent event)throws IOException {
        System.out.println("robot2 is been set");
        ClientViewModel Client = new ClientViewModel();
        Client.figure.setValue(2);
    }
    public void setRobot3() throws IOException {
        System.out.println("robot3 is been set");
        ClientViewModel Client = new ClientViewModel();
        Client.figure.setValue(3);
    }
    public void setRobot4(ActionEvent event) throws IOException {
        System.out.println("robot4 is been set");
        ClientViewModel Client = new ClientViewModel();
        Client.figure.setValue(4);

    }
    public void setRobot5(ActionEvent event) throws IOException {
        ClientViewModel Client = new ClientViewModel();
        Client.figure.setValue(5);
        System.out.println("robot5 is been set");
    }
    public void setRobot6(ActionEvent event) throws IOException {
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
    public ImageView getRobot2() {
        return robot2;
    }

    public ImageView getRobot3() {
        return robot3;
    }

    public ImageView getRobot4() {
        return robot4;
    }

    public ImageView getRobot5() {
        return robot5;
    }

    public ImageView getRobot16() {
        return robot6;
    }


//andere Variente for chooseRobot
    /*
    *    @FXML
    private void handleButtonAction(ActionEvent event) {
        if (event.getSource().equals(btnSignUp)) {
            new FadeIn(pnSignUp).play();
            pnSignUp.toFront();
        }
        if (event.getSource().equals(getStarted)) {
            new FadeIn(pnSignIn).play();
            pnSignIn.toFront();
        }
        loginNotifier.setOpacity(0);
        userName.setText("");
        passWord.setText("");
    }
*/
    @FXML
    private void handleMouseEvent(MouseEvent event) {
        if (event.getSource() == robot1) {
            System.out.println("debug");
        }
        nameField.setText("");

    }


   /* public StringProperty getNameProperty() {
        return username;
    }*/
}

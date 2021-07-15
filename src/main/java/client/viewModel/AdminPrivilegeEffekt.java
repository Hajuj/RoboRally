package client.viewModel;

import client.model.ClientGameModel;
import client.model.ClientModel;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class AdminPrivilegeEffekt implements Initializable {

    public CheckBox reg_0;
    public CheckBox reg_1;
    public CheckBox reg_2;
    public CheckBox reg_3;
    public CheckBox reg_4;
    public ClientModel model = ClientModel.getInstance();
    public ClientGameModel clientGameModel = ClientGameModel.getInstance();
//TODO in init Methode wenn getActualRegsiter müsenn ausgeblenedt werden &&getactualPhase

    public void handleRegister(MouseEvent mouseEvent) {
        //int choosenRegister;
        if (reg_0.isSelected() == true) {
            clientGameModel.activateAdminPrivilege(0);
        }
        if (reg_1.isSelected() == true) {
            clientGameModel.activateAdminPrivilege(1);
        }
        if (reg_2.isSelected() == true) {
            clientGameModel.activateAdminPrivilege(2);
        }
        if (reg_3.isSelected() == true) {
            clientGameModel.activateAdminPrivilege(3);
        }
        if (reg_4.isSelected() == true) {
            clientGameModel.activateAdminPrivilege(4);
        }
        Stage stage = (Stage) reg_4.getScene().getWindow();
        stage.close();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        if (clientGameModel.getActualPhase() == 3) {
            int actualRegister = clientGameModel.getValueActualRegister();
            if (actualRegister == 0) {
                reg_0.setVisible(false);
            }
            if (actualRegister == 1) {
                reg_0.setVisible(false);
                reg_1.setVisible(false);
            }
            if (actualRegister == 2) {
                reg_0.setVisible(false);
                reg_1.setVisible(false);
                reg_2.setVisible(false);

            }
            if (actualRegister == 3) {
                reg_0.setVisible(false);
                reg_1.setVisible(false);
                reg_2.setVisible(false);
                reg_3.setVisible(false);
            }
            if (actualRegister == 4) {
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setContentText("you cant choose any Register because they are already played.Next time :/ ");
            }
        }
    }
}


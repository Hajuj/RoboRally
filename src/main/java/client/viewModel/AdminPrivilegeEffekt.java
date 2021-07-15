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

/**
 * The type Admin privilege effekt.
 */
public class AdminPrivilegeEffekt implements Initializable {

    /**
     * The Reg 0.
     */
    public CheckBox reg_0;
    /**
     * The Reg 1.
     */
    public CheckBox reg_1;
    /**
     * The Reg 2.
     */
    public CheckBox reg_2;
    /**
     * The Reg 3.
     */
    public CheckBox reg_3;
    /**
     * The Reg 4.
     */
    public CheckBox reg_4;
    /**
     * The Model.
     */
    public ClientModel model = ClientModel.getInstance();
    /**
     * The Client game model.
     */
    public ClientGameModel clientGameModel = ClientGameModel.getInstance();
//TODO in init Methode wenn getActualRegsiter müsenn ausgeblenedt werden &&getactualPhase

    /**
     * Handle register.
     * Sets the chosen register as admin privilege register
     *
     * @param mouseEvent the mouse event
     */
    public void handleRegister(MouseEvent mouseEvent) {
        //int choosenRegister;
        if(reg_0.isSelected() == true) {
            clientGameModel.activateAdminPrivilege(0);
        }
        if(reg_1.isSelected() == true) {
            clientGameModel.activateAdminPrivilege(1);
        }
        if(reg_2.isSelected() == true) {
            clientGameModel.activateAdminPrivilege(2);
        }
        if(reg_3.isSelected() == true) {
            clientGameModel.activateAdminPrivilege(3);
        }
        if(reg_4.isSelected() == true) {
            clientGameModel.activateAdminPrivilege(4);
        }
        Stage stage = (Stage) reg_4.getScene().getWindow();
        stage.close();
        /*System.out.println ( mouseEvent.getSource () );
        System.out.println ( mouseEvent.getSource ().toString () );
        //String register = (String) mouseEvent.getSource ();
        //int choosenRegister = register.charAt ( 4 );
       // int choosenRegister = Integer.parseInt(String.valueOf(mouseEvent.getSource ().toString ().charAt(4)));
*/
    }

    /**
     * Shows the viable registers
     * @param location
     * @param resources
     */

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if(clientGameModel.getActualPhase() == 3) {
            int actualRegister = clientGameModel.getValueActualRegister();
            if(actualRegister == 0) {
                reg_0.setVisible(false);
            }
            if(actualRegister == 1) {
                reg_0.setVisible(false);
                reg_1.setVisible(false);
            }
            if(actualRegister == 2) {
                reg_0.setVisible(false);
                reg_1.setVisible(false);
                reg_2.setVisible(false);

            }
            if(actualRegister == 3) {
                reg_0.setVisible(false);
                reg_1.setVisible(false);
                reg_2.setVisible(false);
                reg_3.setVisible(false);
            }
            if(actualRegister == 4) {
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setContentText("you cant choose any Register because they are already played.Next time :/ ");
            }
        }
    }
}


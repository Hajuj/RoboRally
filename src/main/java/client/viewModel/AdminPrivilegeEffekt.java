package client.viewModel;

import client.model.ClientGameModel;
import client.model.ClientModel;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.util.ResourceBundle;

public class AdminPrivilegeEffekt  {

    public CheckBox reg_0;
    public CheckBox reg_1;
    public CheckBox reg_3;
    public CheckBox reg_4;
    public ClientModel model = ClientModel.getInstance();
    public ClientGameModel clientGameModel = ClientGameModel.getInstance();


    public void handleRegister(MouseEvent mouseEvent) {
        String register = (String) mouseEvent.getSource ();
        int choosenRegister = register.charAt ( 4 );
        clientGameModel.setChoosenRegister(choosenRegister);
    }

}


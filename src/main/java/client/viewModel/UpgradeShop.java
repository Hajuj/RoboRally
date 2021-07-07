package client.viewModel;

import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;

import javax.swing.*;
import javax.swing.text.html.ImageView;
import java.awt.*;
import javafx.scene.control.PopupControl;
import javafx.scene.text.Text;


public class UpgradeShop {
    public Tooltip toolTip1;
    public Text cubesNum;

    public void showDescription(MouseEvent mouseEvent) {
      toolTip1.setText ( "card effekt " );

    }

    public void buyCard(MouseEvent mouseEvent) {
    }

    public void chooseUpgradeCard(MouseEvent mouseEvent) {
        ImageView choosenCard = (ImageView) mouseEvent.getSource ();
        
    }
}

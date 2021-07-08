package game.boardelements;

import game.Element;

import java.util.ArrayList;

/**
 * @author Ilja Knis
 */
public class PushPanel extends Element {

    public PushPanel(String type, String isOnBoard, ArrayList<String> orientations, ArrayList<Integer> registers) {
        super(type, isOnBoard, orientations, registers);
    }
}

package game.boardelements;

import game.Element;

import java.util.ArrayList;

/**
 * The type Push panel.
 *
 * @author Ilja Knis
 */
public class PushPanel extends Element {

    /**
     * Instantiates a new Push panel.
     *
     * @param type         the type
     * @param isOnBoard    the board position
     * @param orientations the orientations
     * @param registers    the registers
     */
    public PushPanel(String type, String isOnBoard, ArrayList<String> orientations, ArrayList<Integer> registers) {
        super(type, isOnBoard, orientations, registers);
    }
}

package game.boardelements;

import game.Element;

import java.util.ArrayList;

/**
 * The type Restart point.
 *
 * @author Ilja Knis
 */
public class RestartPoint extends Element {

    /**
     * Instantiates a new Restart point.
     *
     * @param type         the type
     * @param isOnBoard    the board position
     * @param orientations the orientations
     */
    public RestartPoint(String type, String isOnBoard, ArrayList<String> orientations) {
        super(type, isOnBoard, orientations);
    }
}

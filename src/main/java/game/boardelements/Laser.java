package game.boardelements;

import game.Element;

import java.util.ArrayList;

/**
 * The type Laser.
 *
 * @author Ilja Knis
 */
public class Laser extends Element {

    /**
     * Instantiates a new Laser.
     *
     * @param type         the type
     * @param isOnBoard    the board position
     * @param orientations the orientations
     * @param count        the count
     */
    public Laser(String type, String isOnBoard, ArrayList<String> orientations, int count) {
        super(type, isOnBoard, orientations, count);
    }
}

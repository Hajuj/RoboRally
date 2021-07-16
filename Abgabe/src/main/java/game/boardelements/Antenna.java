package game.boardelements;

import game.Element;

import java.util.ArrayList;

/**
 * The type Antenna.
 *
 * @author Ilja Knis
 */
public class Antenna extends Element {

    /**
     * Instantiates a new Antenna.
     *
     * @param type         the type
     * @param isOnBoard    the board position
     * @param orientations the orientations
     */
    public Antenna(String type, String isOnBoard, ArrayList<String> orientations) {
        super(type, isOnBoard, orientations);
    }
}

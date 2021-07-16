package game.boardelements;

import game.Element;

import java.util.ArrayList;

/**
 * The type Wall.
 *
 * @author Ilja Knis
 */
public class Wall extends Element {

    /**
     * Instantiates a new Wall.
     *
     * @param type         the type
     * @param isOnBoard    the board position
     * @param orientations the orientations
     */
    public Wall(String type, String isOnBoard, ArrayList<String> orientations) {
        super(type, isOnBoard, orientations);
    }
}

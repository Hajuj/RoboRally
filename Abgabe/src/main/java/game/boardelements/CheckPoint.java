package game.boardelements;

import game.Element;

/**
 * The type Check point.
 *
 * @author Ilja Knis
 */
public class CheckPoint extends Element {

    /**
     * Instantiates a new Check point.
     *
     * @param type      the type
     * @param isOnBoard the board position
     * @param count     the count
     */
    public CheckPoint(String type, String isOnBoard, int count) {
        super(type, isOnBoard, count);
    }
}

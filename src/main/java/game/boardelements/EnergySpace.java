package game.boardelements;

import game.Element;

/**
 * The type Energy space.
 *
 * @author Ilja Knis
 */
public class EnergySpace extends Element {

    /**
     * Instantiates a new Energy space.
     *
     * @param type      the type
     * @param isOnBoard the board position
     * @param count     the count
     */
    public EnergySpace(String type, String isOnBoard, int count) {
        super(type, isOnBoard, count);
    }
}

package game.boardelements;

import game.Element;

import java.util.ArrayList;

/**
 * @author Ilja Knis
 */
public class Laser extends Element {

    public Laser(String type, String isOnBoard, ArrayList<String> orientations, int count) {
        super(type, isOnBoard, orientations, count);
    }
}

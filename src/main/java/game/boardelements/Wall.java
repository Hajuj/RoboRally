package game.boardelements;

import game.Element;

import java.util.ArrayList;

/**
 * @author Ilja Knis
 */
public class Wall extends Element {

    public Wall(String type, String isOnBoard, ArrayList<String> orientations) {
        super(type, isOnBoard, orientations);
    }
}

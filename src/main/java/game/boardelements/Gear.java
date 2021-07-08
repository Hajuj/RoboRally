package game.boardelements;

import game.Element;

import java.util.ArrayList;

/**
 * @author Ilja Knis
 */
public class Gear extends Element {

    private final String colour;

    public Gear(String type, String isOnBoard, ArrayList<String> orientations) {
        super(type, isOnBoard, orientations);

        if (orientations.contains("counterclockwise")) {
            this.colour = "red";
        } else {
            this.colour = "green";
        }
    }

    public String getColour() {
        return colour;
    }
}

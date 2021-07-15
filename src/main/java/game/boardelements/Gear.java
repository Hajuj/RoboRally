package game.boardelements;

import game.Element;

import java.util.ArrayList;

/**
 * The type Gear.
 *
 * @author Ilja Knis
 */
public class Gear extends Element {

    private final String colour;

    /**
     * Instantiates a new Gear.
     *
     * @param type         the type
     * @param isOnBoard    the board position
     * @param orientations the orientations
     */
    public Gear(String type, String isOnBoard, ArrayList<String> orientations) {
        super(type, isOnBoard, orientations);

        if(orientations.contains("counterclockwise")) {
            this.colour = "red";
        } else {
            this.colour = "green";
        }
    }

    /**
     * Gets colour.
     *
     * @return the colour
     */
    public String getColour() {
        return colour;
    }
}

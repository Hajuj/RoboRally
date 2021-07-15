package game.boardelements;

import com.google.gson.annotations.Expose;
import game.Element;

import java.util.ArrayList;

/**
 * The type Conveyor belt.
 *
 * @author Ilja Knis
 */
public class ConveyorBelt extends Element {

    @Expose
    private final String colour;

    /**
     * Instantiates a new Conveyor belt.
     *
     * @param type         the type
     * @param isOnBoard    the board position
     * @param speed        the speed
     * @param orientations the orientations
     */
    public ConveyorBelt(String type, String isOnBoard, int speed, ArrayList<String> orientations) {
        super(type, isOnBoard, speed, orientations);

        if(speed == 1) {
            this.colour = "green";
        } else {
            this.colour = "blue";
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

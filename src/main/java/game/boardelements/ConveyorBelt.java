package game.boardelements;

import com.google.gson.annotations.Expose;
import game.Element;

import java.util.ArrayList;

/**
 * @author Ilja Knis
 */
public class ConveyorBelt extends Element {

    @Expose
    private final String colour;

    public ConveyorBelt(String type, String isOnBoard, int speed, ArrayList<String> orientations) {
        super(type, isOnBoard, speed, orientations);

        if (speed == 1) {
            this.colour = "green";
        } else {
            this.colour = "blue";
        }
    }

    public String getColour() {
        return colour;
    }
}

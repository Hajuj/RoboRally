package game.boardelements;

import com.google.gson.annotations.Expose;
import game.Element;

import java.util.ArrayList;


/**
 * @author Ilja Knis
 */
public class Antenna extends Element {

    @Expose
    private ArrayList<String> orientations;

    public Antenna(String type, String isOnBoard, ArrayList<String> orientations) {
        this.type = type;
        this.isOnBoard = isOnBoard;
        this.orientations = new ArrayList<>();
        this.orientations.addAll(orientations);
    }

    public String getType() {
        return type;
    }

    public String getIsOnBoard() {
        return null;
    }


}

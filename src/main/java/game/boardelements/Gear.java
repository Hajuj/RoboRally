package game.boardelements;

import com.google.gson.annotations.Expose;
import game.Element;

import java.util.ArrayList;

/**
 * @author Ilja Knis
 */
public class Gear extends Element {

    @Expose
    private String type;
    @Expose
    private String isOnBoard;
    @Expose
    private ArrayList<String> orientations;
    private String colour;

    public Gear(String type, String isOnBoard, ArrayList<String> orientations){
        this.type = type;
        this.orientations = new ArrayList<>();
        this.isOnBoard = isOnBoard;
        this.orientations.addAll(orientations);

        if(orientations.contains("counterclockwise")){
            this.colour = "red";
        }
        else{
            this.colour = "green";
        }

    }

    public String getType() {
        return type;
    }

    public String getIsOnBoard() {
        return isOnBoard;
    }

    public ArrayList<String> getOrientations() {
        return orientations;
    }

    public String getColour() {
        return colour;
    }
}

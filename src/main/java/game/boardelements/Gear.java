package game.boardelements;

import com.google.gson.annotations.Expose;
import game.Element;

import java.util.ArrayList;

/**
 * @author Ilja Knis
 */
public class Gear extends Element {

    @Expose
    private ArrayList<String> orientations;

    private String colour;

    public Gear(String type, String isOnBoard){
        this.type = "Gear";
        this.orientations = new ArrayList<>();

        this.isOnBoard = isOnBoard;
        //Where do we get the colour?

        if(colour.equals("red")){
            this.orientations.add("counterclockwise");
        }
        else{
            this.orientations.add("clockwise");
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
}

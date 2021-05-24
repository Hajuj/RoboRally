package game.boardelements;

import com.google.gson.annotations.Expose;
import game.Element;

import java.util.ArrayList;


/**
 * @author Ilja Knis
 */
public class Antenna extends Element {

    @Expose
    private String type;

    @Expose
    private ArrayList<String> orientations;

    public Antenna() {
        this.type = "Antenna";
        this.orientations = new ArrayList<>();
        this.orientations.add("right");
    }

    public String getType() {
        return type;
    }

    //TODO find out if antenna should function as a blocker
    public boolean isBlocker() {
        return false;
    }

}

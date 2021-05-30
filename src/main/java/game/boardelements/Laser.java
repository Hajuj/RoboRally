package game.boardelements;

import com.google.gson.annotations.Expose;
import game.Element;

import java.util.ArrayList;

/**
 * @author Ilja Knis
 */
public class Laser extends Element {

    @Expose
    private String type;

    @Expose
    private ArrayList<String> orientations;

    @Expose
    private int count;

    public Laser(int count, String orientation){
        this.type = "Laser";
        this.orientations = new ArrayList<>();
        this.orientations.add(orientation);
        this.count = count;
    }

    public ArrayList<String> getOrientations() {
        return orientations;
    }

    public int getCount() {
        return count;
    }

    public String getType() {
        return type;
    }

    public boolean isBlocker() {
        return false;
    }
}

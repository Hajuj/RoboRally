package game.boardelements;

import com.google.gson.annotations.Expose;
import game.Element;

import java.util.ArrayList;

/**
 * @author Ilja Knis

public abstract class RotatingBelt extends Element {

    @Expose
    private String type;

    @Expose
    private ArrayList<String> orientations;

    @Expose
    private int speed;

    @Expose
    private Boolean isCrossing;

    public RotatingBelt(String orientationOne, String orientationTwo, int speed, boolean isCrossing){
        this.type = "RotatingBelt";
        this.orientations = new ArrayList<>();
        this.orientations.add(orientationOne);
        this.orientations.add(orientationTwo);
        this.speed = speed;
        this.isCrossing = isCrossing;
    }

    public String getType() {
        return type;
    }

    public boolean isBlocker() {
        return false;
    }

    public int getSpeed() {
        return speed;
    }

    public ArrayList<String> getOrientations() {
        return orientations;
    }

    public Boolean getCrossing() {
        return isCrossing;
    }
}*/

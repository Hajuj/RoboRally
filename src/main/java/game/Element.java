package game;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;

/**
 * @author Ilja Knis
 */
public class Element {

    @Expose
    public String type;
    @Expose
    private String isOnBoard;
    @Expose
    private int speed;
    @Expose
    private ArrayList<String> orientations;
    @Expose
    private int count;

    public Element(){
    }

    public String getType(){
        return this.type;
    }

    public String getIsOnBoard(){
        return this.isOnBoard;
    }

    public ArrayList<String> getOrientations() {
        return this.orientations;
    }

    public int getSpeed() {
        return this.speed;
    }

    public int getCount() {
        return this.count;
    }
}


package game.boardelements;

import com.google.gson.annotations.Expose;
import game.Element;

import java.util.ArrayList;

public class Gear extends Element {

    @Expose
    private String type;

    //symbolizes the direction of rotation
    @Expose
    private ArrayList<String> orientations;

    public Gear(String colour){
        this.type = "Gear";
        this.orientations = new ArrayList<>();
        if(colour.equals("red")){
            this.orientations.add("left");
        }
        else{
            this.orientations.add("right");
        }
    }

    public String getType() {
        return type;
    }

    public boolean isBlocker() {
        return false;
    }

    public ArrayList<String> getOrientations() {
        return orientations;
    }
}

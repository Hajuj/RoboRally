package game.boardelements;

import com.google.gson.annotations.Expose;
import game.Element;

import java.util.ArrayList;

public class Belt extends Element {

    @Expose
    private String type;

    @Expose
    private ArrayList<String> orientations;

    @Expose
    private int speed;

    public Belt(String orientation, String colour) {

        this.type = "Belt";
        this.orientations = new ArrayList<>();
        this.orientations.add(orientation);

        if(colour.equals("green")){
            this.speed = 1;
        }else{
            this.speed = 2;
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

    public int getSpeed() {
        return speed;
    }
}

package game.boardelements;

import com.google.gson.annotations.Expose;
import game.Element;

import java.util.ArrayList;

public class PushPanel extends Element {

    @Expose
    private String type;

    @Expose
    private ArrayList<String> orientations;

    @Expose
    private ArrayList<Integer> registers;

    public PushPanel(String orientation){
        this.type = "PushPanel";
        this.orientations = new ArrayList<>();
        this.orientations.add(orientation);
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

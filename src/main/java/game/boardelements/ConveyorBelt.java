package game.boardelements;

import com.google.gson.annotations.Expose;
import game.Element;

import java.util.ArrayList;

/**
 * @author Ilja Knis
 */
public class ConveyorBelt extends Element {

    @Expose
    private int speed;
    @Expose
    private ArrayList<String> orientations;

    private String colour;

    public ConveyorBelt(String type, String isOnBoard, ArrayList<String> orientations) {

        this.type = type;
        this.isOnBoard = isOnBoard;
        this.orientations = new ArrayList<>();
        this.orientations.addAll(orientations);

        //Where do we get the colour?
        if(colour.equals("green")){
            this.speed = 1;
        }else{
            this.speed = 2;
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

    public int getSpeed() {
        return speed;
    }
}

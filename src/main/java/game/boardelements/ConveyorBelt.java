package game.boardelements;

import com.google.gson.annotations.Expose;
import game.Element;

import java.util.ArrayList;

/**
 * @author Ilja Knis
 */
public class ConveyorBelt extends Element {

    @Expose
    private String type;
    @Expose
    private String isOnBoard;
    @Expose
    private int speed;
    @Expose
    private ArrayList<String> orientations;
    @Expose
    private String colour;

    public ConveyorBelt(String type, String isOnBoard, int speed, ArrayList<String> orientations) {

        this.type = type;
        this.isOnBoard = isOnBoard;
        this.orientations = new ArrayList<>();
        this.orientations.addAll(orientations);
        this.speed = speed;

        if(speed == 1){
            this.colour = "green";
        }else{
            this.colour = "blue";
        }

    }


    public String getType() {
        return type;
    }

    public String getIsOnBoard() {
        return isOnBoard;
    }

    public String getColour() {
        return colour;
    }

    public ArrayList<String> getOrientations() {
        return orientations;
    }

    public int getSpeed() {
        return speed;
    }
}
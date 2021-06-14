package game;

import com.google.gson.annotations.Expose;
import javafx.geometry.Orientation;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Ilja Knis
 */
public class Element {

    private String type;
    private String isOnBoard;
    private int speed;
    private ArrayList<String> orientations;
    private int count;
    private ArrayList<Integer> registers;

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

    public ArrayList<Integer> getRegisters() {
        return this.registers;
    }



}


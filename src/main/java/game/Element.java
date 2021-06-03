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


    /**
     * Used to load the raw card textures from filesystem
     */
   /* private void loadImagePath(Element e, Orientation o){
        textures = new Texture[7];
        String [] conveyorBelt= new String[] {"BlueBelt","GreenBelt","RotatingBeltBlue1","RotatingBeltBlue2","RotatingBeltBlue3","RotatingBeltGreen1","RotatingBeltGreen2","RotatingBeltGreen3"};
        for(int i = 0; i<7; i++){
            String path = "assets/Card/" + cardNames[i] + ".png";
            textures[i] = new Texture(path);
        }
    }*/
}


package game;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;

/**
 * @author Ilja Knis
 */
public class Element {
    @Expose
    private String type;
    @Expose
    private String isOnBoard;
    @Expose
    private int speed;
    @Expose
    private ArrayList<String> orientations;
    @Expose
    private int count;
    @Expose
    private ArrayList<Integer> registers;

    public Element(String type, String isOnBoard) {
        this.type = type;
        this.isOnBoard = isOnBoard;
    }

    public Element(String type, String isOnBoard, ArrayList<String> orientations) {
        this.type = type;
        this.isOnBoard = isOnBoard;
        this.orientations = new ArrayList<>();
        this.orientations.addAll(orientations);
    }

    public Element(String type, String isOnBoard, int count) {
        this.type = type;
        this.isOnBoard = isOnBoard;
        this.count = count;
    }

    public Element(String type, String isOnBoard, int speed, ArrayList<String> orientations) {
        this.type = type;
        this.isOnBoard = isOnBoard;
        this.speed = speed;
        this.orientations = new ArrayList<>();
        this.orientations.addAll(orientations);
    }

    public Element(String type, String isOnBoard, ArrayList<String> orientations, int count) {
        this.type = type;
        this.isOnBoard = isOnBoard;
        this.count = count;
        this.orientations = new ArrayList<>();
        this.orientations.addAll(orientations);
    }

    public Element(String type, String isOnBoard, ArrayList<String> orientations, ArrayList<Integer> registers) {
        this.type = type;
        this.isOnBoard = isOnBoard;
        this.registers = registers;
        this.orientations = new ArrayList<>();
        this.orientations.addAll(orientations);
    }


    public String getType() {
        return this.type;
    }

    public String getIsOnBoard() {
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

    public void setCount(int count) {
        this.count = count;
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

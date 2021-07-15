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

    /**
     * Constructor for elements: Empty, StartPoint, Pit
     *
     * @param type      is the name of the element
     * @param isOnBoard is the board the element is on
     */
    public Element(String type, String isOnBoard) {
        this.type = type;
        this.isOnBoard = isOnBoard;
    }

    /**
     * Constructor for elements: Antenna, Gear, RestartPoint, Wall
     *
     * @param type         is the name of the element
     * @param isOnBoard    is the board the element is on
     * @param orientations are the orientations of the element
     */
    public Element(String type, String isOnBoard, ArrayList<String> orientations) {
        this.type = type;
        this.isOnBoard = isOnBoard;
        this.orientations = new ArrayList<>();
        this.orientations.addAll(orientations);
    }

    /**
     * Constructor for elements: CheckPoint, EnergySpace
     *
     * @param type      is the name of the element
     * @param isOnBoard is the board the element is on
     * @param count     is the amount that specific element
     */
    public Element(String type, String isOnBoard, int count) {
        this.type = type;
        this.isOnBoard = isOnBoard;
        this.count = count;
    }

    /**
     * Constructor for element: ConveyorBelt
     *
     * @param type         is the name of the element
     * @param isOnBoard    is the board the element is on
     * @param speed        is the speed of the element
     * @param orientations are the orientations of the element
     */
    public Element(String type, String isOnBoard, int speed, ArrayList<String> orientations) {
        this.type = type;
        this.isOnBoard = isOnBoard;
        this.speed = speed;
        this.orientations = new ArrayList<>();
        this.orientations.addAll(orientations);
    }

    /**
     * Constructor for element: Laser
     *
     * @param type         is the name of the element
     * @param isOnBoard    is the board the element is on
     * @param orientations are the orientations of the element
     * @param count        is the amount that specific element
     */
    public Element(String type, String isOnBoard, ArrayList<String> orientations, int count) {
        this.type = type;
        this.isOnBoard = isOnBoard;
        this.count = count;
        this.orientations = new ArrayList<>();
        this.orientations.addAll(orientations);
    }

    /**
     * Constructor for element: PushPanel
     *
     * @param type         is the name of the element
     * @param isOnBoard    is the board the element is on
     * @param orientations are the orientations of the element
     * @param registers    are the registers assigned to that element
     */
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

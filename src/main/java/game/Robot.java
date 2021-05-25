package game;

import java.util.ArrayList;

/**
 * @author Ilja Knis
 */
public class Robot {

    private int xPosition;
    private int yPosition;
    private String name;
    private ArrayList<String> orientation;

    public Robot(String name, int xPosition, int yPosition){
        this.name = name;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        orientation.add("Right");
    }

    public int getxPosition() {
        return xPosition;
    }

    public int getyPosition() {
        return yPosition;
    }

    public String getName() {
        return name;
    }

    public void setxPosition(int xPosition) {
        this.xPosition = xPosition;
    }

    public void setyPosition(int yPosition) {
        this.yPosition = yPosition;
    }

    public void setName(String name) {
        this.name = name;
    }
}

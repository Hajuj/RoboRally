package game;

import java.util.ArrayList;

/**
 * @author Ilja Knis <З
 */
public class Robot {

    private int xPosition;
    private int yPosition;
    private String name;
    private String orientation;


    //TODO: Orientation immer nach rechts am Anfang? Oder da, wo die Antenne schaut? keine Ahnung
    public Robot (String name, int xPosition, int yPosition) {
        this.name = name;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.orientation = "right";
    }

    public int getxPosition () {
        return xPosition;
    }

    public int getyPosition () {
        return yPosition;
    }

    public String getName () {
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

    public String getOrientation() {
        return orientation;
    }

    public void setOrientation(String orientation) {
        this.orientation = orientation;
    }

}

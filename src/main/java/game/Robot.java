package game;

import java.util.ArrayList;

/**
 * @author Ilja Knis <З
 */
public class Robot {

    private int xPosition;
    private int yPosition;
    private String name;
    private ArrayList<String> orientation = new ArrayList<>();
    private int schadenPunkte = 0;


    //TODO: Orientation immer nach rechts am Anfang? Oder da, wo die Antenne schaut? keine Ahnung
    public Robot (String name, int xPosition, int yPosition) {
        this.name = name;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        orientation.add("Right");
    }

    public int getSchadenPunkte () {
        return schadenPunkte;
    }

    public void setSchadenPunkte (int schadenPunkte) {
        this.schadenPunkte = schadenPunkte;
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

}

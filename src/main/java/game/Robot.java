package game;

/**
 * @author Ilja Knis <З
 */
public class Robot {

    private int xPosition;
    private int yPosition;
    private String name;
    private String orientation;

    /**
     * Constructor to create a new Robot.
     * @param name      is the name of the robot
     * @param xPosition is the x position of the robot on the map
     * @param yPosition is the y position of the robot on the map
     */
    public Robot(String name, int xPosition, int yPosition) {
        this.name = name;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.orientation = "right";
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

    public String getOrientation() {
        return orientation;
    }

    public void setOrientation(String orientation) {
        this.orientation = orientation;
    }

}

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
     *
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

    /**
     * Gets position.
     *
     * @return the position
     */
    public int getxPosition() {
        return xPosition;
    }

    /**
     * Gets position.
     *
     * @return the position
     */
    public int getyPosition() {
        return yPosition;
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets position.
     *
     * @param xPosition the x position
     */
    public void setxPosition(int xPosition) {
        this.xPosition = xPosition;
    }

    /**
     * Sets position.
     *
     * @param yPosition the y position
     */
    public void setyPosition(int yPosition) {
        this.yPosition = yPosition;
    }

    /**
     * Sets name.
     *
     * @param name the name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets orientation.
     *
     * @return the orientation
     */
    public String getOrientation() {
        return orientation;
    }

    /**
     * Sets orientation.
     *
     * @param orientation the orientation
     */
    public void setOrientation(String orientation) {
        this.orientation = orientation;
    }

}

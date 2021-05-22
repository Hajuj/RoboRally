package game;

/**
 * @author Ilja Knis
 */
public class Robot {

    private int xPosition;
    private int yPosition;
    private String name;
    private Orientation orientation;

    public Robot(String name, int xPosition, int yPosition){
        this.name = name;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.orientation = Orientation.RIGHT;
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

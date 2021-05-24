package game;

/**
 * @author Ilja Knis
 */
public class Player {

    private final int playerID;
    private String name;
    private int figure;

    public int getFigure () {
        return figure;
    }

    public void setFigure (int figure) {
        this.figure = figure;
    }

    public Player (int playerID) {
        this.playerID = playerID;
    }

    public int getPlayerID () {
        return playerID;
    }

    public void setName (String name) {
        this.name = name;
    }

    public String getName () {
        return name;
    }


    public void pickRobot (int figure, String name) {
        this.figure = figure;
        this.name = name;
    }
}

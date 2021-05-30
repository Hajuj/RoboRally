package game.boardelements;

import game.Element;

/**
 * @author Ilja Knis
 */
public class Empty extends Element {

    public Empty(String type, String isOnBoard) {
        this.type = type;
        this.isOnBoard = isOnBoard;
    }

    public String getType() {
        return type;
    }

    public String getIsOnBoard() {
        return isOnBoard;
    }

}

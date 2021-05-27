package game;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;

/**
 * @author Ilja Knis
 */
public abstract class Element {

    @Expose
    public String type;
    @Expose
    public String isOnBoard;

    public Element(){
    }

    public abstract String getType();
    public abstract String getIsOnBoard();
}


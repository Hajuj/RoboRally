package game;

import java.util.ArrayList;

/**
 * @author Ilja Knis
 */
public abstract class Element {

    private String type;
    private ArrayList<String> orientations;

    public Element(){
    }

    public abstract String getType();

    public abstract boolean isBlocker();
}


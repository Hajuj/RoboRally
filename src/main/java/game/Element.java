package game;

//TODO consider using interface with method "elementEffect(Robot robot)"
//     numbers for map specifications (instead of coordinations)
//     coordination (x,y) -> conversion to map numbers (1,1) -> 1, (1,2) -> 2,...

import com.google.gson.annotations.Expose;

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


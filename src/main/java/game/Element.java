package game;


//TODO consider using interface with method "elementEffect(Robot robot)"
//     should Element include x and y coordinates for position?

import com.google.gson.annotations.Expose;

/**
 * @author Ilja Knis
 */
public abstract class Element {
    @Expose
    public String elementName;

    @Expose
    public int amount;

    public Element(){
        elementName = "BasicElement";
    }

    public String getElementName(){
        return elementName;
    }

    public int getAmount() {
        return amount;
    }

    public abstract boolean isBlocker();
}


package game;

//TODO consider using interface with method "elementEffect(Robot robot)"
//     numbers for map specifications (instead of coordinations)
//     coordination (x,y) -> conversion to map numbers (1,1) -> 1, (1,2) -> 2,...

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


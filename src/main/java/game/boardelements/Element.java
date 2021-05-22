package game.boardelements;


//TODO consider using interface with method "elementEffect(Robot robot)"

/**
 * @author Ilja Knis
 */
public class Element {
    private String elementName;
    private int amount;


    public Element(){

    }

    public String getElementName(){
        return elementName;
    }

    public int getAmount() {
        return amount;
    }
}


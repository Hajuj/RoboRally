package game.boardelements;

import com.google.gson.annotations.Expose;
import game.Element;

public class Wall extends Element {

    @Expose
    private String elementName;

    public Wall(){
        elementName = "Wall";
    }

    @Override
    public boolean isBlocker() {
        return true;
    }


}

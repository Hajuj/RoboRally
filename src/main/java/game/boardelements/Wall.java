package game.boardelements;

import com.google.gson.annotations.Expose;
import game.Element;

public class Wall extends Element {

    public Wall() {
        elementName = "Wall";
        amount = 1;
    }

    @Override
    public boolean isBlocker() {
        return true;
    }


}

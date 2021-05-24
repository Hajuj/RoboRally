package game.boardelements;

import com.google.gson.annotations.Expose;
import game.Element;

public class RestartPoint extends Element {

    @Expose
    private String type;

    public RestartPoint(){
        this.type = "RestartPoint";
    }

    public String getType() {
        return type;
    }

    public boolean isBlocker() {
        return false;
    }
}

package game.boardelements;

import com.google.gson.annotations.Expose;
import game.Element;

public class CheckPoint extends Element {

    @Expose
    private String type;

    @Expose
    private int count;

    public CheckPoint(int count) {
        this.type = "Checkpoint";
        this.count = count;
    }

    @Override
    public String getType() {
        return type;
    }

    public boolean isBlocker() {
        return false;
    }

    public int getCount() {
        return count;
    }
}

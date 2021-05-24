package game.boardelements;

import com.google.gson.annotations.Expose;
import game.Element;

public class EnergySpace extends Element {
    @Expose
    private String type;

    @Expose
    private int count;

    public EnergySpace(int count){
        this.type = "EnergySpace";
        this.count = count;
    }

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

package game.boardelements;

import com.google.gson.annotations.Expose;
import game.Element;

import java.util.ArrayList;

/**#
 * @author Ilja Knis
 */
public class PushPanel extends Element {

    @Expose
    private final String type;
    @Expose
    private final String isOnBoard;
    @Expose
    private final ArrayList<String> orientations;
    @Expose
    private final ArrayList<Integer> registers;

    public PushPanel(String type, String isOnBoard, ArrayList<String> orientations, ArrayList<Integer> registers){
        this.type = type;
        this.isOnBoard = isOnBoard;
        this.orientations = new ArrayList<>();
        this.orientations.addAll(orientations);
        this.registers = registers;
    }

    public String getType() {
        return type;
    }

    public String getIsOnBoard() {
        return isOnBoard;
    }

    public ArrayList<String> getOrientations() {
        return orientations;
    }

    public ArrayList<Integer> getRegisters() {
        return registers;
    }
}

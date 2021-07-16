package json.protocol;

import com.google.gson.annotations.Expose;
import server.ClientHandler;
import server.MessageHandler;
import server.Server;

/**
 * @author Mohamad
 */
public class SelectedCardBody implements ClientMessageAction<SelectedCardBody> {
    @Expose
    private String card;
    @Expose
    private int register;

    public SelectedCardBody(String card, int register) {
        this.card = card;
        this.register = register;
    }

    @Override
    public void triggerAction(Server server, ClientHandler clientHandler, SelectedCardBody bodyObject, MessageHandler messageHandler) {
        messageHandler.handleSelectedCard(server, clientHandler, bodyObject);
    }

    public String getCard() {
        return card;
    }

    public int getRegister() {
        return register;
    }
}

package json.protocol;

import com.google.gson.annotations.Expose;
import server.ClientHandler;
import server.MessageHandler;
import server.Server;

/**
 * @author Mohamad
 */
public class BuyUpgradeBody implements ClientMessageAction<BuyUpgradeBody> {

    @Expose
    private boolean isBuying;
    @Expose
    private String card;

    public BuyUpgradeBody(boolean isBuying, String card) {
        this.isBuying = isBuying;
        this.card = card;
    }

    @Override
    public void triggerAction(Server server, ClientHandler clientHandler, BuyUpgradeBody bodyObject, MessageHandler messageHandler) {
        messageHandler.handleBuyUpgrade(server, clientHandler, bodyObject);
    }

    public boolean isBuying() {
        return isBuying;
    }

    public String getCard() {
        return card;
    }
}

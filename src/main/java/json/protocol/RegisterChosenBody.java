package json.protocol;

import client.model.ClientModel;
import client.model.MessageHandler;
import com.google.gson.annotations.Expose;

/**
 * @author Viktoria
 */
public class RegisterChosenBody implements ServerMessageAction<RegisterChosenBody> {
    @Expose
    private int clientID;

    @Expose
    private int register;


    @Override
    public void triggerAction(ClientModel client, RegisterChosenBody bodyObject, MessageHandler messageHandler) {
        messageHandler.handleRegisterChosen(client, bodyObject);
    }


    public int getClientID() {
        return clientID;
    }

    public int getRegister() {
        return register;
    }

    public RegisterChosenBody(int clientID, int register) {
        this.clientID = clientID;
        this.register = register;
    }
}

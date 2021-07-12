package json.protocol;

import client.model.ClientModel;
import com.google.gson.annotations.Expose;
import server.ClientHandler;
import server.MessageHandler;
import server.Server;

public class ChooseRegisterBody implements ClientMessageAction<ChooseRegisterBody> {

    @Expose
    private int register;

    @Override
    public void triggerAction (Server server, ClientHandler clientHandler, ChooseRegisterBody bodyObject, MessageHandler messageHandler) {
        messageHandler.handleChooseRegister(server, clientHandler, bodyObject);
    }

    public ChooseRegisterBody (int register) {
        this.register = register;
    }

    public int getRegister () {
        return register;
    }
}

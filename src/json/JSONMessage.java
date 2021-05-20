package json;

import com.google.gson.annotations.Expose;

/**
 * @author Mohamad, Viktoria
 */

public class JSONMessage {
    @Expose
    private String messageType;
    @Expose
    private Object messageBody;

    public JSONMessage(String messageType, Object messageBody) {
        this.messageType = messageType;
        this.messageBody = messageBody;
    }

    public String getMessageType() {
        return messageType;
    }

    public Object getMessageBody() {
        return messageBody;
    }

}

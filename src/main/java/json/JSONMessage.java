package json;

import com.google.gson.annotations.Expose;

/**
 * @author Mohamad, Viktoria
 * JSON message hat nach Protokol nur zwei Attribute; messageType (String) und messageBody (eine Klasse)
 * Expose ist bei den beiden true und sie werden beide serialized / deserialized
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

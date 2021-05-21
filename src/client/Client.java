package client;

import game.Player;
import json.JSONSerializer;
import json.JSONMessage;
import json.MessageHandler;
import json.protocol.HelloServerBody;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Logger;

/**
 * @author Mohamad, Viktoria
 */

public class Client {
    private Socket socket;
    private final String SERVER_IP;
    private final int SERVER_PORT;
    private boolean waitingForServer;

    private static final Logger logger = Logger.getLogger(Client.class.getName());
    private String protocolVersion = "Version 0.1";
    private MessageHandler messageHandler = new MessageHandler();
    private String group = "BlindeBonbons";

    private Player player;


    //konstuktor for client
    public Client (String serverIP, int serverPort) {
        this.SERVER_IP = serverIP;
        this.SERVER_PORT = serverPort;
    }

    /**
     * This method is responsible for connecting the client to the specified server.
     * It uses the {@link client.ClientApplication} to get the IP and Port.
     *
     * @return <code>true</code> if connection could be established.
     */
    public boolean connectClient () {
        try {
            //Create socket to connect to server at serverIP:serverPort
            logger.info("Trying to connect to the server on the port " + SERVER_IP + " : " + SERVER_PORT);
            socket = new Socket(SERVER_IP, SERVER_PORT);

            //Start new Thread, that reads incoming messages from server
            ClientThread clientThread = new ClientThread(this, socket);
            Thread readerThread = new Thread(clientThread);
            readerThread.start();

            //TODO kein Busy-Waiting, change to notify()
            //waiting for server response - waitingForHelloClient is changed wenn die clientThread bekommt
            //ein JSONMessage mit dem type HelloClient
            waitingForServer = true;
            while (waitingForServer) {
                logger.info("Waiting for the server answer...");
                if (waitingForServer) try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            //Outputstream for clientsocket und writer for it
            OutputStreamWriter out = new OutputStreamWriter(socket.getOutputStream());
            PrintWriter writer = new PrintWriter(out);

            // Give the server Information about group, AI and the protocol version with the
            //JSONMessage with HelloServerBody
            //for normal clients is isAI false.
            JSONMessage jsonMessage = new JSONMessage("HelloServer", new HelloServerBody(group, false, protocolVersion));
            writer.println(JSONSerializer.serializeJSON(jsonMessage));
            writer.flush();

        } catch (IOException exp) {
            exp.printStackTrace();
        }
        return false;
    }

    public void setPlayer (Player player) {
        this.player = player;
    }

    public MessageHandler getMessageDistributer () {
        return messageHandler;
    }

    public void setWaitingForServer (boolean waitingForServer) {
        this.waitingForServer = waitingForServer;
    }
}

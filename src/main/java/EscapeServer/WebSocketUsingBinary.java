package EscapeServer;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

@ServerEndpoint("/bws")
public class WebSocketUsingBinary extends MyWebSocket {

    private Session session;
    private User user;
    /**
     * @param session
     */
    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        addOnlineCount();
        user = new User(this);
        System.out.println("We have a new connection! Total connection count: " + getOnlineCount());
    }

    @OnClose
    public void onClose() {
        if(MyWebSocket.myWebSocketSet.contains(this)) MyWebSocket.myWebSocketSet.remove(this);
        String name = "#";
        if(user.player != null && MyWebSocket.onlineUsernames.contains(user.player.getName())){
            name = user.player.getName();
            MyWebSocket.onlineUsernames.remove(user.player.getName());
        }
        try {
            this.session.close();
            this.session = null;
            if(!name.equals("#"))sendAllMessage("00049#" + name);
        } catch (IOException e) {
            e.printStackTrace();
        }
        subOnlineCount();           //在线数减1
        System.out.println("Someone disconnected! Total current connection amount: " + getOnlineCount());
    }

    /**
     * @param message
     * @param session
     */
    @OnMessage
    public void onMessage(ByteBuffer message, Session session) throws IOException {
        String s = StandardCharsets.UTF_8.decode(message).toString();
        System.out.println("WebSocketUsingString message from client in binary: " + s);
        user.processMessage(s);
    }

    /**
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        if(MyWebSocket.myWebSocketSet.contains(this)) MyWebSocket.myWebSocketSet.remove(this);
        if(user.player != null && MyWebSocket.onlineUsernames.contains(user.player.getName())) MyWebSocket.onlineUsernames.remove(user.player.getName());
        System.out.println("Error");
        error.printStackTrace();
    }

    /**
     * @param message
     * @throws IOException
     */

    void sendAllMessage(String message) {
        for (MyWebSocket item : MyWebSocket.myWebSocketSet) {
            try {
                item.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    void sendMessage(String message) throws IOException {
        ByteBuffer msg = StandardCharsets.UTF_8.encode(message);
        this.session.getBasicRemote().sendBinary(msg);
    }

    static synchronized int getOnlineCount() {
        return MyWebSocket.onlineCount;
    }

    static synchronized void addOnlineCount() {
        MyWebSocket.onlineCount++;
    }

    static synchronized void subOnlineCount() {
        MyWebSocket.onlineCount--;
    }

    public void clientMessage(String message) throws IOException {
        user.processMessage(message);
    }
}

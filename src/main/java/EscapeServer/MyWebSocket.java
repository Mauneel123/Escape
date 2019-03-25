package EscapeServer;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;

class MyWebSocket {
    static int onlineCount = 0;
    static CopyOnWriteArraySet<MyWebSocket> myWebSocketSet = new CopyOnWriteArraySet<MyWebSocket>();
    static CopyOnWriteArraySet<String> onlineUsernames = new CopyOnWriteArraySet<String>();

    static List<String> chatHistory;
    static int newChatAmount;
    void sendMessage(String message) throws IOException {
    }

    void sendAllMessage(String message) {
    }

    public void clientMessage(String message) throws IOException {
    }
}

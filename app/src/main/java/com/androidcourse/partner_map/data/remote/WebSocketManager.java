package com.androidcourse.partner_map.data.remote;

import android.os.Handler;
import android.os.Looper;

import com.androidcourse.partner_map.app.Constants;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class WebSocketManager {
    private static WebSocketManager instance;
    private WebSocket webSocket;
    private final OkHttpClient client;
    private final List<MessageListener> messageListeners = new ArrayList<>();
    private final List<ConnectionListener> connectionListeners = new ArrayList<>();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private int reconnectCount = 0;
    private static final int MAX_RECONNECT = 3;
    private String token;
    private boolean isConnected = false;

    public interface MessageListener {
        void onMessage(String type, String payload);
    }

    public interface ConnectionListener {
        void onConnectionChanged(boolean connected);
    }

    private WebSocketManager() {
        client = new OkHttpClient();
    }

    public static synchronized WebSocketManager getInstance() {
        if (instance == null) {
            instance = new WebSocketManager();
        }
        return instance;
    }

    public void connect(String token) {
        this.token = token;
        if (webSocket != null) {
            webSocket.close(1000, "Reconnecting");
        }
        Request request = new Request.Builder()
                .url(Constants.WS_URL + "?token=" + token)
                .build();
        webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                isConnected = true;
                reconnectCount = 0;
                notifyConnectionChange(true);
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                mainHandler.post(() -> {
                    String type = "NEW_MESSAGE";
                    for (MessageListener listener : messageListeners) {
                        listener.onMessage(type, text);
                    }
                });
            }

            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                isConnected = false;
                notifyConnectionChange(false);
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                isConnected = false;
                notifyConnectionChange(false);
                if (reconnectCount < MAX_RECONNECT) {
                    reconnectCount++;
                    mainHandler.postDelayed(() -> connect(token), 5000);
                }
            }
        });
    }

    public void disconnect() {
        if (webSocket != null) {
            webSocket.close(1000, "Logout");
            webSocket = null;
        }
        isConnected = false;
        reconnectCount = 0;
        notifyConnectionChange(false);
    }

    public void sendMessage(String payload) {
        if (webSocket != null && isConnected) {
            webSocket.send(payload);
        }
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void addMessageListener(MessageListener listener) {
        if (!messageListeners.contains(listener)) {
            messageListeners.add(listener);
        }
    }

    public void removeMessageListener(MessageListener listener) {
        messageListeners.remove(listener);
    }

    public void addConnectionListener(ConnectionListener listener) {
        if (!connectionListeners.contains(listener)) {
            connectionListeners.add(listener);
        }
    }

    public void removeConnectionListener(ConnectionListener listener) {
        connectionListeners.remove(listener);
    }

    private void notifyConnectionChange(boolean connected) {
        mainHandler.post(() -> {
            for (ConnectionListener listener : connectionListeners) {
                listener.onConnectionChanged(connected);
            }
        });
    }
}

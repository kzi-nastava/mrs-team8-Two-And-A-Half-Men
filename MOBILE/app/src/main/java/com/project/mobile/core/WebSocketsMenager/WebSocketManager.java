package com.project.mobile.core.WebSocketsMenager;



import android.os.Handler;
import android.os.Looper;
import android.util.Log;


import androidx.annotation.NonNull;

import com.project.mobile.BuildConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import io.reactivex.disposables.Disposable;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;


public class WebSocketManager {
    private static WebSocketManager instance;
    private final StompClient stompClient;
    private final Map<String, List<MessageCallback>> topicCallbacks = new HashMap<>();
    private final Map<String, Disposable> subscriptions = new HashMap<>();
    private static final String TAG = "WebSocketManager";
    private final Handler reconnectHandler = new Handler(Looper.getMainLooper());
    private static final int RECONNECT_DELAY = 5000;
    private boolean isConnected = false;
    private boolean isConnecting = false;
    private Disposable lifecycleDisposable;

    private WebSocketManager() {
        Log.d("WebSocketManager", "Initializing WebSocketManager with URL: " + "http://" + BuildConfig.BASE_URL + ":8080/socket/websocket");
        stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP,
                "http://" + BuildConfig.BASE_URL + ":8080/socket/websocket");

        if (lifecycleDisposable != null && !lifecycleDisposable.isDisposed()) {
            lifecycleDisposable.dispose();
        }

        lifecycleDisposable = stompClient.lifecycle()
                .subscribe(lifecycleEvent -> {
                    switch (lifecycleEvent.getType()) {
                        case OPENED:
                            Log.d(TAG, "STOMP Connected");
                            isConnected = true;
                            isConnecting = false;  // ← Reset flag
                            break;
                        case ERROR:
                            Log.e(TAG, "STOMP Error", lifecycleEvent.getException());
                            isConnected = false;
                            isConnecting = false;  // ← Reset flag
                            scheduleReconnect();
                            break;
                        case CLOSED:
                            Log.d(TAG, "STOMP Closed");
                            isConnected = false;
                            isConnecting = false;  // ← Reset flag
                            scheduleReconnect();
                            break;
                    }
                }, throwable -> {
                    Log.e(TAG, "❌ Lifecycle error", throwable);
                    isConnecting = false;  // ← Reset flag
                });
    }

    private static synchronized WebSocketManager getInstance() {
        if (instance == null) instance = new WebSocketManager();
        return instance;
    }


    public static void subscribe(@NonNull String topic, @NonNull MessageCallback callback) {
        WebSocketManager manager = getInstance();

        List<MessageCallback> callbacks = manager.topicCallbacks.get(topic);
        if (callbacks == null) {
            callbacks = new ArrayList<>();
            manager.topicCallbacks.put(topic, callbacks);
        }
        if (!callbacks.contains(callback)) callbacks.add(callback);

        if (!manager.isConnected && !manager.isConnecting) {
            manager.connect();
        }

        if (!manager.subscriptions.containsKey(topic)) {
            Disposable disposable = manager.stompClient.topic(topic)
                    .subscribe(message -> {
                        List<MessageCallback> cbs = manager.topicCallbacks.get(topic);
                        if (cbs != null) {
                            for (MessageCallback cb : new ArrayList<>(cbs)) {
                                cb.onMessage(message.getPayload());
                            }
                        }
                    }, throwable -> Log.e(TAG, "Subscribe error for topic " + topic, throwable));

            manager.subscriptions.put(topic, disposable);
        }
    }

    public static void unsubscribe(@NonNull String topic, @NonNull MessageCallback callback) {
        WebSocketManager manager = getInstance();

        List<MessageCallback> callbacks = manager.topicCallbacks.get(topic);
        if (callbacks != null) {
            callbacks.remove(callback);
            if (callbacks.isEmpty()) {
                Disposable disposable = manager.subscriptions.remove(topic);
                if (disposable != null && !disposable.isDisposed()) disposable.dispose();
                manager.topicCallbacks.remove(topic);
            }
        }
        if (manager.topicCallbacks.isEmpty()) {
            manager.disconnect();
        }
    }

    private static void unsubscribe(@NonNull String topic) {
        WebSocketManager manager = getInstance();
        List<MessageCallback> callbacks = manager.topicCallbacks.get(topic);
        if (callbacks != null) {
            callbacks.clear();
        }

        Disposable disposable = manager.subscriptions.remove(topic);
        if (disposable != null && !disposable.isDisposed()) disposable.dispose();
        manager.topicCallbacks.remove(topic);

        if (manager.topicCallbacks.isEmpty()) {
            manager.disconnect();
        }
    }

    public static void send(@NonNull String destination, @NonNull String message) {
        WebSocketManager manager = getInstance();
        if (!manager.isConnected) manager.connect();

        manager.stompClient.send(destination, message)
                .subscribe(() -> Log.d(TAG, "Message sent to " + destination),
                        throwable -> Log.e(TAG, "Send error", throwable));
    }


    private void connect() {
        if (!isConnected && !isConnecting) {
            isConnecting = true;
            stompClient.connect();
        }
    }

    private void disconnect() {
        reconnectHandler.removeCallbacksAndMessages(null);
        for (Disposable d : subscriptions.values()) {
            if (!d.isDisposed()) d.dispose();
        }
        subscriptions.clear();
        topicCallbacks.clear();
        stompClient.disconnect();
        isConnected = false;
        isConnecting = false;
    }

    private void scheduleReconnect() {
        reconnectHandler.removeCallbacksAndMessages(null);
        if (!topicCallbacks.isEmpty()) {
            reconnectHandler.postDelayed(() -> {
                if (!isConnected && !topicCallbacks.isEmpty()) connect();
            }, RECONNECT_DELAY);
        }
    }
}

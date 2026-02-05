package com.project.mobile.core.WebSocketsMenager;

public interface MessageCallback {

    /* Callback massage hendles it dont forget to move to main thread if you want to hange it !!
    *
    * new Handler(Looper.getMainLooper()).post(() -> {
        // safe to update UI or show notification
        showNotification(data);
    });
    * exemple how
    *
    * */

    void onMessage(String message);

}

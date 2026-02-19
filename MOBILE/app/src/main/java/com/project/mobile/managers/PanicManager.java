package com.project.mobile.managers;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.project.mobile.AdminActivity;
import com.project.mobile.R;
import com.project.mobile.core.WebSocketsMenager.MessageCallback;
import com.project.mobile.core.WebSocketsMenager.WebSocketManager;

public class PanicManager {

    private static final String TAG = "PanicManager";
    private static final String CHANNEL_ID = "panic_notifications";
    private static final String CHANNEL_NAME = "Panic Notifications";

    private static PanicManager instance;

    private final Context context;
    private MessageCallback wsCallback;
    private Long subscribedUserId = null;

    private final java.util.List<PanicListener> listeners = new java.util.ArrayList<>();

    // ─── DTO ───────────────────────────────────────────────────────────────

    public static class PanicNotification {
        private String passengerName;
        private String driverName;
        private String location;
        private String rideId;

        public String getPassengerName() { return passengerName; }
        public String getDriverName() { return driverName; }
        public String getLocation() { return location; }
        public String getRideId() { return rideId; }
    }

    // ─── LISTENER ──────────────────────────────────────────────────────────

    public interface PanicListener {
        void onPanicReceived(PanicNotification notification);
        void onError(String error);
    }

    // ─── CONSTRUCTOR ───────────────────────────────────────────────────────

    private PanicManager(Context context) {
        this.context = context.getApplicationContext();
        createNotificationChannel();
    }

    public static synchronized PanicManager getInstance(Context context) {
        if (instance == null) {
            instance = new PanicManager(context);
        }
        return instance;
    }

    // ─── SUBSCRIPTION ──────────────────────────────────────────────────────

    /**
     * Call this after login, only for ADMIN users
     */
    public void subscribeToPanic(long userId) {
        if (subscribedUserId != null && subscribedUserId == userId) {
            Log.d(TAG, "Already subscribed to panic for user " + userId);
            return;
        }

        unsubscribeFromPanic();
        subscribedUserId = userId;

        Log.d(TAG, "Subscribing to /topic/panic");

        wsCallback = WebSocketManager.subscribe("/topic/panic", message -> {
            new Handler(Looper.getMainLooper()).post(() -> handleNewPanic(message));
        });
    }

    /**
     * Call this on logout or when user is no longer ADMIN
     */
    public void unsubscribeFromPanic() {
        if (subscribedUserId != null && wsCallback != null) {
            Log.d(TAG, "Unsubscribing from panic");
            WebSocketManager.unsubscribe("/topic/panic", wsCallback);
            wsCallback = null;
            subscribedUserId = null;
        }
    }

    // ─── WEBSOCKET HANDLING ────────────────────────────────────────────────

    private void handleNewPanic(String message) {
        try {
            Log.d("PanicManager", "Received panic message: " + message);
            Gson gson = new Gson();
            PanicNotification notification = gson.fromJson(message, PanicNotification.class);

            Log.d(TAG, "Panic received! RideId: " + notification.getRideId());

            if (hasNotificationPermission()) {
                showPushNotification(notification);
            }

            playPanicSound();
            vibrateDevice();

            // Notify all listeners (e.g. your Activity/Fragment)
            for (PanicListener listener : listeners) {
                listener.onPanicReceived(notification);
            }

        } catch (Exception e) {
            Log.e(TAG, "Error parsing panic notification", e);
        }
    }

    // ─── PUSH NOTIFICATION ─────────────────────────────────────────────────

    private void showPushNotification(PanicNotification notification) {
        android.app.NotificationManager nm =
                (android.app.NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // This intent opens RideDetailActivity and passes the rideId
        // The activity will then navigate to the correct fragment
        Intent intent = new Intent(context, AdminActivity.class);
        intent.putExtra("openFragment", "PanicHandleFragment");
        intent.putExtra("rideId", Long.parseLong(notification.getRideId())); // pass as Long to match your fragment
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                notification.getRideId().hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        androidx.core.app.NotificationCompat.Builder builder =
                new androidx.core.app.NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_taxi)
                        .setContentTitle("🚨 Panic Alert")
                        .setContentText("Driver: " + notification.getDriverName() + " | " + notification.getLocation())
                        .setPriority(androidx.core.app.NotificationCompat.PRIORITY_HIGH)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent);

        nm.notify(notification.getRideId().hashCode(), builder.build());
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    android.app.NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Panic alerts for admins");
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{0, 300, 100, 300, 100, 300});

            android.app.NotificationManager nm =
                    context.getSystemService(android.app.NotificationManager.class);
            nm.createNotificationChannel(channel);
        }
    }

    private void playPanicSound() {
        try {
            Uri soundUri = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.notification_sound);
            android.media.MediaPlayer mediaPlayer = android.media.MediaPlayer.create(context, soundUri);
            if (mediaPlayer != null) {
                mediaPlayer.setVolume(1.0f, 1.0f); // full volume for panic
                mediaPlayer.start();
                mediaPlayer.setOnCompletionListener(mp -> mp.release());
            } else {
                Uri defaultUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
                android.media.Ringtone ringtone = RingtoneManager.getRingtone(context, defaultUri);
                ringtone.play();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error playing panic sound", e);
        }
    }

    private void vibrateDevice() {
        try {
            Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            if (vibrator != null && vibrator.hasVibrator()) {
                long[] pattern = {0, 300, 100, 300, 100, 300};
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1));
                } else {
                    vibrator.vibrate(pattern, -1);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error vibrating device", e);
        }
    }

    private boolean hasNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                    == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    // ─── LISTENERS ─────────────────────────────────────────────────────────

    public void addListener(PanicListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeListener(PanicListener listener) {
        listeners.remove(listener);
    }

    // ─── CLEANUP ───────────────────────────────────────────────────────────

    public void cleanup() {
        unsubscribeFromPanic();
        listeners.clear();
    }
}
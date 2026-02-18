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
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.project.mobile.DTO.notifications.Notification;
import com.project.mobile.R;
import com.project.mobile.activities.NotificationsActivity;
import com.project.mobile.core.WebSocketsMenager.MessageCallback;
import com.project.mobile.core.WebSocketsMenager.WebSocketManager;
import com.project.mobile.core.retrofitClient.RetrofitClient;
import com.project.mobile.service.NotificationService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationManager {
    private static final String TAG = "NotificationManager";
    private static final String CHANNEL_ID = "ride_notifications";
    private static final String CHANNEL_NAME = "Ride Notifications";
    private static NotificationManager instance;

    private final Context context;
    private final NotificationService notificationService;
    private final List<Notification> notifications = new ArrayList<>();
    private final List<NotificationListener> listeners = new ArrayList<>();
    
    private MessageCallback wsCallback;
    private Long subscribedUserId = null;
    private boolean isLoading = false;

    public interface NotificationListener {
        void onNotificationsChanged(List<Notification> notifications);
        void onUnreadCountChanged(int count);
        void onError(String error);
    }

    private NotificationManager(Context context) {
        this.context = context.getApplicationContext();
        this.notificationService = RetrofitClient.retrofit.create(NotificationService.class);
        createNotificationChannel();
    }

    public static synchronized NotificationManager getInstance(Context context) {
        if (instance == null) {
            instance = new NotificationManager(context);
        }
        return instance;
    }

    // ─── SUBSCRIPTION ──────────────────────────────────────────────────────

    /**
     * Subscribe to notifications for a specific user
     */
    public void subscribeToNotifications(long userId) {
        if (subscribedUserId != null && subscribedUserId == userId) {
            Log.d(TAG, "Already subscribed to notifications for user " + userId);
            return;
        }

        // Unsubscribe from previous user if exists
        unsubscribeFromNotifications();

        subscribedUserId = userId;
        String topic = "/topic/notifications/" + userId;

        Log.d(TAG, "Subscribing to notifications for user " + userId);

        wsCallback = WebSocketManager.subscribe(topic, message -> {
            new Handler(Looper.getMainLooper()).post(() -> {
                handleNewNotification(message);
            });
        });

        // Fetch all notifications on subscribe
        fetchAllNotifications();
    }

    /**
     * Unsubscribe from notifications
     */
    public void unsubscribeFromNotifications() {
        if (subscribedUserId != null && wsCallback != null) {
            String topic = "/topic/notifications/" + subscribedUserId;
            Log.d(TAG, "Unsubscribing from notifications");
            WebSocketManager.unsubscribe(topic, wsCallback);
            wsCallback = null;
            subscribedUserId = null;
        }
    }

    // ─── DATA FETCHING ─────────────────────────────────────────────────────

    /**
     * Fetch all notifications from server
     */
    public void fetchAllNotifications() {
        isLoading = true;

        notificationService.getAllNotifications().enqueue(new Callback<List<Notification>>() {
            @Override
            public void onResponse(@NonNull Call<List<Notification>> call, 
                                   @NonNull Response<List<Notification>> response) {
                isLoading = false;
                if (response.isSuccessful() && response.body() != null) {
                    List<Notification> fetchedNotifications = response.body();
                    
                    // Sort by timestamp (newest first)
                    Collections.sort(fetchedNotifications, new Comparator<Notification>() {
                        @Override
                        public int compare(Notification n1, Notification n2) {
                            return n2.getTimestamp().compareTo(n1.getTimestamp());
                        }
                    });

                    synchronized (notifications) {
                        notifications.clear();
                        notifications.addAll(fetchedNotifications);
                    }

                    notifyListeners();
                    Log.d(TAG, "Fetched " + fetchedNotifications.size() + " notifications");
                } else {
                    notifyError("Failed to load notifications");
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Notification>> call, @NonNull Throwable t) {
                isLoading = false;
                Log.e(TAG, "Error fetching notifications", t);
                notifyError("Failed to load notifications: " + t.getMessage());
            }
        });
    }

    // ─── WEBSOCKET HANDLING ────────────────────────────────────────────────

    /**
     * Handle new notification from WebSocket
     */
    private void handleNewNotification(String message) {
        try {
            Gson gson = new Gson();
            Notification notification = gson.fromJson(message, Notification.class);

            Log.d(TAG, "New notification received: " + notification.getTitle());

            // Add to list (prepend for newest first)
            synchronized (notifications) {
                notifications.add(0, notification);
            }

            // Show push notification
             if (hasNotificationPermission()) {
                showPushNotification(notification);
            }

            // Play sound and vibrate
            playNotificationSound();
            vibrateDevice();

            // Notify listeners
            notifyListeners();

        } catch (Exception e) {
            Log.e(TAG, "Error parsing notification", e);
        }
    }

    // ─── NOTIFICATION ACTIONS ──────────────────────────────────────────────

    /**
     * Mark notification as read
     */
    public void markAsRead(long notificationId) {
        // Optimistically update local
        synchronized (notifications) {
            for (Notification n : notifications) {
                if (n.getId() == notificationId) {
                    n.setRead(true);
                    break;
                }
            }
        }
        notifyListeners();

        // Update on server
        notificationService.markAsRead(notificationId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Notification " + notificationId + " marked as read");
                } else {
                    // Revert on error
                    synchronized (notifications) {
                        for (Notification n : notifications) {
                            if (n.getId() == notificationId) {
                                n.setRead(false);
                                break;
                            }
                        }
                    }
                    notifyListeners();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Log.e(TAG, "Error marking notification as read", t);
                // Revert on error
                synchronized (notifications) {
                    for (Notification n : notifications) {
                        if (n.getId() == notificationId) {
                            n.setRead(false);
                            break;
                        }
                    }
                }
                notifyListeners();
            }
        });
    }

    /**
     * Mark all notifications as read
     */
    public void markAllAsRead() {
        // Optimistically update local
        synchronized (notifications) {
            for (Notification n : notifications) {
                n.setRead(true);
            }
        }
        notifyListeners();

        // Update on server
        notificationService.markAllAsRead().enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "All notifications marked as read");
                } else {
                    fetchAllNotifications(); // Reload on error
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Log.e(TAG, "Error marking all as read", t);
                fetchAllNotifications(); // Reload on error
            }
        });
    }

    /**
     * Delete notification
     */
    public void deleteNotification(long notificationId) {
        // Store for potential rollback
        Notification deletedNotification = null;
        int deletedIndex = -1;

        synchronized (notifications) {
            for (int i = 0; i < notifications.size(); i++) {
                if (notifications.get(i).getId() == notificationId) {
                    deletedNotification = notifications.remove(i);
                    deletedIndex = i;
                    break;
                }
            }
        }
        notifyListeners();

        // Delete on server
        final Notification finalDeleted = deletedNotification;
        final int finalIndex = deletedIndex;

        notificationService.deleteNotification(notificationId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Notification " + notificationId + " deleted");
                } else if (finalDeleted != null) {
                    // Revert on error
                    synchronized (notifications) {
                        notifications.add(finalIndex, finalDeleted);
                    }
                    notifyListeners();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Log.e(TAG, "Error deleting notification", t);
                if (finalDeleted != null) {
                    synchronized (notifications) {
                        notifications.add(finalIndex, finalDeleted);
                    }
                    notifyListeners();
                }
            }
        });
    }

    /**
     * Clear all read notifications
     */
    public void clearReadNotifications() {
        // Store read notifications for rollback
        final List<Notification> readNotifications = new ArrayList<>();
        synchronized (notifications) {
            for (Notification n : new ArrayList<>(notifications)) {
                if (n.isRead()) {
                    readNotifications.add(n);
                    notifications.remove(n);
                }
            }
        }
        notifyListeners();

        // Delete on server
        notificationService.clearReadNotifications().enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Read notifications cleared");
                } else {
                    // Revert on error
                    synchronized (notifications) {
                        notifications.addAll(readNotifications);
                        Collections.sort(notifications, new Comparator<Notification>() {
                            @Override
                            public int compare(Notification n1, Notification n2) {
                                return n2.getTimestamp().compareTo(n1.getTimestamp());
                            }
                        });
                    }
                    notifyListeners();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Log.e(TAG, "Error clearing read notifications", t);
                synchronized (notifications) {
                    notifications.addAll(readNotifications);
                    Collections.sort(notifications, new Comparator<Notification>() {
                        @Override
                        public int compare(Notification n1, Notification n2) {
                            return n2.getTimestamp().compareTo(n1.getTimestamp());
                        }
                    });
                }
                notifyListeners();
            }
        });
    }

    // ─── PUSH NOTIFICATIONS ────────────────────────────────────────────────

    /**
     * Show Android push notification
     */
    private void showPushNotification(Notification notification) {
        android.app.NotificationManager notificationManager = 
            (android.app.NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(context, NotificationsActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
            context,
            (int) notification.getId(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_taxi)
            .setContentTitle(notification.getTitle())
            .setContentText(notification.getMessage())
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent);

        notificationManager.notify((int) notification.getId(), builder.build());
    }

    /**
     * Create notification channel (Android 8.0+)
     */
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                android.app.NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Notifications for ride updates");
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{0, 500, 200, 500});

            android.app.NotificationManager notificationManager = 
                context.getSystemService(android.app.NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    /**
     * Play notification sound
     */
    private void playNotificationSound() {
        try {
            // Try to play custom sound from raw folder
            Uri soundUri = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.notification_sound);
            android.media.MediaPlayer mediaPlayer = android.media.MediaPlayer.create(context, soundUri);
            if (mediaPlayer != null) {
                mediaPlayer.setVolume(0.5f, 0.5f);
                mediaPlayer.start();
                mediaPlayer.setOnCompletionListener(mp -> mp.release());
            } else {
                // Fallback to default notification sound
                Uri defaultUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                android.media.Ringtone ringtone = RingtoneManager.getRingtone(context, defaultUri);
                ringtone.play();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error playing notification sound", e);
        }
    }

    /**
     * Vibrate device
     */
    private void vibrateDevice() {
        try {
            Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            if (vibrator != null && vibrator.hasVibrator()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createWaveform(new long[]{0, 500, 200, 500}, -1));
                } else {
                    vibrator.vibrate(new long[]{0, 500, 200, 500}, -1);
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
        return true; // Older versions don't need permission
    }

    // ─── GETTERS ───────────────────────────────────────────────────────────

    public List<Notification> getNotifications() {
        synchronized (notifications) {
            return new ArrayList<>(notifications);
        }
    }

    public List<Notification> getUnreadNotifications() {
        List<Notification> unread = new ArrayList<>();
        synchronized (notifications) {
            for (Notification n : notifications) {
                if (!n.isRead()) {
                    unread.add(n);
                }
            }
        }
        return unread;
    }

    public List<Notification> getReadNotifications() {
        List<Notification> read = new ArrayList<>();
        synchronized (notifications) {
            for (Notification n : notifications) {
                if (n.isRead()) {
                    read.add(n);
                }
            }
        }
        return read;
    }

    public int getUnreadCount() {
        int count = 0;
        synchronized (notifications) {
            for (Notification n : notifications) {
                if (!n.isRead()) {
                    count++;
                }
            }
        }
        return count;
    }

    public boolean isLoading() {
        return isLoading;
    }

    // ─── LISTENERS ─────────────────────────────────────────────────────────

    public void addListener(NotificationListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeListener(NotificationListener listener) {
        listeners.remove(listener);
    }

    private void notifyListeners() {
        List<Notification> currentNotifications = getNotifications();
        int unreadCount = getUnreadCount();

        for (NotificationListener listener : listeners) {
            listener.onNotificationsChanged(currentNotifications);
            listener.onUnreadCountChanged(unreadCount);
        }
    }

    private void notifyError(String error) {
        for (NotificationListener listener : listeners) {
            listener.onError(error);
        }
    }

    // ─── CLEANUP ───────────────────────────────────────────────────────────

    /**
     * Cleanup (call on logout)
     */
    public void cleanup() {
        unsubscribeFromNotifications();
        synchronized (notifications) {
            notifications.clear();
        }
        listeners.clear();
        notifyListeners();
    }
}

package com.project.mobile.service;

import com.project.mobile.DTO.notifications.Notification;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.Path;

public interface NotificationService {

    @GET("notifications")
    Call<List<Notification>> getAllNotifications();

    @PATCH("notifications/{id}/read")
    Call<Void> markAsRead(@Path("id") long notificationId);

    @PATCH("notifications/read-all")
    Call<Void> markAllAsRead();

    @DELETE("notifications/{id}")
    Call<Void> deleteNotification(@Path("id") long notificationId);

    @DELETE("notifications/read")
    Call<Void> clearReadNotifications();
}

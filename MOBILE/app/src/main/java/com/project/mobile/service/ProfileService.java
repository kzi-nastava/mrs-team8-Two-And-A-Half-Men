package com.project.mobile.service;

import com.project.mobile.DTO.profile.ImageUploadResponse;
import com.project.mobile.DTO.profile.PasswordChangeRequest;
import com.project.mobile.DTO.profile.ProfileResponse;
import com.project.mobile.DTO.profile.ProfileUpdateRequest;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;

public interface ProfileService {
    @GET("profile")
    Call<ProfileResponse> getProfile();

    @PUT("api/v1/profile")
    Call<ProfileResponse> updateProfile(@Body ProfileUpdateRequest request);

    @Multipart
    @POST("api/v1/profile/upload-image")
    Call<ImageUploadResponse> uploadProfileImage(@Part MultipartBody.Part image);

    @POST("api/v1/profile/change-password")
    Call<Void> changePassword(@Body PasswordChangeRequest request);

    @DELETE("api/v1/profile/pending-changes")
    Call<Void> cancelPendingChanges();
}

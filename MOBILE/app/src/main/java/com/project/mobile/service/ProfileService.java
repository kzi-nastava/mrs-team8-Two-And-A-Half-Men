package com.project.mobile.service;

import com.project.mobile.DTO.profile.CancelRequestResponse;
import com.project.mobile.DTO.profile.ImageUploadResponse;
import com.project.mobile.DTO.profile.PasswordChangeRequest;
import com.project.mobile.DTO.profile.ProfileResponse;
import com.project.mobile.DTO.profile.ProfileUpdateRequest;
import com.project.mobile.DTO.profile.ProfileUpdateResponse;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ProfileService {
    @GET("profile")
    Call<ProfileResponse> getProfile();

    @PATCH("profile")
    Call<   ProfileUpdateResponse> updateProfile(@Body ProfileUpdateRequest request);

    @Multipart
    @PUT("profile/picture")
    Call<ImageUploadResponse> uploadProfileImage(@Part MultipartBody.Part file);

    @PATCH("profile/change-password")
    Call<ProfileResponse> changePassword(@Body PasswordChangeRequest request);

    @POST("profile-update-requests/{id}/cancel")
    Call<CancelRequestResponse> cancelPendingChanges(@Path("id") long requestId);
}

package com.project.mobile.service;

import com.project.mobile.DTO.users.BlockUserRequest;
import com.project.mobile.DTO.users.DriverRegistrationRequest;
import com.project.mobile.DTO.users.DriverRegistrationResponse;
import com.project.mobile.DTO.users.UserDetailResponse;
import com.project.mobile.DTO.users.UserPageResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface AdminUserService {
    
    @GET("users")
    Call<UserPageResponse> getUsers(
        @Query("page") int page,
        @Query("size") int size,
        @Query("sortBy") String sortBy,
        @Query("sortDirection") String sortDirection,
        @Query("email") String email,
        @Query("firstName") String firstName,
        @Query("lastName") String lastName,
        @Query("role") String role,
        @Query("isBlocked") Boolean isBlocked,
        @Query("driverStatus") String driverStatus,
        @Query("hasPendingRequests") Boolean hasPendingRequests
    );

    @POST("register/drivers")
    Call<DriverRegistrationResponse> registerDriver(@Body DriverRegistrationRequest request);

    @GET("users/{userId}")
    Call<UserDetailResponse> getUserDetails(@Path("userId") long userId);

    @PATCH("users/{userId}/block")
    Call<Void> blockUser(@Path("userId") long userId, @Body BlockUserRequest request);

    @PATCH("users/{userId}/unblock")
    Call<Void> unblockUser(@Path("userId") long userId);

    @POST("profile-update-requests/{requestId}/approve")
    Call<Void> approveChangeRequest(@Path("requestId") long requestId);

    @POST("profile-update-requests/{requestId}/reject")
    Call<Void> rejectChangeRequest(@Path("requestId") long requestId);
}

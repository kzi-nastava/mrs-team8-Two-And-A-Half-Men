package com.project.mobile.service;

import com.project.mobile.DTO.users.DriverRegistrationRequest;
import com.project.mobile.DTO.users.DriverRegistrationResponse;
import com.project.mobile.DTO.users.UserPageResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
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
    
    // TODO: Add other methods as needed:
    // - getUserDetails(userId)
    // - blockUser(userId, reason)
    // - unblockUser(userId)
    // - approveChangeRequest(requestId)
    // - rejectChangeRequest(requestId)
}

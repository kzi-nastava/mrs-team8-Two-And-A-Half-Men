package com.project.mobile.service;

import com.project.mobile.DTO.Ride.ActivateRequestDTO;
import com.project.mobile.DTO.Auth.ForgotPasswordDTO;
import com.project.mobile.DTO.Auth.MeInfo;
import com.project.mobile.DTO.Auth.RegisterDto;
import com.project.mobile.DTO.Auth.ResetPasswordDTO;
import com.project.mobile.DTO.Auth.UserLoginRequest;
import com.project.mobile.DTO.Auth.UserLoginResponseDto;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface AuthService {

    @POST("login") // replace with your endpoint
    Call<UserLoginResponseDto> loginUser(@Body UserLoginRequest loginDto);

    @GET("me")
    Call<MeInfo> getCurrentUser();

    @POST("users/register")
    Call<ResponseBody> registerUser(@Body RegisterDto dto);

    @POST("activate")
    Call<ResponseBody> activateAccount(@Body ActivateRequestDTO token);
    @POST("forgot-password")
    Call<ResponseBody> forgotPassword(@Body ForgotPasswordDTO dto);

    @POST("reset-password")
    Call<ResponseBody> resetPassword(@Body ResetPasswordDTO dto);

}

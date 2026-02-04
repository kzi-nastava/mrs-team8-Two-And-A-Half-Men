package com.project.mobile.service;

import com.project.mobile.DTO.ActivateRequestDTO;
import com.project.mobile.DTO.MeInfo;
import com.project.mobile.DTO.RegisterDto;
import com.project.mobile.DTO.UserLoginRequest;
import com.project.mobile.DTO.UserLoginResponseDto;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
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

}

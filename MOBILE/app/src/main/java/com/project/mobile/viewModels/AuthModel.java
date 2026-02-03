package com.project.mobile.viewModels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.project.mobile.DTO.LoginTransfer;
import com.project.mobile.DTO.MeInfo;
import com.project.mobile.DTO.UserLoginRequest;
import com.project.mobile.DTO.UserLoginResponseDto;
import com.project.mobile.core.TokenHeandler.JwtToken;
import com.project.mobile.core.retrofitClient.RetrofitClient;
import com.project.mobile.service.AuthService;

import java.util.concurrent.CompletableFuture;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthModel extends ViewModel {


    public MutableLiveData<LoginTransfer> loginResultLiveData = new MutableLiveData<>();
    private AuthService authService = RetrofitClient.retrofit.create(AuthService.class);
    private MeInfo cachedMeInfo = null;

    public void loginUser(UserLoginRequest request) {
        Call<UserLoginResponseDto> call = authService.loginUser(request);

        call.enqueue(new Callback<UserLoginResponseDto>() {
            @Override
            public void onResponse(Call<UserLoginResponseDto> call, Response<UserLoginResponseDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    JwtToken.SaveToken(response.body().getAccessToken());
                    String role = response.body().getRole();
                    loginResultLiveData.postValue(new LoginTransfer(true, role, null));
                } else {
                    loginResultLiveData.postValue(new LoginTransfer(false, null, "Invalid credentials or server error"));
                }
            }

            @Override
            public void onFailure(Call<UserLoginResponseDto> call, Throwable t) {
                loginResultLiveData.postValue(new LoginTransfer(false, null, t.getMessage()));
            }
        });

    }

    public CompletableFuture<MeInfo> getMeInfo() {
        CompletableFuture<MeInfo> future = new CompletableFuture<>();

        // If cached, return immediately
        if (cachedMeInfo != null) {
            future.complete(cachedMeInfo);
            return future;
        }

        // Otherwise fetch from API
        Call<MeInfo> call = authService.getCurrentUser();
        call.enqueue(new Callback<MeInfo>() {
                         @Override
                         public void onResponse(Call<MeInfo> call, Response<MeInfo> response) {
                             if (response.isSuccessful() && response.body() != null) {
                                 cachedMeInfo = response.body();
                                 future.complete(cachedMeInfo);
                             } else {
                                 future.complete(null);
                             }
                         }
                         @Override
                         public void onFailure(Call<MeInfo> call, Throwable t) {
                                future.complete(null);
                         }
                     }
        );
        return future;
    }
}
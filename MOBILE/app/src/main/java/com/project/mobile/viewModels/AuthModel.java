package com.project.mobile.viewModels;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.project.mobile.DTO.Ride.ActivateRequestDTO;
import com.project.mobile.DTO.Auth.ForgotPasswordDTO;
import com.project.mobile.DTO.Auth.LoginTransfer;
import com.project.mobile.DTO.Auth.MeInfo;
import com.project.mobile.DTO.Auth.RegisterDto;
import com.project.mobile.DTO.MessageResponseDTO;
import com.project.mobile.DTO.Auth.ResetPasswordDTO;
import com.project.mobile.DTO.Auth.UserLoginRequest;
import com.project.mobile.DTO.Auth.UserLoginResponseDto;
import com.project.mobile.core.TokenHeandler.JwtToken;
import com.project.mobile.core.retrofitClient.RetrofitClient;
import com.project.mobile.service.AuthService;

import org.json.JSONObject;

import java.util.concurrent.CompletableFuture;

import okhttp3.ResponseBody;
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
                                 JwtToken.clearToken();
                                 future.complete(null);
                             }
                         }
                         @Override
                         public void onFailure(Call<MeInfo> call, Throwable t) {
                                JwtToken.clearToken();
                                future.complete(null);
                         }
                     }
        );
        return future;
    }
    public CompletableFuture<MessageResponseDTO> RegisterUser(RegisterDto registerData) {
            Call<ResponseBody> call = authService.registerUser(registerData);
            CompletableFuture<MessageResponseDTO> future = new CompletableFuture<>();

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        String message = "Registration failed";
                        Log.d("REGISTER_USER", "Response code: " + response.code());
                        Log.d("Body" , String.valueOf(response.body()));
                        if (response.body() != null) {
                            Log.d("REGISTER_USER", "Response body is not null");
                            String json = response.body().string();
                            JSONObject obj = new JSONObject(json);
                            message = obj.optString("message", message);
                        }
                        if(response.errorBody() != null) {
                            Log.d("REGISTER_USER", "Error body is not null");
                            String errorJson = response.errorBody().string();
                            JSONObject errorObj = new JSONObject(errorJson);
                            message = errorObj.optString("error", message);
                        }

                        future.complete(new MessageResponseDTO(response.isSuccessful(), message));

                    } catch (Exception e) {
                        future.complete(new MessageResponseDTO(false, "Response parsing error"));
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    future.complete(new MessageResponseDTO(false, "Server error: " + t.getMessage()));
                }
            });

            return future;
        }
    public CompletableFuture<MessageResponseDTO> activateAccount(String token) {
        Call<ResponseBody> call = authService.activateAccount(new ActivateRequestDTO(token));
        CompletableFuture<MessageResponseDTO> future = new CompletableFuture<>();

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String message = "Activation failed";
                    Log.d("ACTIVATE_ACCOUNT", "Response code: " + response.code());

                    if (response.body() != null) {
                        Log.d("ACTIVATE_ACCOUNT", "Response body is not null");
                        String json = response.body().string();
                        JSONObject obj = new JSONObject(json);
                        message = obj.optString("message", message);
                    }

                    if (response.errorBody() != null) {
                        Log.d("ACTIVATE_ACCOUNT", "Error body is not null");
                        String errorJson = response.errorBody().string();
                        JSONObject errorObj = new JSONObject(errorJson);
                        message = errorObj.optString("message", message);
                    }

                    future.complete(new MessageResponseDTO(response.isSuccessful(), message));

                } catch (Exception e) {
                    Log.e("ACTIVATE_ACCOUNT", "Error parsing response", e);
                    future.complete(new MessageResponseDTO(false, "Response parsing error"));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("ACTIVATE_ACCOUNT", "Network error", t);
                future.complete(new MessageResponseDTO(false, "Server error: " + t.getMessage()));
            }
        });

        return future;
    }

    public void logout() {
        JwtToken.clearToken();
        cachedMeInfo = null;
    }

    public CompletableFuture<MessageResponseDTO> forgotPassword(String email) {
        Log.d("FORGOT_PASSWORD", "Initiating forgot password for email: " + email);
        Call<ResponseBody> call = authService.forgotPassword(new ForgotPasswordDTO(email));
        CompletableFuture<MessageResponseDTO> future = new CompletableFuture<>();

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String message = "Failed to send reset instructions";
                    Log.d("FORGOT_PASSWORD", "Response code: " + response.code());

                    if (response.body() != null) {
                        String json = response.body().string();
                        JSONObject obj = new JSONObject(json);
                        message = obj.optString("message", message);
                    }

                    if (response.errorBody() != null) {
                        String errorJson = response.errorBody().string();
                        JSONObject errorObj = new JSONObject(errorJson);
                        message = errorObj.optString("message", message);
                    }

                    future.complete(new MessageResponseDTO(response.isSuccessful(), message));

                } catch (Exception e) {
                    Log.e("FORGOT_PASSWORD", "Error parsing response", e);
                    future.complete(new MessageResponseDTO(false, "Response parsing error"));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("FORGOT_PASSWORD", "Network error", t);
                future.complete(new MessageResponseDTO(false, "Server error: " + t.getMessage()));
            }
        });

        return future;
    }
    public CompletableFuture<MessageResponseDTO> resetPassword(String token, String newPassword) {
        Call<ResponseBody> call = authService.resetPassword(new ResetPasswordDTO(token, newPassword));
        CompletableFuture<MessageResponseDTO> future = new CompletableFuture<>();

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String message = "Password reset failed";
                    Log.d("RESET_PASSWORD", "Response code: " + response.code());

                    if (response.body() != null) {
                        String json = response.body().string();
                        JSONObject obj = new JSONObject(json);
                        message = obj.optString("message", message);
                    }

                    if (response.errorBody() != null) {
                        String errorJson = response.errorBody().string();
                        JSONObject errorObj = new JSONObject(errorJson);
                        message = errorObj.optString("message", message);
                    }

                    future.complete(new MessageResponseDTO(response.isSuccessful(), message));

                } catch (Exception e) {
                    Log.e("RESET_PASSWORD", "Error parsing response", e);
                    future.complete(new MessageResponseDTO(false, "Response parsing error"));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("RESET_PASSWORD", "Network error", t);
                future.complete(new MessageResponseDTO(false, "Server error: " + t.getMessage()));
            }
        });

        return future;
    }

}
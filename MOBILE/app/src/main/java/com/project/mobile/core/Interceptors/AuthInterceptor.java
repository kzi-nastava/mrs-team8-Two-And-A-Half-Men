package com.project.mobile.core.Interceptors;

import androidx.annotation.NonNull;

import com.project.mobile.core.TokenHeandler.JwtToken;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {


    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        okhttp3.Request originalRequest = chain.request();

        okhttp3.Request.Builder builder = originalRequest.newBuilder();
        String authToken = JwtToken.getToken();
        if (authToken != null && !authToken.isEmpty()) {
            builder.header("Authorization", "Bearer " + authToken);
        }

        okhttp3.Request modifiedRequest = builder.build();
        return chain.proceed(modifiedRequest);
    }

}

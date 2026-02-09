package com.project.mobile.core.retrofitClient;

import com.project.mobile.BuildConfig;
import com.project.mobile.core.Interceptors.AuthInterceptor;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public interface RetrofitClient {
    public static String BASE_URL = "http://"+ BuildConfig.BASE_URL  + ":8080/api/v1/";

    public static OkHttpClient SetUpClient(){
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        //Add interceptor for JWT token here in future
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(120, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS)
                .addInterceptor(interceptor)
                .addInterceptor(new AuthInterceptor())
            .build();

        return client;
    }

    public static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(SetUpClient())
            .build();
}

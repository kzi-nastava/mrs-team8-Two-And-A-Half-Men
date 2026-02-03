package com.project.mobile.core.TokenHeandler;

import android.content.SharedPreferences;

import android.content.Context;

import com.project.mobile.core.Application.MyApp;

public class JwtToken {
    private static String jwtToken="";
    private static final String PREF_NAME = "auth_prefs";
    private static final String KEY_TOKEN = "jwt_token";


    public static String getToken(){
        if(jwtToken.isEmpty()){
            SharedPreferences prefs = MyApp.getAppContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            jwtToken = prefs.getString(KEY_TOKEN, "");
        }
        return jwtToken;
    }
    public static void SaveToken(String token){
        clearToken();
        jwtToken=token;
        SharedPreferences prefs = MyApp.getAppContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_TOKEN, token);
        editor.apply();
    }

    public static void clearToken(){
        jwtToken="";
        SharedPreferences prefs = MyApp.getAppContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(KEY_TOKEN);
        editor.apply();
    }


}

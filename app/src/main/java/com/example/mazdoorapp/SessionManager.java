package com.example.mazdoorapp;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static  String SERVICE = "ser";
    SharedPreferences sharedPreferences;
    public SessionManager(Context context) {
        sharedPreferences = context.getSharedPreferences("ser", Context.MODE_PRIVATE);


    }
    public void saveService(String serviceType) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SERVICE, serviceType);
        editor.apply();

    }
    public String fetchService(){
        return sharedPreferences.getString(SERVICE,null);
    }
}

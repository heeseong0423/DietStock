package com.fournineseven.dietstock;

import android.app.Application;
import android.util.Log;

import com.fournineseven.dietstock.config.TaskServer;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class App extends Application {
    public static Retrofit retrofit;
}

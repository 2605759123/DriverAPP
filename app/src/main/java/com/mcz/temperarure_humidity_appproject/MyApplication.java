package com.mcz.temperarure_humidity_appproject;
import android.app.Application;
import android.content.Context;
    public class MyApplication
    {
        private static Context context;

        public static void setContext(Context context) {
            MyApplication.context = context;
        }
        public static Context getContext() {
            return context;
        }
    }
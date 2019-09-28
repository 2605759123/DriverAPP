package com.mcz.temperarure_humidity_appproject.app.ui.base;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.mcz.temperarure_humidity_appproject.R;

import java.lang.reflect.Field;

/**
 * Created by mcz on 2018/1/8.
 */

public class LoginBaseActivity extends AppCompatActivity{
        @Override
        protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }

        /**
         * 设置沉浸式状态栏
         * 使用背景图片填充沉浸式
         */
    protected void setStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            final ViewGroup linear_bar = (ViewGroup) findViewById(R.id.login_linear_id);
            final int statusHeight = getStatusBarHeight();
            linear_bar.post(new Runnable() {
                @Override
                public void run() {
                    int titleHeight = linear_bar.getHeight();
                    android.widget.LinearLayout.LayoutParams params = (android.widget.LinearLayout.LayoutParams) linear_bar.getLayoutParams();
                    params.height = statusHeight + titleHeight;
                    linear_bar.setLayoutParams(params);
                }
            });
        }
    }
    /**
     * 获取状态栏的高度
     * @return
     */
    protected int getStatusBarHeight(){
        try
        {
            Class<?> c= Class.forName("com.android.internal.R$dimen");
            Object obj=c.newInstance();
            Field field=c.getField("status_bar_height");
            int x= Integer.parseInt(field.get(obj).toString());
            return  getResources().getDimensionPixelSize(x);
        }catch(Exception e){
            e.printStackTrace();
        }
        return 0;
    }
}

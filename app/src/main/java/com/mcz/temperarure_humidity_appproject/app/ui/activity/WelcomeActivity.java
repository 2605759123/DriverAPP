package com.mcz.temperarure_humidity_appproject.app.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;

import com.mcz.temperarure_humidity_appproject.R;
import com.mcz.temperarure_humidity_appproject.app.ui.base.LoginBaseActivity;


/**
 * Created by mcz on 2018/1/8.
 */

public class WelcomeActivity extends LoginBaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_layout);
//        ImmersionBar.with(this)
//                .statusBarColor(R.color.blue)
//                .fitsSystemWindows(true)
//                .init();
        //设置沉浸式标题栏
        setStatusBar();
        initPixels();
        init();
    }
    private void init(){

//        try {
//           String jia= Descode.encryptDES(Config.all_url,"20180108");
//            Log.i("s",jia);
//            String jie=  Descode.decryptDES("Lvl03UP25pMweAowD/2ayvVfukpdgGnRbkl4UWjjcHA=\n","20180108");
//            Log.i("s",jie);
//        } catch ( Exception e ) {
//            e.printStackTrace();
//        }
        /**
         * 使用 线程进行 界面跳转
         */
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run(){
                startActivity(new Intent(WelcomeActivity.this, LoginActivity .class));
                finish();
                overridePendingTransition(R.anim.zoom_enter,
                        R.anim.zoom_exit);
            }
        },2000);
    }

    public static int  load_pm_width=0;
    public static int  load_pm_height=0;
    /**
     * 获取手机分辨率大小
     */
    private void initPixels() {
        DisplayMetrics dm = new DisplayMetrics();

        getWindowManager().getDefaultDisplay().getMetrics(dm);
        // 获得手机的宽度和高度像素单位为px//
        // 分辨率:1080*1920
        // density=3.0 scaledDensity=3.0 xdpi=428.625 ydpi=424.069
        String strPM = "手机屏幕分辨率为:" + dm.widthPixels + "* " + dm.heightPixels;
        load_pm_width=dm.widthPixels;
        load_pm_height= dm.heightPixels;
        Log.i("xxx", strPM);
    }

}

package com.mcz.temperarure_humidity_appproject.app.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.mcz.temperarure_humidity_appproject.MainActivity;
import com.mcz.temperarure_humidity_appproject.R;
import com.mcz.temperarure_humidity_appproject.app.ui.base.LoginBaseActivity;
import com.mcz.temperarure_humidity_appproject.app.utils.Config;
import com.mcz.temperarure_humidity_appproject.app.utils.DataManager;
import com.mcz.temperarure_humidity_appproject.app.utils.WaitDialogUtil;

import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mcz on 2018/1/3.
 */

public class LoginActivity extends LoginBaseActivity {


    @BindView(R.id.ed_service_address)
    EditText edServiceAddress;
    @BindView(R.id.iv_clean_address)
    ImageView ivCleanAddress;
    @BindView(R.id.imageView2)
    ImageView imageView2;
    @BindView(R.id.et_username)
    EditText etUsername;
    @BindView(R.id.iv_clean_username)
    ImageView ivCleanUsername;
    @BindView(R.id.imageView3)
    ImageView imageView3;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.clean_password)
    ImageView cleanPassword;
    @BindView(R.id.iv_show_pwd)
    ImageView ivShowPwd;
    @BindView(R.id.btn_login)
    Button btnLogin;

    @BindView(R.id.ed_service_port)
    EditText edt_port;
    @BindView(R.id.iv_clean_port)
    ImageView img_cleanport;
//    @BindView(R.id.btn_start_sy)
//    Button btn_sy;

    SharedPreferences sp;

    String user = "", pwd = "", address = "",port="";
//    private SmartHomeApplication mApplication;

    private WaitDialogUtil mProgressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        ButterKnife.bind(this);
        //设置沉浸式标题栏
        setStatusBar();
        init();
    }
    MediaPlayer mediaPlayer=null;
    private void bofang(){
//        为activity注册的默认 音频通道 。
        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
        AudioManager audioService = (AudioManager) this
                .getSystemService(Context.AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
        }
         mediaPlayer = new MediaPlayer();

        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//            @Override
//            public void onPrepared(MediaPlayer mp) {
//                mediaPlayer.start();
//            }
//        });
//        　注册事件。当播放完毕一次后，重新指向流文件的开头，以准备下次播放。
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mediaPlayer.start();
                mediaPlayer.setLooping(true);
            }
        });
        AssetFileDescriptor file =this.getResources().openRawResourceFd(
                R.raw.airpp);
        try{
            mediaPlayer.setDataSource(file.getFileDescriptor(),
                    file.getStartOffset(), file.getLength());
            file.close();
            mediaPlayer.setVolume(0.10f,200L);
            mediaPlayer.prepare();
        }catch (IOException ioe) {
            Log.w("eeeee", ioe);
            mediaPlayer = null;
        }
    }
        int pooo=0;
    private void init() {
//      电信
//        String usname = sp.getString("appId", "fRZoFQo6oPZZlHjFpDhvHUttLiEa");
//        String uspwd = sp.getString("userpwd", "YtFEJCiVM0UfQju75B51ZN0tIe8a");
        //华为
        String usname = sp.getString("appId", "_XIZlIii_vq4lMaEkhZ3VNQUhD0a");
        String uspwd = sp.getString("userpwd", "rrHlE1WkWywWatlvK6xYIha9vZsa");
//        String usaddress = sp.getString("seraddress", "180.101.147.89");
        String usaddress = sp.getString("seraddress", "139.159.133.59");
        String usport = sp.getString("post", "8743");
        if (!usaddress.equals("")) {
            edServiceAddress.setText(usaddress);
            edServiceAddress.setSelection(usaddress.length());
        }
        if (!usname.equals("")) {
            edt_port.setText(usport);
        }
        if (!usname.equals("")) {
            etUsername.setText(usname);
        }
        if (!uspwd.equals("")) {
            etPassword.setText(uspwd);
        }


    }

    @OnClick({R.id.iv_clean_address, R.id.iv_clean_username, R.id.clean_password, R.id.iv_show_pwd, R.id.iv_clean_port, R.id.btn_login})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_clean_address:
                edServiceAddress.setText("");
                break;
            case R.id.iv_clean_port:
                edt_port.setText("");
                break;
            case R.id.iv_clean_username:
                etUsername.setText("");
                break;
            case R.id.clean_password:
                etPassword.setText("");
                break;
            case R.id.iv_show_pwd:
                pwd_show_A_hide();
                break;
            case R.id.btn_login:
                //登录
                Login_into();
                break;
        }
    }

    /**
     * 密码显示与隐藏
     */
    private void pwd_show_A_hide() {
        if (etPassword.getInputType() != InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
            etPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            ivShowPwd.setImageResource(R.drawable.img_show_pwd);
        } else {
            etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            ivShowPwd.setImageResource(R.drawable.img_hide_pwd);
        }
        String pwds = etPassword.getText().toString();
        if (!TextUtils.isEmpty(pwds))
            etPassword.setSelection(pwds.length());
    }

    private void Login_into() {
        address = edServiceAddress.getText().toString().trim();
        port=edt_port.getText().toString().trim();
        user = etUsername.getText().toString().trim();
        pwd = etPassword.getText().toString().trim();
        Config.all_url="https://"+address+":"+port;
        if (TextUtils.isEmpty(address)) {
            Toast.makeText(LoginActivity.this, "请输入IP", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(port)){
            Toast.makeText(LoginActivity.this, "请输入端口号", Toast.LENGTH_SHORT).show();
        }
        else {
            if (TextUtils.isEmpty(user) || TextUtils.isEmpty(pwd)) {
                Toast.makeText(LoginActivity.this, "用户信息不能为空", Toast.LENGTH_SHORT).show();
            } else {
//                if (! sp.getString("token","").equals("")){
//                    Intent intent=new Intent(getApplication(), MainActivity.class);
//                    startActivity(intent);
//                }else{
                    Init_Demand_HTTPS();
//                }
            }
        }
    }

    /**
     *
     * 用户信息请求
     */
    private void Init_Demand_HTTPS() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                Message msg = new Message();
                Bundle data = new Bundle();
                try {
                    String url="https://"+address+":"+port+"/iocm/app/sec/v1.1.0/login";
//                    https://218.4.33.71:8743/iocm/app/sec/v1.1.0/login
                    String json= DataManager.Login_Request(LoginActivity.this,url,user,pwd);
                    data.putString("json", json);
                    Log.i("bbbb", "josn1" + json);
                } catch ( Exception e ) {
                    data.putString("errmsg", e.getMessage());
                }
                msg.setData(data);
                loginhandler.sendMessage(msg);
            }
        }).start();


    }

    private Handler loginhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String json = data.getString("json");
            Log.i("aaa", "josn1" + json);
//            {
//                "accessToken": "dc4d1368-2e43-4185-b22b-13714e22eb92",
//                    "tokenType": "bearer",
//                    "refreshToken": "a769565a-7d0a-4079-9fe3-1a97b84309a8",
//                    "expiresIn": 43200,
//                    "scope": "[read, write]"
//            }
            String token="";
            try {
                JSONObject jsono = new JSONObject(json);
                 token=jsono.getString("accessToken");
                sp.edit().putString("token",token).commit();
                sp.edit().putString("appId",etUsername.getText().toString().trim()).commit();
                sp.edit().putString("userpwd",etPassword.getText().toString().trim()).commit();
                sp.edit().putString("seraddress",edServiceAddress.getText().toString().trim()).commit();
                sp.edit().putString("post",edt_port.getText().toString().trim()).commit();

//                获取到的accessToken
            }catch (Exception e ){
                e.printStackTrace();
                Toast.makeText(LoginActivity.this, "请求失败,请检查参数设置！", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent=new Intent(getApplication(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    };



}

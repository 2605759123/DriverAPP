package com.mcz.temperarure_humidity_appproject;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gyf.barlibrary.ImmersionBar;
import com.mcz.temperarure_humidity_appproject.app.model.DataInfo;
import com.mcz.temperarure_humidity_appproject.app.model.PullrefreshListviewAdapter;
//import com.mcz.Temperarure_humidity_appproject.app.ui.activity.HistoryActivity;
import com.mcz.temperarure_humidity_appproject.app.ui.activity.InputManualActivity;
import com.mcz.temperarure_humidity_appproject.app.ui.activity.LoginActivity;
import com.mcz.temperarure_humidity_appproject.app.ui.zxing.zxing.new_CaptureActivity;
import com.mcz.temperarure_humidity_appproject.app.utils.Config;
import com.mcz.temperarure_humidity_appproject.app.utils.DataManager;
import com.mcz.temperarure_humidity_appproject.app.view.view.IPullToRefresh;
import com.mcz.temperarure_humidity_appproject.app.view.view.LoadingLayout;
import com.mcz.temperarure_humidity_appproject.app.view.view.PullToRefreshBase;
import com.mcz.temperarure_humidity_appproject.app.view.view.PullToRefreshFooter;
import com.mcz.temperarure_humidity_appproject.app.view.view.PullToRefreshHeader;
import com.mcz.temperarure_humidity_appproject.app.view.view.PullToRefreshListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MyActivity1 extends AppCompatActivity {

    //String datajson = "";
    //String token = "";
    //SharedPreferences sp;
    //private String login_appid;
    SharedPreferences sp;
    String user = "", pwd = "", address = "",port="";//ocean平台处
    EditText textusername;
    EditText textPassword;//数据库端
    TextView Register;
    private CheckBox rem_pw, auto_login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my1);
        Button button1=(Button)findViewById(R.id.mylogin);
        textusername=(EditText)findViewById(R.id.username);
        textPassword=(EditText)findViewById(R.id.password);
        Register=(TextView) findViewById(R.id.SignIn);
        rem_pw = (CheckBox) findViewById(R.id.cb_mima);
        auto_login = (CheckBox) findViewById(R.id.cb_auto);
        //Toast.makeText(MyActivity1.this,"警告：目前只允许存在一个设备，如果有多个，只能获取最后添加设备的数据",Toast.LENGTH_LONG).show();
        button1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                try {
                    LoginRequest();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        Register.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MyActivity1.this,MyRegisterActivity.class);
                startActivity(intent);
            }
        });
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        textusername.setText(sp.getString("account",""));
        init();
        //sp = PreferenceManager.getDefaultSharedPreferences(this);
        //init();

        //判断记住密码多选框的状态
        if(sp.getBoolean("ISCHECK", false))
        {
            //设置默认是记录密码状态
            rem_pw.setChecked(true);
            textusername.setText(sp.getString("USER_NAME",""));
            textPassword.setText(sp.getString("PASSWORD", ""));
            //判断自动登陆多选框状态
            if(sp.getBoolean("AUTO_ISCHECK", false))
            {
                //设置默认是自动登录状态
                auto_login.setChecked(true);
                //跳转界面
                try {
                    LoginRequest();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }

        //监听记住密码多选框按钮事件
        rem_pw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if (rem_pw.isChecked()) {
                    Log.i("xuanzhong","记住密码已选中");
                    sp.edit().putBoolean("ISCHECK", true).commit();

                }else {
                    Log.i("xuanzhong","记住密码没有选中");
                    sp.edit().putBoolean("ISCHECK", false).commit();

                }

            }
        });

        //监听自动登录多选框事件
        auto_login.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if (auto_login.isChecked()) {
                    Log.i("xuanzhong","自动登陆已选中");
                    sp.edit().putBoolean("AUTO_ISCHECK",true).commit();

                } else {
                    Log.i("xuanzhong","自动登陆没有选中");
                    sp.edit().putBoolean("AUTO_ISCHECK", false).commit();
                }
            }
        });

    }
   // private void init(){
    //    login_appid = sp.getString("appId",""); //账号
  //     token = sp.getString("token", "");  //密码
   // }









    private void init(){
        user = sp.getString("appId", "_XIZlIii_vq4lMaEkhZ3VNQUhD0a");//华为应用服务器登陆用户名
        pwd = sp.getString("userpwd", "rrHlE1WkWywWatlvK6xYIha9vZsa");//华为应用服务器登陆用户名
        address = sp.getString("seraddress", "139.159.133.59");//华为云登陆地址
        port = sp.getString("post", "8743");//华为云登陆端口
    }

    private void Login_into() {
        address=address.toString().trim();
        port=port.toString().trim();
        user=user.toString().trim();
        pwd=pwd.toString().trim();

        Config.all_url="https://"+address+":"+port;
        Init_Demand_HTTPS();
    }
    private void Init_Demand_HTTPS() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                Message msg = new Message();
                Bundle data = new Bundle();
                try {
                    String url="https://"+address+":"+port+"/iocm/app/sec/v1.1.0/login";
//                    https://218.4.33.71:8743/iocm/app/sec/v1.1.0/login
                    String json= DataManager.Login_Request(MyActivity1.this,url,user,pwd); //获取token
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
                sp.edit().putString("appId",user.trim()).commit();
                sp.edit().putString("userpwd",pwd.trim()).commit();
                sp.edit().putString("seraddress",address.trim()).commit();
                sp.edit().putString("post",port.trim()).commit();
                sp.edit().putString("account",textusername.getText().toString().trim()).commit();//用户名写入配置
                sp.edit().putString("LoginPassword",textPassword.getText().toString().trim()).commit();//密码写入配置
                //此处进行了修改
//                获取到的accessToken
            }catch (Exception e ){
                e.printStackTrace();
                Toast.makeText(MyActivity1.this, "请求失败,请检查参数设置！", Toast.LENGTH_SHORT).show();
                return;
            }
            //登录成功和记住密码框为选中状态才保存用户信息
            if(rem_pw.isChecked())
            {
                //记住用户名、密码、
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("USER_NAME", String.valueOf(textusername.getText()));
                editor.putString("PASSWORD", String.valueOf(textPassword.getText()));
                editor.commit();
            }
            Intent intent=new Intent(getApplication(), MyMainActivity.class);
            startActivity(intent);
            finish();
        }
    };



    //------------以下为数据库登陆验证的地方

    public void LoginRequest() throws IOException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i("poi2","123");
                HttpURLConnection connection=null;
                BufferedReader reader=null;
                String account=textusername.getText().toString();
                String password=textPassword.getText().toString();
                Log.i("1user",account);
                if (account.equals("")){
                    Log.i("poi3","123");
                    toast("请输入用户名");
                }else if (password.equals("")){
                    Log.i("poi4","123");
                    toast("请输入密码");
                }else{
                    Log.i("poi5","123");
                    try{
                        String url1 = "http://47.102.201.183:8080/test/LoginServlet?account="+account+"&password="+password;
                        //String tag = "Login";
                        URL url=new URL(url1);
                        connection=(HttpURLConnection)url.openConnection();
                        connection.setRequestMethod("GET");
                        connection.setConnectTimeout(8000);
                        connection.setReadTimeout(8000);
                        InputStream in=connection.getInputStream();
                        //下面对获取到的输入流进行读取
                        reader=new BufferedReader(new InputStreamReader(in));
                        StringBuilder response=new StringBuilder();
                        String line;
                        while ((line=reader.readLine())!=null){
                            response.append(line);
                        }
                        String result="";
                        result=LoginJSON(response.toString());
                        Log.i("result",result);
                        if (result.equals("")){
                            //数据库连接不上
                            errorLogin();
                        }else if (result.equals("success")){
                            //数据库验证登陆成功
                            Log.i("poi6","123");
                            Login_into();//开始登陆华为云ocean平台
                        }else if(result.equals("failed")){
                            //数据库验证账号或密码错误
                            noLogin();
                        }
                    }catch (Exception e){
                        errorLogin();
                        e.printStackTrace();
                    }finally {
                        if(reader!=null){
                            try {
                                reader.close();
                            }catch (IOException e){
                                e.printStackTrace();
                            }
                        }
                        if (connection!=null){
                            connection.disconnect();
                        }
                    }
                }

            }
        }).start();
    }
    private void toast(final String toast){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MyActivity1.this,toast,Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void errorLogin(){
        //服务器连接失败
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MyActivity1.this,"验证账号密码失败，请检查网络或联系我们",Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void noLogin(){
        //账号或密码错误
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MyActivity1.this,"账号或密码错误，请检查",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String LoginJSON(String jsondata){
        try{
            Log.i("jsondata",jsondata);
            JSONObject js=new JSONObject(jsondata);
            String jsonObject=js.getJSONObject("params").toString();
            Log.i("3232:",jsonObject);
            JSONObject object=new JSONObject(jsonObject);
            String ob=object.optString("Result");
            Log.i("ob",ob);
            return ob;

        }catch (Exception e){
            e.printStackTrace();
            return "";
        }
    }

}

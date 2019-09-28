package com.mcz.temperarure_humidity_appproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mcz.temperarure_humidity_appproject.app.ui.activity.InputManualActivity;
import com.mcz.temperarure_humidity_appproject.app.utils.Config;
import com.mcz.temperarure_humidity_appproject.app.utils.DataManager;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import butterknife.BindView;



public class MyPlusActivity extends AppCompatActivity {
    @BindView(R.id.plus_shuruname)
    EditText shuruname;//输入的账号
    @BindView(R.id.plus_shurucode)
    EditText shurucode;//输入的密码
    @BindView(R.id.plus_next)
    Button next;
    SharedPreferences sp;
    String Intention;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_plus);
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        next=(Button)findViewById(R.id.plus_next);
        shuruname=(EditText)findViewById(R.id.plus_shuruname);
        shurucode=(EditText)findViewById(R.id.plus_shurucode);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(next.getText()=="返回"){
                    finish();
                }else {
                    String name = shuruname.getText().toString().trim();//设备名称
                    String code_edt = shurucode.getText().toString().trim();//标识码
//                code="867967023179486";
                    if (name.equals("")){
                        Toast.makeText(MyPlusActivity.this, "请输入设备名", Toast.LENGTH_SHORT).show();
                    }else {
                        if (code_edt.equals("")) {
                            Toast.makeText(MyPlusActivity.this, "请输入设备标识码", Toast.LENGTH_SHORT).show();
                        } else {

                            Load_Data(name, code_edt);

                        }
                    }
                }

            }
        });
    }
    private void Load_Data(final String name, final String code_edt) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                Message msg = new Message();
                Bundle data = new Bundle();
                try {
                    String login_appid = sp.getString("appId", "");
                    String token = sp.getString("token", "");
//                    String dvid="1839632e-84e7-4a22-a781-ae3e856a8b37";
                    String url = Config.all_url + "/iocm/app/reg/v1.2.0/devices?appId=" + login_appid;
//                    https: //server:port/iocm/app/reg/v1.2.0/devices?appId={appId}
                    String json = DataManager.Register_DEVICEID(getApplicationContext(), url, login_appid, token,
                            code_edt);
                    data.putString("json", json);
                } catch ( Exception e ) {
//                    hideProgress();
                    data.putString("errmsg", e.getMessage());
                }
                msg.setData(data);
                Datahandler.sendMessage(msg);
            }
        }).start();
    }
    private Handler Datahandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String json = data.getString("json");
            String resultDVId = "";
            Log.i("aaa", "josn1" + json);
            try {
                JSONObject jsonObject = new JSONObject(json);
                resultDVId = jsonObject.optString("deviceId");

            } catch ( Exception e ) {
                Toast.makeText(MyPlusActivity.this, "请输入正确的MAC或SIM卡号或设备esn号", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
                return;
            }
            String name = shuruname.getText().toString().trim();
            //update_deviceinfo(resultDVId,name);
            update_deviceinfo(resultDVId,name);
            Intention=resultDVId;
            try {
                IntentionRequest();
                next.setText("返回");
                Log.i("mkkkk","123");
                Toast.makeText(MyPlusActivity.this,"注册成功",Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                toast("数据库：未知错误");
                e.printStackTrace();
            }


        }
    };
    private void update_deviceinfo(final String deviceID,final String name) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                Message msg = new Message();
                Bundle data = new Bundle();
                try {
                    String login_appid = sp.getString("appId", "");
                    String token = sp.getString("token", "");
                    String url = Config.all_url + "/iocm/app/dm/v1.1.0/devices/" + deviceID+"?appId="+login_appid;
//                    https://server:port/iocm/app/dm/v1.1.0/devices/{deviceId}?appId={appId}
                    String json = DataManager.UPDATE_DEVICEID(getApplicationContext(), url, login_appid, token,name);//添加设备信息
                    data.putString("json", json);
                } catch ( Exception e ) {
//                    hideProgress();
                    data.putString("errmsg", e.getMessage());
                }
                msg.setData(data);
//                Datahandler.sendMessage(msg);
            }
        }).start();
    }







    //------------以下为数据库登陆验证的地方

    public void IntentionRequest() throws IOException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i("poi2","123");
                HttpURLConnection connection=null;
                BufferedReader reader=null;
                String account=sp.getString("account","");
                String password=sp.getString("LoginPassword","");
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
                        String url1 = "http://47.102.201.183:8080/test/IntentionServlet?account="+account+"&password="+password+"&Intention="+Intention;
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
                        result=IntentionJSON(response.toString());
                        Log.i("result",result);
                        if (result.equals("")){
                            //数据库连接不上
                            toast("数据库连接失败，请联系我们");//一般不会这样
                        }else if (result.equals("成功")){
                            //成功写入
                            Log.i("poi6ok","123");
                        }else if(result.equals("失败")){
                            //数据库写入失败(这里最好加上把加上的设备删除)
                            toast("未知原因：数据库写入失败，请联系我们");
                        }
                    }catch (Exception e){
                        toast("数据库连接失败，请联系我们");
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
                Toast.makeText(MyPlusActivity.this,toast,Toast.LENGTH_SHORT).show();
            }
        });
    }


    private String IntentionJSON(String jsondata){
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

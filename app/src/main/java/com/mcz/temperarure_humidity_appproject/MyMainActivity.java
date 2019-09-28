package com.mcz.temperarure_humidity_appproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.mcz.temperarure_humidity_appproject.app.model.DataInfo;
import com.mcz.temperarure_humidity_appproject.app.model.PullrefreshListviewAdapter;
import com.mcz.temperarure_humidity_appproject.app.utils.Config;
import com.mcz.temperarure_humidity_appproject.app.utils.DataManager;
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
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import com.mcz.temperarure_humidity_appproject.app.view.view.PullToRefreshBase;

public class MyMainActivity extends AppCompatActivity {
    //String datajson = "";
    String token = "";
    SharedPreferences sp;
    private String login_appid;
    private List<DataInfo> mlist = null;
    RelativeLayout rela_nodata;
    private MyAdapter adapter;
    ListView listView;
    PullToRefreshListView mListView;//下拉刷新
    private View mNoMoreView;
    int returndata=0;//控制
    int returndata2=0;
    private Handler handler=null;
    TextView dataxinlv;//心率数据
    TextView datashuzhangya;//舒张压
    TextView datatiwen;//体温
    TextView makedataText;
    Button shuaxin;
    Button Delete;
    Button Plus;
    Button returndenglu;
    private List<String> gatewayId=new ArrayList<String>();
    private List<String> deviceId=new ArrayList<String>();
    private String json=null;
    private String tuatus="201";
    private String deletetuatus="204";
    String account;
    String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_main);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mIntent = new Intent(MyMainActivity.this, MyPlusActivity.class);
                startActivity(mIntent);
            }
        });*/

        dataxinlv=(TextView)findViewById(R.id.xinlv);
        datashuzhangya=(TextView)findViewById(R.id.xueya);
        datatiwen=(TextView)findViewById(R.id.tiwen);
        makedataText=(TextView)findViewById(R.id.makedatatext);
        shuaxin=(Button)findViewById(R.id.mymain_shuaxin);
        returndenglu=(Button)findViewById(R.id.person);
        //创建属于主线程的handler
        handler=new Handler();
        //StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
        //StrictMode.setThreadPolicy(policy);
        //setContentView(R.layout.main_activity_layout);
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        account=sp.getString("account","");
        password=sp.getString("LoginPassword","");

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    init();
                    try {
                        Thread.sleep(10000);//设置10s// 延迟，10s刷新一次
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
        shuaxin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                init();
            }
        });
        Plus=(Button)findViewById(R.id.img_choose);
        Plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(MyMainActivity.this, MyPlusActivity.class);
                startActivity(mIntent);
            }
        });
        returndenglu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sp.edit().putBoolean("AUTO_ISCHECK", false).commit();
                Intent intent=new Intent(MyMainActivity.this,MyActivity1.class);
                startActivity(intent);
                finish();
            }
        });
        //mListView=(PullToRefreshListView)findViewById(R.id.main_pull_refresh_lv) ;//下拉刷新

        //Log.i("oktem",mlist.get(0).getDevicetemperature());
    }
    //xia 构建Runnable对象，在runnable中更新界面
    Runnable   dataUi=new  Runnable(){
        @Override
        public void run() {
            //更新界面
            //dataxinlv.setText(mlist.get(0).getDevicetemperature());//更新心率
            //datashuzhangya.setText(mlist.get(0).getDevicehumidity());//更新舒张压
            //datatiwen.setText(mlist.get(0).getDevicetiwen());//更新体温
            //MyAdapter adapter=new MyAdapter(MyMainActivity.this,R.layout.mydata,mlist);
            //adapter.setlogin_appid(login_appid);
            //adapter.settoken(token);
            adapter = new MyAdapter( MyMainActivity.this);//创建下面的数据视图
            adapter.clearItem();
            Log.i("mlistsize", String.valueOf(mlist.size()));
            for (int i = 0; i < mlist.size(); i++) {
                Log.i("kloid",mlist.get(i).getDeviceId());
                if (account.equals("root@qq.com")){
                    adapter.addItem(mlist.get(i));
                }else if (jianchagateid(mlist.get(i).getDeviceId())){
                    Log.i("utrue", String.valueOf(i));
                    adapter.addItem(mlist.get(i));
                }
                //在跳转时传入item界面
            }
            adapter.setlogin_appid(login_appid);
            adapter.settoken(token);
            listView=(ListView)findViewById(R.id.list_view);
            listView.setAdapter(adapter);
            CharSequence dateText = DateFormat.format("yyyy-MM-dd kk:mm:ss", new Date());
            String TIME=dateText.toString().substring(11);
            makedataText.setText(TIME);
            //Toast.makeText(MyMainActivity.this,"获取成功",Toast.LENGTH_SHORT).show();
        }
    };
    Runnable nodataUi=new Runnable() {
        @Override
        public void run() {
            if (makedataText.getText().length()==8){
                makedataText.setText("收取中.");
            }else if(makedataText.getText().length()==4){
                makedataText.setText("收取中..");
            }else if(makedataText.getText().length()==5){
                makedataText.setText("收取中...");
            }else if(makedataText.getText().length()==6){
                makedataText.setText("收取中.");
            }
            //Toast.makeText(MyMainActivity.this,"正在获取数据",Toast.LENGTH_SHORT).show();
        }
    };
    Runnable truenodataUi=new Runnable() {
        @Override
        public void run() {
            //dataxinlv.setText("null");//更新心率
            //datashuzhangya.setText("null");//更新舒张压
            //datatiwen.setText("null");//更新体温
            Toast.makeText(MyMainActivity.this,"服务器上没有数据",Toast.LENGTH_SHORT).show();
        }
    };
    Runnable nulldataUi=new Runnable() {
        @Override
        public void run() {
            makedataText.setText("无设备");
            Toast.makeText(MyMainActivity.this,"没有发现设备，请先点击“PLUS”创建设备",Toast.LENGTH_SHORT).show();
        }
    };
    private void init(){
        login_appid = sp.getString("appId",""); //账号
        token = sp.getString("token", "");  //token
        Log.i("token", "-------=======----------" + token);
        int fnum1=0;
        int Onum1=10;
        //adapter.clearItem();
        //adapter = new PullrefreshListviewAdapter( this);
        //adapter.setlogin_appid(login_appid);
        //adapter.settoken(token);
        //mListView.setAdapter(adapter);
        List<DataInfo> result = null;
        result=ListviewADD_Data(fnum1,Onum1);
        //int truetep = mlist.size();
        //String step=String.valueOf(truetep);
        //Log.i("tep2:",step);
        try {
            returndata2=0;
            SearchRequest();
        } catch (IOException e) {
            e.printStackTrace();
        }


        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true)
                {
                    if(returndata2==1){
                        handler.post(nulldataUi);
                        break;
                    }
                    if (returndata==0) {
                        Log.i("zmlista:", "nodata");
                        handler.post(nodataUi);
                        try {
                            Thread.sleep(200);//设置延迟，
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    else if(returndata==1&&returndata2==2){
                        //Log.i("mlista:","eee");
                        Log.i("mlista:",mlist.get(0).getDevicetemperature());
                        handler.post(dataUi);
                        //tem.setText("mlist.get(0).getDevicetemperature()");
                        break;
                    }
                    else if(returndata==2){//没有发现设备
                        handler.post(nulldataUi);
                        break;
                    }
                    else if(returndata==3){
                        handler.post(truenodataUi);
                        break;
                    }
                }

            }
        }).start();

    }

    private List<DataInfo> ListviewADD_Data(final int Fnum, final int Onum) {
        returndata=0;
        //int i=0;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.i("pppppppppppppppppp",token);
                    String add_url = Config.all_url + "/iocm/app/dm/v1.3.0/devices?appId=" + login_appid
                            + "&pageNo=" + Fnum + "&pageSize=" + Onum;
                    Log.i("bbbbbbbbb",add_url);
                    Log.i("ccccccccccccc",login_appid);
                    Log.i("ddddddddddddddddddd",token);
                    String json = DataManager.Txt_REQUSET(MyMainActivity.this, add_url, login_appid, token);
                    Log.i("json1111:",json);
                    mlist = new ArrayList<DataInfo>();

                    JSONObject jo = new JSONObject(json);
                    JSONArray jsonArray = jo.getJSONArray("devices");
                    Log.i("jslength:", String.valueOf(jsonArray.length()));
                    for (int i = 0; i < jsonArray.length(); i++) {
                        //int j=1;
                        Log.i("eeeeeeeeeeeee1",String.valueOf(i));
                        DataInfo dataInfo = new DataInfo();
                        String deviceinfo = jsonArray.getJSONObject(i).getString("deviceInfo");
                        Log.i("1device2:",deviceinfo);
                        String servicesinfo = "null";
                        try{
                            servicesinfo = jsonArray.getJSONObject(i).getString("services");

                        }catch (Exception e){
                            //j++;
                            //if(j==jsonArray.length())
                            //    returndata=3;
                            Log.i("debug",Log.getStackTraceString(e));
                        }

                        //Log.i("1services2",servicesinfo);
                        if (!servicesinfo.equals("null")){
                            Log.i("eeeeeeeeeeeee2",String.valueOf(i));
                            JSONArray jsa=new JSONArray(servicesinfo);
                            for (int j = 0; j < jsa.length(); j++) {
                                String ser_data = jsa.getJSONObject(j).getString("data");
                                JSONObject jsonObject = new JSONObject(ser_data);

                                dataInfo.setDevicetemperature(jsonObject.optString("xinlv"));
                                dataInfo.setDevicehumidity(jsonObject.optString("xueya"));
                                dataInfo.setDevicetiwen(jsonObject.optString("tiwen"));
                                Log.i("edatainfo:",dataInfo.getDevicetiwen());
                            }
//
                            dataInfo.setDeviceId(jsonArray.getJSONObject(i).optString("deviceId"));
                            dataInfo.setGatewayId(jsonArray.getJSONObject(i).optString("gatewayId"));
                            dataInfo.setLasttime(jsonArray.getJSONObject(i).optString("lastModifiedTime"));
                            JSONObject object = new JSONObject(deviceinfo);
                            dataInfo.setDeviceName(object.optString("name"));
                            //  dataInfo.setDeviceType(object.optString("deviceType"));//model  deviceType
                            dataInfo.setDeviceStatus(object.optString("status"));
                            Log.i("dataInfo:",dataInfo.getDevicetemperature());
                            mlist.add(dataInfo);
                            Log.i("mlista:",mlist.get(0).getDevicetemperature());
                        }else{
                            Log.i("eeeeeeeeeeeee3",String.valueOf(i));
                            dataInfo.setDeviceId(jsonArray.getJSONObject(i).optString("deviceId"));
                            dataInfo.setGatewayId(jsonArray.getJSONObject(i).optString("gatewayId"));
                            dataInfo.setLasttime(jsonArray.getJSONObject(i).optString("lastModifiedTime"));
                            JSONObject object = new JSONObject(deviceinfo);
                            dataInfo.setDeviceName(object.optString("name"));
                            //  dataInfo.setDeviceType(object.optString("deviceType"));//model  deviceType
                            dataInfo.setDeviceStatus(object.optString("status"));

                            dataInfo.setDevicetemperature("null");
                            dataInfo.setDevicehumidity("null");
                            dataInfo.setDevicetiwen("null");
                            mlist.add(dataInfo);
                        }

                    }
                    if (jsonArray.length()==0){
                        Log.i("zheshi2:","2");
                        returndata=2;
                    }
                    else{
                        returndata=1;
                    }
                } catch ( Exception e ) {
                    Log.i("iiiiiiiiiiii",token);
                    Log.i("debug",Log.getStackTraceString(e));

                    e.printStackTrace();
                }


            }
        }).start();
        return mlist;
    }
    private void Delete_shebei(){
        new Thread(new Runnable() {
            @Override
            public void run() {

                String add_url = Config.all_url + "/iocm/app/dm/v1.1.0/devices/"+mlist.get(0).getDeviceId()+"?appId=" + login_appid;
                try {
                    json= DataManager.Delete_DEVICEID(MyMainActivity.this,add_url, login_appid, token);
                    if(json.equals(deletetuatus))
                    {


                        Looper.prepare();
                        mlist.remove(0);
                        Toast.makeText(MyMainActivity.this,"删除设备成功",Toast.LENGTH_SHORT).show();
                        Message message=new Message();
                        message.what=1;
                        Looper.loop();

                    }
                    else
                    {
                        Looper.prepare();
                        Toast.makeText(MyMainActivity.this,"删除设备失败",Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i("debug",Log.getStackTraceString(e));
                }
            }
        }).start();
    }

    //------------以下为数据库登陆验证的地方

    public void SearchRequest() throws IOException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i("poi2","123");
                HttpURLConnection connection=null;
                BufferedReader reader=null;

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
                        String url1 = "http://47.102.201.183:8080/test/SearchServlet?account="+account+"&password="+password;
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
                        //gatewayId.clear();
                        gatewayId = Arrays.asList(result.split(","));
                        if (gatewayId.size()==0){
                            if (!(account.equals("root@qq.com"))){
                                returndata2=1;//代表该用户下没有设备
                            }else{
                                returndata2=2;
                            }

                        }else if (gatewayId.get(0).equals("")){
                            Log.i("tsize=1","test");
                            if (!(account.equals("root@qq.com"))){
                                returndata2=1;//代表该用户下没有设备
                            }else{
                                returndata2=2;
                            }
                        }else{

                            for (int k=0;k<gatewayId.size();k++){
                                Log.i("results",gatewayId.get(k));
                            }
                            returndata2=2;//代表数据库gateID获取成功
                        }


                    }catch (Exception e){
                        toast("数据库连接失败，请联系我们");
                        Log.i("debugg",Log.getStackTraceString(e));
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
                Toast.makeText(MyMainActivity.this,toast,Toast.LENGTH_SHORT).show();
            }
        });
    }


    private String IntentionJSON(String jsondata){
        try{
            Log.i("jsondata",jsondata);
            JSONObject js=new JSONObject(jsondata);
            String jsonObject=js.getJSONObject("1").toString();
            Log.i("3232:",jsonObject);
            JSONObject object=new JSONObject(jsonObject);
            String ob=object.optString("Intention");
            Log.i("ob",ob);
            return ob;

        }catch (Exception e){
            e.printStackTrace();
            return "";
        }
    }


    private Boolean jianchagateid(String id){
        for (int i=0;i<gatewayId.size();i++){
            Log.i("gateideee",gatewayId.get(i));
            Log.i("gateideeeid",id);
            if (id.equals(gatewayId.get(i))){
                Log.i("gateideeetrue","test");
                return true;
            }
        }
        return false;
    }



}

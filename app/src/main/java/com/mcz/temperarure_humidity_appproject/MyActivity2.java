package com.mcz.temperarure_humidity_appproject;
import android.annotation.SuppressLint;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Context;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gyf.barlibrary.ImmersionBar;
import com.mcz.temperarure_humidity_appproject.app.HistoricaldataActivity;
import com.mcz.temperarure_humidity_appproject.app.model.DataInfo;
import com.mcz.temperarure_humidity_appproject.app.model.PullrefreshListviewAdapter;
//import com.mcz.Temperarure_humidity_appproject.app.ui.activity.HistoryActivity;
import com.mcz.temperarure_humidity_appproject.app.ui.activity.InputManualActivity;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import android.os.Handler;
import android.os.Bundle;


public class MyActivity2 extends AppCompatActivity {
    //String datajson = "";
    String token = "";
    SharedPreferences sp;
    private String login_appid;
    private List<DataInfo> mlist = null;
    RelativeLayout rela_nodata;
    private PullrefreshListviewAdapter adapter;
    PullToRefreshListView mListView;
    private View mNoMoreView;
    int returndata=0;//控制
    private Handler handler=null;
    TextView dataxinlv;//心率数据
    TextView datashuzhangya;//舒张压
    TextView datatiwen;//体温
    Button Delete;
    private List<String> gatewayId=new ArrayList<String>();
    private List<String> deviceId=new ArrayList<String>();
    private String json=null;
    private String tuatus="201";
    private String deletetuatus="204";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my2);
        dataxinlv=(TextView)findViewById(R.id.nowxinlv);
        datashuzhangya=(TextView)findViewById(R.id.nowshuzhangya);
        datatiwen=(TextView)findViewById(R.id.nowtiwen);
        //创建属于主线程的handler
        handler=new Handler();
        //StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
        //StrictMode.setThreadPolicy(policy);
        //setContentView(R.layout.main_activity_layout);
        Button button1=(Button)findViewById(R.id.RealTimeHeartRate);
        Button buttonhistory=(Button)findViewById(R.id.History);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                init();
            }
        });
        buttonhistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(MyActivity2.this, MyHistoryActivity.class);
                mIntent.putExtra("deviceId", mlist.get(0).getDeviceId());
                mIntent.putExtra("gatewayId", mlist.get(0).getDeviceId());
                startActivity(mIntent);
            }
        });
        Button Plus=(Button)findViewById(R.id.Plus);
        Plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(MyActivity2.this, MyPlusActivity.class);
                startActivity(mIntent);
            }
        });
        Delete=(Button)findViewById(R.id.Delete);
        Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Delete_shebei();
            }
        });
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        init();
        //Log.i("oktem",mlist.get(0).getDevicetemperature());
    }
    //xia 构建Runnable对象，在runnable中更新界面
    Runnable   dataUi=new  Runnable(){
        @Override
        public void run() {
            //更新界面
            dataxinlv.setText(mlist.get(0).getDevicetemperature());//更新心率
            datashuzhangya.setText(mlist.get(0).getDevicehumidity());//更新舒张压
            datatiwen.setText(mlist.get(0).getDevicetiwen());//更新体温
            Toast.makeText(MyActivity2.this,"心率："+mlist.get(0).getDevicetemperature()+"舒张压："+mlist.get(0).getDevicehumidity()+"体温："+mlist.get(0).getDevicetiwen(),Toast.LENGTH_SHORT).show();
        }
    };
    Runnable nodataUi=new Runnable() {
        @Override
        public void run() {
            Toast.makeText(MyActivity2.this,"正在获取数据",Toast.LENGTH_SHORT).show();
        }
    };
    Runnable truenodataUi=new Runnable() {
        @Override
        public void run() {
            dataxinlv.setText("null");//更新心率
            datashuzhangya.setText("null");//更新舒张压
            datatiwen.setText("null");//更新体温
            Toast.makeText(MyActivity2.this,"服务器上没有数据",Toast.LENGTH_SHORT).show();
        }
    };
    Runnable nulldataUi=new Runnable() {
        @Override
        public void run() {
            Toast.makeText(MyActivity2.this,"没有发现设备，请先点击“PLUS”创建设备",Toast.LENGTH_SHORT).show();
        }
    };
    private void init(){
        login_appid = sp.getString("appId",""); //账号
        token = sp.getString("token", "");  //token
        Log.i("token", "-------=======----------" + token);
        int fnum1=0;
        int Onum1=5;
        //adapter.clearItem();
        //adapter = new PullrefreshListviewAdapter( this);
        //adapter.setlogin_appid(login_appid);
        //adapter.settoken(token);
        //mListView.setAdapter(adapter);
        List<DataInfo> result = null;
        result=ListviewADD_Data(0,5);
        //int truetep = mlist.size();
        //String step=String.valueOf(truetep);
        //Log.i("tep2:",step);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true)
                {
                    if (returndata==0) {
                        Log.i("zmlista:", "nodata");
                        handler.post(nodataUi);
                        try {
                            Thread.sleep(2000);//设置延迟，不然不行
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    else if(returndata==1){
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

    private List<DataInfo> ListviewADD_Data(int Fnum, int Onum) {
        returndata=0;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.i("pppppppppppppppppp",token);
                    String add_url = Config.all_url + "/iocm/app/dm/v1.3.0/devices?appId=" + login_appid
                            + "&pageNo=" + "0" + "&pageSize=" + "5";
                    Log.i("bbbbbbbbb",add_url);
                    Log.i("ccccccccccccc",login_appid);
                    Log.i("ddddddddddddddddddd",token);
                    String json = DataManager.Txt_REQUSET(MyActivity2.this, add_url, login_appid, token);
                    Log.i("json1111:",json);
                    mlist = new ArrayList<DataInfo>();

                    JSONObject jo = new JSONObject(json);
                    JSONArray jsonArray = jo.getJSONArray("devices");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        DataInfo dataInfo = new DataInfo();
                        String deviceinfo = jsonArray.getJSONObject(i).getString("deviceInfo");
                        Log.i("1device2:",deviceinfo);
                        String servicesinfo = jsonArray.getJSONObject(i).getString("services");
                        Log.i("1services2",servicesinfo);
                        if (!servicesinfo.equals("null")){
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
                            returndata=1;
                        }else{
                            Log.i("eeeeeeeeeeeee",json);
                            dataInfo.setDeviceId(jsonArray.getJSONObject(i).optString("deviceId"));
                            dataInfo.setGatewayId(jsonArray.getJSONObject(i).optString("gatewayId"));
                            dataInfo.setLasttime(jsonArray.getJSONObject(i).optString("lastModifiedTime"));
                            JSONObject object = new JSONObject(deviceinfo);
                            dataInfo.setDeviceName(object.optString("name"));
                            //  dataInfo.setDeviceType(object.optString("deviceType"));//model  deviceType
                            dataInfo.setDeviceStatus(object.optString("status"));

                            dataInfo.setDevicetemperature("暂无数据");
                            dataInfo.setDevicehumidity("暂无数据");

                            mlist.add(dataInfo);
                            returndata=1;
                        }

                    }
                    if (jsonArray.length()==0){
                        Log.i("zheshi2:","2");
                        returndata=2;
                    }
                } catch ( Exception e ) {
                    Log.i("iiiiiiiiiiii",token);
                    Log.i("debug",Log.getStackTraceString(e));
                    returndata=3;
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
                    json= DataManager.Delete_DEVICEID(MyActivity2.this,add_url, login_appid, token);
                    if(json.equals(deletetuatus))
                    {


                        Looper.prepare();
                        mlist.remove(0);
                        Toast.makeText(MyActivity2.this,"删除设备成功",Toast.LENGTH_SHORT).show();
                        Message message=new Message();
                        message.what=1;
                        Looper.loop();

                    }
                    else
                    {
                        Looper.prepare();
                        Toast.makeText(MyActivity2.this,"删除设备失败",Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i("debug",Log.getStackTraceString(e));
                }
            }
        }).start();
    }

}

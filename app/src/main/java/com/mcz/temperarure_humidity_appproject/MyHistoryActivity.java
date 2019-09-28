package com.mcz.temperarure_humidity_appproject;

import android.content.Intent;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gyf.barlibrary.ImmersionBar;
import com.mcz.temperarure_humidity_appproject.R;
import com.mcz.temperarure_humidity_appproject.app.HistoricaldataActivity;
import com.mcz.temperarure_humidity_appproject.app.model.DataInfo;
import com.mcz.temperarure_humidity_appproject.app.model.PullrefreshListviewAdapter2;
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
import java.util.Timer;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyHistoryActivity extends AppCompatActivity {
    String deviceId="";
    String gatewayId="";
    String token2 = "";
    TextView makedataText2;
    Button myhistoryshuaxin;
    Button viewhistory;
    SharedPreferences sp2;
    int returndata=0;//控制
    private MyAdapter2 adapter2;
    private Handler handler=null;
    private List<DataInfo> mlist2 = null;
    private String time;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_history);
        makedataText2=(TextView)findViewById(R.id.makedatatext2);
        myhistoryshuaxin=(Button)findViewById(R.id.myhistory_shuaxin);
        //创建属于主线程的handler
        handler=new Handler();
        sp2 = PreferenceManager.getDefaultSharedPreferences(this);
        Intent intent=getIntent();
        deviceId=intent.getStringExtra("deviceId");
        gatewayId=intent.getStringExtra("gatewayId");
        viewhistory=(Button)findViewById(R.id.img_viewhistroy);
        viewhistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MyHistoryActivity.this,MyHistoryActivity2.class);
                intent.putExtra("deviceId", deviceId);
                intent.putExtra("gatewayId", gatewayId);
                startActivity(intent);
            }
        });
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
        myhistoryshuaxin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                init();
            }
        });
    }
    private void init(){
        token2 = sp2.getString("token", "");
        List<DataInfo> result = null;
        result=ListviewADD_Data();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true)
                {
                    if (returndata==0) {
                        Log.i("z2mlista:", "nodata");
                        handler.post(nodataUi);
                        try {
                            Thread.sleep(200);//设置延迟，不然不行
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    else {
                        //Log.i("mlista:","eee");
                        //Log.i("m2lista:",mlist2.get(0).getDevicetemperature());
                        handler.post(dataUi);
                        //tem.setText("mlist.get(0).getDevicetemperature()");
                        break;
                    }
                }

            }
        }).start();
    }

    //xia 构建Runnable对象，在runnable中更新界面
    Runnable   dataUi=new  Runnable(){
        @Override
        public void run() {
            if(mlist2.size()!=0){
                //更新界面
            /*Historydata.setText(null);
            for (i=0;i<mlist2.size();i++){
                Historydata.append(i+1+".");
                Historydata.append("心率："+mlist2.get(i).getDevicetemperature());//添加心率
                Historydata.append("血压："+mlist2.get(i).getDevicehumidity());//添加血压
                Historydata.append("体温："+mlist2.get(i).getDevicetiwen());//添加体温
                //CharSequence dateText = DateFormat.format("yyyy-MM-dd kk:mm:ss", new Date());
                //时间格式转换
                time=mlist2.get(i).getDevicetimestamp();
                int b = Integer.parseInt(time.substring(9,11));
                b=b+8;
                String TIME= time.substring(0,4)+"-"+time.substring(4,6)+"-"+time.substring(6,8)+"  "+b+":"+time.substring(11,13)+":"+time.substring(13,15);
                Historydata.append("操作时间："+TIME+"\n");
            }*/
                adapter2 = new MyAdapter2( MyHistoryActivity.this);//创建下面的数据视图
                adapter2.clearItem();
                Log.i("mlistsize", String.valueOf(mlist2.size()));
                for (int j = 0; j < mlist2.size(); j++) {
                    adapter2.addItem(mlist2.get(j));
                    //mlist集合是用于存放界面中的值  并在跳转时传入item界面
                }
                ListView listView=(ListView)findViewById(R.id.list_view2);
                listView.setAdapter(adapter2);
                CharSequence dateText = DateFormat.format("yyyy-MM-dd kk:mm:ss", new Date());
                String TIME=dateText.toString().substring(11);
                makedataText2.setText(TIME);
            }
            else {
                makedataText2.setText("没有记录");
            }


        }
    };
    Runnable nodataUi=new Runnable() {
        @Override
        public void run() {
            if (makedataText2.getText().length()==8){
                makedataText2.setText("收取中.");
            }else if(makedataText2.getText().length()==4){
                makedataText2.setText("收取中..");
            }else if(makedataText2.getText().length()==5){
                makedataText2.setText("收取中...");
            }else if(makedataText2.getText().length()==6){
                makedataText2.setText("收取中.");
            }
            //Toast.makeText(MyMainActivity.this,"正在获取数据",Toast.LENGTH_SHORT).show();
        }
    };

    private List<DataInfo> ListviewADD_Data() {
        returndata=0;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    String login_appid = sp2.getString("appId","");
                    ///////////////////////////************************2.3.2 查询单个设备信息***********************////////////////////////////
//            String add_url = Config.all_url + "/iocm/app/dm/v1.3.0/devices?appId=" + login_appid
//                    + "&pageNo=" + Fnum + "&pageSize=" + Onum;
                    ////////////////////////////////////////////************************查询设备历史数据*****************/////////////////////////////////////
                    String add_url = Config.all_url + "/iocm/app/data/v1.1.0/deviceDataHistory?deviceId="+deviceId+"&gatewayId="+gatewayId;
                    Log.i("zzza","123");
                    String json = DataManager.Txt_REQUSET(MyHistoryActivity.this, add_url, login_appid, token2);
                    Log.i("bbbbbbbbbbbbbbbbbbbbbbb", "josn1" + json);
                    mlist2 = new ArrayList<DataInfo>();

                    JSONObject jo = new JSONObject(json);
                    JSONArray jsonArray = jo.getJSONArray("deviceDataHistoryDTOs");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        DataInfo dataInfo = new DataInfo();
                        String timestamp=jsonArray.getJSONObject(i).getString("timestamp");
                        String timestamps=dataTextc(timestamp);
                        Log.i("timestamp",timestamp);
                        Log.i("timestamps",timestamps);
                        dataInfo.setDevicetimestamp(timestamps);
                        String ser_data = jsonArray.getJSONObject(i).getString("data");
                        //   Log.i("bbbbbbbbbbbbbbbbbbbbbb","timestamp            "+timestamp);
//                dataInfo.setDevicetimestamp(timestamp);
                        JSONObject jsonObject = new JSONObject(ser_data);
                        dataInfo.setDevicetemperature(jsonObject.optString("xinlv"));
                        dataInfo.setDevicehumidity(jsonObject.optString("xueya"));
                        dataInfo.setDevicetiwen(jsonObject.optString("tiwen"));
                        mlist2.add(dataInfo);
                    }
                    returndata=1;

                } catch ( Exception e ) {
                    e.printStackTrace();
                }
            }
        }).start();
        return mlist2;
    }
    private String dataTextc(String datatext){
        int year;
        int month;
        int day;
        int hour;
        int minute;
        int second;
        String years;
        String months;
        String days;
        String hours;
        String minutes;
        String seconds;
        year= Integer.parseInt(datatext.substring(0,4));
        month= Integer.parseInt(datatext.substring(4,6));
        day= Integer.parseInt(datatext.substring(6,8));
        hour= Integer.parseInt(datatext.substring(9,11));
        minute= Integer.parseInt(datatext.substring(11,13));
        second= Integer.parseInt(datatext.substring(13,15));
        second=second+30;
        if (second>=60){
            second=second-60;
            minute++;
        }
        if (second==0){
            seconds="00";
        }else if (second<10){
            seconds="0"+second;
        }else {
            seconds= String.valueOf(second);
        }
        minute=minute+4;
        if (minute>=60){
            minute=minute-60;
            hour++;
        }
        if (minute==0){
            minutes="00";
        }else if (minute<10) {
            minutes = "0" + minute;
        }else {
            minutes=String.valueOf(minute);
        }
        hour=hour+8;
        if (hour>=24){
            hour=hour-24;
            day++;
        }
        if (hour==0){
            hours="00";
        }else if (hour<10){
            hours="0"+hour;
        }else {
            hours=String.valueOf(hour);
        }
        if (day>31){
            day=day-31;
            month++;
        }else if (day>30 && (month==4 || month==6 ||month==9 ||month==11)){
            day=day-30;
            month++;
        }else if (day>28 && month==2){
            day=day-28;
            month++;
        }
        if (day<10){
            days="0"+day;
        }else {
            days=String.valueOf(day);
        }
        if (month>12){
            month=month-12;
            year++;
        }
        if (month<10){
            months="0"+month;
        }else {
            months= String.valueOf(month);
        }
        years=String.valueOf(year);
        String TIME=years+"-"+months+"-"+days+"  "+hours+":"+minutes+":"+seconds;
        return TIME;
    }


}

package com.mcz.temperarure_humidity_appproject;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mcz.temperarure_humidity_appproject.app.model.DataInfo;
import com.mcz.temperarure_humidity_appproject.app.utils.Config;
import com.mcz.temperarure_humidity_appproject.app.utils.DataManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;

public class MyHistoryActivity2 extends AppCompatActivity {

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


    private ChartView mChartView1;
    private ChartView mChartView2;
    private ChartView mChartView3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_history2);
        mChartView1 = (ChartView) findViewById(R.id.chart_view_1);
        mChartView2 = (ChartView) findViewById(R.id.chart_view_2);
        mChartView3 = (ChartView) findViewById(R.id.chart_view_3);
        makedataText2=(TextView)findViewById(R.id.makedatatext3) ;
        //创建属于主线程的handler
        handler=new Handler();
        sp2 = PreferenceManager.getDefaultSharedPreferences(this);
        Intent intent=getIntent();
        deviceId=intent.getStringExtra("deviceId");
        gatewayId=intent.getStringExtra("gatewayId");
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    init();
                    try {
                        Thread.sleep(1000);//设置10s// 延迟，10s刷新一次
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
        //init();
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
                        Log.i("z2mlista333:", "nodata");
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
            Log.i("mlist9", String.valueOf(mlist2.size()));
            if(mlist2.size()!=0){
                setData();
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
                    String json = DataManager.Txt_REQUSET(MyHistoryActivity2.this, add_url, login_appid, token2);
                    Log.i("bbbbbbbbbbbbbbbbbbbbbbb", "josn1" + json);
                    mlist2 = new ArrayList<DataInfo>();
                    JSONObject jo = new JSONObject(json);
                    JSONArray jsonArray = jo.getJSONArray("deviceDataHistoryDTOs");
                    Log.i("jsons", String.valueOf(jsonArray.length()));
                    for (int i = 0; i < jsonArray.length(); i++) {
                        DataInfo dataInfo = new DataInfo();
                        String timestamp=jsonArray.getJSONObject(i).getString("timestamp");
                        String timestamps=dataTextc(timestamp);
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

    private void setData() {
        String title1 = "最近心率";
        String title2="最近血压";
        String title3="最近体温";
        String[] xLabel1 = {"0", "1", "2", "3", "4", "5", "6"};
        String[] data1 = {"0", "1", "2", "3", "4", "5", "6"};
        String[] data2 = {"0", "1", "2", "3", "4", "5", "6"};
        String[] data3 = {"0", "1", "2", "3", "4", "5", "6"};
        if (mlist2.size()>=7){
            for (int i=0;i<7;i++){
                data1[i]=mlist2.get(7-i-1).getDevicetemperature();//xinlv
                String time2=mlist2.get(7-i-1).getDevicetimestamp();
                Log.i("time23:",time2);
                xLabel1[i]=time2.substring(12);
                data2[i]=mlist2.get(7-i-1).getDevicehumidity();//xueya
                data3[i]=mlist2.get(7-i-1).getDevicetiwen();//tiwen
            }
        }else{
            int j;
            int i;
            for (i = 0; i<mlist2.size(); i++){
                data1[i]=mlist2.get(mlist2.size()-i-1).getDevicetemperature();//xinlv
                String time2=mlist2.get(mlist2.size()-i-1).getDevicetimestamp();
                xLabel1[i]=time2.substring(12);
                data2[i]=mlist2.get(mlist2.size()-i-1).getDevicehumidity();//xueya
                data3[i]=mlist2.get(mlist2.size()-i-1).getDevicetiwen();
            }
            j=i;
            j--;
            for(;i<7;i++){
                data1[i]=mlist2.get(mlist2.size()-j-1).getDevicetemperature();//xinlv
                String time2=mlist2.get(mlist2.size()-j-1).getDevicetimestamp();
                xLabel1[i]=time2.substring(12);
                data2[i]=mlist2.get(mlist2.size()-j-1).getDevicehumidity();//xueya
                data3[i]=mlist2.get(mlist2.size()-j-1).getDevicetiwen();//tiwen
            }
        }
        mChartView1.setTitle(title1);
        mChartView1.setxLabel(xLabel1);
        mChartView1.setData(data1);
        mChartView1.fresh();
        mChartView2.setTitle(title2);
        mChartView2.setxLabel(xLabel1);
        mChartView2.setData(data2);
        mChartView2.fresh();
        mChartView3.setTitle(title3);
        mChartView3.setxLabel(xLabel1);
        mChartView3.setData(data3);
        mChartView3.fresh();
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

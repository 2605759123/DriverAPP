package com.mcz.temperarure_humidity_appproject.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.gyf.barlibrary.ImmersionBar;
import com.mcz.temperarure_humidity_appproject.R;
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

import butterknife.BindView;
import butterknife.ButterKnife;

public class HistoricaldataActivity extends AppCompatActivity {

    String deviceId="";
    String gatewayId="";
    String token2 = "";
    SharedPreferences sp2;
    @BindView(R.id.main_pull_refresh_his)
    PullToRefreshListView mListView2;

    @BindView(R.id.main_relative_main)
    RelativeLayout rela_nodata2;

    @BindView(R.id.img_histor)
    ImageView back;

    private View mNoMoreView2;

    private PullrefreshListviewAdapter2 adapter2;

    private List<DataInfo> mlist2 = null;

    boolean type_choose2 = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historicaldata);//開始運行activity_historicaldata界面
        Log.i("runhist","123");
        ButterKnife.bind(this);
        ImmersionBar.with(this)
                .statusBarColor(R.color.blues)
                .fitsSystemWindows(true)
                .init();
        sp2 = PreferenceManager.getDefaultSharedPreferences(this);
        init();
        Intent intent=getIntent();
        deviceId=intent.getStringExtra("deviceId");
        gatewayId=intent.getStringExtra("gatewayId");
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }



    int item_position = 0;

    private void hintKbTwo() {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm.isActive()&&getCurrentFocus()!=null){
            if (getCurrentFocus().getWindowToken()!=null) {
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }


    private void init() {


        token2 = sp2.getString("token", "");
        hintKbTwo();
        rela_nodata2.setVisibility(View.GONE);

        //搜索設備時候監聽圖片

        bofang();


        ///listview
        mNoMoreView2 = getLayoutInflater().inflate(R.layout.no_device_more_footer, null);
        // mListView.setOnItemClickListener(this);
        mListView2.setLoadingLayoutCreator(new PullToRefreshBase.LoadingLayoutCreator() {
            @Override
            public LoadingLayout create(Context context, boolean headerOrFooter,
                                        PullToRefreshBase.Orientation orientation) {
                if (headerOrFooter)
                    return new PullToRefreshHeader(context);
                else
                    return new PullToRefreshFooter(context, PullToRefreshFooter.Style.EMPTY_NO_MORE.EMPTY_NO_MORE);

            }
        });
//        Drawable drawable = getResources().getDrawable(R.color.transparent);
        mListView2.getRefreshableView().setSelector(getResources().getDrawable(R.color.transparent));
        //设置可上拉刷新和下拉刷新
        mListView2.setMode(PullToRefreshBase.Mode.BOTH);
        //异步加载数据
        mListView2.setOnRefreshListener(new IPullToRefresh.OnRefreshListener<ListView>() {

            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView,
                                  boolean headerOrFooter) {
                //填充数据
//                getList_AsyncTask(headerOrFooter);
                new LoadDataAsyncTask2(headerOrFooter, false).execute();
            }
        });

        adapter2 = new PullrefreshListviewAdapter2( this);
        mListView2.getRefreshableView().addFooterView(mNoMoreView2);
        mListView2.setAdapter(adapter2);
        mListView2.getRefreshableView().removeFooterView(mNoMoreView2);

    }
    /**
     * listview添加数据
     *

     * @return
     */
//查詢當個設備方法
    private List<DataInfo> ListviewADD_Data() {
        try {

            String login_appid = sp2.getString("appId","");
            ///////////////////////////************************2.3.2 查询单个设备信息***********************////////////////////////////
//            String add_url = Config.all_url + "/iocm/app/dm/v1.3.0/devices?appId=" + login_appid
//                    + "&pageNo=" + Fnum + "&pageSize=" + Onum;
            ////////////////////////////////////////////************************查询设备历史数据*****************/////////////////////////////////////
            String add_url = Config.all_url + "/iocm/app/data/v1.1.0/deviceDataHistory?deviceId="+deviceId+"&gatewayId="+gatewayId;
            String json = DataManager.Txt_REQUSET(HistoricaldataActivity.this, add_url, login_appid, token2);
            Log.i("bbbbbbbbbbbbbbbbbbbbbbb", "josn1" + json);
            mlist2 = new ArrayList<DataInfo>();

            JSONObject jo = new JSONObject(json);
            JSONArray jsonArray = jo.getJSONArray("deviceDataHistoryDTOs");

            for (int i = 0; i < jsonArray.length(); i++) {
                DataInfo dataInfo = new DataInfo();
                String timestamp=jsonArray.getJSONObject(i).getString("timestamp");
                dataInfo.setDevicetimestamp(timestamp);
                String ser_data = jsonArray.getJSONObject(i).getString("data");
                //   Log.i("bbbbbbbbbbbbbbbbbbbbbb","timestamp            "+timestamp);
//                dataInfo.setDevicetimestamp(timestamp);
                JSONObject jsonObject = new JSONObject(ser_data);
                dataInfo.setDevicehumidity(jsonObject.optString("Humidity"));
                dataInfo.setDevicetemperature(jsonObject.optString("Temperature"));
                mlist2.add(dataInfo);

            }

        } catch ( Exception e ) {
            e.printStackTrace();
        }

        return mlist2;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
    }




    /**
     * 异步加载任务
     */
    //下标标识
////////


    private class LoadDataAsyncTask2 extends AsyncTask<Void, Void, List<DataInfo>> {
        private boolean mHeaderOrFooter;
        private boolean is_Add;

        public LoadDataAsyncTask2(boolean headerOrFooter, boolean isadd) {
            mHeaderOrFooter = headerOrFooter;
            is_Add = isadd;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (mHeaderOrFooter) {
                mListView2.setVisibility(View.VISIBLE);
            }
            mListView2.getRefreshableView().removeFooterView(mNoMoreView2);
        }

        @Override
        protected List<DataInfo> doInBackground(Void... params) {
            if (HistoricaldataActivity.this.isFinishing()) {
                return null;
            }
            List<DataInfo> result = null;

            try {
                //首先判断是否查询所有还是单个
                if (!is_Add) {
                    if (mHeaderOrFooter && type_choose2) {
                        //下拉刷新走的方法
                        if (type_choose2 == false) {
                            adapter2.clearItem();
                            mlist2 = new ArrayList<DataInfo>();
                        }
                        type_choose2 = true;

                        result = ListviewADD_Data();

//                    begin=7;
                    } else {
//                    //上拉加载走的方法
                        if (type_choose2 == false) {
                            adapter2.clearItem();
                            mlist2 = new ArrayList<DataInfo>();
                            result = ListviewADD_Data();
                            type_choose2 = true;
                        } else {
//                            adapter.clearItem();
                            result = ListviewADD_Data();
                            type_choose2 = true;
                        }
                    }
                } else {
                    //更据ID查询
                    //  result = Listview_Inputmanual(datajson);
                }


                return result;
            } catch ( Exception e ) {
                e.printStackTrace();
                return null;
            }
        }

        /**
         * 完成时的方法
         */
        @Override
        protected void onPostExecute(List<DataInfo> result) {
            super.onPostExecute(result);
            mListView2.onRefreshComplete();
            if (HistoricaldataActivity.this.isFinishing()) {
                return;
            }
            if (result != null) {
                if (mHeaderOrFooter) {
                    CharSequence dateText = DateFormat.format("yyyy-MM-dd kk:mm:ss", new Date());
                    for (LoadingLayout layout : mListView2.getLoadingLayoutProxy(true, false).getLayouts()) {
                        ((PullToRefreshHeader) layout).setLastRefreshTime(":" + dateText);
                    }
                    adapter2.clearItem();
                    mlist2 = new ArrayList<DataInfo>();
                }
                if (adapter2.getCount() == 0 && result.size() == 0) {
                    mListView2.setVisibility(View.GONE);
                    mListView2.getRefreshableView().removeFooterView(mNoMoreView2);
                    rela_nodata2.setVisibility(View.VISIBLE);
                } else if (result.size()>5) {
                    //在这里判断数据的多少确定下一步能否上拉加载
//                    if (result.size()<5){
//                        mListView.getRefreshableView().removeFooterView(mNoMoreView);
//                    }else{
                    if (result.size()%5==0){
                    }else{
                        mListView2.setFooterRefreshEnabled(false);
                        mListView2.getRefreshableView().addFooterView(mNoMoreView2);
                        rela_nodata2.setVisibility(View.GONE);
                        mListView2.setVisibility(View.VISIBLE);
                    }

//                    }

                } else if (mHeaderOrFooter) {

                    if (result.size()>0){
                        rela_nodata2.setVisibility(View.GONE);
                        mListView2.setVisibility(View.VISIBLE);
                    }
                    mListView2.setFooterRefreshEnabled(true);
                    mListView2.getRefreshableView().removeFooterView(mNoMoreView2);
                }

                addlistdata(result);
//
                adapter2.notifyDataSetChanged();//刷新完成
//                }
            }else{
                rela_nodata2.setVisibility(View.VISIBLE);
                Toast.makeText(HistoricaldataActivity.this, "暂无设备信息", Toast.LENGTH_SHORT).show();
            }

//            new  Thread  (new Runnable() {
//                @Override
//                public void run() {
//                    new LoadDataAsyncTask2(true, false).execute();//查询所有
//                }
//            }) .start();
        }
    }
    /**
     * 数据循环添加
     *
     * @param result
     */
    private void addlistdata(List<DataInfo> result) {
        int count = result.size();
        if(result.size()>0){
            sp2.edit().putBoolean("isplayer",true).commit();
            try {
                mediaPlayer.stop();
                mediaPlayer.prepare();
                mediaPlayer.seekTo(0);
            } catch(IOException e) {
                e.printStackTrace();
            }

        }

        adapter2.clearItem();
        for (int i = 0; i < count; i++) {
            adapter2.addItem(result.get(i));
            mlist2.add(result.get(i));
            //mlist集合是用于存放界面中的值  并在跳转时传入item界面
        }
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



    /**
     * 在进入界面的时候自动加载一遍
     * 第一次有数据显示
     */
    private void refreshButtonClicked() {
        mListView2.setVisibility(View.VISIBLE);
        mListView2.setMode(IPullToRefresh.Mode.BOTH);
        mListView2.setRefreshing();
    }
    //MyThread线程任务类


    @Override
    protected void onResume() {
        super.onResume();
        //实列话MyThread类

        if ((adapter2 != null && adapter2.getCount() == 0)) {
            refreshButtonClicked();
        }
        hintKbTwo();
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        refreshButtonClicked();
        hintKbTwo();
    }

}

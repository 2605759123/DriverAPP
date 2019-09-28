package com.mcz.temperarure_humidity_appproject;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.SwipeLayout;
import com.mcz.temperarure_humidity_appproject.app.HistoricaldataActivity;
import com.mcz.temperarure_humidity_appproject.app.model.DataInfo;
import com.mcz.temperarure_humidity_appproject.app.model.PullrefreshListviewAdapter;
import com.mcz.temperarure_humidity_appproject.app.utils.Config;
import com.mcz.temperarure_humidity_appproject.app.utils.DataManager;

import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends BaseAdapter {
    private int resourceId;
    private Context mContext;
    private List<DataInfo> mlist=null;
    private String login_appid = "";
    private String token = "";
    private List<String> gatewayId=new ArrayList<String>();
    private List<String> deviceId=new ArrayList<String>();
    private String tuatus="201";
    private String deletetuatus="204";
    private String json=null;
    private ViewHolder vh;


    public MyAdapter(Context context) {
        Log.i("mk3","123");
        this.mContext=context;
        mlist=new ArrayList<DataInfo>();
    }

    public void settoken(String st){this.token=st;}
    public void setlogin_appid(String sl){this.login_appid=sl;}
    public void clearItem() {
        mlist.clear();
    }
    public void addItem(DataInfo item) {
        mlist.add(item);
    }

    @Override
    public int getCount() {
        Log.i("wmlist", String.valueOf(mlist.size()));
        return mlist.size();
    }

    @Override
    public DataInfo getItem(int position) {
        DataInfo item = null;
        if (position >= 0 && getCount() > position) {
            item = mlist.get(position);
        }
        return item;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Log.i("mk2","123");
        if (convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.mydata,parent,false);
            Log.i("mk1","123");
            vh = new ViewHolder();
            vh.tv_name = (TextView) convertView.findViewById(R.id.name_data);
            vh.tv_stutas = (TextView) convertView.findViewById(R.id.status_data);
            vh.temperature = (TextView) convertView.findViewById(R.id.xinlv);//心率
            vh.tv_Devicehumidity = (TextView) convertView.findViewById(R.id.xueya);//血压
            vh.tiwen=(TextView)convertView.findViewById(R.id.tiwen);//体温
            vh.swipeLayout=(SwipeLayout)convertView.findViewById(R.id.swipe);
            convertView.setTag(vh);
        }else{
            Log.i("mk1","321");
            vh = (ViewHolder) convertView.getTag();
        }
        DataInfo info = getItem(position);
        vh.tv_name.setText(info.getDeviceName());
        if (info.getDeviceStatus().contains("OFFLINE")){
            vh.tv_stutas.setText("离线");
        }else {
            vh.tv_stutas.setText("在线");
        }
        deviceId.add(info.getDeviceId());
        gatewayId.add(info.getDeviceId());
        vh.swipeLayout.setClickToClose(true);
        vh.swipeLayout.close(true);
//        swipeLayout.open();
        vh.swipeLayout.setOnDoubleClickListener(new SwipeLayout.DoubleClickListener() {
            @Override
            public void onDoubleClick(SwipeLayout layout, boolean surface) {
                Intent mIntent = new Intent(mContext, MyHistoryActivity.class);
//                String deviceId = mlist.get(position).getDeviceId();
//                String gatewayId = mlist.get(position).getGatewayId();
                mIntent.putExtra("deviceId", deviceId.get(position));
                mIntent.putExtra("gatewayId", gatewayId.get(position));
                mContext.startActivity(mIntent);

            }
        });
        vh.temperature.setText(info.getDevicetemperature());//设置数据xinlv
        vh.tv_Devicehumidity.setText(info.getDevicehumidity());//设置数据xueya
        vh.tiwen.setText(info.getDevicetiwen());//设置数据体温
        ImageView trash=(ImageView)convertView.findViewById(R.id.trash1);
        final View finalConvertView = convertView;
        trash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        String add_url = Config.all_url + "/iocm/app/dm/v1.1.0/devices/"+deviceId.get(position)+"?appId=" + login_appid;
                        try {
                            json= DataManager.Delete_DEVICEID(mContext,add_url, login_appid, token);
                            if(json.equals(deletetuatus))
                            {


                                Looper.prepare();
                                mlist.remove(position);
                                Toast.makeText(finalConvertView.getContext(),"删除设备成功",Toast.LENGTH_SHORT).show();
                                deviceId.remove(position);
                                Message message=new Message();
                                message.what=1;
                                handler.sendMessage(message);
                                Looper.loop();

                            }
                            else
                            {
                                Looper.prepare();
                                Toast.makeText(finalConvertView.getContext(),"删除设备失败",Toast.LENGTH_SHORT).show();
                                Looper.loop();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });

        return  convertView;
        //return super.getView(position, convertView, parent);
    }
    private Handler handler=new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case 1:notifyDataSetChanged();
                    vh.swipeLayout.close(true);

                    Log.i("1","1");
                    break;
                default:break;
            }
        }

    };
    class ViewHolder{
        TextView tv_name;
        TextView tv_stutas;
        TextView tv_Devicehumidity,temperature,tiwen;
        SwipeLayout swipeLayout;

    }

}


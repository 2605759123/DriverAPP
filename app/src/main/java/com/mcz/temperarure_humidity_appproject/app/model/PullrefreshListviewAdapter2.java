package com.mcz.temperarure_humidity_appproject.app.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mcz.temperarure_humidity_appproject.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pc on 2018/5/12.
 */

public class PullrefreshListviewAdapter2 extends BaseAdapter{
    private Context mContext2;
    private List<DataInfo> mlist2=null;
    private String time;
    public  PullrefreshListviewAdapter2( Context context){
        this.mContext2=context;
        mlist2=new ArrayList<DataInfo>();

    }
    public void clearItem() {
        mlist2.clear();
    }
    public void addItem(DataInfo item) {
        mlist2.add(item);
    }
    @Override
    public int getCount() {
        return mlist2.size();
    }

    @Override
    public DataInfo getItem(int position) {
        DataInfo item = null;
        if (position >= 0 && getCount() > position) {
            item = mlist2.get(position);
        }
        return item;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final PullrefreshListviewAdapter2.ViewHolder vh;
        if (convertView == null){
            convertView = LayoutInflater.from(mContext2).inflate(R.layout.hisitem,parent,false);
            vh = new PullrefreshListviewAdapter2.ViewHolder();
            vh.temperature = (TextView) convertView.findViewById(R.id.temperature);
             vh.timestamp=(TextView) convertView.findViewById(R.id.timestamp);
            vh.humidity=(TextView) convertView.findViewById(R.id.humidity);
            convertView.setTag(vh);
        }else{
            vh = (PullrefreshListviewAdapter2.ViewHolder) convertView.getTag();
        }
        DataInfo info = getItem(position);
        //时间格式转换
        time=info.getDevicetimestamp();

       ;
        String TIME= time.substring(0,4)+"-"+time.substring(4,6)+"-"+time.substring(6,8)+"  "+time.substring(9,11)+":"+time.substring(11,13)+":"+time.substring(13,15);
        /////////////////////////////////////////////////////////////////////////////////////////////////////
        vh.humidity.setText(info.getDevicehumidity());
        vh.temperature.setText(info.getDevicetemperature());
        vh.timestamp.setText(TIME);
        //////////////////////////////////////////////////////////////////////////////////////////////////////
        return convertView;
    }

    class ViewHolder{

        TextView temperature;
         TextView timestamp;
        TextView humidity;
    }
}

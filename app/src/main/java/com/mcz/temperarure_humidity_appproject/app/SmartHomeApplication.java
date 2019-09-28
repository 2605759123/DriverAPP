package com.mcz.temperarure_humidity_appproject.app;

//import com.huawei.iot.smarthome.api.Device;
//import com.huawei.iot.smarthome.api.DeviceApi;
//import com.huawei.iot.smarthome.api.Event;
//import com.huawei.iot.smarthome.api.EventApi;
//import com.huawei.iot.smarthome.api.LoginApi;
//import com.huawei.iot.smarthome.api.Rule;
//import com.huawei.iot.smarthome.api.RuleApi;
//import com.huawei.iot.smarthome.api.SysApi;
//import com.huawei.iot.smarthome.api.UserApi;
//import com.huawei.smarthome.service.base.frame.BaseApplication;
//
//import java.util.List;
//
///**
// * Created by mcz on 2018/1/3.
// */
//
//public class SmartHomeApplication extends BaseApplication {
//
//    private static SmartHomeApplication smartHomeApplication;
//    private List<Device> mDeviceList;
//    private List<Event> mEventList;
//    private List<Rule> mRuleList;
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//        Context mContext=getApplicationContext();
//        smartHomeApplication=this;
////        初始化SDK
//        SysApi.init(mContext);
//        LoginApi.init(mContext);
//        UserApi.init(mContext);
//        EventApi.init(mContext);
//        DeviceApi.init(mContext);
//        RuleApi.init(mContext);
//
//    }
//    public static SmartHomeApplication getInstance(){
//        if (smartHomeApplication == null) {
//            smartHomeApplication = new SmartHomeApplication();
//        }
//        return smartHomeApplication;
//    }
//
//    public void setDeviceList(List<Device> deviceList) {
//        mDeviceList = deviceList;
//    }
//
//    public List<Device> getDeviceList() {
//        return mDeviceList;
//    }
//    public void setRuleList(List<Rule> RuleList) {
//        mRuleList = RuleList;
//    }
//
//    public List<Rule> getRuleList() {
//        return mRuleList;
//    }
//
//    public void setEventList(List<Event> eventList) {
//        mEventList = eventList;
//    }
//
//    public List<Event> getEventList() {
//        return mEventList;
//    }
//
//    @Override
//    public void onRegisterModuleApp() {
//
//    }
//
//    @Override
//    public void onTerminate() {
//        super.onTerminate();
//        SysApi.destroy();
//        UserApi.destroy();
//        EventApi.destroy();
//        DeviceApi.destroy();
//    }
//}

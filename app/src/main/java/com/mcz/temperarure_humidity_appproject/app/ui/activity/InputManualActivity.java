package com.mcz.temperarure_humidity_appproject.app.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gyf.barlibrary.ImmersionBar;
import com.mcz.temperarure_humidity_appproject.MainActivity;
import com.mcz.temperarure_humidity_appproject.R;
import com.mcz.temperarure_humidity_appproject.app.utils.Config;
import com.mcz.temperarure_humidity_appproject.app.utils.DataManager;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mcz on 2018/1/10.
 */

public class InputManualActivity extends AppCompatActivity {


    @BindView(R.id.img_manualcancle)
    ImageView imgManualcancle;
    @BindView(R.id.txt_input_result)
    TextView txt_result;//设备id
    @BindView(R.id.edt_manual_input_name)
    EditText edtManualInput_name;//设备名称
    @BindView(R.id.edt_manual_input)
    EditText edtManualInput;//标识码
    @BindView(R.id.btn_manual_input)
    Button btnManualInput;
    SharedPreferences sp;
    @BindView(R.id.txt_ceshiresult)
    TextView txt;
    @BindView(R.id.lin_result)
    LinearLayout lin_res;

    @BindView(R.id.edt_phone)
    EditText edtPhone;
    @BindView(R.id.lin_show_input_phone)
    LinearLayout linShowInputPhone;
    @BindView(R.id.lin_intoid_news)
    LinearLayout lin_intoID;
    @BindView(R.id.tx_bar)
    TextView tx_bar;
    private int  position_phone=0;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manual_layout);
        ButterKnife.bind(this);
        ImmersionBar.with(this)
                .statusBarColor(R.color.blues)
                .fitsSystemWindows(true)
                .init();
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        init();
    }

    private void init() {
        tx_bar.setText("手动输入");
        hintKbTwo();
//        Intent intent=getIntent();
//        code=intent.getStringExtra("json");
//        edtManualInput.setText(code);
//        if (!TextUtils.isEmpty(code))
//            edtManualInput.setSelection(code.length());
        txt_result.setTextIsSelectable(true);
        imgManualcancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent=new Intent(InputManualActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });
        lin_res.setVisibility(View.GONE);
        lin_intoID.setVisibility(View.GONE);
        btnManualInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnManualInput.getText().toString().trim().equals("确定")) {
                    if (edtPhone.getText().length()!=0){
                        if (edtPhone.getText().toString().trim().matches(Config.phoneFormat)){
                            PUSH_Data(txt_result.getText().toString().trim(),edtPhone.getText().toString().trim());
                        }else{
                            Toast.makeText(InputManualActivity.this, "请输入正确的手机格式", Toast.LENGTH_SHORT).show();
                        }

                    }else{
                        Intent intent = new Intent(InputManualActivity.this, MainActivity.class);
//                        intent.putExtra("json", "123");
////                      setResult(REQUEST_CODE, intent);
                        startActivity(intent);
                        finish();
                    }

                } else {
                    String name = edtManualInput_name.getText().toString().trim();//设备名称
                    String code_edt = edtManualInput.getText().toString().trim();//标识码
//                code="867967023179486";
                    if (name.equals("")){
                        Toast.makeText(InputManualActivity.this, "请输入设备名", Toast.LENGTH_SHORT).show();
                    }else {
                        if (code_edt.equals("")) {
                            Toast.makeText(InputManualActivity.this, "请输入设备标识码", Toast.LENGTH_SHORT).show();
                        } else {

                            Load_Data(name, code_edt);

                        }
                    }
                }
            }
        });
//        remessageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                position_phone++;
//                if (position_phone==1){
//                    remessageView.setBackgroundResource(R.drawable.setting_switch_on);
//                    linShowInputPhone.setVisibility(View.VISIBLE);
//                }else if (position_phone==2){
//                    remessageView.setBackgroundResource(R.drawable.setting_switch_off);
//                    linShowInputPhone.setVisibility(View.GONE);
//                    hintKbTwo();
//                    position_phone=0;
//                }
//            }
//        });
//        edtPhone
    }
    /**
     * 关闭软键盘
     */
    private void hintKbTwo() {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm.isActive()&&getCurrentFocus()!=null){
            if (getCurrentFocus().getWindowToken()!=null) {
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
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
                Toast.makeText(InputManualActivity.this, "请输入正确的MAC或SIM卡号或设备esn号", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
                return;
            }
            String name = edtManualInput_name.getText().toString().trim();
            update_deviceinfo(resultDVId,name);

            lin_res.setVisibility(View.VISIBLE);
            lin_intoID.setVisibility(View.VISIBLE);
            txt_result.setText(resultDVId);
            btnManualInput.setText("确定");
            hintKbTwo();
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
                    String json = DataManager.UPDATE_DEVICEID(getApplicationContext(), url, login_appid, token,name);
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

    //tuisong
    private void PUSH_Data(final String name, final String code_edt) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                Message msg = new Message();
                Bundle data = new Bundle();
                try {
                        String url=Config.push_url+"rege/"+name+"/"+code_edt;
                    String json = DataManager.Phone_http(getApplicationContext(), url);
                    data.putString("json", json);
                } catch ( Exception e ) {
//                    hideProgress();
                    data.putString("errmsg", e.getMessage());
                }
                msg.setData(data);
                phone_handler.sendMessage(msg);
            }
        }).start();
    }


    private Handler phone_handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String json = data.getString("json");
            if (json.equals("create successful")){
//                Intent intent = new Intent(InputManualActivity.this, MainActivity.class);
//                intent.putExtra("json", "123");
//                setResult(REQUEST_CODE, intent);
//                startActivity(intent);
                finish();
                Log.i("phone===",json);
            }else{
                Toast.makeText(InputManualActivity.this, "请检查网络连接", Toast.LENGTH_SHORT).show();
            }
            hintKbTwo();
        }
    };

}

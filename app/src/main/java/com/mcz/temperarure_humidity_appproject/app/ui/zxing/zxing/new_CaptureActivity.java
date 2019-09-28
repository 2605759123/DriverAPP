package com.mcz.temperarure_humidity_appproject.app.ui.zxing.zxing;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.zxing.Result;
import com.gyf.barlibrary.ImmersionBar;
import com.mcz.temperarure_humidity_appproject.R;
import com.mcz.temperarure_humidity_appproject.app.ui.activity.InputCodeActivity;
import com.yzq.zxinglibrary.android.CaptureActivity;

/**
 * Created by pc on 2018/5/17.
 */

public class new_CaptureActivity extends CaptureActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImmersionBar.with(this)
                .statusBarColor(R.color.common_text)
                .fitsSystemWindows(true)
                .init();
    }

    @Override
    public void handleDecode(Result rawResult) {
        super.handleDecode(rawResult);
        if (rawResult.getText() == null) {
            Log.i(" handleDecode", "resultString is null");
            Toast.makeText(new_CaptureActivity.this, "Scan failed!", Toast.LENGTH_SHORT).show();
        } else {
//            if (resultString.contains("http://") || resultString.contains("https://")){
            Intent intent = new Intent(getApplication(), InputCodeActivity.class);
            intent.putExtra("json", rawResult.getText());
//                setResult(REQUEST_CODE,intent);
            startActivity(intent);
          //  finish();
        }
    }
}

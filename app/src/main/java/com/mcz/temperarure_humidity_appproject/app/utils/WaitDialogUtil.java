package com.mcz.temperarure_humidity_appproject.app.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mcz.temperarure_humidity_appproject.R;

/**
 * Created by mcz on 2018/1/3.
 */

public class WaitDialogUtil extends Dialog {
    /**
     * 等待框内容
     */
    private TextView mTxtMessage;
    /**
     * 等待框进度条
     */
    private ProgressBar mProgressBar;
    private String mTag;

    /**
     * 构造函数
     *
     * @param context
     *            上下文
     */
    public WaitDialogUtil(Context context) {
        super(context);
        initUI();
    }

    /**
     * 初始化UI
     */
    private void initUI() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.progress_with_layout);
        mProgressBar = (ProgressBar) this.findViewById(R.id.progressbar);
        mTxtMessage = (TextView) this.findViewById(R.id.txt_message);
        mTxtMessage.setText(getContext().getResources().getString(R.string.base_loading));
        this.setCanceledOnTouchOutside(false);
    }

    /**
     * 设置消息
     *
     * @param resId
     *            消息id
     */
    public void setMessage(int resId) {
        if (mTxtMessage != null) {
            mTxtMessage.setText(resId);
        }
    }

    /**
     * 设置消息
     *
     * @param message
     *            消息
     */
    public void setMessage(String message) {
        if (mTxtMessage != null) {
            mTxtMessage.setText(message);
        }
    }

    public void setMessageVisibility(int visibility) {
        if (mTxtMessage != null) {
            mTxtMessage.setVisibility(visibility);
        }
    }

    @Override
    public void show() {
        try {
            super.show();
        } catch (Exception e) {
        }
    }

    @Override
    public void dismiss() {
        try {
            super.dismiss();
        } catch (Exception e) {
        }
    }

   // public String getTag() {
      //  return mTag;
   // }

  //  public void setTag(String tag) {
      //  mTag = tag;
   // }
}

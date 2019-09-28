package com.mcz.temperarure_humidity_appproject.app.view.view;


import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mcz.temperarure_humidity_appproject.R;


/**
 *
 *
 * 自定义listview
 * 上拉加载更多
 * @author mcz
 *
 */
public class PullToRefreshFooter extends LoadingLayout {

    public enum Style {
        NORMAL, MORE, EMPTY_NO_MORE
    }

    private LinearLayout mLoadLayout;

    private TextView mHintView, mHintMoreView;

    private Style mStyle = Style.NORMAL;

    public PullToRefreshFooter(Context context) {
        this(context, Style.NORMAL);
    }

    public PullToRefreshFooter(Context context, Style style) {
        super(context, false, PullToRefreshBase.Orientation.VERTICAL);
        setContentView(R.layout.pull_to_refresh_footer);

        mLoadLayout = (LinearLayout) findViewById(R.id.footer_loading_layout);
        mHintView = (TextView) findViewById(R.id.footer_hint);
        mHintMoreView = (TextView) findViewById(R.id.footer_hint_more);
        mStyle = style;
    }

    @Override
    public void pullToRefresh() {
        mHintView.setText(R.string.xlistview_footer_hint_normal);
    }

    @Override
    public void refreshing() {
        mHintView.setVisibility(View.GONE);
        mLoadLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void releaseToRefresh() {
        mHintView.setText(R.string.xlistview_footer_hint_ready);
    }

    @Override
    public void onPull(float scaleOfLayout) {
    }

    @Override
    public void reset() {
        mHintView.setText(R.string.xlistview_footer_hint_normal);
        mHintView.setVisibility(View.VISIBLE);
        mLoadLayout.setVisibility(View.GONE);
        mHintMoreView.setVisibility(View.GONE);
    }

    @Override
    public void disableRefresh() {
        if (mStyle == Style.MORE) {
            mHintMoreView.setVisibility(View.VISIBLE);
        }
        mHintView.setText(R.string.xlistview_footer_no_more);
        if (mStyle == Style.EMPTY_NO_MORE)
            mHintView.setVisibility(View.GONE);
    }
}
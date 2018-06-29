package com.list.refresh.refreshview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class HeaderView extends RelativeLayout implements RefreshListView.NestedScrollingCallBack {

    private static final String TAG = "pw_HeaderView ";

    private TextView leftTv, contentTv, btnTv, refreshTv;

    private String content = "查找可连接WiFi";

    private CoverView coverView;
    private ImageView imageBg;

    private int imageMoveDistance;
    private CircleLoadView circleLoadView;

    public HeaderView(Context context) {
        this(context, null);
    }

    public HeaderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HeaderView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        inflate(context, R.layout.fragment_wifi_j_list_header, this);
        initView();
        initDistance();
    }

    private void initView() {
        leftTv = findViewById(R.id.left_title);
        contentTv = findViewById(R.id.content);
        btnTv = findViewById(R.id.ap_check_btn);
        refreshTv = findViewById(R.id.refresh_title);
        coverView = findViewById(R.id.cover_view);
        imageBg = findViewById(R.id.image_bg);
        circleLoadView = findViewById(R.id.circle_load_view);

        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnTv.setVisibility(btnTv.getVisibility() == VISIBLE ? INVISIBLE : VISIBLE);
                callHeadViewHeightChange.onHeightChange(btnTv.getVisibility() == VISIBLE);
            }
        });
    }

    private void initDistance(){
        imageMoveDistance = UiUtil.dip2px(getContext(), 72);
    }

    @Override
    public void nestedScrolling(int type, float percent) {
        if (type == 0) {
            leftTv.setAlpha(1 - percent);
            contentTv.setAlpha(percent);
            btnTv.setAlpha(percent);
            refreshTv.setAlpha(0);

            coverView.updateView(percent);
            imageBg.setTranslationY(0);
            refreshTv.setTranslationY(0);
            btnTv.setTranslationY(0);
            circleLoadView.updateView(-1);


        } else if (type == 1) {
            percent = Math.min(percent, 1);
            leftTv.setAlpha(0);
            btnTv.setAlpha(1);
            if (percent < 0.01) {
                contentTv.setText(content);
                contentTv.setAlpha(1);
                refreshTv.setAlpha(0);
            } else if (percent < 0.3) {
                refreshTv.setText("下拉查找更多WiFi");
                contentTv.setAlpha(0);
                refreshTv.setAlpha(1);
            } else {
                refreshTv.setText("松开查找更多");
                contentTv.setAlpha(0);
                refreshTv.setAlpha(1);
                btnTv.setAlpha(1-percent);
            }
            coverView.updateView(1);
            imageBg.setTranslationY(percent * imageMoveDistance);
            refreshTv.setTranslationY(percent * imageMoveDistance * 1.7f);
            btnTv.setTranslationY(percent * imageMoveDistance * 1.7f);
            circleLoadView.updateView(percent);
        }
    }

    @Override
    public void onRefresh() {
        contentTv.setText("正在查找WiFi");
    }

    CallHeadViewHeightChange callHeadViewHeightChange;
    public void setCallHeadViewHeightChange(CallHeadViewHeightChange callHeadViewHeightChange) {
        this.callHeadViewHeightChange = callHeadViewHeightChange;
    }

    public interface CallHeadViewHeightChange{
        void onHeightChange(boolean isBig);
    }
}

package com.list.refresh.refreshview.shrink;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.NestedScrollingParentHelper;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;
import android.widget.Scroller;

import com.list.refresh.refreshview.CardLayout;
import com.list.refresh.refreshview.HeaderView;
import com.list.refresh.refreshview.R;

public class ShrinkListView extends FrameLayout implements ShrinkLayout.OnShrinkScrollListener{

    private String LOG = "RefreshListView ";

    private ShrinkLayout mTarget;
    private RecyclerView recyclerView;
    private HeaderView headerView;


    public ShrinkListView(Context context) {
        this(context, null);
    }

    public ShrinkListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShrinkListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        inflate(context, R.layout.shrink_layout, this);
        initView();
    }

    private void initView() {
        mTarget = findViewById(R.id.target);
        recyclerView = findViewById(R.id.refresh_recycler_view);
        headerView = findViewById(R.id.refresh_headerview);
        headerView.setCallHeadViewHeightChange(new HeaderView.CallHeadViewHeightChange() {
            @Override
            public void onHeightChange(boolean isBig) {
                mTarget.upDateView(isBig);
            }
        });
        mTarget.setOnShrinkScrollListener(this);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
       /* mTarget.setTop(0);
        mTarget.offsetChildTopAndBottom(mCurrentTargetOffsetTop);*/
    }

    @Override
    public void onScrolled(int type, float percent) {
        headerView.nestedScrolling(type, percent);
    }


    private void doCallBackRefresh(){
        headerView.onRefresh();
    }

    public interface NestedScrollingCallBack{
        void nestedScrolling(int type, float percent);
        void onRefresh();
    }

    public RecyclerView getRecyclerView(){
        return recyclerView;
    }

    public ShrinkLayout getCardLayout() {
        return mTarget;
    }

    public HeaderView getHeaderView() {
        return headerView;
    }
}

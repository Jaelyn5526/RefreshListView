package com.list.refresh.refreshview.nest;

import android.animation.ValueAnimator;
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

import static android.support.v4.widget.ViewDragHelper.INVALID_POINTER;

public class RefreshListView2 extends FrameLayout implements NestedScrollingParent{

    private String LOG = "RefreshListView ";
    private int DEFAULT_CLOSE_TOP = 52;
    private int DEFAULT_REFRESH_TOP = 126;
    private int DEFAULT_REFRESH_TOP_MIN = 91;
    private int DEFAULT_OPEN_TOP = 198;

    private CardLayout mTarget;
    private RecyclerView recyclerView;
    private HeaderView headerView;

    private boolean mReturningToStart = false;
    private boolean mRefreshing = false;
    private boolean mIsBeingDragged = false;
    private int mActivePointerId;
    private int mInitialDownY;
    private int mTouchSlop;

    private int mCurrentTargetOffsetTop = 0;
    private int mDragCloseDistance;
    private int mDragRefreshDistance;
    private int mDragTotalDistance;
    private int mDragCloseLine;
    private int mDragOpenLine;
    private float mDisCloseRefresh;
    private float mDisRefresheOpen;

    private Scroller scroller;

    private NestedScrollingParentHelper mParentHelper;

    public RefreshListView2(Context context) {
        this(context, null);
    }

    public RefreshListView2(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RefreshListView2(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        inflate(context, R.layout.refresh_layout, this);
        initDistance(true);
        initView();
    }

    private void initView() {
        Log.d("onTouchEvent", "initView");
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        mCurrentTargetOffsetTop = mDragRefreshDistance;
        mTarget = findViewById(R.id.target);
        recyclerView = findViewById(R.id.refresh_recycler_view);
        headerView = findViewById(R.id.refresh_headerview);
        headerView.setCallHeadViewHeightChange(new HeaderView.CallHeadViewHeightChange() {
            @Override
            public void onHeightChange(boolean isBig) {
                initDistance(isBig);
                finishSpinner(mCurrentTargetOffsetTop, false);
            }
        });
        scroller = new Scroller(getContext());
        mParentHelper = new NestedScrollingParentHelper(this);
    }

    public void initDistance(boolean isBig){
        final DisplayMetrics metrics = getResources().getDisplayMetrics();
        if (isBig) {
            mDragRefreshDistance = (int) (DEFAULT_REFRESH_TOP * metrics.density);
        } else {
            mDragRefreshDistance = (int) (DEFAULT_REFRESH_TOP_MIN * metrics.density);
        }
        mDragCloseDistance = (int) (DEFAULT_CLOSE_TOP * metrics.density);
        mDragTotalDistance = (int) (DEFAULT_OPEN_TOP * metrics.density);
        mDragCloseLine = (mDragRefreshDistance - mDragCloseDistance) / 3 + mDragCloseDistance;
        mDragOpenLine =(mDragTotalDistance - mDragRefreshDistance) / 3 + mDragRefreshDistance;
        mDisCloseRefresh = mDragRefreshDistance - mDragCloseDistance;
        mDisRefresheOpen = mDragTotalDistance - mDragRefreshDistance;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        Log.d(LOG, "onLayout " + mCurrentTargetOffsetTop);
        mTarget.setTop(0);
        mTarget.offsetChildTopAndBottom(mCurrentTargetOffsetTop);
    }

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        return true;
    }

    @Override
    public void onNestedScrollAccepted(@NonNull View child, @NonNull View target, @ViewCompat.ScrollAxis int nestedScrollAxes){
        mParentHelper.onNestedScrollAccepted(child, target, nestedScrollAxes);
    }

    @Override
    public void onStopNestedScroll(View child) {
        super.onStopNestedScroll(child);
    }

    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        super.onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
    }

    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        Log.d(LOG, "onNestedPreScroll " + dy);
        if (dy < 0) {
             if (!target.canScrollVertically(-1)){
                 //下拉
                 mTarget.offsetChildTopAndBottom(-dy);
                 mCurrentTargetOffsetTop = mTarget.getCurrTop();

//                 ViewCompat.offsetTopAndBottom(mTarget,-dy);
//                 mCurrentTargetOffsetTop = mTarget.getTop();
                 consumed[1] = dy;
                 doCallBackScrolling(mCurrentTargetOffsetTop);
             }
        } else if (dy > 0) {
            if (mCurrentTargetOffsetTop > mDragCloseDistance){
                //上拉
                if (mCurrentTargetOffsetTop - dy < mDragCloseDistance) {
                    dy = mCurrentTargetOffsetTop - mDragCloseDistance;
                }
                mTarget.offsetChildTopAndBottom(-dy);
                mCurrentTargetOffsetTop = mTarget.getCurrTop();
//                ViewCompat.offsetTopAndBottom(mTarget,-dy);
//                mCurrentTargetOffsetTop = mTarget.getTop();
                consumed[1] = dy;
                doCallBackScrolling(mCurrentTargetOffsetTop);
            }
        }
        /*if (dy < 0){
            //下拉
            Log.d(LOG, "onNestedPreScroll " + dy);
            if (((RecyclerView)target).canScrollVertically(-1)){
                return;
            } else {
                ViewCompat.offsetTopAndBottom(mTarget,-dy);
                mCurrentTargetOffsetTop = mTarget.getTop();
                consumed[1] = dy;
            }
            doCallBackScrolling(mCurrentTargetOffsetTop);
        } else if (dy > 0){
            //上拉
            *//*if ((mTarget.getTop() - dy) <= mDragCloseDistance){
                dy = mDragCloseDistance - mTarget.getTop();
            }*//*
            Log.d(LOG, "onNestedPreScroll " + dy);
            ViewCompat.offsetTopAndBottom(mTarget,-dy);
            mCurrentTargetOffsetTop = mTarget.getTop();
            consumed[1] = dy;
            doCallBackScrolling(mCurrentTargetOffsetTop);
        }*/
    }

    @Override
    public int getNestedScrollAxes() {
        return mParentHelper.getNestedScrollAxes();
    }

    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        Log.d(LOG, "onNestedPreFling " + velocityX +" " + velocityY);
        return true;
    }


    /**
     * @return Whether it is possible for the child view of this layout to
     * scroll up. Override this if the child view is a custom view.
     */
    public boolean canChildScroll(int direction) {
        boolean canScroll = recyclerView.canScrollVertically(direction);
        return canScroll;
    }

    private void finishSpinner(float targetTop, boolean needCall) {
        if (targetTop <= mDragCloseLine){
            //关闭
            scroller.startScroll(0, mCurrentTargetOffsetTop, 0, mDragCloseDistance - mCurrentTargetOffsetTop);
        } else if (targetTop <= mDragOpenLine){
            //展开
            scroller.startScroll(0, mCurrentTargetOffsetTop, 0, mDragRefreshDistance - mCurrentTargetOffsetTop);
        } else {
            //刷新
            scroller.startScroll(0, mCurrentTargetOffsetTop, 0, mDragTotalDistance - mCurrentTargetOffsetTop);
        }
        invalidate();
    }


    @Override
    public void computeScroll() {
        if (scroller.computeScrollOffset()) {
            int currOffsetTop = scroller.getCurrY();
            mTarget.offsetChildTopAndBottom(currOffsetTop - mCurrentTargetOffsetTop);
            mCurrentTargetOffsetTop = mTarget.getCurrTop();
            doCallBackScrolling(mCurrentTargetOffsetTop);
            invalidate();
        }
    }


    private void moveSpinner(MotionEvent ev, float dy) {
        if (dy > 0) {
            if (canChildScroll(-1)){
                recyclerView.onTouchEvent(ev);
            } else if ((mTarget.getTop() + dy) < mDragCloseDistance){
                ViewCompat.offsetTopAndBottom(mTarget, mDragCloseDistance - mTarget.getTop());
                mCurrentTargetOffsetTop = mTarget.getTop();
            } else {
                ViewCompat.offsetTopAndBottom(mTarget, (int) (dy+0.5f));
                mCurrentTargetOffsetTop = mTarget.getTop();
            }
        } else if (dy < 0){
            if ((mTarget.getTop() + dy) <= mDragCloseDistance){
//                Log.d(LOG, "moveSpinner << 0 " + );
                ViewCompat.offsetTopAndBottom(mTarget,mDragCloseDistance - mTarget.getTop());
                mCurrentTargetOffsetTop = mTarget.getTop();
                if (!mTarget.canScrollVertically() && canChildScroll(1)){
                    recyclerView.onTouchEvent(ev);
                }
            } else {
                ViewCompat.offsetTopAndBottom(mTarget, (int) (dy+0.5f));
                mCurrentTargetOffsetTop = mTarget.getTop();
            }
        }
        doCallBackScrolling(mCurrentTargetOffsetTop);

    }

    float percent;
    private void doCallBackScrolling(int mCurrentTargetOffsetTop){
        if (mCurrentTargetOffsetTop <= mDragRefreshDistance){
            //展开状态
            percent = (mCurrentTargetOffsetTop - mDragCloseDistance) / mDisCloseRefresh;
            headerView.nestedScrolling(0, percent);
//            mTarget.nestedScrolling(0, percent);
        } else {
            //刷新状态
            percent = (mCurrentTargetOffsetTop - mDragRefreshDistance) / mDisRefresheOpen;
            headerView.nestedScrolling(1, percent);
//            mTarget.nestedScrolling(1, percent);
        }

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

    public CardLayout getCardLayout() {
        return mTarget;
    }

    public HeaderView getHeaderView() {
        return headerView;
    }
}

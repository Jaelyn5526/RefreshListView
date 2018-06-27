package com.list.refresh.refreshview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Scroller;

import static android.support.v4.widget.ViewDragHelper.INVALID_POINTER;

public class RefreshListView extends FrameLayout {

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

    private ValueAnimator animator;
    private Scroller scroller;

    public RefreshListView(Context context) {
        this(context, null);
    }

    public RefreshListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RefreshListView(Context context, AttributeSet attrs, int defStyle) {
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
        ViewCompat.offsetTopAndBottom(mTarget, mCurrentTargetOffsetTop);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        final int action = ev.getActionMasked();
        int pointerIndex;

        if (mReturningToStart && action == MotionEvent.ACTION_DOWN) {
            mReturningToStart = false;
        }

        if (!isEnabled() || mReturningToStart || mRefreshing) {
            // Fail fast if we're not in a state where a swipe is possible
            return false;
        }

        switch (action) {
            case MotionEvent.ACTION_DOWN:
//                setTargetOffsetTopAndBottom(mOriginalOffsetTop - mCircleView.getTop());
                mActivePointerId = ev.getPointerId(0);
                mIsBeingDragged = false;

                pointerIndex = ev.findPointerIndex(mActivePointerId);
                if (pointerIndex < 0) {
                    return false;
                }
                mInitialDownY = (int) (ev.getY(pointerIndex) + 0.5f);
                mLastTouchY = (int) (ev.getY() + 0.5f);
                break;

            case MotionEvent.ACTION_MOVE:
                if (mActivePointerId == INVALID_POINTER) {
                    Log.e(LOG, "Got ACTION_MOVE event but don't have an active pointer id.");
                    return false;
                }

                pointerIndex = ev.findPointerIndex(mActivePointerId);
                if (pointerIndex < 0) {
                    return false;
                }
                int y = (int) (ev.getY(pointerIndex) + 0.5f);
                startDragging(y);
               /* if (mTarget.getTop() != mDragCloseDistance && mIsBeingDragged){
                    return true;
                }*/
                break;

            case MotionEvent.ACTION_POINTER_UP:
                onSecondaryPointerUp(ev);
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mIsBeingDragged = false;
                mActivePointerId = INVALID_POINTER;
                break;
        }
        Log.d(LOG, "sBeingDragged: " + mIsBeingDragged);
        return mIsBeingDragged;
    }

    private void startDragging(float y) {
        final float yDiff = y - mInitialDownY;
        if (Math.abs(yDiff) > mTouchSlop && !mIsBeingDragged) {
            if (yDiff < 0 && mTarget.getTop() <= mDragCloseDistance){
                mIsBeingDragged = false;
            } else {
                mIsBeingDragged = true;
            }
        }
    }

    private void onSecondaryPointerUp(MotionEvent ev) {
        final int pointerIndex = ev.getActionIndex();
        final int pointerId = ev.getPointerId(pointerIndex);
        if (pointerId == mActivePointerId) {
            // This was our active pointer going up. Choose a new
            // active pointer and adjust accordingly.
            final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
            mActivePointerId = ev.getPointerId(newPointerIndex);
        }
    }

    float mLastTouchY = 0;

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        final int action = ev.getActionMasked();
        int pointerIndex = -1;

        if (mReturningToStart && action == MotionEvent.ACTION_DOWN) {
            mReturningToStart = false;
        }

        if (!isEnabled() || mReturningToStart || mRefreshing) {
            // Fail fast if we're not in a state where a swipe is possible
            return false;
        }

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mActivePointerId = ev.getPointerId(0);
                mIsBeingDragged = false;

                break;
            case MotionEvent.ACTION_POINTER_DOWN: {
                pointerIndex = ev.getActionIndex();
                if (pointerIndex < 0) {
                    Log.e(LOG,
                            "Got ACTION_POINTER_DOWN event but have an invalid action index.");
                    return false;
                }
                mActivePointerId = ev.getPointerId(pointerIndex);
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                pointerIndex = ev.findPointerIndex(mActivePointerId);
                if (pointerIndex < 0) {
                    Log.e(LOG, "Got ACTION_MOVE event but have an invalid active pointer id.");
                    return false;
                }

                int y = (int) (ev.getY(pointerIndex) + 0.5f);
                float dy = y - mLastTouchY;
                mLastTouchY = y;
                startDragging(y);
                if (mIsBeingDragged) {
                    moveSpinner(ev, dy);
                }

                break;
            }


            case MotionEvent.ACTION_POINTER_UP:
                onSecondaryPointerUp(ev);
                break;

            case MotionEvent.ACTION_UP: {
                pointerIndex = ev.findPointerIndex(mActivePointerId);
                if (pointerIndex < 0) {
                    Log.e(LOG, "Got ACTION_UP event but don't have an active pointer id.");
                    return false;
                }

                if (mIsBeingDragged) {
                    mIsBeingDragged = false;
                    if (mCurrentTargetOffsetTop <= mDragCloseDistance){
                        recyclerView.onTouchEvent(ev);
                    } else {
                        finishSpinner(mCurrentTargetOffsetTop, true);
                    }
                }
                mActivePointerId = INVALID_POINTER;
                return false;
            }
            case MotionEvent.ACTION_CANCEL:
                return false;
        }

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
            ViewCompat.offsetTopAndBottom(mTarget, currOffsetTop - mCurrentTargetOffsetTop);
            mCurrentTargetOffsetTop = mTarget.getTop();
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
            mTarget.nestedScrolling(0, percent);
        } else {
            //刷新状态
            percent = (mCurrentTargetOffsetTop - mDragRefreshDistance) / mDisRefresheOpen;
            headerView.nestedScrolling(1, percent);
            mTarget.nestedScrolling(1, percent);
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

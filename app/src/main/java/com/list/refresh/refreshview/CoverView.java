package com.list.refresh.refreshview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

public class CoverView extends View{

    private Rect rect = new Rect();
    private int top, bottom;
    private int rectMaxHeight;
    private Paint paint;

    public CoverView(Context context) {
        this(context, null);
    }

    public CoverView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CoverView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        top = bottom = UiUtil.dip2px(getContext(), 289);
        rectMaxHeight = bottom - UiUtil.dip2px(getContext(), 49);
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        rect.set(0, top, getWidth(), bottom);
        canvas.drawRect(rect, paint);
    }

    public void updateView(float percent){
        top = (int) (bottom - rectMaxHeight * (1 - percent));
        postInvalidate();
    }
}

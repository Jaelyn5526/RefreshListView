package com.list.refresh.refreshview;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.TouchDelegate;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewParent;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import java.lang.reflect.Method;

/**
 * UI工具类
 *
 * @author EX-YANGZHIHONG001
 * @version 1.0
 */
public class UiUtil {

    private static long lastClickTime;
    public static float density = 1;

    public static void setPressedSafe(View view, boolean pressed) {
        if (view != null && view.isPressed() != pressed) {
            view.setPressed(pressed);
        }
    }

    public static void setActivatedSafe(View view, boolean activated) {
        if (view != null && view.isActivated() != activated) {
            view.setActivated(activated);
        }
    }

    public static void setVisibility(View view, int visibility) {
        if (view != null && view.getVisibility() != visibility) {
            view.setVisibility(visibility);
        }
    }

    public static void setVisibility(View parent, int id, int visibility) {
        if (parent != null) {
            View view = parent.findViewById(id);
            if (view != null) {
                setVisibility(view, visibility);
            }
        }
    }

    public static void setBackgroundResourceSafe(View parent, int id, int resId) {
        if (parent != null) {
            View view = parent.findViewById(id);
            if (view != null) {
                view.setBackgroundResource(resId);
            }
        }
    }

    public static void setEnabledSafe(View parent, int id, boolean enabled) {
        if (parent != null) {
            View view = parent.findViewById(id);
            setEnabledSafe(view, enabled);
        }
    }

    public static void setEnabledSafe(View view, boolean enabled) {
        if (view != null) {
            view.setEnabled(enabled);
        }
    }

    public static void setOnClickListenerSafe(View parent, int id, OnClickListener l) {
        if (parent != null) {
            View view = parent.findViewById(id);
            if (view != null) {
                view.setOnClickListener(l);
            }
        }
    }

    public static void requestFocus(View view) {
        if (view != null) {
            view.setFocusable(true);
            view.setFocusableInTouchMode(true);
            view.requestFocus();
        }
    }

    public static boolean isEditTextEmpty(EditText edit) {
        return edit.getText() == null || edit.getText().toString().trim().length() <= 0;
    }

    public static boolean hideInputMethod(Activity activity) {
        return hideInputMethod(activity, activity.getWindow().getDecorView().getWindowToken());
    }

    public static boolean hideInputMethod(Dialog dialog) {
        return hideInputMethod(dialog.getContext(), dialog.getWindow().getDecorView().getWindowToken());
    }

    public static boolean hideInputMethod(Context context, IBinder token) {
        InputMethodManager im = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        return im.hideSoftInputFromWindow(token, 0);
    }

    public static void showInputMethod(Context context, View view) {
        InputMethodManager im = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        im.showSoftInput(view, 0);
    }

    public static void recycleBitmap(Bitmap bitmap) {
        if (bitmap != null) {
            bitmap.recycle();
        }
    }

    public static void recycleBitmap(BitmapDrawable drawable) {
        if (drawable != null) {
            recycleBitmap(drawable.getBitmap());
        }
    }

    public static void dismissDialog(Dialog pd) {
        if (pd != null && pd.isShowing()) {
            pd.dismiss();
        }
    }


    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        float scale = 1f;
        /*如果是fragment传进的context，activity被内存回收但Fragment并不会随着Activity的回收而被回收
        创建的所有Fragment会被保存到Bundle里面，从而导致Fragment丢失对应的Activity,因此需要context非空判断*/
        if (null != context) {
            scale = context.getResources().getDisplayMetrics().density;
        }
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        float scale = 1f;
        if (null != context) {
            scale = context.getResources().getDisplayMetrics().density;
        }
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 sp 的单位 转成为 px(像素)
     */
    public static int sp2px(Context context, int spVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spVal, context.getResources()
                .getDisplayMetrics());
    }

    public static int dp(float value) {
        if (value == 0) {
            return 0;
        }
        return (int) Math.ceil(density * value);
    }

    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < 800) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    public static int[] getArrayIds(Context context, int arrayId) {
        Resources res = context.getResources();
        TypedArray icon = res.obtainTypedArray(arrayId);
        int len = icon.length();
        int[] resIds = new int[len];
        for (int i = 0; i < len; i++)
            resIds[i] = icon.getResourceId(i, 0);
        icon.recycle();
        return resIds;
    }

    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * 扩大View的触摸和点击响应范围,最大不超过其父View范围
     *
     * @param view
     * @param left
     * @param top
     * @param right
     * @param bottom
     */
    public static void expandViewTouchDelegate(final View view, final int left,
                                               final int top, final int right, final int bottom) {
        if (view != null) {
            final ViewParent p = view.getParent();
            if (p != null && p instanceof View) {
                final View v = (View) p;
                v.post(new Runnable() {
                    @Override
                    public void run() {
                        Rect bounds = new Rect();
                        view.setEnabled(true);
                        view.getHitRect(bounds);

                        bounds.left -= left;
                        bounds.top -= top;
                        bounds.right += right;
                        bounds.bottom += bottom;

                        TouchDelegate touchDelegate = new TouchDelegate(bounds, view);

                        if (View.class.isInstance(p)) {
                            v.setTouchDelegate(touchDelegate);
                        }
                    }
                });
            }
        }
    }

    /**
     * 改变TextView的字体颜色
     *
     * @param view
     * @param hasFocus
     */
    public static void changeTextColor(Context context, TextView view, boolean hasFocus) {
        if (context != null && view != null) {
            if (hasFocus) {
                view.setTextColor(context.getResources().getColor(R.color.text_emp_blue));
            } else {
                view.setTextColor(context.getResources().getColor(R.color.edit_text));
            }
        }
    }

    /**
     * 获取传递过来的VIEW的坐标
     *
     * @param view
     * @return
     */
    public static int[] getLocationInWindow(View view) {
        int[] location = new int[2];
        if (view != null) {
            view.getLocationInWindow(location);
        }
        return location;
    }

    /**
     * @param context
     * @description:通过反射方式收缩通知栏
     */
    public static void collapsingNotification(Context context) {
        @SuppressWarnings("WrongConstant") Object service = context.getSystemService("statusbar");
        try {
            if (null == service) {
                return;
            }

            Class<?> clazz = Class.forName("android.app.StatusBarManager");
            int sdkVersion = android.os.Build.VERSION.SDK_INT;
            Method collapse = null;
            if (sdkVersion <= 16) {
                collapse = clazz.getMethod("collapse");
            } else {
                collapse = clazz.getMethod("collapsePanels");
            }

            collapse.setAccessible(true);
            collapse.invoke(service);
        } catch (Exception e) {
            Log.e("ClassReflectUtils", e.getMessage(), e);
        }
    }

    public static DisplayMetrics getDisplayMetrics(Activity activity) {
        DisplayMetrics metrics = new DisplayMetrics();
        if (null != activity) {
            activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        }
        return metrics;
    }

}

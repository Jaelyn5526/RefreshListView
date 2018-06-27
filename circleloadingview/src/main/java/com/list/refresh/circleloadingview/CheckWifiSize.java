package com.list.refresh.circleloadingview;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;


public class CheckWifiSize{

    private int i = 0;
    private boolean isCallBack = true;
    private boolean isRun = false;
    private HandlerThread thread;
    private Handler handler;
    private static CheckWifiSize checkWifiSize;

    private CheckWifiSize(){
        thread = new HandlerThread("CheckWifiSize");
        thread.start();
        handler = new Handler(thread.getLooper()){
            @Override
            public void handleMessage(Message msg) {
                checkWifi();
            }
        };

    }

    public static synchronized CheckWifiSize getInstance(){
        if (checkWifiSize == null){
            checkWifiSize = new CheckWifiSize();
        }
        return checkWifiSize;
    }

    public void star(){
        if (!isRun){
            isRun = true;
            handler.sendEmptyMessage(0);
        }
    }

    public void checkWifi() {
        isRun = true;
        int allSize = 0;
        if (allSize > 0) {
            if (isCallBack) {
                isCallBack = false;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        callBackFinish(true);
                    }
                }, 3000 - Math.min(i, 2) * 1500); // 0-3000, 1-1500, 2-0
            }
            reset();
        } else if (i > 4) {
            callBackFinish(false);
            reset();
        } else {
            handler.sendEmptyMessageDelayed(0, 1000);
            i++;
        }
    }

    public void reset(){
        i = 0;
        isCallBack = true;
        isRun = false;
    }

    public boolean isRun(){
        return isRun;
    }

    private List<WifiSizeCallBack> callBacks = new ArrayList<>();

    public void addCallBack(@NonNull  WifiSizeCallBack callBack){
        if (!callBacks.contains(callBack)){
            callBacks.add(callBack);
        }
    }

    public void removeCallBack(@NonNull  WifiSizeCallBack callBack){
        callBacks.remove(callBack);
    }

    private void callBackFinish(final boolean haveSupportWifi){


    }

    public interface WifiSizeCallBack{
        void scanFinish(boolean haveSupportWifi);
    }


}

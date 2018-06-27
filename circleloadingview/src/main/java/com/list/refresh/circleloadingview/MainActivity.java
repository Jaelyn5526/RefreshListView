package com.list.refresh.circleloadingview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckWifiSize.getInstance().addCallBack(new CheckWifiSize.WifiSizeCallBack() {
                    @Override
                    public void scanFinish(boolean haveSupportWifi) {

                    }
                });
            }
        });
    }
}

package cn.example.test_webview_omed.utils;

import android.view.View;

import java.util.Calendar;

/**
 * Created by WenMing on 2016/8/21.21:02
 * Introduction:避免双击的点击监听事件
 */
public abstract class AvoidDoubleClickListener implements View.OnClickListener {
    private long lastTime = 0;
    private int delayTime;

    public AvoidDoubleClickListener(int time){
        delayTime = time;
    }


    @Override
    public void onClick(View v) {
        long currentTime = Calendar.getInstance().getTimeInMillis();
        if (currentTime - lastTime > delayTime) {
            lastTime = currentTime;
            onNoDoubleClick(v);
        }
    }

    public abstract void onNoDoubleClick(View view);
}

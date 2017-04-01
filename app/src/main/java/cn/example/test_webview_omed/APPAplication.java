package cn.example.test_webview_omed;

import android.app.Application;
import android.util.Log;

import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.TbsDownloader;
import com.tencent.smtt.sdk.TbsListener;

public class APPAplication extends Application {
    private static final String TAG = "APPAplication";

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        //搜集本地tbs内核信息并上报服务器，服务器返回结果决定使用哪个内核。
        boolean b = TbsDownloader.needDownload(getApplicationContext(), false);
        Log.i(TAG, "onCreate: TbsDownloader.needDownload(getApplicationContext(), false) " + b);

        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {

            @Override
            public void onViewInitFinished(boolean arg0) {
                // TODO Auto-generated method stub
                Log.e(TAG, " onViewInitFinished is " + arg0);
            }

            @Override
            public void onCoreInitFinished() {
                // TODO Auto-generated method stub

            }
        };
        QbSdk.setTbsListener(new TbsListener() {
            @Override
            public void onDownloadFinish(int i) {
                Log.d(TAG, "onDownloadFinish is " + i);
            }

            @Override
            public void onInstallFinish(int i) {
                Log.d(TAG, "onInstallFinish is " + i);
            }

            @Override
            public void onDownloadProgress(int i) {
                Log.d(TAG, "onDownloadProgress:" + i);
            }
        });

        QbSdk.initX5Environment(getApplicationContext(), cb);
    }

}

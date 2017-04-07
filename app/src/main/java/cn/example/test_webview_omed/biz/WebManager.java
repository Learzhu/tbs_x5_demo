package cn.example.test_webview_omed.biz;

import android.app.DownloadManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.widget.Toast;

import java.io.File;

public class WebManager {

    private Context mContext;

    private DownloadManager mDownloadManager;

    public WebManager(Context context) {
        mContext = context;
        mDownloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
    }

    /**
     * 将普通的缩略的URL转换为标准的HTTP协议的URL
     *
     * @param url 需要转换的URL
     * @return
     */
    public String checkUrl(String url) {
        if (url.startsWith("http://") || url.startsWith("https://") || url.startsWith("HTTPS://")) {
            return url;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("http://");
        stringBuilder.append(url);
        return stringBuilder.toString();
    }

    /**
     * 开始下载文件
     *
     * @param url
     * @param file
     * @return
     */
    public long prepareDownload(String url, File file) {
        int downloadManagerState = mContext.getPackageManager().getApplicationEnabledSetting("com.android.providers.downloads");
        //下载组件可用状态判断
        if (downloadManagerState == PackageManager.COMPONENT_ENABLED_STATE_DEFAULT ||
                downloadManagerState == PackageManager.COMPONENT_ENABLED_STATE_ENABLED) {
            Uri uri = Uri.parse(url);
            DownloadManager.Request request = new DownloadManager.Request(uri);
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
            request.setDestinationUri(Uri.fromFile(file));
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
            request.setVisibleInDownloadsUi(false);
            return mDownloadManager.enqueue(request);
        } else {
            Toast.makeText(mContext, "您的下载管理程序已停用，请开启", Toast.LENGTH_LONG).show();
            return 0;
        }
    }

    public void cancelDownload(long reference) {
        mDownloadManager.remove(reference);
    }

    public DownloadManager getDownloadManager() {
        return mDownloadManager;
    }
}

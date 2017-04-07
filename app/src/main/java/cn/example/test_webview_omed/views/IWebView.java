package cn.example.test_webview_omed.views;

import android.webkit.GeolocationPermissions;


public interface IWebView {
    void onUrlChanged(String url);

    void onDownloadStart(String url, String fileName, long contentLength);

    void onReceivedTitle(String title);

    void onReceiveError();

    void onLoadStart();

    void onProgressChanged(int progress);

    void onLoadFinish(String url, String title, boolean isError);

    void onTitleChanged(String title);

    void onObtainLocate(String origin, GeolocationPermissions.Callback callback);

    void onLoadIntent(String url);

    void onDownloadImage(String url);

    void onShowCustomView();

    void onHideCustomView();
}

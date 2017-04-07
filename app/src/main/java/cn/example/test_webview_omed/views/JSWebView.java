package cn.example.test_webview_omed.views;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tencent.smtt.export.external.interfaces.ClientCertRequest;
import com.tencent.smtt.export.external.interfaces.HttpAuthHandler;
import com.tencent.smtt.export.external.interfaces.IX5WebChromeClient;
import com.tencent.smtt.export.external.interfaces.JsPromptResult;
import com.tencent.smtt.export.external.interfaces.JsResult;
import com.tencent.smtt.export.external.interfaces.SslError;
import com.tencent.smtt.export.external.interfaces.SslErrorHandler;
import com.tencent.smtt.export.external.interfaces.WebResourceError;
import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.DownloadListener;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import cn.example.test_webview_omed.utils.LogUtils;
import cn.example.test_webview_omed.utils.SecurityJsBridgeBundle;


/**
 * JSWebView.java是极搜浏览器的网站显示容器类。
 *
 * @author Learzhu
 * @version 2.2.5 2017/3/19 19:14
 * @update UserName 2017/3/19 19:14
 * @updateDes
 * @include {@link WebChromeClient , WebViewClient}
 */

public class JSWebView extends WebView {

    private static final String TAG = "JSWebView";

    /**
     * 用于传递title[书签跳转的时候]
     */
    public static final String EXTRA_WEB_TITLE = "webTitle";

    /**
     * 传递URL[HomeFragment,SearchActivity,BookmarkEditActivity跳转使用]
     */
    public static final String EXTRA_WEB_URL = "webUrl";

    /**
     * 用于标记是否当前的WebView已经消失
     */
    private boolean isGone;

    /**
     * 用于记录网址解析是否出错
     */
    private boolean isError;

    /**
     * 记录Title的集合
     */
    private Map<String, String> urlTitleMap = new HashMap<>();

    /**
     * 用于传递的上下文
     */
    private Context mContext;

    /**
     * 标记是否支持小屏在界面中的非全屏显示的状态
     */
    private static boolean isSmallWebViewDisplayed = false;

    /**
     * 下面部分用于管理用户和WebView的交互
     */
    private boolean isClampedY = false;
    private Map<String, Object> mJsBridges;
    private TextView tog;
    private RelativeLayout.LayoutParams layoutParams;
    private RelativeLayout refreshRela;

    /**
     * 记录浏览历史
     */
    private String mPendingHistory;

    /**
     * 记录当前的WebView的Title
     */
    private String mTitle;

    /**
     * 用于悬浮的窗口，为了方便管理一个类持有一个对象
     */
    private WebViewTransport mWebViewTransport;

    /**
     * 悬浮的webView
     */
    private WebView mWebView;

    /**
     * won't require too many features but rendering HTML
     * 解析网址的客户端
     */
    private WebViewClient mWebViewClient = new WebViewClient() {
        @Override
        public void onLoadResource(WebView view, String url) {
            super.onLoadResource(view, url);
        }
//        @Override
//        public void onPageCommitVisible(WebView view, String url) {
//            super.onPageCommitVisible(view, url);
//            LogUtils.v("---------- onPageCommitVisible ");
//        }

        @Override
        public void onReceivedClientCertRequest(WebView view, ClientCertRequest request) {
            super.onReceivedClientCertRequest(view, request);
            LogUtils.v("---------- onReceivedClientCertRequest ");
        }

        @Override
        public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm) {
            super.onReceivedHttpAuthRequest(view, handler, host, realm);
            boolean flag = handler.useHttpAuthUsernamePassword();
            LogUtils.v("---------- onReceivedHttpAuthRequest flag +" + flag);
        }

        /**
         * 拦截事件
         * @param view
         * @param event
         * @return
         */
        @Override
        public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
            LogUtils.v("---------- shouldOverrideKeyEvent ");
            return super.shouldOverrideKeyEvent(view, event);
        }


        @Override
        public void onScaleChanged(WebView view, float oldScale, float newScale) {
            super.onScaleChanged(view, oldScale, newScale);
            LogUtils.v("---------- onScaleChanged ");
        }

        @Override
        public void onReceivedLoginRequest(WebView view, String realm, String account, String args) {
            super.onReceivedLoginRequest(view, realm, account, args);
            LogUtils.v("---------- onReceivedLoginRequest ");
        }

        // Force links to be opened inside WebView and not in Default Browser
        // Thanks http://stackoverflow.com/a/33681975/1815624

        /**
         * 实现点击webView页面的链接
         *  return true 表示当前url即使是重定向url也不会再执行（除了在return true之前使用webview.loadUrl(url)除外，因为这个会重新加载）
         * return false  表示由系统执行url，直到不再执行此方法，即加载完重定向的url（即具体的url，不再有重定向）
         * @param view
         * @param url
         * @return
         */
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            //跳转的时候记录下当前的网页的位置
            //此时无效，因为此时的方法是TBS X5的WebView为FrameLayout的
//            int scrollY = view.getScrollY();
//            int scrollY = view.getWebScrollY();
//            AnchorTable anchorTable = new AnchorTable();
//            anchorTable.setScrollY(scrollY);
//            anchorTable.setUrl(view.getUrl());
//            anchorTable.setWebView(view.toString());
//            anchorTable.save();
//            String s = view.toString();
            LogUtils.v("------11111---- view.toString() " + view.toString());

            LogUtils.v("------11111---- shouldOverrideUrlLoading " + view.getUrl());
            LogUtils.v("-------22222--- shouldOverrideUrlLoading " + url);
            if (!url.startsWith("http") && !url.startsWith("https")) {
                //APP界面内跳转
                if (mSyWebViewCallback != null) {
                    mSyWebViewCallback.onLoadIntent(url);
                }
                return true;
            }
            return false;
        }

        @Override
        public void onFormResubmission(WebView view, Message dontResend, Message resend) {
            super.onFormResubmission(view, dontResend, resend);
            LogUtils.v("---------- onFormResubmission " + view.getUrl());
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            LogUtils.v("---------- onPageStarted " + url);
            if (mSyWebViewCallback != null) {
                mSyWebViewCallback.onUrlChanged(url);
                mSyWebViewCallback.onLoadStart();
            }
        }


        @Override
        public void onReceivedSslError(WebView webView, SslErrorHandler sslErrorHandler, SslError sslError) {
            super.onReceivedSslError(webView, sslErrorHandler, sslError);
            LogUtils.v("---------- onReceivedSslError " + sslError.toString());
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            LogUtils.v("---------- onPageFinished " + url);
            if (!getSettings().getLoadsImagesAutomatically()) {
                getSettings().setLoadsImagesAutomatically(true);
            }
            //该处可能出现直接加载下一个方法没有start
            if (mSyWebViewCallback != null) {
                mSyWebViewCallback.onUrlChanged(url);
            }
        }

        @Override
        public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
            LogUtils.v("---------- doUpdateVisitedHistory " + url);
//            if (mSyWebViewCallback != null) {
//                mSyWebViewCallback.doUpdateVisitedHistory(view.getTitle(), url);
//            }
//            Log.i(TAG, "---------- doUpdateVisitedHistory " + url);
            mPendingHistory = url;
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            LogUtils.v("---------- onReceivedError errorCode " + errorCode);
            LogUtils.v("---------- onReceivedError " + view.getUrl());
            LogUtils.v("---------- onReceivedError failingUrl" + failingUrl);
//            loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            isError = true;
            pauseTimers();
            stopLoading();
            mSyWebViewCallback.onReceiveError();
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
//            loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
//            isError = true;
//            pauseTimers();
//            stopLoading();
//            mSyWebViewCallback.onReceiveError();
        }

        @Override
        public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
            super.onReceivedHttpError(view, request, errorResponse);
        }

        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
            return super.shouldInterceptRequest(view, request);
        }
    };

    /**
     * 用于管理JS等处理的类
     */
    private WebChromeClient mWebChromeClient = new WebChromeClient() {
        View myVideoView;
        View myNormalView;
        IX5WebChromeClient.CustomViewCallback callback;

        @Override
        public void onReceivedTitle(WebView view, String title) {
            LogUtils.v("---------- onReceivedTitle " + title);
            mTitle = title;
            mSyWebViewCallback.onReceivedTitle(title);
            urlTitleMap.put(view.getUrl(), title);
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (mSyWebViewCallback != null) {
                mSyWebViewCallback.onProgressChanged(newProgress);
            }
            //如果加载成功就加入到webView的缓存
            if (newProgress == 100) {
                if (mSyWebViewCallback != null) {
                    String url = view.getUrl();
                    mSyWebViewCallback.onLoadFinish(url, urlTitleMap.get(url), isError);
                    //此处加载完毕未必的true
                    isError = false;
                    mSyWebViewCallback.onTitleChanged(urlTitleMap.get(url));
                }
            }
        }

        @Override
        public boolean onJsConfirm(WebView webView, String s, String s1, JsResult jsResult) {
            return super.onJsConfirm(webView, s, s1, jsResult);
        }

        /**
         * 全屏播放配置
         */
        @Override
        public void onShowCustomView(View view, IX5WebChromeClient.CustomViewCallback customViewCallback) {
            //实际获取到的webView的控件
//            FrameLayout normalView = (FrameLayout) ((Activity) getContext()).findViewById(R.id.js_webview);
            FrameLayout normalView = (FrameLayout) JSWebView.this;
            ViewGroup viewGroup = (ViewGroup) normalView.getParent();
            viewGroup.removeView(normalView);
            viewGroup.addView(view);
            myVideoView = view;
            myNormalView = normalView;
            callback = customViewCallback;

            //全屏播放的时候横屏
            if (mSyWebViewCallback != null) {
                mSyWebViewCallback.onShowCustomView();
            }
        }

        @Override
        public void onHideCustomView() {
            if (callback != null) {
                callback.onCustomViewHidden();
                callback = null;
            }
            if (myVideoView != null) {
                ViewGroup viewGroup = (ViewGroup) myVideoView.getParent();
                viewGroup.removeView(myVideoView);
                viewGroup.addView(myNormalView);
            }
            //全屏播放的时候横屏
            if (mSyWebViewCallback != null) {
                mSyWebViewCallback.onHideCustomView();
            }
        }

        @Override
        public boolean onShowFileChooser(WebView arg0,
                                         ValueCallback<Uri[]> arg1, FileChooserParams arg2) {
            // TODO Auto-generated method stub
            Log.e("app", "onShowFileChooser");
            return super.onShowFileChooser(arg0, arg1, arg2);
        }

        @Override
        public void openFileChooser(ValueCallback<Uri> uploadFile, String acceptType, String captureType) {
            Log.e("app", "openFileChooser");
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            try {
                ((Activity) (JSWebView.this.getContext())).startActivityForResult(Intent.createChooser(intent, "choose files"),
                        1);
            } catch (android.content.ActivityNotFoundException ex) {

            }

            super.openFileChooser(uploadFile, acceptType, captureType);
        }

        /**
         * webview 的窗口转移
         */
        @Override
        public boolean onCreateWindow(WebView arg0, boolean arg1, boolean arg2, Message msg) {
            // TODO Auto-generated method stub
            if (JSWebView.isSmallWebViewDisplayed == true) {

                mWebViewTransport = (WebViewTransport) msg.obj;
                if (mWebView != null) {
                    JSWebView.this.removeView(mWebView);
                }
                mWebView = new WebView(JSWebView.this.getContext()) {

                    protected void onDraw(Canvas canvas) {
                        super.onDraw(canvas);
                        Paint paint = new Paint();
                        paint.setColor(Color.GREEN);
                        paint.setTextSize(15);
                        canvas.drawText("新建窗口", 10, 10, paint);
                    }
                };
                mWebView.setWebViewClient(new WebViewClient() {
                    public boolean shouldOverrideUrlLoading(WebView arg0, String arg1) {
                        arg0.loadUrl(arg1);
                        return true;
                    }
                });
                FrameLayout.LayoutParams lp = new LayoutParams(400, 600);
                lp.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
                JSWebView.this.addView(mWebView, lp);
                mWebViewTransport.setWebView(mWebView);
                msg.sendToTarget();
            }
            return true;
        }

        @Override
        public boolean onJsAlert(WebView arg0, String arg1, String arg2, JsResult arg3) {
            /**
             * 这里写入你自定义的window alert
             */
            // AlertDialog.Builder builder = new Builder(getContext());
            // builder.setTitleTv("X5内核");
            // builder.setPositiveButton("确定", new
            // DialogInterface.OnClickListener() {
            //
            // @Override
            // public void onClick(DialogInterface dialog, int which) {
            // // TODO Auto-generated method stub
            // dialog.dismiss();
            // }
            // });
            // builder.show();
            // arg3.confirm();
            // return true;
            Log.i("yuanhaizhou", "setJSWebView = null");
            return super.onJsAlert(null, "www.baidu.com", "aa", arg3);
        }


        /**
         * 对应js 的通知弹框 ，可以用来实现js 和 android之间的通信
         */
        @Override
        public boolean onJsPrompt(WebView arg0, String arg1, String arg2, String arg3, JsPromptResult arg4) {
            // 在这里可以判定js传过来的数据，用于调起android native 方法
            if (JSWebView.this.isMsgPrompt(arg1)) {
                if (JSWebView.this.onJsPrompt(arg2, arg3)) {
                    return true;
                } else {
                    return false;
                }
            }
            return super.onJsPrompt(arg0, arg1, arg2, arg3, arg4);
        }
    };


    @SuppressWarnings("unused")
    public JSWebView(Context context) {
        super(context);
        this.mContext = context;
//        setBackgroundColor(85621);
        initWebViewSetting();
        initWebView();
    }


    @SuppressLint("SetJavaScriptEnabled")
    public JSWebView(Context arg0, AttributeSet arg1) {
        super(arg0, arg1);
        this.mContext = arg0;
        initWebViewSetting();
        initWebView();
    }

    @SuppressWarnings("unused")
    public JSWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initWebViewSetting();
        initWebView();
    }

    /**
     * Pass only a VideoEnabledWebChromeClient instance.
     */
    @Override
    @SuppressLint("SetJavaScriptEnabled")
    public void setWebChromeClient(WebChromeClient client) {
        getSettings().setJavaScriptEnabled(true);
        if (client instanceof WebChromeClient) {
            this.mWebChromeClient = client;
        }
        super.setWebChromeClient(client);
    }

    @Override
    public void loadData(String data, String mimeType, String encoding) {
        super.loadData(data, mimeType, encoding);
    }

    @Override
    public void loadDataWithBaseURL(String baseUrl, String data, String mimeType, String encoding, String historyUrl) {
        super.loadDataWithBaseURL(baseUrl, data, mimeType, encoding, historyUrl);
    }

    @Override
    public void loadUrl(String url) {
        super.loadUrl(url);
    }

    @Override
    public void loadUrl(String url, Map<String, String> additionalHttpHeaders) {
        super.loadUrl(url, additionalHttpHeaders);
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if (visibility == GONE) {
            try {
                WebView.class.getMethod("onPause").invoke(this);
            } catch (Exception e) {
                e.printStackTrace();
                this.pauseTimers();
                this.isGone = true;
            }
        } else if (visibility == VISIBLE && getVisibility() == VISIBLE) {
            try {
                WebView.class.getMethod("onResume").invoke(this);
            } catch (Exception e) {
                e.printStackTrace();
                this.resumeTimers();
                this.isGone = false;
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (isGone) {
            destroy();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        stopLoading();
        pauseTimers();
    }

    @Override
    public void onResume() {
        super.onResume();
        resumeTimers();
    }

    @Override
    public void destroy() {
        clearCache(true);
        clearFormData();
        clearMatches();
        clearSslPreferences();
        clearDisappearingChildren();
        clearHistory();
        clearAnimation();
        removeAllViews();
        super.destroy();
    }

    public static void setSmallWebViewEnabled(boolean enabled) {
        isSmallWebViewDisplayed = enabled;
    }

    public void addJavascriptBridge(SecurityJsBridgeBundle jsBridgeBundle) {
        if (this.mJsBridges == null) {
            this.mJsBridges = new HashMap<String, Object>(5);
        }

        if (jsBridgeBundle != null) {
            String tag = SecurityJsBridgeBundle.BLOCK + jsBridgeBundle.getJsBlockName() + "-"
                    + SecurityJsBridgeBundle.METHOD + jsBridgeBundle.getMethodName();
            this.mJsBridges.put(tag, jsBridgeBundle);
        }
    }

    public String getWebTitle() {
        return mTitle == null ? "" : mTitle;
    }

    /**
     * 下面部分为管理用户交互的监听动作
     *
     * @param ev
     * @param view
     * @return
     */
    // TBS: Do not use @Override to avoid false calls
    public boolean tbs_dispatchTouchEvent(MotionEvent ev, View view) {
        boolean r = super.super_dispatchTouchEvent(ev);
        android.util.Log.d("Bran", "dispatchTouchEvent " + ev.getAction() + " " + r);
        return r;
    }

    // TBS: Do not use @Override to avoid false calls
    public boolean tbs_onInterceptTouchEvent(MotionEvent ev, View view) {
        boolean r = super.super_onInterceptTouchEvent(ev);
        return r;
    }

    public void tbs_onScrollChanged(int l, int t, int oldl, int oldt, View view) {
        super_onScrollChanged(l, t, oldl, oldt);
    }

    public void tbs_onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY, View view) {
//        if (getContext() instanceof RefreshActivity) {
//            if (this.tog == null) {
//                this.tog = (TextView) ((Activity) getContext()).findViewById(R.id.refreshText);
//                layoutParams = (RelativeLayout.LayoutParams) (this.tog.getLayoutParams());
//                this.refreshRela = (RelativeLayout) ((Activity) getContext()).findViewById(R.id.refreshPool);
//            }
//            if (isClampedY && !clampedY) {
//                this.reload();
//            }
//            if (clampedY) {
//                this.isClampedY = true;
//
//            } else {
//                this.isClampedY = false;
//            }
//        }
        super_onOverScrolled(scrollX, scrollY, clampedX, clampedY);
    }

    public void tbs_computeScroll(View view) {
        super_computeScroll();
    }

    public boolean tbs_overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX,
                                    int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent, View view) {
//        if (getContext() instanceof RefreshActivity) {
//            if (this.isClampedY) {
//                if ((refreshRela.getTop() + (-deltaY)) / 2 < 255) {
//                    this.tog.setAlpha((refreshRela.getTop() + (-deltaY)) / 2);
//                } else
//                    this.tog.setAlpha(255);
//                this.refreshRela.layout(refreshRela.getLeft(), refreshRela.getTop() + (-deltaY), refreshRela.getRight(),
//                        refreshRela.getBottom() + (-deltaY));
//                this.layout(this.getLeft(), this.getTop() + (-deltaY) / 2, this.getRight(),
//                        this.getBottom() + (-deltaY) / 2);
//            }
//        }
        return super_overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX,
                maxOverScrollY, isTouchEvent);
    }

    /**
     * 用于下拉刷新的时候使用暂时不用
     *
     * @param event
     * @param view
     * @return
     */
    public boolean tbs_onTouchEvent(MotionEvent event, View view) {
//        if (getContext() instanceof RefreshActivity) {
//            if (event.getAction() == MotionEvent.ACTION_UP && this.tog != null) {
//                this.isClampedY = false;
//                this.tog.setAlpha(0);
//                this.refreshRela.layout(refreshRela.getLeft(), 0, refreshRela.getRight(), refreshRela.getBottom());
//                this.layout(this.getLeft(), 0, this.getRight(), this.getBottom());
//            }
//
//        }
        return super_onTouchEvent(event);
    }

    public void setWebViewCallback(IWebView callback) {
        mSyWebViewCallback = callback;
    }

    @Override
    public String getTitle() {
        return mTitle;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        LogUtils.v("l " + l);
        LogUtils.v("t " + t);
        LogUtils.v("oldl " + oldl);
        LogUtils.v("oldt " + oldt);
    }

    /**
     * 初始化webView的设置参数
     */
    private void initWebViewSetting() {
        WebSettings settings = getSettings();
        //允许JS调用弹出Alert
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setAllowFileAccess(true);// 设置允许访问文件数据
        //只要本地有，无论是否过期，或者no-cache，都使用缓存中的数据
        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
//        settings.setCacheMode(WebSettings.LOAD_DEFAULT);

        //排版适应屏幕
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        settings.setLoadWithOverviewMode(true);
        settings.setPluginState(WebSettings.PluginState.ON_DEMAND);
        settings.setDatabaseEnabled(true);
        settings.setJavaScriptEnabled(true);
        //可任意比例缩放设置webview推荐使用的窗口。
        settings.setUseWideViewPort(true);
        settings.setSupportZoom(true);
        settings.setAppCacheEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setGeolocationEnabled(true);
        //隐藏缩放按钮
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        settings.setAppCacheMaxSize(Long.MAX_VALUE);
        //暂时关闭多窗口支持不会调用onCreateWindow
        settings.setSupportMultipleWindows(false);

        //添加加密后的UA字符串
        String userAgentString = settings.getUserAgentString();
        LogUtils.d("----------userAgentString " + userAgentString);
//        settings.setUserAgentString(userAgentString + ";" + getMD5UAString());
//        LogUtils.d("----------userAgentString result " + userAgentString + ";" + getMD5UAString());
        File file = new File(Environment.getExternalStorageDirectory().getPath() + "/cache", "/http");
        if (!file.exists()) {
            file.mkdirs();
        }
        settings.setAppCachePath(file.getPath());
        settings.setGeolocationDatabasePath(file.getPath());
        //尝试全屏效果
        settings.setPluginState(WebSettings.PluginState.ON);
//        settings.setPluginState(WebSettings.PluginState.ON_DEMAND);
        setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        setWebViewClient(mWebViewClient);
        setWebChromeClient(mWebChromeClient);
        setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                LogUtils.v("---------- onDownloadStart url " + url + " \ncontentLength " + contentLength);
                LogUtils.v("---------- onDownloadStart userAgent " + userAgent + " \ncontentDisposition " + contentDisposition);
                Uri uri = Uri.parse(url);
                String fileName = "";
                if (contentDisposition.contains("filename")) { //解析contentDisposition 格式为 contentDisposition attachment;filename="xxx.*"
                    String[] split = contentDisposition.split("filename=");
                    if (split[1].contains("\"")) {
                        String[] split2 = split[1].split("\"");
                        fileName = split2[1];
                    } else {
                        fileName = split[1];
                    }
                    LogUtils.v("---------- onDownloadStart fileName " + fileName);
                }
                LogUtils.v("---------- onDownloadStart mimetype " + mimetype + " \nuri " + uri.getPath());
                LogUtils.v("---------- onDownloadStart uri.getAuthority() " + uri.getScheme() + " \nuri " + uri.getScheme());
                LogUtils.v("---------- onDownloadStart uri.getQuery() " + uri.getUserInfo() + " \nuri " + uri.getUserInfo());
                mSyWebViewCallback.onDownloadStart(url, fileName, contentLength);
            }
        });
        if (Build.VERSION.SDK_INT > 19) {
            settings.setLoadsImagesAutomatically(true);
        } else {
            settings.setLoadsImagesAutomatically(false);
        }

        //长按文本
        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                LogUtils.v("long click");
                WebView.HitTestResult result = getHitTestResult();
                LogUtils.v("long click type " + result.getType());
                //如果是图片长按就下载保存
                if (result.getType() == HitTestResult.IMAGE_TYPE) {
                    String url = result.getExtra();
                    if (!url.startsWith("http")) {
                        return false;
                    }
                    if (mSyWebViewCallback != null) {
                        mSyWebViewCallback.onDownloadImage(url);
                        return true;
                    }
                }
                return false;
            }
        });
    }

    /**
     * 设置webView的监听等
     */
    private void initWebView() {
//        this.setWebViewClientExtension(new X5WebViewEventHandler(this));// 配置X5webview的事件处理
        this.setWebViewClient(mWebViewClient);
        this.setWebChromeClient(mWebChromeClient);
        //this.setWebChromeClient(chromeClient);
        //WebStorage webStorage = WebStorage.getInstance();
        this.getView().setClickable(true);
        this.getView().setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
    }

    /**
     * 获取MD5机密后的UA
     */
//    private String getMD5UAString() {
    //新增的设置站内的UA
//        StringBuilder uaStringBuilder = new StringBuilder();
//        uaStringBuilder.append("jsbrowser/");
//        uaStringBuilder.append(VersionUtils.getVersionString() + "/");
//        uaStringBuilder.append(JSApp.deviceId + "/");
//        //加密后的验证码 jisou+uuid+zhangyou
//        StringBuilder identifyingCode = new StringBuilder();
//        identifyingCode.append("jisou").append(JSApp.deviceId).append("zhangyou");
//        uaStringBuilder.append(MD5Utils.MD5Encode(identifyingCode.toString(), "UTF-8", true));
//        LogUtils.d("------------getMD5UAString " + uaStringBuilder.toString());
//        return uaStringBuilder.toString();
//    }

    private IWebView mSyWebViewCallback;

    /**
     * 当webchromeClient收到 web的prompt请求后进行拦截判断，用于调起本地android方法
     *
     * @param methodName 方法名称
     * @param blockName  区块名称
     * @return true ：调用成功 ； false ：调用失败
     */
    private boolean onJsPrompt(String methodName, String blockName) {
        String tag = SecurityJsBridgeBundle.BLOCK + blockName + "-" + SecurityJsBridgeBundle.METHOD + methodName;

        if (this.mJsBridges != null && this.mJsBridges.containsKey(tag)) {
            ((SecurityJsBridgeBundle) this.mJsBridges.get(tag)).onCallMethod();
            return true;
        } else {
            return false;
        }
    }

    /**
     * 判定当前的prompt消息是否为用于调用native方法的消息
     *
     * @param msg 消息名称
     * @return true 属于prompt消息方法的调用
     */
    private boolean isMsgPrompt(String msg) {
        if (msg != null && msg.startsWith(SecurityJsBridgeBundle.PROMPT_START_OFFSET)) {
            return true;
        } else {
            return false;
        }
    }
}

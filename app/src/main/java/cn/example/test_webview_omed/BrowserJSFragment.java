package cn.example.test_webview_omed;

import android.content.pm.ActivityInfo;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewStub;
import android.webkit.GeolocationPermissions;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tencent.smtt.sdk.CookieSyncManager;

import cn.example.test_webview_omed.biz.WebManager;
import cn.example.test_webview_omed.utils.AvoidDoubleClickListener;
import cn.example.test_webview_omed.utils.LogUtils;
import cn.example.test_webview_omed.utils.NetUtil;
import cn.example.test_webview_omed.views.IWebView;
import cn.example.test_webview_omed.views.JSWebView;
import cn.example.test_webview_omed.views.MultipleStateLayout;

/**
 * APP中的bug复原
 */
public class BrowserJSFragment extends BaseFragment implements IWebView {
    private static final String TAG = "BrowserJSFragment";
    /**
     * 作为一个浏览器的示例展示出来，采用android+web的模式
     */
    private JSWebView webView;

    /**
     * 网页的管理类
     */
    private WebManager mWebManager;

    /**
     * 展示网页模块的布局控件
     */
    private LinearLayout webPage;

    /**
     * 网页模块中停止加载的按钮控件（也是加载完成后刷新的按钮）
     */
    private ImageButton ibStop;

    /**
     * 网页模块中展示网页标题的控件
     */
    private TextView tvWebTitle;

    private EditText etUrl;
    /**
     * 网页模块中多种状态(错误，加载中，内容)下的布局控件
     */
    private MultipleStateLayout webLayout;


    //根控件view

    @Override
    protected int getLayoutView() {
        return R.layout.fg_home;
    }

    /**
     * 初始化网页界面的控件及控件的监听事件
     */
    private void initWebPage() {
        //网页显示的时候都修改底部的导航栏的状态
        mWebManager = new WebManager(mContext);
        ViewStub viewStubWeb = (ViewStub) rootView.findViewById(R.id.viewStub_web);
        webPage = (LinearLayout) viewStubWeb.inflate();
        webLayout = (MultipleStateLayout) rootView.findViewById(R.id.layout_web);
        tvWebTitle = (TextView) rootView.findViewById(R.id.tv_web_url);
        ibStop = (ImageButton) rootView.findViewById(R.id.ib_stop);
        ibStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (ibStop.getTag() == null) {
//                    return;
//                }
//                webView.reload();
                if (webView.canGoBack()) {
                    webView.goBack();
                }
            }
        });
    }

    public void loadUrl(String input, boolean... isReload) {
        if (webView == null) {
            initWebView();
        }
        webPage.setVisibility(View.VISIBLE);
        webView.setVisibility(View.VISIBLE);
        webView.loadUrl(mWebManager.checkUrl(input));
    }

    /**
     * 初始化网页控件
     */
    private void initWebView() {
        if (webLayout == null) {
            initWebPage();
        }
        webView = new JSWebView(mContext);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.setWebViewCallback(this);
        webView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });

        //添加webView
        Log.i(TAG, "initWebView: isLongClickable " + webView.isLongClickable());
        webView.setLongClickable(true);
        Log.i(TAG, "initWebView: isHapticFeedbackEnabled" + webView.isHapticFeedbackEnabled());
        webView.setHapticFeedbackEnabled(true);
        webLayout.setWebView(webView);
        //TBS自己的缓存
        CookieSyncManager.createInstance(getActivity());
        CookieSyncManager.getInstance().sync();
    }


    @Override
    protected void init() {
        initWebPage();
//        String url = "https://m.baidu.com/?from=844b&vit=fps#iact=wiseindex%2Ftabs%2Fnews%2Factivity%2Fnewsdetail%3D%257B%2522linkData%2522%253A%257B%2522name%2522%253A%2522iframe%252Fmib-iframe%2522%252C%2522id%2522%253A%2522feed%2522%252C%2522index%2522%253A0%252C%2522url%2522%253A%2522https%253A%252F%252Fm.baidu.com%252Ffeed%252Fdata%252Fvideoland%253Fpd%253Dwise%2526s_type%253Dvideo%2526vid%253D6638997211322204635%2526srchid%253D14b302bf5c51a30b%2522%252C%2522title%2522%253A%2522%25E4%25B8%2589%25E4%25B8%258D%25E7%259F%25A5%25E5%25BD%25B1%25E8%25A7%2586%2522%257D%257D";
        String url = "https://m.baidu.com";
        loadUrl(url);
    }

    @Override
    protected void initSet() {

    }

    @Override
    public void onUrlChanged(String url) {
        tvWebTitle.setText(url);
    }

    @Override
    public void onDownloadStart(String url, String fileName, long contentLength) {

    }

    @Override
    public void onReceivedTitle(String title) {
        LogUtils.v("---------- onReceivedTitle tvWebTitle" + title);
        if (title == null) {
            title = webView.getUrl();
        }
        tvWebTitle.setText(title);
    }

    @Override
    public void onReceiveError() {
        webLayout.setState(MultipleStateLayout.STATE_ERROR);
        View refreshTv = webLayout.getView(MultipleStateLayout.STATE_ERROR, R.id.tv_refresh);
        //点击刷新按钮刷新
        if (refreshTv != null) {
            refreshTv.setOnClickListener(new AvoidDoubleClickListener(1000) {
                @Override
                public void onNoDoubleClick(View view) {
                    //有网络的情况就显示加载界面并请求WebView重新加载
                    netState = NetUtil.getNetworkState(mContext);
                    if (netState != NetUtil.NETWORK_NONE) {
                        //有网络的情况
                        webLayout.setState(MultipleStateLayout.STATE_LOADING);
                        String url = webView.getUrl();
                        if (TextUtils.isEmpty(url) || url.equals("about:blank")) {
                            //如果回传的URL是空的就传递之前的URL
                            webView.loadUrl(url);
                        } else {
                            //清除当前网页的缓存
//                            webView.clearCache(true);
                            webView.reload();
                        }
                    } else {
//                        ToastUtil.showShortToast(mContext, "网络不给力");
                    }
                }
            });
        }
    }

    @Override
    public void onLoadStart() {
        webLayout.setState(MultipleStateLayout.STATE_CONTENT);
    }

    @Override
    public void onProgressChanged(int progress) {

    }

    @Override
    public void onLoadFinish(String url, String title, boolean isError) {
        //因为此处必定是加载完成100的情况，但是因为存在断网也会执行两次100所以加上这个判断
        if (NetUtil.isConnected(getActivity())) {
            if (isError) {
                webLayout.setState(MultipleStateLayout.STATE_ERROR);
            }
        } else {
            webLayout.setState(MultipleStateLayout.STATE_ERROR);
            //TBS的断网页面覆盖了
//            webPage.setVisibility(View.VISIBLE);
//            webView.setVisibility(View.GONE);
        }

        if (webPage.getVisibility() == View.GONE) {
            return;
        }
    }

    @Override
    public void onTitleChanged(String title) {

    }

    @Override
    public void onObtainLocate(String origin, GeolocationPermissions.Callback callback) {

    }

    @Override
    public void onLoadIntent(String url) {

    }

    @Override
    public void onDownloadImage(String url) {

    }

    @Override
    public void onShowCustomView() {
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    @Override
    public void onHideCustomView() {
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
}

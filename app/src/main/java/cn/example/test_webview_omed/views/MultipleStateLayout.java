package cn.example.test_webview_omed.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import cn.example.test_webview_omed.R;


/**
 * Created by WenMing on 2016/8/29.13:51
 * Introduction:包含三种网络状态的界面控件[加载,连接,错误]
 */
public class MultipleStateLayout extends FrameLayout {

    public static final int STATE_LOADING = 1;
    public static final int STATE_CONTENT = 2;
    public static final int STATE_ERROR = 3;

    private JSWebView webView;

    private int state = STATE_CONTENT;

    public MultipleStateLayout(Context context) {
        this(context, null);
    }

    public MultipleStateLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MultipleStateLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    /**
     * 设置正常加载后显示的WebView
     *
     * @param webView
     */
    public void setWebView(JSWebView webView) {
        this.webView = webView;
//        addView(webView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        addView(this.webView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    private ViewStub viewStubLoading;
    private ViewStub viewStubError;

    private ViewGroup loadingLayout;
    private RelativeLayout errorLayout;

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        viewStubLoading = (ViewStub) findViewById(R.id.state_loading_page);
        viewStubError = (ViewStub) findViewById(R.id.state_error_page);
    }


    public void setState(int state) {
        this.state = state;
        if (state == STATE_CONTENT) {
            contentPage();
        } else if (state == STATE_ERROR) {
            errorPage();
        } else {
            loadingPage();
        }
    }

    /**
     * 内容页面
     */
    private void contentPage() {
        webView.setVisibility(VISIBLE);
        if (loadingLayout != null) {
            loadingLayout.setVisibility(GONE);
        }
        if (errorLayout != null) {
            errorLayout.setVisibility(GONE);
        }
    }

    /**
     * 加载页
     */
    private void loadingPage() {
        if (loadingLayout == null) {
            loadingLayout = (ViewGroup) viewStubLoading.inflate();
        }
        loadingLayout.setVisibility(VISIBLE);
        if (errorLayout != null) {
            errorLayout.setVisibility(GONE);
        }
        if (webView != null) {
            webView.setVisibility(GONE);
        }
    }

    /**
     * 错误页
     */
    private void errorPage() {
        if (loadingLayout != null) {
            loadingLayout.setVisibility(GONE);
        }
        if (webView != null) {
            webView.setVisibility(GONE);
        }
        if (errorLayout == null) {
            errorLayout = (RelativeLayout) viewStubError.inflate();
        }
        errorLayout.setVisibility(VISIBLE);
    }

    public View getView(int state, int resId) {
        if (state == STATE_ERROR) {
            if (errorLayout != null) {
                return errorLayout.findViewById(resId);
            }
        }
        return null;
    }

    public int getState() {
        return state;
    }
}

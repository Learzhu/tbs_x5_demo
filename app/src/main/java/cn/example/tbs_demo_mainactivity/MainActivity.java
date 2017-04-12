//package cn.example.tbs_demo_mainactivity;
//
//// This two lines is just for app patch's compatibility.
//// Please do not modify!
//
//import android.annotation.TargetApi;
//import android.app.Activity;
//import android.app.AlarmManager;
//import android.app.AlertDialog;
//import android.app.PendingIntent;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.content.pm.ActivityInfo;
//import android.graphics.PixelFormat;
//import android.media.AudioManager;
//import android.net.ConnectivityManager;
//import android.net.NetworkInfo;
//import android.net.Uri;
//import android.os.Build;
//import android.os.Bundle;
//import android.os.Environment;
//import android.os.Handler;
//import android.os.Message;
//import android.text.Editable;
//import android.text.TextUtils;
//import android.text.TextWatcher;
//import android.util.Log;
//import android.view.KeyEvent;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.View.OnFocusChangeListener;
//import android.view.ViewGroup;
//import android.view.inputmethod.InputMethodManager;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.FrameLayout;
//import android.widget.ImageButton;
//import android.widget.LinearLayout;
//import android.widget.ProgressBar;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.tencent.mtt.MttTraceEvent;
//import com.tencent.smtt.export.external.DexLoader;
//import com.tencent.smtt.export.external.TbsCoreSettings;
//import com.tencent.smtt.export.external.extension.interfaces.IX5WebViewClientExtension;
//import com.tencent.smtt.export.external.extension.proxy.ProxyWebViewClientExtension;
//import com.tencent.smtt.export.external.interfaces.IX5WebChromeClient.CustomViewCallback;
//import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
//import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
//import com.tencent.smtt.sdk.CookieSyncManager;
//import com.tencent.smtt.sdk.DownloadListener;
//import com.tencent.smtt.sdk.QbSdk;
//import com.tencent.smtt.sdk.TbsDownloader;
//import com.tencent.smtt.sdk.TbsListener;
//import com.tencent.smtt.sdk.TbsShareManager;
//import com.tencent.smtt.sdk.ValueCallback;
//import com.tencent.smtt.sdk.WebChromeClient;
//import com.tencent.smtt.sdk.WebSettings;
//import com.tencent.smtt.sdk.WebSettings.LayoutAlgorithm;
//import com.tencent.smtt.sdk.WebView;
//import com.tencent.smtt.sdk.WebViewCallbackClient;
//import com.tencent.smtt.sdk.WebViewClient;
//import com.tencent.smtt.utils.TbsLog;
//import com.tencent.smtt.utils.TbsLogClient;
//import com.tencent.tbs.drawable.X5LogoDrawable;
//
//import org.json.JSONObject;
//
//import java.io.File;
//import java.net.MalformedURLException;
//import java.net.URL;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.Map;
//
//import cn.example.test_webview_omed.R;
//
//
//public class MainActivity extends Activity {
//	private TbsSdkWebView mWebView;
//	private ViewGroup mViewParent;
//
//	private ImageButton mBack;
//	private ImageButton mForward;
//
//	private Button mRefresh;
//	private Button mExit;
//
//	private ImageButton mHome;
//
//	private Button mTestProcesses;
//	private Button mTestWebviews;
//	private ImageButton mMore;
//	private Button mClearData;
//	private Button	mOpenFile;
//	private Button mOpenGame;
//	private Button      mAddIcon;
//	private Button      mIsIconExist;
//	private Button      mDebugTbs;
//	private Button      mDebugX5;
//	private Button 		mMiniQB;
//
//	private Button 		mFileReader;
//	private Button 		mMultiProcess;
//	private Button 		mClearDebugPlugin;
//
//	private int			mShowMiniQBCount = 0 ;
//
//	private Button mGo;
//	private EditText mUrl;
//
//	private ViewGroup mMenu;
//	private Intent mIntent;
//
////	private static final String mHomeUrl = "https://m.v.qq.com/x/cover/d/d2sguxf7w6gcbz2.html?vid=e03724282ts";// "http://info.3g.qq.com/g/s?sid=AZpCzPNaWxpN1bQXMKL0ap8l&aid=mobile_ss&id=digi_20140224021076&pos=digi_c&icfa=home_touch&iarea=93";
//	private static final String mHomeUrl = "https://m.v.qq.com/";
////	private static final String mHomeUrl = "file:///storage/emulated/0/1.html";
//	private static final String TAG = "TbsStudioClient";
//	private static final int MAX_LENGTH = 14;
//	private boolean mNeedTestPage = false;
//
//	private final int disable = 120;
//	private final int enable = 255;
//
//	private ProgressBar mPageLoadingProgressBar = null;
//
//	private final static String MIDPAGEEXTURL  ="mttbrowser://miniqb/ch=icon?";
//	public static final boolean LOG_VERBOSE = false;
//	private static final String MAIN_HOME_URL = "http://www.qq.com";
//
//	private static StringBuilder mWebContents = null;
//
//	static {
//		mWebContents = new StringBuilder();
//		mWebContents.append("<!DOCTYPE html><html><body>");
//		mWebContents.append("<a href=\"weixin://dl/officialaccounts\">weixin</a>");
//		mWebContents.append("</body></html>");
//	}
//
//	protected static final String LOGTAG = TAG;
//
//	protected static final String SCHEME_DEBUG_TBS = "http://debugtbs.qq.com";
//	protected static final String SCHEME_DEBUG_X5 = "http://debugx5.qq.com";
//
//	public ValueCallback<Uri> uploadFile;
//
//	private URL mIntentUrl;
//
//	private String mIntentUrlString;
//
//	private String midpageIconUrl = null;
//	private final static boolean IS_ENABLE_DEMO_LOG = false;
//
//    //是否需要TBS预初始化
//	private static final boolean SHOULD_PREINIT_TBS = false;
//
//	private TbsListener tbsListener = new TbsListener() {
//
//		@Override
//		public void onDownloadFinish(int errCode) {
//			TbsLog.d(TbsDownloader.LOGTAG, "[MainActivity] onDownloadFinish errCode="
//					+ errCode);
//		}
//
//		@Override
//		public void onInstallFinish(int errCode) {
//			TbsLog.d(TbsDownloader.LOGTAG, "[MainActivity] onInstallFinish errCode="
//					+ errCode);
//			if ((mViewParent != null)
//					&& (errCode == 200 || errCode == 220 || errCode == 221))
//			{
//				mViewParent.post(new DialogRunnable());
//			}
//		}
//
//		public void onCallBackErrorCode(int errCode,String info) {
//			// TODO Auto-generated method stub
//		}
//		@Override
//		public void onDownloadProgress(int progress) {
//
//		}
//	};
//	private Context mContext;
//
//	private class DialogRunnable implements Runnable
//	{
//		@Override
//		public void run ()
//		{
//			restartDialog();
//		}
//	}
//
//	private void restartDialog()
//	{
//		mPrivateHandler.sendEmptyMessageDelayed(MSG_RESTART_APP, 3000);
//
//        AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
//        dialog.setTitle("TBS内核");
//        dialog.setMessage("TBS安装成功，即将进行重启...");
//        AlertDialog dlg = dialog.create();
//        dlg.show();
//	}
//
//	private void restartApplication(int delay)
//	{
//		try {
//			Log.e(LOGTAG, LOGTAG + " is going to restartApplication...");
//			PendingIntent intent = PendingIntent.getActivity(this.getBaseContext(), 0, new Intent(getIntent()), getIntent().getFlags());
//			AlarmManager manager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
//			manager.set(AlarmManager.RTC, System.currentTimeMillis() + delay, intent);
//		}catch (Exception e){
//			e.printStackTrace();
//		}
//		System.exit(34377);
//	}
//
//	private Handler mPrivateHandler = new Handler() {
//		@Override
//		public void handleMessage(Message msg) {
//			switch (msg.what) {
//			case MSG_OPEN_TEST_URL:
//				if (!mNeedTestPage) {// 未开启页面测试
//					return;
//				}
//
//				if (mCurrentUrl > mUrlEndNum) {// 超过页面的最大测试数目
//					setLandScapeAndFullScreen(false);// 跑完之后，退出横屏和全屏
//					return;
//				}
//
//				// 构造新的url
//				String testUrl = "file:///sdcard/outputHtml/html/"
//						+ Integer.toString(mCurrentUrl) + ".html";
//				TbsLog.d("test", "handleMessage testUrl=" + testUrl);
//				// 访问新的url
//				if (mWebView != null) {
//					mWebView.loadUrl(testUrl);
//				}
//
//				mCurrentUrl++;
//				break;
//			case MSG_INIT_UI:
//				init();
//				break;
//
//			case MSG_RESTART_APP:
//				restartApplication(1000);
//				break;
//
//			case MSG_INIT_INSTALL_TBS:
//				Log.d(LOGTAG, "start MSG_INIT_INSTALL_TBS...");
//
//				//#if ${enable.installstatictbs}
//				// install
//				QbSdk.preinstallStaticTbs(mContext);
//
//				Log.d(LOGTAG, "before QbSdk.preInit...");
//
//				// init
//				QbSdk.preInit(mContext);
//
//				Log.d(LOGTAG, "after MSG_INIT_INSTALL_TBS...");
//				//#endif
//
//				break;
//
//			case MSG_CLEAR_ALL_CACHE: {
//
//				Log.d(LOGTAG, "handleMessage MSG_CLEAR_ALL_CACHE...");
//
//				QbSdk.clearAllWebViewCache(getApplicationContext(),true);
//				Log.d(LOGTAG, "handleMessage MSG_CLEAR_ALL_CACHE...");
//
//				sendEmptyMessageDelayed(MSG_CLEAR_ALL_CACHE, 1000);
//				break;
//			}
//
//			}
//			super.handleMessage(msg);
//		}
//	};
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		getWindow().setFormat(PixelFormat.TRANSLUCENT);
//		mContext = this;
//
//		HashMap<String, Object> map = new HashMap<String, Object>();
//		map.put(TbsCoreSettings.TBS_SETTINGS_USE_PRIVATE_CLASSLOADER, true);
//		QbSdk.initTbsSettings(map);
//
//		if (SHOULD_PREINIT_TBS) {
//			mPrivateHandler.sendEmptyMessage(MSG_INIT_INSTALL_TBS);// 安装静态打包的TBS内核
//		}
//
//		Log.e(LOGTAG, "onCreate...");
//		TbsLog.d(TAG, "onCreate - current default classLoader: " + this.getClassLoader());
//		TbsLog.d(TAG, "onCreate - current sdk classLoader: " + DexLoader.class.getClassLoader());
//
//		mIntent = getIntent();
//		if(mIntent.getData()!= null)
//			TbsLog.d(TAG, "onCreate: " + mIntent + ", " + mIntent.getData().toString());
//		else
//			TbsLog.d(TAG, "onCreate: " + mIntent + ", null" );
//		if (mIntent != null) {
//			try {
//				String intentUrl = mIntent.getData().toString();
//				if(intentUrl.startsWith(MIDPAGEEXTURL))
//					midpageIconUrl = intentUrl;
//				else
//				{
//					mIntentUrlString = intentUrl;
//					mIntentUrl = new URL(mIntent.getData().toString());
//				}
//			} catch (MalformedURLException e) {
//				e.printStackTrace();
//			} catch (NullPointerException e) {
//
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//		TbsLog.d(TAG, "onCreate: " + mIntent + ", mIntentUrl: " + mIntentUrl);
//		// 在条件满足时开启硬件加速
//		try {
//			if (Build.VERSION.SDK_INT >= 11) {
//				getWindow()
//						.setFlags(
//								android.view.WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
//								android.view.WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		/*getWindow().addFlags(
//				android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN);*/
//		setContentView(R.layout.activity_main);
//		MttTraceEvent.setTraceEnableFlag(false);
//		mViewParent = (ViewGroup) findViewById(R.id.webView1);
//
//		initBtnListenser();
//
//        // 延迟1.5s创建webview
//		mPrivateHandler.sendEmptyMessageDelayed(MSG_INIT_UI, 1000);
//
//		QbSdk.setTbsListener(tbsListener);
//
//		// monitor views
//		ViewServer.get(this).addWindow(this);
//
//		if (!TbsShareManager.isThirdPartyApp(this))
//		{
//			boolean needDownload = TbsDownloader.needDownload(this, TbsDownloader.DOWNLOAD_OVERSEA_TBS);
//			TbsLog.d(TbsDownloader.LOGTAG, "[MainActivity] onCreate  needDownload="
//					+ needDownload);
//
//			if (needDownload && isNetworkWifi(this)) {
//				TbsDownloader.startDownload(this);
//			}
//		}
//	}
//
//	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
//	private void changGoForwardButton(WebView view) {
//		try {
//			if (view.canGoBack())
//				mBack.setImageAlpha(enable);
//			else
//				mBack.setImageAlpha(disable);
//			if (view.canGoForward())
//				mForward.setImageAlpha(enable);
//			else
//				mForward.setImageAlpha(disable);
//			if (view.getUrl()!=null && view.getUrl().equalsIgnoreCase(mHomeUrl)) {
//				mHome.setImageAlpha(disable);
//				mHome.setEnabled(false);
//			} else {
//				mHome.setImageAlpha(enable);
//				mHome.setEnabled(true);
//			}
//		} catch (Throwable t) { }
//	}
//
//	private void initProgressBar() {
//		mPageLoadingProgressBar = (ProgressBar) findViewById(R.id.progressBar1);// new
//																				// ProgressBar(getApplicationContext(),
//																				// null,
//																				// android.R.attr.progressBarStyleHorizontal);
//		mPageLoadingProgressBar.setMax(100);
//		mPageLoadingProgressBar.setProgressDrawable(this.getResources()
//				.getDrawable(R.drawable.color_progressbar));
//	}
//
//	class CallbackClient implements WebViewCallbackClient {
//
//		@Override
//		public boolean onTouchEvent(MotionEvent event, View view) {
//			if (LOG_VERBOSE)
//			Log.e("CallbackClient", "MainActivity - CallbackClient -- onTouchEvent:" + event);
//			return mWebView.tbs_onTouchEvent(event, view);
//		}
//
//		@TargetApi(Build.VERSION_CODES.GINGERBREAD)
//		@Override
//		public boolean overScrollBy(int deltaX, int deltaY, int scrollX,
//				int scrollY, int scrollRangeX, int scrollRangeY,
//				int maxOverScrollX, int maxOverScrollY,
//				boolean isTouchEvent, View view) {
//			if (LOG_VERBOSE)
//			Log.e("CallbackClient", "MainActivity - CallbackClient -- overScrollBy");
//			return mWebView.tbs_overScrollBy(deltaX, deltaY, scrollX, scrollY,
//					scrollRangeX, scrollRangeY, maxOverScrollX, maxOverScrollY,
//					isTouchEvent, view);
//		}
//
//		@Override
//		public void computeScroll(View view) {
//			if (LOG_VERBOSE)
//			Log.e("CallbackClient", "MainActivity - CallbackClient -- computeScroll");
//			mWebView.tbs_computeScroll(view);
//		}
//
//		@TargetApi(Build.VERSION_CODES.GINGERBREAD)
//		@Override
//		public void onOverScrolled(int scrollX, int scrollY, boolean clampedX,
//				boolean clampedY, View view) {
//			if (LOG_VERBOSE)
//			Log.e("CallbackClient", "MainActivity - CallbackClient -- onOverScrolled");
//			mWebView.tbs_onOverScrolled(scrollX, scrollY, clampedX, clampedY, view);
//		}
//
//		@Override
//		public void onScrollChanged(int l, int t, int oldl, int oldt, View view) {
//			if (LOG_VERBOSE)
//				Log.e("CallbackClient", "MainActivity - CallbackClient -- onScrollChanged");
//			mWebView.tbs_onScrollChanged(l, t, oldl, oldt, view);
//		}
//
//		@Override
//		public boolean dispatchTouchEvent(MotionEvent ev, View view) {
//			if (LOG_VERBOSE)
//			Log.e("CallbackClient", "MainActivity - CallbackClient -- dispatchTouchEvent");
//			return mWebView.tbs_dispatchTouchEvent(ev, view);
//		}
//
//		@Override
//		public boolean onInterceptTouchEvent(MotionEvent ev, View view) {
//			if (LOG_VERBOSE)
//			Log.e("CallbackClient", "MainActivity - CallbackClient -- onInterceptTouchEvent");
//			return mWebView.tbs_onInterceptTouchEvent(ev, view);
//		}
//
//	};
//
//	private CallbackClient mCallbackClient = new CallbackClient();
//
//
//	private void init() {
//		// ========================================================
//		// 创建WebView
//		//mWebView = new DemoWebView(this);
//		mWebView = new TbsSdkWebView(this);
//
//		// clear cache test
//		//mTestHandler.sendEmptyMessageDelayed(MSG_CLEAR_ALL_CACHE, 5000);
//
//		Log.w(LOGTAG, "Current SDK_INT:" + Build.VERSION.SDK_INT);
//		Log.i(LOGTAG, "cl ctx: " + mContext.getClassLoader());
//		Log.i(LOGTAG, "cl current2: " + TbsLog.class.getClassLoader());
//
//		// set Callback client
//		mWebView.setWebViewCallbackClient(mCallbackClient);
//		mViewParent.addView(mWebView, new FrameLayout.LayoutParams(
//				FrameLayout.LayoutParams.FILL_PARENT,
//				FrameLayout.LayoutParams.FILL_PARENT));
//
//		initProgressBar();
//
//		// 设置Client
//		mWebView.setWebViewClient(mWebViewClient);
//		mWebView.setWebChromeClient(mWebChromeClient);
//		mWebView.setDownloadListener(mDownloadListener);
//
//		// 各种设置
//		Log.e(LOGTAG, "init - tbs sdk_version: " + WebView.getTbsSDKVersion(mContext));
//		if (isInX5Mode(mWebView)) {
//			Log.e(LOGTAG, "init - is in x5 mode! core_version: " + WebView.getTbsCoreVersion(mContext));
//			// mWebView.getX5WebViewExtension().getQQBrowserVersion());
//			mWebView.getX5WebViewExtension().setWebViewClientExtension(mWebViewClientExtension );
//			mWebView.getX5WebViewExtension().setAudioAutoPlayNotify(true);
//			mWebView.getX5WebViewExtension().invokeMiscMethod("someExtensionMethod", new Bundle());
//			mWebView.getX5WebViewExtension().setWebChromeClientExtension(SpecialHandle.getLocalWebChromeClientExtension(this));
//			Bundle bundle=new Bundle();
//			bundle.putBoolean("mode", true);
//			mWebView.getX5WebViewExtension().invokeMiscMethod("setIsViewSourceMode",bundle);
//
//			X5LogoDrawable topLogoDrawable = new X5LogoDrawable(mWebView, true, true, getResources());
//			mWebView.getX5WebViewExtension().setOverScrollParams(0, 0, 0, 0,
//					Integer.MAX_VALUE / 2, 0, null, topLogoDrawable, null);
//		} else {
//			Log.e(LOGTAG, "init - not in x5 mode! core_version: " + WebView.getTbsCoreVersion(mContext));
//		}
//
//		//if(WebView.getTbsCoreVersion(mContext) != 0 && !isInX5Mode(mWebView) ){
//		//	Log.e(LOGTAG, "init - can not load X5: " + WebView.getTbsCoreVersion(mContext));
//		//	System.exit(0);
//		//}
//
//		WebSettings webSetting = mWebView.getSettings();
//		webSetting.setJavaScriptEnabled(true);
//		webSetting.setAllowFileAccess(true);
//		webSetting.setLayoutAlgorithm(LayoutAlgorithm.NARROW_COLUMNS);
//		webSetting.setSupportZoom(true);
//		webSetting.setBuiltInZoomControls(true);
//		webSetting.setUseWideViewPort(true);
//		webSetting.setSupportMultipleWindows(false);
//		webSetting.setLoadWithOverviewMode(true);
//		webSetting.setAppCacheEnabled(true);
//		webSetting.setDatabaseEnabled(true);
//		webSetting.setDomStorageEnabled(true);
//		webSetting.setGeolocationEnabled(true);
//		webSetting.setAppCacheMaxSize(Long.MAX_VALUE);
//		webSetting.setAppCachePath(this.getDir("appcache", 0).getPath());
//		webSetting.setDatabasePath(this.getDir("databases", 0).getPath());
//		webSetting.setGeolocationDatabasePath(this.getDir("geolocation", 0)
//				.getPath());
//		if (mWebView.getX5WebViewExtension() != null) {
//			Bundle data = new Bundle();
//			data.putBoolean("standardFullScreen", false);//true表示标准全屏，会调起onShowCustomView()，false表示X5全屏；不设置默认false，
//
//			data.putBoolean("supportLiteWnd", false);//false：关闭小窗；true：开启小窗；不设置默认true，
//
//			mWebView.getX5WebViewExtension().invokeMiscMethod("setVideoParams", data);
//		}
//		// webSetting.setPageCacheCapacity(IX5WebSettings.DEFAULT_CACHE_CAPACITY);
//		webSetting.setPluginState(WebSettings.PluginState.ON_DEMAND);
//		webSetting.setRenderPriority(WebSettings.RenderPriority.HIGH);
//		webSetting.setDisplayZoomControls(false);
//		webSetting.setMediaPlaybackRequiresUserGesture(false);
//		CookieSyncManager.createInstance(this);
//		CookieSyncManager.getInstance().sync();
//
//		if(mIntentUrlString != null)
//		{
//			mWebView.loadUrl(mIntentUrlString);
//		}
//		else
//		{
//			mWebView.loadUrl(mHomeUrl);
//		}
//	}
//
//	private boolean isInX5Mode(TbsSdkWebView webview) {
//		return (webview != null && webview.getX5WebViewExtension() != null);
//	}
//
//	private WebViewClient mWebViewClient = new WebViewClient() {
//		@Override
//		public boolean shouldOverrideUrlLoading(WebView view, String url) {
//			if (SpecialHandle.shouldOverrideUrlLoading(view, url)) {
//				return true;
//			}
//			return false;
//		}
//
//		public void onReceivedHttpAuthRequest(WebView view,
//				com.tencent.smtt.export.external.interfaces.HttpAuthHandler handler, String host, String realm) {
//			SpecialHandle.onReceivedHttpAuthRequest(MainActivity.this, view, handler, host, realm);
//		};
//
//		@Override
//		public WebResourceResponse shouldInterceptRequest(WebView view,
//				WebResourceRequest request) {
//			// TODO Auto-generated method stub
//			if(IS_ENABLE_DEMO_LOG)
//				Log.e(LOGTAG, "request.getUrl().toString() is " + request.getUrl().toString());
//
//			return super.shouldInterceptRequest(view, request);
//		}
//
//		public void onPageStarted(WebView view, String url, android.graphics.Bitmap favicon) {
//			if (mPageLoadingProgressBar != null) {
//				mPageLoadingProgressBar.setVisibility(View.VISIBLE);
//			}
//		};
//
//		@Override
//		public void onPageFinished(WebView view, String url) {
//			super.onPageFinished(view, url);
//			moreMenuClose();
//			// mTestHandler.sendEmptyMessage(MSG_OPEN_TEST_URL);
//			mPrivateHandler.sendEmptyMessageDelayed(MSG_OPEN_TEST_URL, 5000);// 5s?
//			if (Build.VERSION.SDK_INT >= 16)
//				changGoForwardButton(view);
//			/* mWebView.showLog("test Log"); */
//
//			if (mPageLoadingProgressBar != null) {
//				mPageLoadingProgressBar.setVisibility(View.GONE);
//			}
//		}
//
//		@Override
//		public void onDetectedBlankScreen(String url, int status) {
//			TbsLog.d(TAG,  "demo onDetectedBlankScreen url="+url+" status="+status);
//		}
//
//	};
//	private WebChromeClient mWebChromeClient = new WebChromeClient() {
//
//		private CustomViewCallback	mVideoCallback;
//		private View	mVideoView;
//
//		@Override
//		public void onReceivedTitle(WebView view, String title) {
//		}
//		public void onShowCustomView(View view, CustomViewCallback callback) {
//			mVideoCallback = callback;
//			mVideoView  = view;
//			FrameLayout decorView= (FrameLayout) getWindow().getDecorView();
//			decorView.removeView(view);
//			decorView.addView(view);
// 		};
//		public void onHideCustomView() {
//			FrameLayout decorView= (FrameLayout) getWindow().getDecorView();
//			decorView.removeView(mVideoView);
//			mVideoCallback.onCustomViewHidden();
//		};
//
//		public void onGeolocationPermissionsShowPrompt(final String origin, final com.tencent.smtt.export.external.interfaces.GeolocationPermissionsCallback callback) {
//			SpecialHandle.onGeolocationPermissionsShowPrompt(origin, callback, MainActivity.this);
//		};
//
//		@Override
//		public void openFileChooser(ValueCallback<Uri> uploadFile,
//				String acceptType, String captureType) {
//			String message = "选择文件";
//			if (captureType.equalsIgnoreCase("*")) {
//				if (acceptType.startsWith("video")) {
//					message = "选择录像文件";
//				} else if (acceptType.startsWith("image")) {
//					message = "使用拍照文件";
//				} else if (acceptType.startsWith("audio")) {
//					message = "使用录音文件";
//				}
//			} else if (!TextUtils.isEmpty(acceptType)) {
//				message = "选择"+acceptType+"文件";
//			}
//			Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
//			MainActivity.this.uploadFile = uploadFile;
//			Intent i = new Intent(Intent.ACTION_GET_CONTENT);
//			i.addCategory(Intent.CATEGORY_OPENABLE);
//			i.setType("*/*");
//			MainActivity.this.startActivityForResult(
//					Intent.createChooser(
//							i,
//							getResources().getString(
//									R.string.choose_uploadfile)), 0);
//		}
//
//		@Override
//		public void onProgressChanged(WebView view, int newProgress) {
//			mPageLoadingProgressBar.setProgress(newProgress);
//			if (mPageLoadingProgressBar != null && newProgress >= 100) {
//				mPageLoadingProgressBar.setVisibility(View.GONE);
//			}
//			MainActivity.this.changGoForwardButton(view);
//		}
//	};
//	private DownloadListener mDownloadListener = new DownloadListener() {
//
//			@Override
//			public void onDownloadStart(String arg0, String arg1, String arg2,
//					String arg3, long arg4) {
//				TbsLog.d(TAG, "url: " + arg0);
//				new AlertDialog.Builder(MainActivity.this)
//						.setTitle("是否下载")
//						.setPositiveButton("yes",
//								new DialogInterface.OnClickListener() {
//
//									@Override
//									public void onClick(DialogInterface dialog,
//											int which) {
//										Toast.makeText(
//												MainActivity.this,
//												"fake message: i'll download...",
//												1000).show();
//									}
//								})
//						.setNegativeButton("no",
//								new DialogInterface.OnClickListener() {
//
//									@Override
//									public void onClick(DialogInterface dialog,
//											int which) {
//										// TODO Auto-generated method stub
//										Toast.makeText(
//												MainActivity.this,
//												"fake message: refuse download...",
//												1000).show();
//									}
//								})
//						.setOnCancelListener(
//								new DialogInterface.OnCancelListener() {
//
//									@Override
//									public void onCancel(DialogInterface dialog) {
//										// TODO Auto-generated method stub
//										Toast.makeText(
//												MainActivity.this,
//												"fake message: refuse download...",
//												1000).show();
//									}
//								}).show();
//			}
//		};
//
//	private IX5WebViewClientExtension mWebViewClientExtension = new ProxyWebViewClientExtension() {
//
//		public void onReceivedViewSource(String data) {
//			TbsLog.d("onReceivedViewSource", data);
//		};
//
//		@Override
//		public Object onMiscCallBack(String method,
//				Bundle bundle) {
//			if (method == "onSecurityLevelGot") {
//				Toast.makeText(
//						MainActivity.this,
//						"Security Level Check: \nit's level is "
//								+ bundle.getInt("level"), 1000)
//						.show();
//			}
//			else if(method == "notifyAutoAudioPlay")
//			{
//				String message1 = "当前网页有背景音乐，是否需要静音？";
//				new AlertDialog.Builder(MainActivity.this).setTitle("Test").setMessage(message1)
//						.setPositiveButton("继续播放", new DialogInterface.OnClickListener()
//						{
//							public void onClick(DialogInterface dialog, int which)
//							{
//								mWebView.getX5WebViewExtension().invokeMiscMethod("notifyWebCore", null);
//							}
//						}).setNegativeButton("静音", new DialogInterface.OnClickListener()
//						{
//							public void onClick(DialogInterface dialog, int which)
//							{
//								((AudioManager) MainActivity.this.getSystemService(Context.AUDIO_SERVICE))
//										.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
//								mWebView.getX5WebViewExtension().invokeMiscMethod("notifyWebCore", null);
//							}
//						}).setOnCancelListener(new DialogInterface.OnCancelListener()
//						{
//							public void onCancel(DialogInterface dialog)
//							{
//								mWebView.getX5WebViewExtension().invokeMiscMethod("notifyWebCore", null);
//							}
//						}).show();
//				return true;
//			}
//			else if(method == "shouldNotifyAutoAudioPlay")
//			{
//				return null;
//			}
//			return null;
//		}
//
//		@Override
//		public boolean onTouchEvent(MotionEvent event, View view) {
//			if (LOG_VERBOSE)
//			Log.e(LOGTAG, "ProxyWebViewClientExtension - onTouchEvent");
//			return mCallbackClient.onTouchEvent(event, view);
//		}
//
//		   // 1
//		public boolean onInterceptTouchEvent(MotionEvent ev, View view) {
//			return mCallbackClient.onInterceptTouchEvent(ev, view);
//		}
//
//		// 3
//		public boolean dispatchTouchEvent(MotionEvent ev, View view) {
//			return mCallbackClient.dispatchTouchEvent(ev, view);
//		}
//		// 4
//		public boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY,
//				                    int scrollRangeX, int scrollRangeY,
//				                    int maxOverScrollX, int maxOverScrollY,
//				                    boolean isTouchEvent, View view) {
//			return mCallbackClient.overScrollBy(deltaX, deltaY, scrollX, scrollY,
//					scrollRangeX, scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent, view);
//		}
//		// 5
//	    public void onScrollChanged(int l, int t, int oldl, int oldt, View view) {
//	    	mCallbackClient.onScrollChanged(l, t, oldl, oldt, view);
//		}
//	    // 6
//	    public void onOverScrolled(int scrollX, int scrollY, boolean clampedX,
//				boolean clampedY, View view) {
//	    	mCallbackClient.onOverScrolled(scrollX, scrollY, clampedX, clampedY, view);
//		}
//		// 7
//		public void computeScroll(View view) {
//			mCallbackClient.computeScroll(view);
//		}
//
//		public boolean notifyAutoAudioPlay(String url, final com.tencent.smtt.export.external.interfaces.JsResult result) {
//			AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//			builder.setTitle("提示");
//			builder.setMessage("音乐自动播放提示!");
//			builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//
//				@Override
//				public void onClick(DialogInterface dialog, int which) {
//					result.confirm();
//				}
//			});
//			builder.setNeutralButton("取消", new DialogInterface.OnClickListener() {
//
//				@Override
//				public void onClick(DialogInterface dialog, int which) {
//					result.cancel();
//				}
//			});
//			builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
//
//				@Override
//				public void onCancel(DialogInterface dialog) {
//					result.cancel();
//				}
//			});
//			builder.show();
//			return true;
//		};
//
//		@Override
//		public void onResponseReceived(WebResourceRequest request, WebResourceResponse response, int errorCode) {
//			super.onResponseReceived(request, response, errorCode);
//			if(request!=null)
//			{
//				Log.e(LOGTAG,"onResponseReceived request info: request.getUrl()="+request.getUrl().toString()+" request.getMethod()="+request.getMethod()+" mainResource="+request.isForMainFrame()+" errorCode="+errorCode);
//				Map<String,String> requestheaders = request.getRequestHeaders();
//				if(requestheaders!=null)
//				{
//					Iterator iter = requestheaders.entrySet().iterator();
//					while (iter.hasNext()) {
//						Map.Entry entry = (Map.Entry) iter.next();
//						Object key = entry.getKey();
//						Object val = entry.getValue();
//						Log.e(LOGTAG,"------request header---name="+(key!=null?key.toString():"null")+" : "+(val!=null?val.toString():"null"));
//					}
//				}else
//				{
//					Log.e(LOGTAG,"requestheader is null");
//				}
//
//
//			}
//			else
//			{
//				Log.e(LOGTAG,"request is null");
//			}
//
//
//			if(response!=null)
//			{
//				Log.e(LOGTAG,"onResponseReceived response info: mime="+response.getMimeType()+" encoding="+response.getEncoding()+" reasonPhrase="+response.getReasonPhrase()+" statusCode="+response.getStatusCode());
//				Map<String,String> responseheaders = response.getResponseHeaders();
//				if(responseheaders!=null)
//				{
//					Iterator iter = responseheaders.entrySet().iterator();
//					while (iter.hasNext()) {
//						Map.Entry entry = (Map.Entry) iter.next();
//						Object key = entry.getKey();
//						Object val = entry.getValue();
//						Log.e(LOGTAG,"----response header   "+(key!=null?key.toString():"null")+" : "+(val!=null?val.toString():"null"));
//					}
//				}else
//				{
//					Log.e(LOGTAG,"responseheader is null");
//				}
//			}
//			else
//			{
//				Log.e(LOGTAG,"response is null");
//			}
//
//
//		}
//
//		@Override
//		public Object onMiscCallBack(String method,
//				Bundle bundle, Object arg1, Object arg2,
//				Object arg3, Object arg4) {
//
//			if(method.equals("onReportResourceInfo")){
//				if(arg1!=null)
//				{
//					JSONObject performance = (JSONObject)arg1;
//					Log.e(LOGTAG,"sdkdemo onReportResourceInfo performance="+performance.toString());
//				}
//
//				if(arg2!=null)
//				{
//					WebResourceRequest request = (WebResourceRequest)arg2;
//					if(request!=null)
//					{
//						Log.e(LOGTAG,"sdkdemo onReportResourceInfo onResponseReceived request info: request.getUrl()="+request.getUrl().toString()+" request.getMethod()="+request.getMethod()+" mainResource="+request.isForMainFrame());
//						Map<String,String> requestheaders = request.getRequestHeaders();
//						if(requestheaders!=null)
//						{
//							Iterator iter = requestheaders.entrySet().iterator();
//							while (iter.hasNext()) {
//								Map.Entry entry = (Map.Entry) iter.next();
//								Object key = entry.getKey();
//								Object val = entry.getValue();
//								Log.e(LOGTAG,"sdkdemo onReportResourceInfo ------request header---name="+(key!=null?key.toString():"null")+" : "+(val!=null?val.toString():"null"));
//							}
//						}else
//						{
//							Log.e(LOGTAG,"sdkdemo onReportResourceInfo requestheader is null");
//						}
//
//
//					}
//					else
//					{
//						Log.e(LOGTAG,"sdkdemo onReportResourceInfo request is null");
//					}
//				}
//				if(arg3!=null)
//				{
//					WebResourceResponse response = (WebResourceResponse)arg3;
//					if(response!=null)
//					{
//						Log.e(LOGTAG,"sdkdemo onReportResourceInfo response info: mime="+response.getMimeType()+" encoding="+response.getEncoding()+" reasonPhrase="+response.getReasonPhrase()+" statusCode="+response.getStatusCode());
//						Map<String,String> responseheaders = response.getResponseHeaders();
//						if(responseheaders!=null)
//						{
//							Iterator iter = responseheaders.entrySet().iterator();
//							while (iter.hasNext()) {
//								Map.Entry entry = (Map.Entry) iter.next();
//								Object key = entry.getKey();
//								Object val = entry.getValue();
//								Log.e(LOGTAG,"sdkdemo onReportResourceInfo ----response header   "+(key!=null?key.toString():"null")+" : "+(val!=null?val.toString():"null"));
//							}
//						}else
//						{
//							Log.e(LOGTAG,"sdkdemo onReportResourceInfo responseheader is null");
//						}
//					}
//					else
//					{
//						Log.e(LOGTAG,"sdkdemo onReportResourceInfo response is null");
//					}
//				}
//
//				if(arg4!=null)
//				{
//					Integer error = (Integer)arg4;
//					Log.e(LOGTAG,"sdkdemo onReportResourceInfo error="+error.intValue());
//				}
//			}
//
//			return null;
//		}
//
//
//	};
//
//
//	private class LogRunnable implements Runnable
//	{
//		String mLog = null;
//
//		LogRunnable (String log)
//		{
//			mLog = log;
//		}
//
//		@Override
//		public void run ()
//		{
//			Toast.makeText(mViewParent.getContext(), mLog, Toast.LENGTH_SHORT).show();
//		}
//	}
//
//	private void moreMenuClose()
//	{
//		if (mMenu!=null && mMenu.getVisibility()==View.VISIBLE)
//		{
//			mMenu.setVisibility(View.GONE);
//			mMore.setImageDrawable(getResources().getDrawable(R.drawable.theme_toolbar_btn_menu_fg_normal));
//		}
//	}
//
//	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
//	private void initBtnListenser() {
//		mBack = (ImageButton) findViewById(R.id.btnBack1);
//		mForward = (ImageButton) findViewById(R.id.btnForward1);
//		mRefresh = (Button) findViewById(R.id.btnRefresh1);
//		mExit = (Button) findViewById(R.id.btnExit1);
//		mHome = (ImageButton) findViewById(R.id.btnHome1);
//		mTestProcesses = (Button) findViewById(R.id.btnTestProcesses1);
//		mTestWebviews = (Button) findViewById(R.id.btnTestWebviews1);
//		mGo = (Button) findViewById(R.id.btnGo1);
//		mUrl = (EditText) findViewById(R.id.editUrl1);
//		mMore = (ImageButton) findViewById(R.id.btnMore);
//		mMenu = (ViewGroup) findViewById(R.id.menuMore);
//
//		mClearData = (Button) findViewById(R.id.btnClearData);
//		mOpenFile = (Button) findViewById(R.id.btnOpenFile);
//		mOpenGame = (Button) findViewById(R.id.btnOpenGame);
//
//		mAddIcon = (Button) findViewById(R.id.btnAddIcon);
//		mIsIconExist = (Button) findViewById(R.id.btnIsIconExit);
//		mDebugTbs = (Button) findViewById(R.id.btnDebugTbs);
//		mDebugX5 = (Button) findViewById(R.id.btnDebugX5);
//		mMiniQB = (Button) findViewById(R.id.btnMiniQB);
//
//		mFileReader = (Button) findViewById(R.id.btnFileReader);
//		mMultiProcess = (Button) findViewById(R.id.btnMultiProcess);
//		mClearDebugPlugin = (Button) findViewById(R.id.btnClearDebugPlugin);
//
//		if (Build.VERSION.SDK_INT >= 16)
//		{
//			mBack.setImageAlpha(disable);
//			mForward.setImageAlpha(disable);
//			mHome.setImageAlpha(disable);
//		}
//		mHome.setEnabled(false);
//
//		// 显示log的TextView
////		if (TbsConfig.TBS_SCREEN_LOG) {
//			final TextView logView = (TextView) findViewById(R.id.logView1);
////			final Button closeLogView = (Button) findViewById(R.id.closeLogView1);
//			TbsLog.setLogView(logView);
//
//
//			QbSdk.setTbsLogClient(new TbsLogClient(mContext){
//
//				public void showLog(String log) {
//					if (mViewParent != null)
//					{
//						mViewParent.post(new LogRunnable(log));
//					}
//				}
//
//				public void i(String tag, String msg) {
//					Log.i(tag, msg);
//				}
//
//				public void e(String tag, String msg) {
//					Log.e(tag, msg);
//				}
//
//				public void w(String tag, String msg) {
//					Log.w(tag, msg);
//				}
//
//				public void d(String tag, String msg) {
//					Log.d(tag, msg);
//				}
//
//				public void v(String tag, String msg) {
//					Log.v(tag, msg);
//				}
//			});
////			logView.setMovementMethod(ScrollingMovementMethod.getInstance());
////			TbsLog.d(TAG, "mViewParent.getHeight()=" + mViewParent.getHeight(),
////					true);
////			TbsLog.d(TAG, "logView: " + logView.getLineHeight(), true);
////			closeLogView.setOnClickListener(new OnClickListener(){
////
////				@Override
////				public void onClick(View v) {
////					// TODO Auto-generated method stub
////					if (logView!=null)
////					{
////						logView.setVisibility(View.GONE);
////						closeLogView.setVisibility(View.GONE);
////					}
////				}
////
////			});
////
////		}
//
//		mBack.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				moreMenuClose();
//				if (mWebView != null && mWebView.canGoBack())
//					mWebView.goBack();
//			}
//		});
//
//		mForward.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				moreMenuClose();
//				if (mWebView != null && mWebView.canGoForward())
//					mWebView.goForward();
//			}
//		});
//
//		mRefresh.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				moreMenuClose();
//				MttTraceEvent.stopTrace();
//				if (mWebView != null)
//					mWebView.reload();
//			}
//		});
//
//		mExit.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//		        //@not_third_app_start
//				// 检查是否需要重启web进程，已便能够尽快使用x5内核
//				boolean TbsNeedReboot = getTbsNeedReboot();
//				if (TbsNeedReboot) {
//					// TODO 重启web进程
//					TbsLog.e("MainActivity", "TbsNeedReboot");
//				}
//		        //@not_third_app_end
//
//				android.os.Process.killProcess(android.os.Process.myPid());
//			}
//		});
//
//		mGo.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				moreMenuClose();
//				String url = mUrl.getText().toString();
//
//				if("clearqb".equals(url))
//				{
//					File publicDownload = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
//					File mFilePath = publicDownload;
//					try
//					{
//						mFilePath = new File(publicDownload, ".tbs");
//						if (!mFilePath.exists() && !mFilePath.mkdir())
//						{
//							mFilePath = publicDownload;
//						}
//
//						File qbFile = new File(mFilePath, "qb_silent.apk");
//						File qbTempFile = new File(mFilePath, "qb_silent.temp");
//						qbFile.delete();
//						qbTempFile.delete();
//					}
//					catch (Throwable e)
//					{
//						mFilePath = publicDownload;
//					}
//					mWebView.loadUrl(mWebView.getUrl());
//					Toast.makeText(MainActivity.this, "静默下载包清除成功", Toast.LENGTH_SHORT).show();
//					return;
//				}
//				if (url != null && !"".equals(url)) {
//					url = UrlUtils.resolvValidUrl(url);
//					if (url != null) {
//						mWebView.loadUrl(url);
//					}
//				}
//				mWebView.requestFocus();
//			}
//		});
//
//		mMore.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				if (mMenu.getVisibility() == View.GONE)
//				{
//					mMenu.setVisibility(View.VISIBLE);
//                    mMore.setImageDrawable(getResources().getDrawable(R.drawable.theme_toolbar_btn_menu_fg_pressed));
//				}else{
//					mMenu.setVisibility(View.GONE);
//					mMore.setImageDrawable(getResources().getDrawable(R.drawable.theme_toolbar_btn_menu_fg_normal));
//				}
////				QbSdk.startQBWeb(mWebView.getContext(), "http://www.baidu.com");
////				QbSdk.startMiniQBToLoadUrl(mWebView.getContext(), "http://www.baidu.com", null);
//			}
//		});
//
//		mClearData.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				moreMenuClose();
//				QbSdk.clearAllWebViewCache(getApplicationContext(),true);
//				QbSdk.reset(getApplicationContext());
//			}
//		});
//
//		mOpenFile.setOnClickListener(new OnClickListener()
//		{
//
//			@Override
//			public void onClick(View v)
//			{
//				Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//				intent.setType("*/*");
//				intent.addCategory(Intent.CATEGORY_OPENABLE);
//				try
//				{
//					MainActivity.this.startActivityForResult(Intent.createChooser(intent, getResources().getString(R.string.choose_uploadfile)), 1);
//				}
//				catch (android.content.ActivityNotFoundException ex)
//				{
//					Toast.makeText(MainActivity.this, "没有文件管理器", Toast.LENGTH_LONG).show();
//				}
//			}
//		});
//
//		mAddIcon.setOnClickListener(new OnClickListener()
//		{
//
//			@Override
//			public void onClick(View v)
//			{
//				moreMenuClose();
//				boolean ret = QbSdk.createMiniQBShortCut(mWebView.getContext(), "http://www.baidu.com", "简版QB", getResources().getDrawable(R.drawable.adrbar_btn_pay));
//			}
//		});
//
//		mIsIconExist.setOnClickListener(new OnClickListener()
//		{
//
//			@Override
//			public void onClick(View v)
//			{
//
//				moreMenuClose();
//				boolean ret = QbSdk.isMiniQBShortCutExist(mWebView.getContext(), "http://www.baidu.com", "简版QB");
//				if(ret == true)
//					Toast.makeText(MainActivity.this, "简版QB桌面快捷方式已存在！", Toast.LENGTH_LONG).show();
//				else
//					Toast.makeText(MainActivity.this, "简版QB桌面快捷方式不存在！", Toast.LENGTH_LONG).show();
//
//			}
//		});
//
//		// Open debugtbs page
//		mDebugTbs.setOnClickListener(new OnClickListener()
//		{
//			public void onClick(View v)
//			{
//				moreMenuClose();
//				mWebView.loadUrl(SCHEME_DEBUG_TBS);
//			}
//		});
//
//		// Open debugx5 page
//		mDebugX5.setOnClickListener(new OnClickListener()
//		{
//			public void onClick(View v)
//			{
//				moreMenuClose();
//				mWebView.loadUrl(SCHEME_DEBUG_X5);
//			}
//		});
//
//		mMiniQB.setOnClickListener(new OnClickListener() {
//			public void onClick(View arg0) {
//				moreMenuClose();
//
//				HashMap<String, String> params =  new HashMap<String, String>();
//				params.put("entryId", "2");
//				params.put("allowAutoDestory", "true");
//				QbSdk.startQbOrMiniQBToLoadUrl(mWebView.getContext(), "http://www.qq.com", params,null);
//
//				if(mShowMiniQBCount++ == 2)
//				{
//					String extraMessage = WebView.getCrashExtraMessage(arg0.getContext()) ;
//					Log.e("yangyk", extraMessage) ;
//				}
//			}
//		});
//
//		mFileReader.setOnClickListener(new OnClickListener() {
//			public void onClick(View view) {
//				moreMenuClose();
//
//				String sdcard = Environment.getExternalStorageDirectory().getAbsolutePath() ;
//				FileReaderDialog readerDialog = new FileReaderDialog(MainActivity.this, sdcard);
//				readerDialog.refreshFileList();
//				readerDialog.show();
//			}
//		});
//
//		mMultiProcess.setOnClickListener(new OnClickListener() {
//			public void onClick(View view) {
//				moreMenuClose();
//
//				Intent intent = new Intent(view.getContext(), MultiProcessActivity.class);
//				startActivity(intent);
//			}
//		});
//
//		mClearDebugPlugin.setOnClickListener(new OnClickListener() {
//			public void onClick(View arg0) {
//				moreMenuClose();
//
//				String DEBUG_PATH_HEAD = mContext.getDir("tbs", Context.MODE_PRIVATE).getAbsolutePath() + File.separator + "debug";
//				String filePath = DEBUG_PATH_HEAD + File.separator
//						+ "debug" + File.separator + "DebugPlugin.apk";
//
//				File file = new File(filePath);
//				file.delete();
//			}
//		});
//
//
//
//		mUrl.setOnFocusChangeListener(new OnFocusChangeListener() {
//
//			@Override
//			public void onFocusChange(View v, boolean hasFocus) {
//				moreMenuClose();
//				if (hasFocus) {
//					mGo.setVisibility(View.VISIBLE);
//					mRefresh.setVisibility(View.GONE);
//					if (null == mWebView.getUrl()) return;
//					if (mWebView.getUrl().equalsIgnoreCase(mHomeUrl)) {
//						mUrl.setText("");
//						mGo.setText("取消");
//						mGo.setTextColor(0X6F0F0F0F);
//					} else {
//						mUrl.setText(mWebView.getUrl());
//						mUrl.setSelectAllOnFocus(true);
//						mUrl.selectAll();
//						mGo.setText("进入");
//						mGo.setTextColor(0X6F0000CD);
//					}
//				} else {
//					mGo.setVisibility(View.GONE);
//					mRefresh.setVisibility(View.VISIBLE);
//					String title = mWebView.getTitle();
//					if (title != null && title.length() > MAX_LENGTH)
//						mUrl.setText(title.subSequence(0, MAX_LENGTH) + "...");
//					else
//						mUrl.setText(title);
//					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//					imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
//				}
//			}
//
//		});
//
//		mUrl.addTextChangedListener(new TextWatcher() {
//
//			@Override
//			public void afterTextChanged(Editable s) {
//				// TODO Auto-generated method stub
//
//				String url = null;
//				if (mUrl.getText() != null) {
//					url = mUrl.getText().toString();
//				}
//
//				if (url == null
//						|| mUrl.getText().toString().equalsIgnoreCase("")
//						|| (!"clearqb".equals(url) && UrlUtils.resolvValidUrl(url) == null)) {
//					mGo.setText("取消");
//					mGo.setTextColor(0X6F0F0F0F);
//				} else {
//					mGo.setText("进入");
//					mGo.setTextColor(0X6F0000CD);
//				}
//			}
//
//			@Override
//			public void beforeTextChanged(CharSequence s, int start, int count,
//					int after) {
//				// TODO Auto-generated method stub
//
//			}
//
//			@Override
//			public void onTextChanged(CharSequence s, int start, int before,
//					int count) {
//				// TODO Auto-generated method stub
//
//			}
//
//		});
//
//		mHome.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				moreMenuClose();
//				if (mWebView != null)
//					mWebView.loadUrl(mHomeUrl);
//			}
//		});
//
//		mTestProcesses.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				moreMenuClose();
//				// 以新进程方式启动另外一个Activity，模拟多进程测试场景
//				Intent intent = new Intent();
//				intent.setClass(MainActivity.this, ProcessActivity.class);
//				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//				getApplicationContext().startActivity(intent);
//			}
//
//		});
//
//		mTestWebviews.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				moreMenuClose();
//				Intent intent = new Intent();
//				intent.setClass(MainActivity.this, WebviewsActivity.class);
//				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//				getApplicationContext().startActivity(intent);
//			}
//
//		});
//	}
//
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		boolean ret = super.onCreateOptionsMenu(menu);
//		getMenuInflater().inflate(R.menu.main, menu);
//		return ret;
//	}
//
//	private enum TEST_ENUM_INDEX {
//		CONST_IDX_SOLAR, CONST_IDX_PAGE_SOLAR, CONST_IDX_NO_PIC, CONST_IDX_PAGE_PC_2_PHONE, CONST_IDX_FIT_FIRST, CONST_IDX_PRE_READ
//	};
//
//	boolean[] m_selected = new boolean[] { true, true, true, true, false,
//			false, true };
//
//	private void testSettings() {
//		final String[] items = new String[] { "日间模式", "页面入夜间模式", "无图模式",
//				"电脑页转手机版面", "优先访问简版", "预读" };
//
//		// AlertDialog.Builder(this);
//		new AlertDialog.Builder(MainActivity.this)
//				.setTitle("浏览器配置开关")
//				.setMultiChoiceItems(items, m_selected,
//						new DialogInterface.OnMultiChoiceClickListener() {
//							@Override
//							public void onClick(DialogInterface dialog,
//									int which, boolean isChecked) {
//								// TODO Auto-generated method stub
//								m_selected[which] = isChecked;
//							}
//						})
//				.setPositiveButton("测试", new DialogInterface.OnClickListener() {
//
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						// TODO Auto-generated method stub
//						if (mWebView.getX5WebViewExtension() != null) {
//							// mWebView.getSettingsExtension().setDayOrNight(m_selected[TEST_ENUM_INDEX.CONST_IDX_SOLAR.ordinal()]);
//							mWebView.getSettingsExtension()
//									.setPageSolarEnableFlag(
//											m_selected[TEST_ENUM_INDEX.CONST_IDX_PAGE_SOLAR
//													.ordinal()]);
//							mWebView.getSettings()
//									.setLoadsImagesAutomatically(
//											!m_selected[TEST_ENUM_INDEX.CONST_IDX_NO_PIC
//													.ordinal()]);
//							mWebView.getSettingsExtension()
//									.setPreFectch(
//											m_selected[TEST_ENUM_INDEX.CONST_IDX_PRE_READ
//													.ordinal()]);
//							mWebView.getSettingsExtension()
//									.setWapSitePreferred(
//											m_selected[TEST_ENUM_INDEX.CONST_IDX_FIT_FIRST
//													.ordinal()]);
//							mWebView.getSettingsExtension()
//									.setFitScreen(
//											m_selected[TEST_ENUM_INDEX.CONST_IDX_PAGE_PC_2_PHONE
//													.ordinal()]);
//							// mWebView.getSettingsExtension().setPageSolarEnableFlag(selected[TEST_ENUM_INDEX.CONST_IDX_CLOUND.ordinal()]);
//							/*
//							 * mWebView.getSettingsExtension().
//							 * setPageSolarEnableFlag
//							 * (selected[TEST_ENUM_INDEX.CONST_IDX_FULL_SCREEN
//							 * .ordinal()]); mWebView.getSettingsExtension().
//							 * setPageSolarEnableFlag
//							 * (selected[TEST_ENUM_INDEX.CONST_IDX_CLOUND
//							 * .ordinal()]); mWebView.getSettingsExtension().
//							 * setPageSolarEnableFlag
//							 * (selected[TEST_ENUM_INDEX.CONST_IDX_PAGE_PC_2_PHONE
//							 * .ordinal()]); mWebView.getSettingsExtension().
//							 * setPageSolarEnableFlag
//							 * (selected[TEST_ENUM_INDEX.CONST_IDX_FIT_FIRST
//							 * .ordinal()]); mWebView.getSettingsExtension().
//							 * setPageSolarEnableFlag
//							 * (selected[TEST_ENUM_INDEX.CONST_IDX_VERTICAL_ONLY
//							 * .ordinal()]);
//							 */
//							// mWebView.reload();
//						}
//
//						mWebView.setDayOrNight(m_selected[TEST_ENUM_INDEX.CONST_IDX_SOLAR
//								.ordinal()]);
//
//					}
//				}).setNegativeButton("取消", null).show();
//
//	}
//
//	private enum TEST_ENUM_FONTSIZE {
//		FONT_SIZE_SMALLEST, FONT_SIZE_SMALLER, FONT_SIZE_NORMAL, FONT_SIZE_LARGER, FONT_SIZE_LARGEST
//	};
//
//	private TEST_ENUM_FONTSIZE m_font_index = TEST_ENUM_FONTSIZE.FONT_SIZE_NORMAL;
//
//	private void testSettingsFontSize() {
//		final String[] items = new String[] { "超小号", "小号", "中号", "大号", "超大号" };
//
//		// AlertDialog.Builder(this);
//		new AlertDialog.Builder(MainActivity.this)
//				.setTitle("设置字体大小")
//				.setSingleChoiceItems(items, m_font_index.ordinal(),
//						new DialogInterface.OnClickListener() {
//
//							@Override
//							public void onClick(DialogInterface dialog,
//									int which) {
//								// TODO Auto-generated method stub
//								m_font_index = TEST_ENUM_FONTSIZE.values()[which];
//							}
//						})
//				.setPositiveButton("测试", new DialogInterface.OnClickListener() {
//
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						// TODO Auto-generated method stub
//						switch (m_font_index) {
//						case FONT_SIZE_SMALLEST:
//							mWebView.getSettings().setTextSize(
//									WebSettings.TextSize.SMALLEST);
//							break;
//						case FONT_SIZE_SMALLER:
//							mWebView.getSettings().setTextSize(
//									WebSettings.TextSize.SMALLER);
//							break;
//						case FONT_SIZE_NORMAL:
//							mWebView.getSettings().setTextSize(
//									WebSettings.TextSize.NORMAL);
//							break;
//						case FONT_SIZE_LARGER:
//							mWebView.getSettings().setTextSize(
//									WebSettings.TextSize.LARGER);
//							break;
//						case FONT_SIZE_LARGEST:
//							mWebView.getSettings().setTextSize(
//									WebSettings.TextSize.LARGEST);
//							break;
//						}
//						mWebView.reload();
//
//					}
//				}).setNegativeButton("取消", null).show();
//	}
//
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		// TODO Auto-generated method stub
//		super.onOptionsItemSelected(item);
//		int id = item.getItemId();
//		// To avoid "case expressions must be constant expressions" errors.
//		if (R.id.navigation == id) {
//			testSettings();
//		} else if (R.id.action_settings_fontsize == id) {
//			testSettingsFontSize();
//		} else {
//			// TODO:...
//		}
//		return true;
//	}
//
//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//
//		if (keyCode == KeyEvent.KEYCODE_BACK) {
//			if (mWebView != null && mWebView.canGoBack()) {
//				mWebView.goBack();
//				if (Build.VERSION.SDK_INT >= 16)
//					changGoForwardButton(mWebView);
//				return true;
//			} else
//				return super.onKeyDown(keyCode, event);
//		}
//		return super.onKeyDown(keyCode, event);
//	}
//
//	@Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		TbsLog.d(TAG, "onActivityResult, requestCode:" + requestCode
//				+ ",resultCode:" + resultCode);
//
//		if (resultCode == RESULT_OK) {
//			switch (requestCode) {
//			case 0:
//				if (null != uploadFile) {
//					Uri result = data == null || resultCode != RESULT_OK ? null
//							: data.getData();
//					uploadFile.onReceiveValue(result);
//					uploadFile = null;
//				}
//				break;
//			case 1: //打开文档
//
//                //#if ${enable.tbs_reader}
//				Uri uri = data.getData();
//				String path = uri.getPath();
//
//				if (path != null) {
//					Intent intent = new Intent();
//
//					intent.putExtra("filePath", path);
//					intent.setClass(MainActivity.this, ReaderActivity.class);
//					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//					this.getApplicationContext().startActivity(intent);
//				} else {
//					Toast.makeText(MainActivity.this, "路径错误", Toast.LENGTH_LONG).show();
//				}
//				//#endif
//				break;
//			default:
//				break;
//			}
//		}
//		else if (resultCode == RESULT_CANCELED) {
//			if (null != uploadFile) {
//				uploadFile.onReceiveValue(null);
//				uploadFile = null;
//			}
//
//		}
//
//	}
//
//	@Override
//	protected void onNewIntent(Intent intent) {
//		TbsLog.d(TAG, "onNewIntent");
//		if (intent == null || mWebView == null || intent.getData() == null)
//			return;
//		//mWebView.loadUrl(intent.getData().toString());
//		String from = null;
//		if (intent.hasExtra("from")){
//			from = intent.getStringExtra("from");
//		}
//		QbSdk.intentDispatch(mWebView, intent, intent.getData().toString(), from);
//	}
//
//	@Override
//	protected void onDestroy() {
//
//		if (mWebView != null)
//			mWebView.destroy();
//		super.onDestroy();
//		ViewServer.get(this).removeWindow(this);
//
//	}
//
//	@Override
//	public void onResume() {
//		super.onResume();
//		ViewServer.get(this).setFocusedWindow(this);
//	}
//
//	public static final int MSG_OPEN_TEST_URL = 0;
//	public static final int MSG_INIT_UI = 1;
//	protected static final int MSG_CLEAR_ALL_CACHE = 200;
//	protected static final int MSG_RESTART_APP      = 400;
//	protected static final int MSG_INIT_INSTALL_TBS = 300;
//
//	private final int mUrlStartNum = 0;// url起始编号
//	private final int mUrlEndNum = 108;// url结束编号 10;//
//	private int mCurrentUrl = mUrlStartNum;// url当前编号
//
//
//	/**
//	 * 设置全屏
//	 *
//	 * @param flag
//	 */
//	private void setLandScapeAndFullScreen(boolean flag) {
//		LinearLayout navigation = (LinearLayout) findViewById(R.id.navigation);
//		LinearLayout toolbar = (LinearLayout) findViewById(R.id.toolbar1);
//		if (flag) {
//			navigation.setVisibility(View.GONE);
//			toolbar.setVisibility(View.GONE);
//			this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//		} else {
//			navigation.setVisibility(View.VISIBLE);
//			toolbar.setVisibility(View.VISIBLE);
//			this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//		}
//	}
//
//	private boolean isNetworkWifi(Context context) {
//		ConnectivityManager connectivityManager = (ConnectivityManager) context
//				.getSystemService(Context.CONNECTIVITY_SERVICE);
//		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
//		if (activeNetInfo != null
//				&& activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
//			return true;
//		}
//		return false;
//	}
//
//	/**
//	 * 返回是否需要重启web进程
//	 */
//	//@not_third_app_start
//	private boolean getTbsNeedReboot() {
//		if (mWebView == null)
//			return false;
//		else
//			return mWebView.getTbsNeedReboot();
//	}
//	//@not_third_app_end
//
//	@Override
//	public boolean onTouchEvent(MotionEvent event) {
//		// TODO Auto-generated method stub
//		return false;
//	}
//}

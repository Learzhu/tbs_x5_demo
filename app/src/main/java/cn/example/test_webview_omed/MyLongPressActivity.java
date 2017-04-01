package cn.example.test_webview_omed;

import android.app.Activity;
import android.os.Bundle;

import cn.example.test_webview_omed.utils.LongPressListenerWrapper;
import cn.example.test_webview_omed.utils.X5WebView;

public class MyLongPressActivity extends Activity{
	
	/**
	 * 由于X5内核的webview有自己定制的一套长按处理流程
	 * 因此，第三方开发者如果需要定制自己的长按事件，有需要利用部分X5的长按事件
	 * 可以采用以下的做法
	 * 
	 * @author yuanhaizhou
	 * @see LongPressListenerWrapper
	 * 
	 */

	private X5WebView webView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.filechooser_layout);
		
		
		webView=(X5WebView) findViewById(R.id.web_filechooser);
		webView.loadUrl("file:///android_asset/webpage/hitTestResult.html");
		
		webView.setOnLongClickListener(new LongPressListenerWrapper(webView, this));//do long press listener

	}

}

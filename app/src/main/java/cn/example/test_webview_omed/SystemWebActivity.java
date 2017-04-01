package cn.example.test_webview_omed;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.tencent.smtt.sdk.WebView;

import cn.example.test_webview_omed.utils.SystemWebView;

public class SystemWebActivity extends Activity{
	
	

	
	private SystemWebView webView;
	private static final String mHomeUrl = "file:///android_asset/webpage/creat_new_window.html";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fullscreen_sys_web);
		webView=(SystemWebView) findViewById(R.id.full_sys_web_webview);
		webView.loadUrl(mHomeUrl);
		webView.addJavascriptInterface(new Object(){
			
			@JavascriptInterface
			public void alertMsg(String msg){
				Toast.makeText(SystemWebActivity.this,msg, Toast.LENGTH_SHORT).show();
				
			}
		
		},"Android");
		
		webView.setOnLongClickListener(new View.OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				android.webkit.WebView.HitTestResult hitTestResult=SystemWebActivity.this.webView.getHitTestResult();
				final String path=hitTestResult.getExtra();
				switch(hitTestResult.getType()){
				case WebView.HitTestResult.IMAGE_TYPE://获取点击的标签是否为图片
					Toast.makeText(SystemWebActivity.this, "当前选定的图片的URL是"+ path, Toast.LENGTH_LONG).show();
				case WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE://获取点击的标签是否为图片
					Toast.makeText(SystemWebActivity.this, "当前选定的图片的URL是"+ path, Toast.LENGTH_LONG).show();
//					
					break;
				}
				return false;
			}
		});
		
	}
	
	

}

package cn.example.test_webview_omed;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebChromeClient;

import cn.example.test_webview_omed.utils.X5WebView;

//import com.tencent.smtt.sdk.WebIconDatabase;
//import com.tencent.smtt.sdk.WebStorage;

public class FilechooserActivity extends Activity{
	
	
	/**
	 *用于展示在web端<input type=text>的标签被选择之后，文件选择器的制作和生成
	 */
	
	private X5WebView webView;
	private ValueCallback<Uri> uploadFile;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.filechooser_layout);

		
		webView=(X5WebView)findViewById(R.id.web_filechooser);
		
		webView.setWebChromeClient(new WebChromeClient(){
			@Override
			public void openFileChooser(ValueCallback<Uri> uploadFile, String acceptType, String captureType) {
				
				FilechooserActivity.this.uploadFile = uploadFile;
				Intent i = new Intent(Intent.ACTION_GET_CONTENT);
				i.addCategory(Intent.CATEGORY_OPENABLE);
				i.setType("*/*");
				startActivityForResult(Intent.createChooser(i, "test"), 0);
			}
		});
		
		webView.loadUrl("file:///android_asset/webpage/fileChooser.html");
	 
		this.initBtn();
		
//		Log.i("midPageView", "miniQb ret is"+QbSdk.startMiniQBToLoadUrl(this, "http:\\www.baidu.com", null));


	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);		
		
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case 0:
				if (null != uploadFile) {
					Uri result = data == null || resultCode != RESULT_OK ? null
							: data.getData();
					uploadFile.onReceiveValue(result);
					uploadFile = null;
				}
				break;
			case 1: 
				
				Uri uri = data.getData();
				String path = uri.getPath();

				
				break;
			default:
				break;
			}
		} 
		else if (resultCode == RESULT_CANCELED) {
			if (null != uploadFile) {
				uploadFile.onReceiveValue(null);
				uploadFile = null;
			}

		}
	}
	
	
	private void initBtn(){
		Button btnFlush=(Button) findViewById(R.id.bt_filechooser_flush);
		btnFlush.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				webView.reload();
				Log.i("yuanhaizhou", "webview use night mode!");
				//webView.setDayOrNight(false);
			}
		});
		
		Button btnBackForward=(Button) findViewById(R.id.bt_filechooser_back);
		btnBackForward.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				webView.goBack();
			}
		});
		
		Button btnHome=(Button) findViewById(R.id.bt_filechooser_home);
		btnHome.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				webView.loadUrl("file:///android_asset/webpage/fileChooser.html");
			}
		});
		
	}

	
	
	//////////////////////////////////////////////////////////////
	/**
	 * 确保注销配置能够被释放
	 */
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if(this.webView!=null){
			webView.getSettings().setBuiltInZoomControls(true);
			webView.destroy();
		}
		super.onDestroy();
	}

	

}

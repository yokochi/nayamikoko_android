package com.nayamikoko;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

@SuppressLint({ "SetJavaScriptEnabled", "ShowToast" })
public class MainActivity extends Activity implements OnClickListener {
	
	private static final String TAG = "MainActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
    	Button back = (Button)findViewById(R.id.back_button);
    	back.setEnabled(false);
    	Button next = (Button)findViewById(R.id.next_button);
    	next.setEnabled(false);
    	ImageButton reload = (ImageButton)findViewById(R.id.reload_button);
    	reload.setOnClickListener(this);
    	back.setOnClickListener(this);
    	next.setOnClickListener(this);
		
		WebView web = (WebView)findViewById(R.id.web_view);
	    
		WebSettings settings = web.getSettings();
		settings.setBuiltInZoomControls(true);
		settings.setJavaScriptEnabled(true);
		settings.setLoadsImagesAutomatically(true);
		settings.setSupportZoom(true);
		settings.setLightTouchEnabled(true);
				
		
		web.setVerticalScrollbarOverlay(true);
		web.setHorizontalScrollbarOverlay(true);
		web.setWebChromeClient(new MyWebChromeClient());
		web.setWebViewClient(new MyWebViewClient());
		// preferences get url
		
		String url = getText(R.string.url).toString();
		web.loadUrl(url);
	}
	
    private void setProgress(boolean isVisible) {
    	View v = findViewById(R.id.main_linearLayout_progress);
    	if (isVisible) {
    		v.setVisibility(View.VISIBLE);
    	} else {
    		v.setVisibility(View.GONE);
    	}
    }
    
    private void setPageFinished() {
    	WebView web = (WebView)findViewById(R.id.web_view);
    	Button back = (Button)findViewById(R.id.back_button);
    	back.setEnabled(web.canGoBack());
    	Button next = (Button)findViewById(R.id.next_button);
    	next.setEnabled(web.canGoForward());
    }
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			WebView web = (WebView)findViewById(R.id.web_view);
			if (web.canGoBack()) {
				web.goBack();
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

    class MyWebViewClient extends WebViewClient {
    	@Override
    	public void onPageStarted(WebView view, String url, Bitmap favicon) {
    		setProgressBarIndeterminateVisibility(true);
    		setProgress(true);
    	}
    	@Override
    	public void onPageFinished(WebView view, String url) {
    		setProgressBarIndeterminateVisibility(false);
    		setPageFinished();
    		setProgress(false);
    	}
    	@Override
    	public void onReceivedError (WebView view, int errorCode, 
    			String description, String failingUrl){
    		setProgressBarIndeterminateVisibility(false);
    		setPageFinished();
    		setProgress(false);
    		Log.d(TAG, "------------------------- onReceivedError ---------------");
    		showError();
    	}
    	@Override
    	public boolean shouldOverrideUrlLoading(WebView view, String url) {
    		if (url.startsWith("tel:")) {
    			Intent i = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
    			startActivity(i);
    			return true;
    		} else if (url.startsWith("http://maps.google.co.jp")) {
    			Intent i = new Intent();
    			i.setAction(Intent.ACTION_VIEW);
    			i.setClassName("com.google.android.apps.maps","com.google.android.maps.MapsActivity");
    			i.setData(Uri.parse(url));
    			startActivity(i);
    			return true;
    		}
    		return false;
    	}
    }
    
    class MyWebChromeClient extends WebChromeClient {
    }

	@Override
	public void onClick(View view) {
		WebView web = (WebView)findViewById(R.id.web_view);
		if (view.getId() == R.id.back_button) {
			if (web.canGoBack()) {
				web.goBack();
			}
		} else if (view.getId() == R.id.next_button) {
	    	if (web.canGoForward()) {
	    		web.goForward();
	    	}
		} else if (view.getId() == R.id.reload_button) {
			web.reload();
		}
		
	}
	
	private void showError() {
		Toast.makeText(this, "Connection Error", Toast.LENGTH_SHORT).show();
	}
}

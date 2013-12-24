package vk.music.downloader.ui;

import vk.music.downloader.R;
import vk.music.downloader.R.id;
import vk.music.downloader.R.layout;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;

@SuppressLint("SetJavaScriptEnabled")
public class LogInDialog extends Dialog
{

	public static final String CALLBACK_URL = "http://api.vkontakte.ru/blank.html";
	private static final String OAUTH_AUTHORIZE_URL = "http://api.vkontakte.ru/oauth/authorize?client_id=3870143&scope=8&redirect_uri=http://api.vkontakte.ru/blank.html&display=mobile&v=5.0&response_type=token";
	private LogDoneListener logDoneListener;
	private WebView webView;
	private boolean visible;
	
	public LogInDialog(Context context, LogDoneListener logDoneListener, boolean visible)
	{
		super(context);
		this.logDoneListener = logDoneListener;
		this.visible = visible;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));				
		this.setContentView(R.layout.login_dialog);
				
		this.setCancelable(true);

		this.webView = (WebView) this.findViewById(R.id.login_webView);	
		if (!visible) webView.setVisibility(View.INVISIBLE);
		
		webView.setWebViewClient(new VkWebViewClient());
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setSavePassword(false);
		webView.clearView();
		webView.loadUrl(OAUTH_AUTHORIZE_URL);		
		webView.requestFocusFromTouch();
		webView.requestFocus(View.FOCUS_DOWN);
		webView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
				case MotionEvent.ACTION_UP:
					if (!v.hasFocus()) {
						v.requestFocus();
					}
					break;
				}
				return false;
			}
		});
	}
	
	private class VkWebViewClient extends WebViewClient 
	{ 
    	@Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) 
    	{
        	            	
        	if (url.startsWith(CALLBACK_URL) & ( !url.contains("error") )) 
        	{
        		logDoneListener.onResultOk(url);
        		dismiss();
        		return true;
        	} 
        	else if(url.contains("error"))
        	{
        		dismiss();
        		return false;
        	}
        	else 
        	{        		
        	    view.loadUrl(url);
        		return true;
        	}
        }
    	
    	@Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) 
    	{
        	super.onReceivedError(view, errorCode, description, failingUrl);      
        	logDoneListener.onResultError(description); 
        	dismiss();
        }
    	
    	@Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) 
    	{
            super.onPageStarted(view, url, favicon);            
            if( url.contains("error") ) 
            {
            	dismiss();
            	return;
            }
            else if( url.contains("access_token")) 
            {
            	logDoneListener.onResultOk(url);
        		dismiss();
            	return;
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) 
        {
            super.onPageFinished(view, url);
            view.clearCache(true);
            //Log.v("SUN", url);
            //if (url.contains("#") && url.split("#")[0].contains(CALLBACK_URL)) webView.setVisibility(View.INVISIBLE);
        }
    }
	
}

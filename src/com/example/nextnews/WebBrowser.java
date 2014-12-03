package com.example.nextnews;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import android.webkit.WebView;

public class WebBrowser extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		

		
		Intent intent = getIntent();
		String link = intent.getStringExtra("link");	
		
		WebView webView = new WebView(this);

		// 设置WebView属性，能够执行Javascript脚本
		webView.getSettings().setJavaScriptEnabled(true);
		// 加载需要显示的网页
		webView.loadUrl(link);
		setContentView(webView);
	}
}

package com.ampvita.clickopedia;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;


public class WikiActivty extends Activity implements View.OnTouchListener, Handler.Callback {

    private static final int CLICK_ON_WEBVIEW = 1;
    private static final int CLICK_ON_URL = 2;

    private final Handler handler = new Handler(this);

    private WebView webView;
    private WebViewClient client;

    SharedPreferences prefs;
    String lastUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_wiki_activty);
        webView = (WebView) findViewById(R.id.wiki_view);
        webView.setOnTouchListener(this);

        prefs = this.getSharedPreferences(
                "com.ampvita.clickopedia", Context.MODE_PRIVATE);

        client = new WebViewClient(){
            @Override public boolean shouldOverrideUrlLoading(WebView view, String url) {
                lastUrl = url;
                handler.sendEmptyMessage(CLICK_ON_URL);
                return false;
            }
        };

        webView.setWebViewClient(client);
        webView.setVerticalScrollBarEnabled(false);
        webView.loadUrl("http://www.wikipedia.org");

        HashSet<String> hardCoded = (HashSet) prefs.getStringSet("top5000", new HashSet<String>());

        System.out.println(hardCoded.toString());

        //String[] arr = lines.toArray(new String[0]);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v.getId() == R.id.wiki_view && event.getAction() == MotionEvent.ACTION_DOWN){
            handler.sendEmptyMessageDelayed(CLICK_ON_WEBVIEW, 500);
        }
        return false;
    }

    @Override
    public boolean handleMessage(Message msg) {
        if (msg.what == CLICK_ON_URL){
            int clicks = prefs.getInt("clicks", 0); // add one to your clicks counter
            prefs.edit().putInt("clicks", clicks+1);
            handler.removeMessages(CLICK_ON_WEBVIEW);
            Toast.makeText(this, "Url " + lastUrl, Toast.LENGTH_SHORT).show();
            webView.loadUrl(lastUrl);
            return true;
        }
        if (msg.what == CLICK_ON_WEBVIEW){
            Toast.makeText(this, "WebView clicked", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }
}
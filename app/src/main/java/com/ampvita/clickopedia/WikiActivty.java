package com.ampvita.clickopedia;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;


public class WikiActivty extends Activity {

    WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_wiki_activty);
         mWebView = (WebView) findViewById(R.id.wiki_view);
        // add the following line ----------
        mWebView.setWebViewClient(new CustomWebView());
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.loadUrl("http://www.wikipedia.org");

        http://en.wikipedia.org/wiki/User:West.andrew.g/Popular_pages

    }

}

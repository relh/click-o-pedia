package com.ampvita.clickopedia;

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.webkit.WebView;
import android.widget.ImageView;


public class DownloadSplashActivity extends Activity {

    ImageView splash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_download_splash);

        splash = (ImageView) findViewById(R.id.splash);
        splash.setImageResource(R.drawable.wikipedia_splash);

        SystemClock.sleep(3000);

    }

    protected String[] getPages() {
        int i = 1;
        String expr = "<td>"+String.valueOf(i)+"\/td><td><a href=\"[a-z/]+/\"";

        for (int i = 0; i < 5000; i++) {

        }

        return [""];
    }

}

package com.ampvita.clickopedia;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.widget.ImageView;


public class DownloadSplashActivity extends Activity {

    ImageView splash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_download_splash);

        splash = (ImageView) findViewById(R.id.splash);
        splash.setImageResource(R.drawable.wikipedia_splash);

        SystemClock.sleep(1000);

        Intent transition = new Intent(DownloadSplashActivity.this, WikiActivty.class);
        startActivity(transition);
    }
}

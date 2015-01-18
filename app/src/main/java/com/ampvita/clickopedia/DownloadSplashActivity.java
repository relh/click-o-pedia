package com.ampvita.clickopedia;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.widget.ImageView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.Random;


public class DownloadSplashActivity extends Activity {

    Firebase mfr;
    ImageView splash;

    boolean firstStart = true;
    boolean firstFinish = true;

    int openGame = 0;

    String start;
    String finish;

    Random r;

    boolean unsure = true;
    boolean host = false;

    ValueEventListener FirebaseListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot snapshot) { //unsure, host, joiner?
            System.out.println(snapshot.getKey());
            System.out.println(snapshot.getValue());

            if (unsure) {
                unsure = false;
                if (snapshot.getValue() == null) {
                    host = true;

                    int idx = r.nextInt(ClickopediaApplication.top5000.length); // if starting null value, we will host
                    start = (ClickopediaApplication.top5000[idx]);
                    mfr.child("start").setValue(start); // means we are host

                    idx = r.nextInt(ClickopediaApplication.top5000.length); // if starting null value, we will host
                    finish = (ClickopediaApplication.top5000[idx]);
                    mfr.child("finish").setValue(finish); // means we are host
                } else { // if we're not the host, this is all we have to do
                    host = false;

                    if (snapshot.getKey().equals("start")) {
                        start = snapshot.getValue().toString();
                        openGame++;
                        onOpenGameChange();
                        mfr.child("start").removeEventListener(this);
                        mfr.child("start").removeValue();
                    } else {
                        finish = snapshot.getValue().toString();
                        openGame++;
                        onOpenGameChange();
                        mfr.child("finish").removeEventListener(this);
                        mfr.child("finish").removeValue();
                    }
                }
            } else { // either we're hosting, have set the values, and are waiting OR we are a joiner and have got one value and need the other
                if (host) {
                    
                } else {

                }
            }

            if (snapshot.getKey().equals("start")) {
                if (firstStart) { // first value
                    firstStart = false;
                    if (snapshot.getValue() != null) { // chance for open game to exist
                        start = snapshot.getValue().toString();
                        openGame++;
                        onOpenGameChange();
                        mfr.child("start").removeEventListener(this);
                    } else {
                        int idx = r.nextInt(ClickopediaApplication.top5000.length); // if starting null value, we will host
                        start = (ClickopediaApplication.top5000[idx]);
                        mfr.child("start").setValue(start); // means we are host
                    }
                } else { // been here before, now waiting for other player
                    start = snapshot.getValue().toString();
                    if (start != null) { // we found a game!
                        openGame++;
                        onOpenGameChange();
                        mfr.child("start").removeEventListener(this);
                    }
                }
            } else { // is finish
                if (firstFinish) { // first value
                    firstFinish = false;
                    if (snapshot.getValue() != null) { // chance for open game to exist
                        finish = snapshot.getValue().toString();
                        openGame++;
                        onOpenGameChange();
                        mfr.child("finish").removeEventListener(this);
                    } else {
                        int idx = r.nextInt(ClickopediaApplication.top5000.length); // if starting null value, we will host
                        finish = (ClickopediaApplication.top5000[idx]);
                        mfr.child("finish").setValue(finish);
                    }
                } else { // been here before, now waiting for other player
                    finish = snapshot.getValue().toString();
                    if (finish != null) { // we found a game!
                        openGame++;
                        onOpenGameChange();
                        mfr.child("finish").removeEventListener(this);
                    }
                }
            }
        }

        @Override
        public void onCancelled(FirebaseError error) {
        }
    };

    protected void onOpenGameChange() {
        System.out.println(openGame);
        if (openGame == 2) { // Got both a start and finish ideally
            progress();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_splash);
        splash = (ImageView) findViewById(R.id.splash);
        splash.setImageResource(R.drawable.wikipedia_splash);

        r = new Random();

        mfr = ((ClickopediaApplication) getApplication()).myFirebaseRef;

        mfr.child("start").addValueEventListener(FirebaseListener);
        mfr.child("finish").addValueEventListener(FirebaseListener);
    }

    protected void progress() {
        Intent transition = new Intent(DownloadSplashActivity.this, WikiActivty.class);
        transition.putExtra("start", start);
        transition.putExtra("finish", finish);
        startActivity(transition);
    }
}

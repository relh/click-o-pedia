package com.ampvita.clickopedia;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.widget.ImageView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.Random;


public class DownloadSplashActivity extends Activity {

    Firebase mfr;
    ImageView splash;

    boolean ownValues = false;

    boolean gotStart = false;
    boolean gotFinish = false;

    boolean deletedStart = false;
    boolean deletedFinish = false;

    String start;
    String finish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_splash);
        splash = (ImageView) findViewById(R.id.splash);
        splash.setImageResource(R.drawable.wikipedia_splash);

        mfr = ((ClickopediaApplication) getApplication()).myFirebaseRef;

        mfr.child("start").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                System.out.println(snapshot.getValue());
                start = snapshot.getValue().toString();
                if (start == null) {
                    deletedStart = true;
                } else {
                    mfr.child("start").removeValue();
                }
                gotStart = true;
            }

            @Override
            public void onCancelled(FirebaseError error) {
            }
        });

        mfr.child("finish").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                System.out.println(snapshot.getValue());
                finish = snapshot.getValue().toString();
                if (finish == null) {
                    deletedFinish = true;
                } else {
                    mfr.child("finish").removeValue();
                }
                gotFinish = true;
            }
            @Override public void onCancelled(FirebaseError error) { }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Wait for firebase read
        while (!(gotStart && gotFinish)) {
            try {
                System.out.println("waiting for read");
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (start == null || finish == null) { // Gotta make our own values
            Random r = new Random();

            int idx = r.nextInt(ClickopediaApplication.top5000.length);
            start = (ClickopediaApplication.top5000[idx]);

            idx = r.nextInt(ClickopediaApplication.top5000.length);
            finish = (ClickopediaApplication.top5000[idx]);

            System.out.println("I didn't exist :(");

            ownValues = true; // We're using our own key/val

            mfr.child("start").setValue(start);
            mfr.child("finish").setValue(finish);

            waiting();
        } else { // Values already existed!
            progress();
        }
    }

    protected void waiting() {
        // Wait for firebase deletion by partner
        while (!(deletedStart && deletedFinish)) {
            try {
                System.out.println("waiting for delete");
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        progress();
    }

    protected void progress() {
        Intent transition = new Intent(DownloadSplashActivity.this, WikiActivty.class);
        transition.putExtra("start", start);
        transition.putExtra("finish", finish);
        startActivity(transition);
    }
}

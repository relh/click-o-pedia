package com.ampvita.clickopedia;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.Random;


public class LobbyActivity extends ActionBarActivity {
    Firebase mfr;
    ImageView splash;

    int openGame = 0;

    String start;
    String finish;

    Random r;

    boolean unsure = true;
    boolean host = false;

    ValueEventListener FirebaseListener = new ValueEventListener() {

        public void joinerSaveAndZero(DataSnapshot snapshot) {
            if (snapshot.getKey().equals("start")) {
                start = snapshot.getValue().toString();
                openGame++;
                onOpenGameChange();
                mfr.child("start").removeEventListener(FirebaseListener); // might be broken
                mfr.child("start").removeValue();
            } else {
                finish = snapshot.getValue().toString();
                openGame++;
                onOpenGameChange();
                mfr.child("finish").removeEventListener(FirebaseListener); // might be broken
                mfr.child("finish").removeValue();
            }
        }

        @Override
        public void onDataChange(DataSnapshot snapshot) { //unsure, host, joiner?
            if (unsure) {
                unsure = false;
                if (snapshot.getValue() == null) { host = true;
                    int idx = r.nextInt(ClickopediaApplication.top1000.length); // if starting null value, we will host
                    //start = (ClickopediaApplication.top1000[idx]);
                    start = "Barack_Obama";
                    mfr.child("start").setValue(start); // means we are host

                    idx = r.nextInt(ClickopediaApplication.top1000.length); // if starting null value, we will host
                    //finish = (ClickopediaApplication.top1000[idx]);
                    finish = "Incumbent";
                    mfr.child("finish").setValue(finish); // means we are host
                } else { // if we're not the host, this is all we have to do
                    host = false;
                    joinerSaveAndZero(snapshot);
                }
            } else { // either we're hosting, have set the values, and are waiting OR we are a joiner and have got one value and need the other
                if (host) {
                    System.out.println("host looking for null: " + snapshot.getKey() + " " + snapshot.getValue());
                    if (snapshot.getValue() == null) { // should be null because joiner zeroed it
                        openGame++;
                        onOpenGameChange();
                    }
                } else {
                    System.out.println("joiner looking for other val: " + snapshot.getKey() + " " + snapshot.getValue());
                    joinerSaveAndZero(snapshot);
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
        setContentView(R.layout.activity_lobby);

        r = new Random();
        mfr = ((ClickopediaApplication) getApplication()).myFirebaseRef;


        final Button start = (Button) findViewById(R.id.start);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mfr.child("start").addValueEventListener(FirebaseListener);
                mfr.child("finish").addValueEventListener(FirebaseListener);

                start.setText("Waiting for another player...");
            }
        });
    }

    protected void progress() {
        Intent transition = new Intent(LobbyActivity.this, GameActivity.class);
        transition.putExtra("start", start);
        transition.putExtra("finish", finish);
        mfr.removeEventListener(FirebaseListener);
        startActivity(transition);
        finish();
    }
}

package com.ampvita.clickopedia;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


public class FinishActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish);

        int score = getIntent().getIntExtra("score", 0);
        Boolean winner = getIntent().getBooleanExtra("winner", false);

        ImageView splash = (ImageView) findViewById(R.id.splashEnd);
        splash.setImageResource(R.drawable.wikipedia_splash);

        TextView myClicks = (TextView) findViewById(R.id.myClicks);
        TextView theirClicks = (TextView) findViewById(R.id.theirClicks);

        if (winner) {
            myClicks.setText("You won with a score of: " + score + " clicks");
        } else {
            String theirScore = getIntent().getStringExtra("otherScore");

            myClicks.setText("You were " + score + " clicks into your search but were beaten on time!");
            theirClicks.setText("Your opponent got to the finish in " + theirScore + " clicks.");
        }

        Button restart = (Button) findViewById(R.id.restart);
        restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FinishActivity.this, LobbyActivity.class));
                finish();
            }
        });
    }
}

package com.ampvita.clickopedia;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;


public class EndingActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ending);

        int score = getIntent().getIntExtra("score", 0);
        Boolean winner = getIntent().getBooleanExtra("winner", false);

        ImageView splash = (ImageView) findViewById(R.id.splash);
        splash.setImageResource(R.drawable.wikipedia_splash);

        TextView myClicks = (TextView) findViewById(R.id.myClicks);
        TextView theirClicks = (TextView) findViewById(R.id.theirClicks);

        if (winner) {
            myClicks.setText("You won with a score of: " + score + " clicks");
        } else {
            String theirScore = getIntent().getStringExtra("theirScore");

            myClicks.setText("You were " + score + " clicks into your search but were beaten on time!");
            theirClicks.setText("Your opponent got to the finish in " + theirScore + " clicks.");
        }
    }
}

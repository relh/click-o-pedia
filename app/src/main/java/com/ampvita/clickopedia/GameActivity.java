package com.ampvita.clickopedia;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public class GameActivity extends Activity {

    private TextView clickView;
    private int score;

    private WebView webView;
    private WebViewClient client;

    String finish;

    boolean winner = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        score = -1;
        clickView = (TextView) findViewById(R.id.clicks);
        clickView.setText(String.valueOf(score));

        final String start = getIntent().getStringExtra("start");
        finish = getIntent().getStringExtra("finish");

        TextView startText = (TextView) findViewById(R.id.startText);
        TextView finishText = (TextView) findViewById(R.id.finishText);
        startText.setText(Html.fromHtml("<small>Start</small><br /><br />" + "<b>" + start + "</b>"));
        finishText.setText(Html.fromHtml("<small>Finish</small><br /><br />" + "<b>" + finish + "</b>"));

        webView = (WebView) findViewById(R.id.web_view);
        webView.getSettings().setJavaScriptEnabled(true);
        //webView.setOnTouchListener(this);

        final Firebase mfr = ((ClickopediaApplication) getApplication()).myFirebaseRef;
        // can't remove old value event listeners
        // maybe they aren't just added to the children
        final ValueEventListener FinishListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.getValue() == null) { return; }
                if (winner) { mfr.child(finish).removeValue(); }

                AlertDialog.Builder builder;
                AlertDialog alertDialog;

                Context mContext = GameActivity.this;
                LayoutInflater inflater = (LayoutInflater)
                        mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
                View layout = inflater.inflate(R.layout.custom_finish,
                        (ViewGroup) findViewById(R.id.root));

                TextView popUpTitle = (TextView) layout.findViewById(R.id.popupTitle);
                TextView text1 = (TextView) layout.findViewById(R.id.text1);
                TextView text2 = (TextView) layout.findViewById(R.id.text2);
                ImageView pic1 = (ImageView) layout.findViewById(R.id.pic1);
                ImageView pic2 = (ImageView) layout.findViewById(R.id.pic2);
                pic1.setImageResource(R.drawable.portrait);

                if (winner) {
                   popUpTitle.setText("You win!");
                   text1.setText("Your score: " + score);
                   text2.setText("You're the best :)");
                   text2.setGravity(Gravity.CENTER);

                } else {
                   popUpTitle.setText("You lose.");
                   text1.setText("Your score: " + score);
                   text2.setText("Their score: " + snapshot.getValue().toString());
                   pic2.setImageResource(R.drawable.portrait);
                }

                Button dialogButton = (Button) layout.findViewById(R.id.restart);
                // if button is clicked, close the custom dialog
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(GameActivity.this, LobbyActivity.class));
                        finish();
                    }
                });

                builder = new AlertDialog.Builder(mContext);
                builder.setView(layout);
                alertDialog = builder.create();
                alertDialog.show();

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) { }
        };

        mfr.child(finish).addValueEventListener(FinishListener);

        client = new WebViewClient(){
            @Override public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (Uri.parse(url).getHost().contains("wikipedia.org")) {
                    clickView.setText(String.valueOf(score)); //update number of clicks
                    score++;
                    webView.loadUrl(url); //load webpage
                    System.out.println("Found a wiki url: " + Uri.parse(url));
                }
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url)
            {
                System.out.println("I should get this next to load: " + Uri.parse(url));
                String[] urlParts = url.split("/");
                if (urlParts[urlParts.length-1].equals(finish)) {
                    winner = true;
                    mfr.child(finish).setValue(score); // means we are host
                }

                webView.loadUrl("javascript:(function() { " +
                        "var elements = document.getElementsByClassName('header'); " +
                        "elements[0].style.display= 'none'; " +
                        "var elements = document.getElementsByClassName('hlist'); " +
                        "elements[0].style.display= 'none'; " +
                        "var elements = document.getElementsByClassName('watch-this-article'); " +
                        "elements[0].style.display= 'none'; " +
                        "var elements = document.getElementsByClassName('icon-edit-enabled'); " +
                        "elements[0].style.display= 'none'; " +
                        "var elements = document.getElementsByClassName('reference'); " +
                        "elements[0].style.display= 'none'; " +
                        "var elements = document.getElementsByClassName('reflist'); " +
                        "elements[0].style.display= 'none'; " +
                        "var elements = document.getElementsByClassName('last-modified-bar'); " +
                        "elements[0].style.display= 'none'; " +
                        "var elements = document.getElementById('page-secondary-actions'); " +
                        "elements[0].style.display= 'none'; " +
                        "var elements = document.getElementsByClassName('footer'); " +
                        "elements[0].style.display= 'none'; " +
                        "})()");

                System.out.println("finished loading: " + Uri.parse(url));
            }
        };

        webView.setWebViewClient(client);
        webView.setVerticalScrollBarEnabled(false);

        System.out.println("url starting place http://www.en.wikipedia.org/wiki/" + start);
        webView.loadUrl("http://www.en.wikipedia.org/wiki/" + start);
    } //https://torid-heat-2250.firebaseio.com/#-Jfwrmwo4Ri96iuFF6KO|b68b468e76632928959f64f2772ba463

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Check if the key event was the Back button and if there's history
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        // If it wasn't the Back key or there's no web page history, bubble up to the default
        // system behavior (probably exit the activity)
        return super.onKeyDown(keyCode, event);
    }

}

//SharedPreferences prefs;

//prefs = this.getSharedPreferences(
//        "com.ampvita.clickopedia", Context.MODE_PRIVATE);

//int clicks = prefs.getInt("clicks", 0) + 1; // add one to your clicks score
//prefs.edit().putInt("clicks", clicks);

//HashSet<String> hardCoded = (HashSet) prefs.getStringSet("top1000", new HashSet<String>());
//System.out.println(hardCoded.toString());

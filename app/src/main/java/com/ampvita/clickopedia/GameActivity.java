package com.ampvita.clickopedia;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;


public class GameActivity extends Activity implements View.OnTouchListener, Handler.Callback {

    private static final int CLICK_ON_WEBVIEW = 1;
    private static final int CLICK_ON_URL = 2;

    private final Handler handler = new Handler(this);

    private TextView clickView;
    private int score;

    private WebView webView;
    private WebViewClient client;

    String lastUrl;

    String finish;

    ValueEventListener mListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        score = -1;
        clickView = (TextView) findViewById(R.id.score_view);

        final String start = getIntent().getStringExtra("start");
        finish = getIntent().getStringExtra("finish");

        TextView startingSite = (TextView) findViewById(R.id.starting);
        startingSite.setText("Starting page: " + start);

        TextView finishingSite = (TextView) findViewById(R.id.finishing);
        finishingSite.setText("Finishing page: " + finish);

        webView = (WebView) findViewById(R.id.web_view);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setOnTouchListener(this);

        final Firebase mfr = ((ClickopediaApplication) getApplication()).myFirebaseRef;
        // can't remove old value event listeners
        // maybe they aren't just added to the children
        mfr.child(finish).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) { //TODO: this does not successfully move the loser to the end page
                if (!(snapshot.getKey().equals(finish))) {
                    System.out.println("HOW DID THIS HAPPEN: " + snapshot.getKey() + " " + snapshot.getValue());
                    return;
                }
                Intent transition = new Intent(GameActivity.this, FinishActivity.class);
                transition.putExtra("score", score);
                transition.putExtra("theirScore", snapshot.getValue().toString());
                transition.putExtra("winner", false);
                startActivity(transition);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) { }
        });

        client = new WebViewClient(){
            @Override public boolean shouldOverrideUrlLoading(WebView view, String url) {
                lastUrl = url;
                handler.sendEmptyMessage(CLICK_ON_URL);
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url)
            {
                String[] urlParts = url.split("/");
                //System.out.println("url on page finished: " + urlParts[urlParts.length-1]);
                //System.out.println("finish string: " + finish);

                if (urlParts[urlParts.length-1].equals(finish.substring(1))) {
                    Intent transition = new Intent(GameActivity.this, FinishActivity.class);
                    mfr.child(finish).setValue(score); // means we are host
                    transition.putExtra("score", score);
                    transition.putExtra("winner", true);
                    startActivity(transition);
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
            }
        };

        webView.setWebViewClient(client);
        webView.setVerticalScrollBarEnabled(false);

        System.out.println("http://www.en.wikipedia.org/wiki" + start);
        webView.loadUrl("http://www.en.wikipedia.org/wiki" + start);
    } //https://torid-heat-2250.firebaseio.com/#-Jfwrmwo4Ri96iuFF6KO|b68b468e76632928959f64f2772ba463

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v.getId() == R.id.web_view && event.getAction() == MotionEvent.ACTION_DOWN){
            handler.sendEmptyMessageDelayed(CLICK_ON_WEBVIEW, 500);
        }
        return false;
    }

    @Override
    public boolean handleMessage(Message msg) {
        if (msg.what == CLICK_ON_URL) {
            clickView.setText("Clicks so far: " + score); //update number of clicks
            handler.removeMessages(CLICK_ON_WEBVIEW); //remove normal screen clicks
            //Toast.makeText(this, "Url " + lastUrl, Toast.LENGTH_SHORT).show(); //display url

            //check for webpage being a wikipage
            if (lastUrl.toLowerCase().contains("wikipedia")) {
                score++;
                webView.loadUrl(lastUrl); //load webpage
            }
            return true;
        }
        if (msg.what == CLICK_ON_WEBVIEW) {
            //Toast.makeText(this, "WebView clicked", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }
}

//SharedPreferences prefs;

//prefs = this.getSharedPreferences(
//        "com.ampvita.clickopedia", Context.MODE_PRIVATE);

//int clicks = prefs.getInt("clicks", 0) + 1; // add one to your clicks score
//prefs.edit().putInt("clicks", clicks);

//HashSet<String> hardCoded = (HashSet) prefs.getStringSet("top5000", new HashSet<String>());
//System.out.println(hardCoded.toString());

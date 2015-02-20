package com.sifiso.codetribe.minisasslibrary.activities;

import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.sifiso.codetribe.minisasslibrary.R;

import java.util.Timer;
import java.util.TimerTask;
/*
* Sasa 2015-02-20
 */

public class SplashActivity extends ActionBarActivity {
    ImageView imageView;
    TextView imageText;
    Timer timer;
    Context ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getSupportActionBar().hide();
        ctx = getApplicationContext();
        imageView = (ImageView) findViewById(R.id.imgBackground);
        imageText = (TextView) findViewById(R.id.imageText);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                timer = new Timer();


                timer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                RandomPics.getImage(ctx, imageView, imageText);
                            }
                        });

                    }
                }, 1000, 5000);

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_splash, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

package com.sifiso.codetribe.minisasslibrary.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sifiso.codetribe.minisasslibrary.R;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.RequestDTO;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.ResponseDTO;
import com.sifiso.codetribe.minisasslibrary.util.CacheUtil;
import com.sifiso.codetribe.minisasslibrary.util.ErrorUtil;
import com.sifiso.codetribe.minisasslibrary.util.Statics;
import com.sifiso.codetribe.minisasslibrary.util.TimerUtil;
import com.sifiso.codetribe.minisasslibrary.util.Util;
import com.sifiso.codetribe.minisasslibrary.util.WebSocketUtil;
import com.sifiso.codetribe.minisasslibrary.viewsUtil.RandomPics;

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
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               finish();
            }
        });
        flashImages();

    }

    static final int STATUS_REG = 500;

    private void flashImages() {
        TimerUtil.startFlashTime(new TimerUtil.TimerFlashListener() {
            @Override
            public void onStartFlash() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        RandomPics.getImage(ctx, imageView, imageText, new RandomPics.RandomPicsListener() {
                            @Override
                            public void onCompleteFlash() {

                            }
                        });
                    }
                });
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_splash, menu);
        cacheData();
        return true;
    }

    private ResponseDTO response;

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


    @Override
    protected void onDestroy() {
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        TimerUtil.killFlashTimer();
        super.onDestroy();
    }


    @Override
    protected void onPause() {
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        TimerUtil.killFlashTimer();
        super.onPause();
    }

    public void cacheData() {
        RequestDTO req = new RequestDTO();
        req.setRequestType(RequestDTO.GET_DATA);

        try {

            WebSocketUtil.sendRequest(ctx, Statics.MINI_SASS_ENDPOINT, req, new WebSocketUtil.WebSocketListener() {
                @Override
                public void onMessage(final ResponseDTO r) {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            Log.e(LOG, "## getStarterData responded...statusCode: " + r.getStatusCode());
                            if (!ErrorUtil.checkServerError(ctx, r)) {
                                return;
                            }
                            response = r;
                            CacheUtil.cacheData(ctx, r, CacheUtil.CACHE_DATA, new CacheUtil.CacheUtilListener() {
                                @Override
                                public void onFileDataDeserialized(ResponseDTO response) {

                                }

                                @Override
                                public void onDataCached() {

                                }

                                @Override
                                public void onError() {

                                }
                            });


                        }
                    });

                }

                @Override
                public void onClose() {

                }

                @Override
                public void onError(final String message) {

                }
            });
        } catch (Exception e) {

        }
    }

    static final String LOG = SplashActivity.class.getSimpleName();
}

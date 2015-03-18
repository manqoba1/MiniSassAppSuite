package com.sifiso.codetribe.minisasslibrary.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.sifiso.codetribe.minisasslibrary.dto.tranfer.RequestDTO;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.ResponseDTO;
import com.sifiso.codetribe.minisasslibrary.toolbox.WebCheck;
import com.sifiso.codetribe.minisasslibrary.toolbox.WebCheckResult;
import com.sifiso.codetribe.minisasslibrary.util.CacheUtil;
import com.sifiso.codetribe.minisasslibrary.util.ErrorUtil;
import com.sifiso.codetribe.minisasslibrary.util.Statics;
import com.sifiso.codetribe.minisasslibrary.util.TimerUtil;
import com.sifiso.codetribe.minisasslibrary.util.WebSocketUtil;
import com.sifiso.codetribe.minisasslibrary.viewsUtil.RandomPics;

import java.util.Timer;


public class Welcome extends ActionBarActivity {
    ImageView imageView;
    TextView imageText;
    Timer timer;
    Context ctx;
    Button bLogin,bReg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        getSupportActionBar().hide();
        ctx = getApplicationContext();
        bLogin = (Button)findViewById(R.id.btnwLog);
        bReg = (Button)findViewById(R.id.btnCreate);

        imageView = (ImageView) findViewById(R.id.imgBackground);
        // imageText = (TextView) findViewById(com.sifiso.codetribe.minisasslibrary.R.id.imageText);
        //flashImages();

        bLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Welcome.this, SignActivity.class);
                startActivity(intent);
                getSupportActionBar().setTitle("Sign in");
            }
        });


        bReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Welcome.this, RegisterActivity.class);
                startActivity(intent);
                getSupportActionBar().setTitle("Register Team");
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_welcome, menu);
        getCachedData();
        startActivity(new Intent(Welcome.this, SplashActivity.class));
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



    private void getCachedData() {
        final WebCheckResult wcr = WebCheck.checkNetworkAvailability(ctx);

        CacheUtil.getCachedData(getApplicationContext(), CacheUtil.CACHE_DATA, new CacheUtil.CacheUtilListener() {

            @Override
            public void onFileDataDeserialized(ResponseDTO r) {
                Log.d(LOG, r.getCountryList().toString() + "");


            }

            @Override
            public void onDataCached() {

            }

            @Override
            public void onError() {

            }
        });

    }







    @Override
    protected void onDestroy() {


        overridePendingTransition(com.sifiso.codetribe.riverteamapp.R.anim.slide_in_left, com.sifiso.codetribe.riverteamapp.R.anim.slide_out_right);
        TimerUtil.killFlashTimer();
        super.onDestroy();
    }

    @Override
    protected void onPause() {

        overridePendingTransition(com.sifiso.codetribe.riverteamapp.R.anim.slide_in_left, com.sifiso.codetribe.riverteamapp.R.anim.slide_out_right);
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

                            CacheUtil.cacheData(ctx, r, CacheUtil.CACHE_DATA, new CacheUtil.CacheUtilListener() {
                                @Override
                                public void onFileDataDeserialized(ResponseDTO response) {
                                    finish();
                                }

                                @Override
                                public void onDataCached() {
                                    finish();
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

    static final String LOG = Welcome.class.getSimpleName();
}

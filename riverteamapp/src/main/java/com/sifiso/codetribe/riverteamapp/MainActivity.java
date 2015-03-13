package com.sifiso.codetribe.riverteamapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.sifiso.codetribe.minisasslibrary.activities.ImageActivity;
import com.sifiso.codetribe.minisasslibrary.activities.PictureActivity;
import com.sifiso.codetribe.minisasslibrary.activities.SplashActivity;
import com.sifiso.codetribe.minisasslibrary.dto.EvaluationDTO;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.ResponseDTO;
import com.sifiso.codetribe.minisasslibrary.toolbox.WebCheck;
import com.sifiso.codetribe.minisasslibrary.toolbox.WebCheckResult;
import com.sifiso.codetribe.minisasslibrary.util.CacheUtil;

import java.util.List;


public class MainActivity extends ActionBarActivity {
Context ctx;
    private List<EvaluationDTO> evaluationList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startActivity(new Intent(this, PictureActivity.class));
        ctx = getApplicationContext();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        getCachedCompanyData();
        startActivity(new Intent(MainActivity.this, SplashActivity.class));

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

        if (id == R.id.CAM_btnStart) {
            return true;
        }
        if (id == R.id.menu_gallery) {
            Intent i = new Intent(this, ImageActivity.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        overridePendingTransition(com.sifiso.codetribe.minisasslibrary.R.anim.slide_in_left, com.sifiso.codetribe.minisasslibrary.R.anim.slide_out_right);
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void getCachedCompanyData() {
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
static final String LOG = MainActivity.class.getSimpleName();
}

package com.sifiso.codetribe.minisasslibrary.activities;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.location.Location;
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.sifiso.codetribe.minisasslibrary.R;
import com.sifiso.codetribe.minisasslibrary.dto.EvaluationSiteDTO;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.ResponseDTO;
import com.sifiso.codetribe.minisasslibrary.services.CachedSyncService;
import com.sifiso.codetribe.minisasslibrary.util.Statics;
import com.sifiso.codetribe.minisasslibrary.util.Util;

public class GPSscanner extends ActionBarActivity implements LocationListener,
        GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    static final String LOG = GPSscanner.class.getSimpleName();
    Location mCurrentLocation;
    GoogleApiClient mLocationClient;
    ResponseDTO response;
    Location location;
    LocationRequest mLocationRequest;

    static final int GPS_DATA = 102;
    //
    TextView desiredAccuracy, txtLat, txtLng, txtAccuracy;
    RelativeLayout GPS_nameLayout;

    SeekBar seekBar;
    boolean isScanning;
    EvaluationSiteDTO evaluationSite;
    ImageView imgLogo, hero;
    Context ctx;
    ObjectAnimator logoAnimator;
    long start, end;
    Chronometer chronometer;
    boolean pleaseStop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gpsscanner);
        ctx = getApplicationContext();
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mLocationClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mLocationClient.connect();
        setFields();
    }

    private void setFields() {

        desiredAccuracy = (TextView) findViewById(R.id.GPS_desiredAccuracy);
        txtAccuracy = (TextView) findViewById(R.id.GPS_accuracy);
        txtLat = (TextView) findViewById(R.id.GPS_latitude);
        txtLng = (TextView) findViewById(R.id.GPS_longitude);
        GPS_nameLayout = (RelativeLayout) findViewById(R.id.GPS_nameLayout);
        GPS_nameLayout.setVisibility(View.GONE);
        seekBar = (SeekBar) findViewById(R.id.GPS_seekBar);
        imgLogo = (ImageView) findViewById(R.id.GPS_imgLogo);
        hero = (ImageView) findViewById(R.id.GPS_hero);
        chronometer = (Chronometer) findViewById(R.id.GPS_chrono);


        Statics.setRobotoFontBold(ctx, txtLat);
        Statics.setRobotoFontBold(ctx, txtLng);

        imgLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.flashOnce(imgLogo, 100, new Util.UtilAnimationListener() {
                    @Override
                    public void onAnimationEnded() {

                    }
                });
            }
        });

        txtAccuracy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.flashOnce(txtAccuracy, 100, new Util.UtilAnimationListener() {
                    @Override
                    public void onAnimationEnded() {

                    }
                });

            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                desiredAccuracy.setText("" + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    public void startScan() {
        getGPSCoordinates();
        txtAccuracy.setText("0.00");
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.start();
        isScanning = true;

    }

    public void setLocation(Location location) {
        if (evaluationSite == null) {
            evaluationSite = new EvaluationSiteDTO();
        }
        this.location = location;
        txtLat.setText("" + location.getLatitude());
        txtLng.setText("" + location.getLongitude());
        txtAccuracy.setText("" + location.getAccuracy());

        evaluationSite.setLatitude(location.getLatitude());
        evaluationSite.setLongitude(location.getLongitude());
        evaluationSite.setAccuracy(location.getAccuracy());
        Util.flashSeveralTimes(hero, 200, 2, null);
        if (location.getAccuracy() == seekBar.getProgress()
                || location.getAccuracy() < seekBar.getProgress()) {
            isScanning = false;
            chronometer.stop();
            //resetLogo();
            evaluationSite.setLatitude(location.getLatitude());
            evaluationSite.setLongitude(location.getLongitude());
            evaluationSite.setAccuracy(location.getAccuracy());
            GPS_nameLayout.setVisibility(View.VISIBLE);
            stopScan();
            return;
        }

    }

    @Override
    protected void onDestroy() {
        overridePendingTransition(com.sifiso.codetribe.minisasslibrary.R.anim.slide_in_left, com.sifiso.codetribe.minisasslibrary.R.anim.slide_out_right);
        //  TimerUtil.killFlashTimer();
        super.onDestroy();
    }

    @Override
    public void onPause() {
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        Intent eval = new Intent(GPSscanner.this, EvaluationActivity.class);
        eval.putExtra("siteData", evaluationSite);
        setResult(GPS_DATA, eval);
        finish();
        // super.onBackPressed();
    }

    private void getGPSCoordinates() {
        if (!mLocationClient.isConnected()) {
            mLocationClient.connect();
        }
        mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(
                mLocationClient);
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setFastestInterval(1000);

        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mLocationClient, mLocationRequest, this
            );
        } catch (IllegalStateException e) {
            Log.e(LOG, "---- mLocationClient.requestLocationUpdates ILLEGAL STATE", e);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private void stopPeriodicUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mLocationClient, this
        );
        Log.e(LOG,
                "#################### stopPeriodicUpdates - removeLocationUpdates");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(LOG,
                "### onStart, binding RequestSyncService and PhotoUploadService");
        Intent intent = new Intent(this, CachedSyncService.class);
//        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        if (mLocationClient != null) {
            mLocationClient.connect();
            Log.i(LOG,
                    "### onStart - locationClient connecting ... ");
        }
//        getGPSCoordinates();
        startScan();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_gpsscanner, menu);
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
        if (id == android.R.id.home) {
            Intent eval = new Intent(GPSscanner.this, EvaluationActivity.class);
            eval.putExtra("siteData", evaluationSite);
            setResult(GPS_DATA, eval);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStop() {
        Log.d(LOG,
                "#################### onStop");
        if (mLocationClient != null) {
            if (mLocationClient.isConnected()) {
                stopScan();
            }
            // After disconnect() is called, the client is considered "dead".
            mLocationClient.disconnect();
            Log.e(LOG, "### onStop - locationClient isConnected: "
                    + mLocationClient.isConnected());
        }


        super.onStop();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(LOG,
                "### ---> PlayServices onConnected() - gotta start something! >>");
        startScan();
        location = LocationServices.FusedLocationApi.getLastLocation(
                mLocationClient);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onDisconnected() {

    }

    @Override
    public void onLocationChanged(Location loc) {
        Log.w(LOG, "### Location changed, lat: "
                + loc.getLatitude() + " lng: "
                + loc.getLongitude()
                + " -- acc: " + loc.getAccuracy());
        mCurrentLocation = loc;
        setLocation(loc);

        if (loc.getAccuracy() <= ACCURACY_THRESHOLD) {
            location = loc;
            Log.w(LOG, "### Passing location2");
            //gpsScannerDialog.dismiss();
            setLocation(loc);
            stopScan();


            //finish();
            Log.e(LOG, "+++ best accuracy found: " + location.getAccuracy());
        }
    }

    public void stopScan() {
        stopPeriodicUpdates();
        // listener.onLocationConfirmed(evaluationSite);

        isScanning = false;

        chronometer.stop();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    static final int ACCURACY_THRESHOLD = 5;
}

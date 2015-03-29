package com.sifiso.codetribe.minisasslibrary.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.graphics.PorterDuff;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.sifiso.codetribe.minisasslibrary.R;
import com.sifiso.codetribe.minisasslibrary.dialogs.GPSScannerDialog;
import com.sifiso.codetribe.minisasslibrary.dialogs.InsectSelectionDialog;
import com.sifiso.codetribe.minisasslibrary.dto.CategoryDTO;
import com.sifiso.codetribe.minisasslibrary.dto.ConditionsDTO;
import com.sifiso.codetribe.minisasslibrary.dto.EvaluationDTO;
import com.sifiso.codetribe.minisasslibrary.dto.EvaluationSiteDTO;
import com.sifiso.codetribe.minisasslibrary.dto.InsectDTO;
import com.sifiso.codetribe.minisasslibrary.dto.InsectImageDTO;
import com.sifiso.codetribe.minisasslibrary.dto.RiverDTO;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.RequestDTO;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.ResponseDTO;
import com.sifiso.codetribe.minisasslibrary.fragments.EvaluationFragment;
import com.sifiso.codetribe.minisasslibrary.fragments.GPSScanFragment;
import com.sifiso.codetribe.minisasslibrary.fragments.InsectSelectorFragment;
import com.sifiso.codetribe.minisasslibrary.fragments.PageFragment;
import com.sifiso.codetribe.minisasslibrary.services.CachedSyncService;
import com.sifiso.codetribe.minisasslibrary.services.RequestCache;
import com.sifiso.codetribe.minisasslibrary.toolbox.WebCheck;
import com.sifiso.codetribe.minisasslibrary.toolbox.WebCheckResult;
import com.sifiso.codetribe.minisasslibrary.util.CacheUtil;
import com.sifiso.codetribe.minisasslibrary.util.ErrorUtil;
import com.sifiso.codetribe.minisasslibrary.util.RequestCacheUtil;
import com.sifiso.codetribe.minisasslibrary.util.Statics;
import com.sifiso.codetribe.minisasslibrary.util.ToastUtil;
import com.sifiso.codetribe.minisasslibrary.util.Util;
import com.sifiso.codetribe.minisasslibrary.util.WebSocketUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class Evaluation extends ActionBarActivity implements LocationListener,
        GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, GPSScanFragment.GPSScanFragmentListener, EvaluationFragment.EvaluationFragmentListener, InsectSelectorFragment.InsectSelectorFragmentListener {
    static final String LOG = Evaluation.class.getSimpleName();
    static final int ACCURACY_THRESHOLD = 5;
    static final int MAP_REQUESTED = 9007;
    static final int STATUS_CODE = 220;
    Location mCurrentLocation;
    GoogleApiClient mLocationClient;
    ViewPager mPager;
    Menu mMenu;
    PagerAdapter adapter;
    List<PageFragment> pageFragmentList;
    ResponseDTO response;
    Location location;
    LocationRequest mLocationRequest;
    boolean mBound;
    CachedSyncService mService;
    EvaluationFragment evaluationFragment;
    GPSScanFragment gpsScanFragment;
    InsectSelectorFragment insectSelectorFragment;

    private Context ctx;
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            Log.w(LOG, "## RequestSyncService ServiceConnection: onServiceConnected");
            CachedSyncService.LocalBinder binder = (CachedSyncService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
            WebCheckResult wcr = WebCheck.checkNetworkAvailability(ctx, true);
            if (wcr.isWifiConnected()) {
                mService.startSyncCachedRequests(new CachedSyncService.RequestSyncListener() {
                    @Override
                    public void onTasksSynced(int goodResponses, int badResponses) {
                        Log.w(LOG, "@@ cached requests done, good: " + goodResponses + " bad: " + badResponses);
                        getData();
                    }

                    @Override
                    public void onError(String message) {

                    }
                });

               /* Util.pretendFlash(progressBar, 1000, 2, new Util.UtilAnimationListener() {
                    @Override
                    public void onAnimationEnded() {

                    }
                });*/
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            Log.w(LOG, "## RequestSyncService onServiceDisconnected");
            mBound = false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //  setTheme(R.style.EvalListTheme);
        setContentView(R.layout.activity_evaluation);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ctx = getApplicationContext();
        setTitle("Create Evaluations");
        mPager = (ViewPager) findViewById(R.id.SITE_pager);
        PagerTitleStrip strip = (PagerTitleStrip) findViewById(R.id.pager_title_strip);
        strip.setVisibility(View.GONE);
        mLocationClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        if (savedInstanceState != null) {
            response = (ResponseDTO) savedInstanceState.getSerializable("response");
        } else {
            response = (ResponseDTO) getIntent().getSerializableExtra("response");
        }
        codeStatus = getIntent().getIntExtra("statusCode", 0);
        buildPages();
    }

    private void buildPages() {

        pageFragmentList = new ArrayList<>();

        Bundle data = new Bundle();
        data.putSerializable("response", response);

        //gpsScanFragment.setArguments(data);
        evaluationFragment = new EvaluationFragment();
        evaluationFragment.setArguments(data);

        //  insectSelectorFragment = new InsectSelectorFragment();
        // insectSelectorFragment.setmSites(response.getInsectImageList());

        pageFragmentList.add(evaluationFragment);
        // pageFragmentList.add(insectSelectorFragment);
        // pageFragmentList.add(gpsScanFragment);
        //pageFragmentList.notify();
        initializeAdapter();

    }

    private int currentView;

    private void initializeAdapter() {
        try {
            adapter = new PagerAdapter(getSupportFragmentManager());
            mPager.setAdapter(adapter);
            mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageSelected(int arg0) {
                    if (currentView == 1) {
                        mPager.setCurrentItem(1);
                    } else {
                        mPager.setCurrentItem(0);
                    }

                }

                @Override
                public void onPageScrolled(int arg0, float arg1, int arg2) {

                }

                @Override
                public void onPageScrollStateChanged(int arg0) {
                }
            });
        } catch (Exception e) {
            Log.e(LOG, "-- Some shit happened, probably IllegalState of some kind ...");
        }
    }

    int codeStatus;
    RiverDTO river;


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("response", response);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_evaluation, menu);
        // WebCheckResult webCheckResult = WebCheck.checkNetworkAvailability(ctx);

        //getLocalData();
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

    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        switch (reqCode) {
            case MAP_REQUESTED:
                Log.w(LOG, "### map has returned with data?");
                if (resultCode == RESULT_OK) {
                    // evaluationSite = (EvaluationSiteDTO) data.getSerializableExtra("evaluationSite");
                    // projectSiteListFragment.updateSiteLocation(projectSite);
                    evaluationFragment.setResponse(response);
                }
                break;
            case STATUS_CODE:
                Log.w(LOG, "### setting ui has returned with data?");
                if (resultCode == RESULT_OK) {
                    response = (ResponseDTO) data.getSerializableExtra("response");
                    evaluationFragment.setResponse(response);
                }
                break;
            case CREATE_EVALUATION:
                Log.w(LOG, "### setting ui has returned with data?");
                if (resultCode == RESULT_OK) {
                    RiverDTO riv = (RiverDTO) data.getSerializableExtra("riverCreate");
                    //response = (ResponseDTO) data.getSerializableExtra("response");
                    evaluationFragment.setRiverField(riv);
                    evaluationFragment.setResponse(response);
                }
                break;
        }
    }

    static final int CREATE_EVALUATION = 108;


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


    }

    @Override
    protected void onDestroy() {
        overridePendingTransition(com.sifiso.codetribe.minisasslibrary.R.anim.slide_in_left, com.sifiso.codetribe.minisasslibrary.R.anim.slide_out_right);
        //  TimerUtil.killFlashTimer();
        super.onDestroy();
    }


    @Override
    public void onStop() {
        Log.d(LOG,
                "#################### onStop");
        if (mLocationClient != null) {
            if (mLocationClient.isConnected()) {
                stopPeriodicUpdates();
            }
            // After disconnect() is called, the client is considered "dead".
            mLocationClient.disconnect();
            Log.e(LOG, "### onStop - locationClient isConnected: "
                    + mLocationClient.isConnected());
        }

        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
        super.onStop();
    }

    @Override
    public void onLocationChanged(Location loc) {
        Log.w(LOG, "### Location changed, lat: "
                + loc.getLatitude() + " lng: "
                + loc.getLongitude()
                + " -- acc: " + loc.getAccuracy());
        mCurrentLocation = loc;
        if (gpsScanFragment != null) {
            Log.w(LOG, "### Passing location");
            gpsScanFragment.setLocation(loc);
        }
        if (loc.getAccuracy() <= ACCURACY_THRESHOLD) {
            location = loc;
            Log.w(LOG, "### Passing location2");
            //gpsScannerDialog.dismiss();
            gpsScanFragment.setLocation(loc);
            gpsScanFragment.stopScan();
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mLocationClient, this
            );
            Log.e(LOG, "+++ best accuracy found: " + location.getAccuracy());
        }

    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(LOG,
                "### ---> PlayServices onConnected() - gotta start something! >>");
        location = LocationServices.FusedLocationApi.getLastLocation(
                mLocationClient);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onDisconnected() {
        Log.e(LOG,
                "### ---> PlayServices onDisconnected");
    }

    @Override
    public void onStartScanRequested() {
        gpsScanFragment.startScan();
        // AE_pin_point.setVisibility(View.GONE);
        getGPSCoordinates();
    }

    EvaluationSiteDTO evaluationSite;

    @Override
    public void onLocationConfirmed(EvaluationSiteDTO es) {
        Log.w(LOG, "## asking GPSScanner to process confirmed location for site");
        evaluationFragment.setEvaluationSite(es);
        // AE_pin_point.setVisibility(View.VISIBLE);
       /* evaluationSite = new EvaluationSiteDTO();
        evaluationSite.setLocationConfirmed(1);
        evaluationSite.setLatitude(es.getLatitude());
        evaluationSite.setLongitude(es.getLongitude());
        evaluationSite.setAccuracy(es.getAccuracy());
        evaluationSite.setDateRegistered(new Date().getTime());
        evaluationSite.setCategoryID(categoryID);
        evaluationSite.setRiverID(riverID);

        Log.w(LOG, "## Evaluation site created");
        gpsScannerDialog.dismiss();
        stopPeriodicUpdates();*/
    }

    @Override
    public void onEndScanRequested() {
        Log.w(LOG, "## onEndScanRequested");
        getGPSCoordinates();

        stopPeriodicUpdates();
    }

    @Override
    public void onMapRequested(EvaluationSiteDTO evaluationSite) {
        if (evaluationSite.getLatitude() != null) {
            /*Intent i = new Intent(ctx, MonitorMapActivity.class);
            i.putExtra("projectSite", projectSite);
            startActivityForResult(i, MAP_REQUESTED);*/
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(LOG,
                "### ---> PlayServices onConnectionFailed: " + connectionResult.toString());
    }

    @Override
    public void onPause() {
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        super.onPause();
    }


    public void getData() {
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
                                public void onFileDataDeserialized(final ResponseDTO resp) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {

                                        }
                                    });

                                }

                                @Override
                                public void onDataCached() {
                                    //finish();
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

    @Override
    public void onScanGpsRequest() {
        Log.d(LOG, "Map Started");
        gpsScanFragment = new GPSScanFragment();
        pageFragmentList.add(gpsScanFragment);
        adapter.notifyDataSetChanged();
        mPager.setCurrentItem(1, true);
    }

    @Override
    public void onSelectInsectsRequest() {
        Log.d(LOG, "Map Insect");
        insectSelectorFragment = new InsectSelectorFragment();
        insectSelectorFragment.setmSites(response.getInsectImageList());
        pageFragmentList.add(insectSelectorFragment);
        adapter.notifyDataSetChanged();
        mPager.setCurrentItem(1, true);
    }

    @Override
    public void onDoneEvaluationRequest() {

    }


    @Override
    public void onSelectDone(List<InsectImageDTO> insectImages) {
        evaluationFragment.scoreUpdater(insectImages);
    }

    private class PagerAdapter extends FragmentStatePagerAdapter implements PageFragment {

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {

            return (Fragment) pageFragmentList.get(i);
        }

        @Override
        public int getCount() {
            return pageFragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            String title = "Title";

            switch (position) {
                case 0:

                    break;


                default:
                    break;
            }
            return title;
        }

        @Override
        public void animateCounts() {

        }
    }
}

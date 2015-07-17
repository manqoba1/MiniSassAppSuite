package com.sifiso.codetribe.riverteamapp;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.ErrorDialogFragment;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.sifiso.codetribe.minisasslibrary.R;
import com.sifiso.codetribe.minisasslibrary.activities.EvaluationActivity;
import com.sifiso.codetribe.minisasslibrary.activities.MapsActivity;
import com.sifiso.codetribe.minisasslibrary.activities.ProfileActivity;
import com.sifiso.codetribe.minisasslibrary.dto.EvaluationSiteDTO;
import com.sifiso.codetribe.minisasslibrary.dto.RiverDTO;
import com.sifiso.codetribe.minisasslibrary.dto.RiverPartDTO;
import com.sifiso.codetribe.minisasslibrary.dto.RiverPointDTO;
import com.sifiso.codetribe.minisasslibrary.dto.RiverTownDTO;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.RequestDTO;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.ResponseDTO;
import com.sifiso.codetribe.minisasslibrary.fragments.EvaluationListFragment;
import com.sifiso.codetribe.minisasslibrary.fragments.PageFragment;
import com.sifiso.codetribe.minisasslibrary.fragments.RiverListFragment;
import com.sifiso.codetribe.minisasslibrary.fragments.TownListFragment;
import com.sifiso.codetribe.minisasslibrary.services.CreateEvaluationListener;
import com.sifiso.codetribe.minisasslibrary.services.RequestSyncService;
import com.sifiso.codetribe.minisasslibrary.toolbox.BaseVolley;
import com.sifiso.codetribe.minisasslibrary.toolbox.WebCheck;
import com.sifiso.codetribe.minisasslibrary.toolbox.WebCheckResult;
import com.sifiso.codetribe.minisasslibrary.util.CacheUtil;
import com.sifiso.codetribe.minisasslibrary.util.ErrorUtil;
import com.sifiso.codetribe.minisasslibrary.util.SharedUtil;
import com.sifiso.codetribe.minisasslibrary.util.Statics;
import com.sifiso.codetribe.minisasslibrary.util.TimerUtil;
import com.sifiso.codetribe.minisasslibrary.util.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;


public class MainPagerActivity extends ActionBarActivity implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, CreateEvaluationListener {
    static final int NUM_ITEMS = 2;
    static final String LOG = MainPagerActivity.class.getSimpleName();
    Context ctx;
    ProgressBar progressBar;
    RiverListFragment riverListFragment;
    EvaluationListFragment evaluationListFragment;
    TownListFragment townListFragment;
    List<PageFragment> pageFragmentList;
    ViewPager mPager;
    Menu mMenu;
    PagerAdapter adapter;
    private ResponseDTO response;
    private TextView RL_add;

    GoogleApiClient mGoogleApiClient;
    LocationRequest locationRequest;
    Location location;
    boolean mResolvingError;
    static final long ONE_MINUTE = 1000 * 60;
    static final long FIVE_MINUTES = 1000 * 60 * 5;
    private static final int REQUEST_RESOLVE_ERROR = 1001;
    // Unique tag for the error dialog fragment
    private static final String DIALOG_ERROR = "dialog_error";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ctx = getApplicationContext();
        setField();


    }

    private void setField() {
        mPager = (ViewPager) findViewById(R.id.SITE_pager);
        RL_add = (TextView) findViewById(R.id.RL_add);
        RL_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.flashOnce(RL_add, 200, new Util.UtilAnimationListener() {
                    @Override
                    public void onAnimationEnded() {
                        Intent intent = new Intent(MainPagerActivity.this, EvaluationActivity.class);
                        intent.putExtra("statusCode", CREATE_EVALUATION);
                        startActivity(intent);
                    }
                });

            }
        });
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        // mPager.setOffscreenPageLimit(NUM_ITEMS - 1);

        PagerTitleStrip strip = (PagerTitleStrip) findViewById(R.id.pager_title_strip);
        strip.setVisibility(View.GONE);
    }

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            Log.w(LOG, "## RequestSyncService ServiceConnection: onServiceConnected");
            RequestSyncService.LocalBinder binder = (RequestSyncService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
            WebCheckResult wcr = WebCheck.checkNetworkAvailability(ctx, true);
            if (wcr.isWifiConnected()) {
                mService.startSyncCachedRequests(new RequestSyncService.RequestSyncListener() {
                    @Override
                    public void onTasksSynced(int goodResponses, int badResponses) {
                        Log.w(LOG, "@@ cached requests done, good: " + goodResponses + " bad: " + badResponses);
                        // getRiversAroundMe();
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
    boolean mBound;
    RequestSyncService mService;

    @Override
    protected void onStop() {
        Log.d(LOG,
                "#################### onStop");


        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
        Log.w(LOG, "############## onStop stopping google service clients");
        try {
            mGoogleApiClient.disconnect();
        } catch (Exception e) {
            Log.e(LOG, "Failed to Stop something", e);
        }

        super.onStop();
    }


    static final int STATUS_CODE = 220;


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("response", response);
        super.onSaveInstanceState(outState);
    }

    private void buildPages() {
//        calculateDistances();
        pageFragmentList = new ArrayList<PageFragment>();
        riverListFragment = new RiverListFragment();
        Bundle data = new Bundle();
        data.putSerializable("response", response);
        data.putSerializable("evaluationSite", (java.io.Serializable) evaluationSite);

        riverListFragment.setArguments(data);


        pageFragmentList.add(riverListFragment);
        // pageFragmentList.add(evaluationListFragment);

        initializeAdapter();


    }


    @Override
    protected void onStart() {
        Log.e(LOG, "################ onStart .... connect API and location clients ");
        if (!mResolvingError) {  // more about this later
            mGoogleApiClient.connect();
        }
        TimerUtil.killFlashTimer();

        Intent intent = new Intent(MainPagerActivity.this, RequestSyncService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        Log.d(LOG,
                "#################### onStart");
        super.onStart();
    }

    private void initializeAdapter() {
        try {
            adapter = new PagerAdapter(getSupportFragmentManager());
            mPager.setAdapter(adapter);
            mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageSelected(int arg0) {
                    PageFragment pf = pageFragmentList.get(arg0);
                    if (pf instanceof EvaluationListFragment) {
                        if (pf.equals(townListFragment)) {
                            pageFragmentList.remove(evaluationListFragment);
                            adapter.notifyDataSetChanged();
                        }
                        if (pf.equals(riverListFragment)) {
                            pageFragmentList.remove(riverListFragment);
                            adapter.notifyDataSetChanged();
                        }
                        getSupportActionBar().setTitle(evaluationListFragment.getRiverName());
                        isBack = false;
                    } else if (pf instanceof RiverListFragment) {
                        getSupportActionBar().setTitle("MiniSASS ");
                        if (pf.equals(evaluationListFragment)) {
                            pageFragmentList.remove(evaluationListFragment);
                            adapter.notifyDataSetChanged();
                        }
                        if (pf.equals(townListFragment)) {
                            pageFragmentList.remove(townListFragment);
                            adapter.notifyDataSetChanged();
                        }
                        isBack = false;
                    } else if (pf instanceof TownListFragment) {
                        if (pf.equals(evaluationListFragment)) {
                            pageFragmentList.remove(riverListFragment);
                            adapter.notifyDataSetChanged();
                        }
                        if (pf.equals(evaluationListFragment)) {
                            pageFragmentList.remove(evaluationListFragment);
                            adapter.notifyDataSetChanged();
                        }
                        getSupportActionBar().setTitle(townListFragment.getTownName() + " Towns");
                        isBack = false;
                    }

                }

                @Override
                public void onPageScrolled(int arg0, float arg1, int arg2) {

                }

                @Override
                public void onPageScrollStateChanged(int arg0) {
                }
            });
            //ZoomOutPageTransformerImpl z = new ZoomOutPageTransformerImpl();
            // mPager.setPageTransformer(true, z);
        } catch (Exception e) {
            Log.e(LOG, "-- Some shit happened, probably IllegalState of some kind ...");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        mMenu = menu;
        //TimerUtil.killFlashTimer();
        // getLocalData();
        getCachedRiverData();
        // startActivity(new Intent(MainPagerActivity.this, SplashActivity.class));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.profile:
                Intent pro = new Intent(MainPagerActivity.this, Profile.class);
                startActivity(pro);
                return true;
            case R.id.log_out:
                SharedUtil.clearTeam(ctx);
                Intent intent = new Intent(MainPagerActivity.this, SignActivity.class);
                startActivity(intent);
                finish();
                return true;


        }
       /* switch (id) {
            case R.id.add_member:
                break;
            case R.id.log_out:
                break;
            default:

        };*/


        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        overridePendingTransition(com.sifiso.codetribe.minisasslibrary.R.anim.slide_in_left, com.sifiso.codetribe.minisasslibrary.R.anim.slide_out_right);

        super.onPause();
    }

    @Override
    protected void onResume() {
        overridePendingTransition(com.sifiso.codetribe.minisasslibrary.R.anim.slide_out_left, com.sifiso.codetribe.minisasslibrary.R.anim.slide_in_right);

        super.onResume();
    }


    private List<EvaluationSiteDTO> evaluationSite;

    @Override
    public void onRefreshEvaluation(List<EvaluationSiteDTO> siteList, int index) {
        Log.d(LOG, "onclick" + siteList.toString());
        if (siteList.size() == 0) {
            return;
        }
        if (pageFragmentList.size() > 1) {
            pageFragmentList.remove(pageFragmentList.size() - 1);
            adapter.notifyDataSetChanged();
        }
        Bundle b = new Bundle();

        b.putSerializable("evaluationSite", (java.io.Serializable) siteList);
        b.putSerializable("response", response);
        evaluationListFragment = new EvaluationListFragment();
        evaluationListFragment.setArguments(b);
        currentView = 1;
        pageFragmentList.add(evaluationListFragment);
        initializeAdapter();
        adapter.notifyDataSetChanged();
        mPager.setCurrentItem(currentView);
        // mPager.
        //  evaluationListFragment.setEvaluation(siteList);
    }


    @Override
    public void onRefreshMap(RiverDTO river, int result) {
        Intent intent = new Intent(MainPagerActivity.this, MapsActivity.class);
        intent.putExtra("river", river);
        intent.putExtra("displayType", result);
        startActivity(intent);
    }


    @Override
    public void onCreateEvaluation(RiverDTO river) {
        // ToastUtil.toast(ctx, river.getRiverName());
        Intent createEva = new Intent(MainPagerActivity.this, EvaluationActivity.class);
        createEva.putExtra("riverCreate", river);
        createEva.putExtra("response", response);
        createEva.putExtra("statusCode", CREATE_EVALUATION);
        startActivityForResult(createEva, CREATE_EVALUATION);
    }

    @Override
    public void onDirection(Double latitude, Double longitude) {
        startDirectionsMap(latitude, longitude);
    }

    private void startDirectionsMap(double lat, double lng) {
        Log.i(LOG, "startDirectionsMap ..........");
        String url = "http://maps.google.com/maps?saddr="
                + location.getLatitude() + "," + location.getLongitude()
                + "&daddr=" + lat + "," + lng + "&mode=driving";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
        startActivity(intent);
    }

    static final int CREATE_EVALUATION = 108;
    static final int RIVER_VIEW = 13;
    private int currentView;


    boolean mRequestingLocationUpdates;

    protected void startLocationUpdates() {
        Log.w(LOG, "###### startLocationUpdates: " + new Date().toString());
        if (mGoogleApiClient.isConnected()) {
            mRequestingLocationUpdates = true;
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, locationRequest, this);
        }
    }

    static final int ACCURACY_LIMIT = 50;

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
        setLoc(location);
        if (location.getAccuracy() <= ACCURACY_LIMIT) {
            setLoc(location);
            stopLocationUpdates();
            getCachedRiverData();
            // getRiversAroundMe();
        }
        Log.e(LOG, "####### onLocationChanged");
    }


    void setLoc(Location loc) {
        this.location = loc;
        // Log.v(LOG + " network",location.getLongitude()+ " = " + isNetworkEnabled);
        // getCachedRiverData();
    }

    protected void stopLocationUpdates() {
        Log.e(LOG, "###### stopLocationUpdates - " + new Date().toString());
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);
        }
    }


    @Override
    public void onConnected(Bundle bundle) {
        Log.i(LOG, "+++  onConnected() -  requestLocationUpdates ...");
        location = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (location != null) {
            Log.w(LOG, "## requesting location ....lastLocation: "
                    + location.getLatitude() + " "
                    + location.getLongitude() + " acc: "
                    + location.getAccuracy());
        }
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setFastestInterval(2000);

        if (location.getAccuracy() > ACCURACY_LIMIT) {
            startLocationUpdates();
        } else {
            getCachedRiverData();
            // getRiversAroundMe();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }


    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (mResolvingError) {
            // Already attempting to resolve an error.
            return;
        } else if (result.hasResolution()) {
            try {
                mResolvingError = true;
                result.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) {
                // There was an error with the resolution intent. Try again.
                mGoogleApiClient.connect();
            }
        } else {
            // Show dialog using GooglePlayServicesUtil.getErrorDialog()
            showErrorDialog(result.getErrorCode());
            mResolvingError = true;
        }
    }

    /* Creates a dialog for an error message */
    private void showErrorDialog(int errorCode) {
        // Create a fragment for the error dialog
        ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
        // Pass the error that should be displayed
        Bundle args = new Bundle();
        args.putInt(DIALOG_ERROR, errorCode);
        dialogFragment.setArguments(args);
        dialogFragment.show(getFragmentManager(), "errordialog");
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


    boolean isBusy;

    private void calculateDistancesForSite() {
        if (location != null) {
            List<EvaluationSiteDTO> riverPoints = new ArrayList<>();
            Location spot = new Location(LocationManager.GPS_PROVIDER);

            for (EvaluationSiteDTO w : response.getEvaluationSiteList()) {
                spot.setLatitude(w.getLatitude());
                spot.setLongitude(w.getLongitude());
                w.setDistanceFromMe(location.distanceTo(spot));
            }
            Collections.sort(response.getEvaluationSiteList());
        }
    }

    private void getCachedRiverData() {
        final WebCheckResult w = WebCheck.checkNetworkAvailability(ctx);
        CacheUtil.getCachedData(ctx, CacheUtil.CACHE_DATA, new CacheUtil.CacheUtilListener() {
            @Override
            public void onFileDataDeserialized(final ResponseDTO respond) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //Log.d(LOG, new Gson().toJson(respond.getRiverList().get(0)));

                        response = respond;
                        if (response != null) {

                            buildPages();
                        }
                        if (w.isWifiConnected() || w.isMobileConnected()) {
                            getRiversAroundMe();
                        }
                    }
                });


            }

            @Override
            public void onDataCached(ResponseDTO r) {

            }

            @Override
            public void onError() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (w.isWifiConnected() || w.isMobileConnected()) {
                            getRiversAroundMe();
                        }
                    }
                });

            }
        });


    }

    private void getRiversAroundMe() {
        if (location == null) {
            Toast.makeText(ctx, "Busy...getting rivers ...t", Toast.LENGTH_SHORT).show();
            // getRiversAroundMe();
            return;
        }
        if (isBusy) {
            Toast.makeText(ctx, "Busy...getting rivers2 ...", Toast.LENGTH_SHORT).show();
            return;
        }

//TODO remember to change back to location getLatitude and longitude
        RequestDTO w = new RequestDTO();
        w.setRequestType(RequestDTO.LIST_DATA_WITH_RADIUS_RIVERS);
        /*w.setLatitude(-26.30566667);
        w.setLongitude(28.01558333);
        w.setRadius(5);*/
        w.setLatitude(location.getLatitude());
        w.setLongitude(location.getLongitude());
        w.setRadius(5);
        isBusy = true;

        BaseVolley.getRemoteData(Statics.SERVLET_ENDPOINT, w, ctx, new BaseVolley.BohaVolleyListener() {
            @Override
            public void onResponseReceived(ResponseDTO r) {
                isBusy = false;
//              progressBar.setVisibility(View.GONE);
                Log.e(LOG, "## getStarterData responded...statusCode: " + r.getStatusCode());
                if (!ErrorUtil.checkServerError(ctx, r)) {
                    return;
                }

                CacheUtil.cacheData(ctx, r, CacheUtil.CACHE_DATA, new CacheUtil.CacheUtilListener() {
                    @Override
                    public void onFileDataDeserialized(final ResponseDTO resp) {

                    }

                    @Override
                    public void onDataCached(ResponseDTO r) {
                                    /*Intent intent = new Intent(getApplicationContext(), RequestSyncService.class);
                                    startService(intent);*/
                        // getData();
                        response = r;
                        buildPages();
                    }

                    @Override
                    public void onError() {

                    }
                });
            }

            @Override
            public void onVolleyError(VolleyError error) {
                isBusy = false;
                Toast.makeText(ctx, "Problem: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });


    }

    public void getData() {
        RequestDTO w = new RequestDTO();
        w.setRequestType(RequestDTO.GET_DATA);

        BaseVolley.getRemoteData(Statics.SERVLET_ENDPOINT, w, ctx, new BaseVolley.BohaVolleyListener() {
            @Override
            public void onResponseReceived(final ResponseDTO r) {
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

                            }

                            @Override
                            public void onDataCached(ResponseDTO r) {
                                    /*Intent intent = new Intent(getApplicationContext(), RequestSyncService.class);
                                    startService(intent);*/
                                // getData();
                                response = r;
                                buildPages();
                            }

                            @Override
                            public void onError() {

                            }
                        });
                            /*Intent intent = new Intent(getApplicationContext(), RequestSyncService.class);
                            startService(intent);*/
                    }
                });
            }

            @Override
            public void onVolleyError(VolleyError error) {
                isBusy = false;
                Toast.makeText(ctx, "Problem: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });


    }


    boolean isBack = false;

    @Override
    public void onBackPressed() {
        currentView = 0;
        mPager.setCurrentItem(currentView, true);
        if (isBack) {
            super.onBackPressed();
        }
        isBack = true;
    }

    private void calculateDistances() {
        if (location != null) {
            List<RiverPointDTO> riverPoints = new ArrayList<>();
            Location spot = new Location(LocationManager.GPS_PROVIDER);

            for (RiverDTO w : response.getRiverList()) {
                for (RiverPartDTO x : w.getRiverpartList()) {
                    for (RiverPointDTO y : x.getRiverpointList()) {
                        spot.setLatitude(y.getLatitude());
                        spot.setLongitude(y.getLongitude());
                        y.setDistanceFromMe(location.distanceTo(spot));
                    }
                    Collections.sort(x.getRiverpointList());
                    x.setNearestLatitude(x.getRiverpointList().get(0).getLatitude());
                    x.setNearestLongitude(x.getRiverpointList().get(0).getLongitude());
                    x.setDistanceFromMe(x.getRiverpointList().get(0).getDistanceFromMe());
                }
                Collections.sort(w.getRiverpartList());
                w.setNearestLatitude(w.getRiverpartList().get(0).getNearestLatitude());
                w.setNearestLongitude(w.getRiverpartList().get(0).getNearestLongitude());
                w.setDistanceFromMe(w.getRiverpartList().get(0).getDistanceFromMe());


            }
            Collections.sort(response.getRiverList());
        }
    }
}

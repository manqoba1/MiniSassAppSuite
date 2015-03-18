package com.sifiso.codetribe.riverteamapp;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.location.Location;
import android.os.Bundle;
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

import com.google.android.gms.common.api.GoogleApiClient;
import com.sifiso.codetribe.minisasslibrary.activities.MapsActivity;
import com.sifiso.codetribe.minisasslibrary.dto.EvaluationSiteDTO;
import com.sifiso.codetribe.minisasslibrary.dto.RiverDTO;
import com.sifiso.codetribe.minisasslibrary.dto.RiverTownDTO;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.RequestDTO;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.ResponseDTO;
import com.sifiso.codetribe.minisasslibrary.fragments.EvaluationListFragment;
import com.sifiso.codetribe.minisasslibrary.fragments.PageFragment;
import com.sifiso.codetribe.minisasslibrary.fragments.RiverListFragment;
import com.sifiso.codetribe.minisasslibrary.toolbox.WebCheck;
import com.sifiso.codetribe.minisasslibrary.toolbox.WebCheckResult;
import com.sifiso.codetribe.minisasslibrary.util.CacheUtil;
import com.sifiso.codetribe.minisasslibrary.util.ErrorUtil;
import com.sifiso.codetribe.minisasslibrary.util.Statics;
import com.sifiso.codetribe.minisasslibrary.util.WebSocketUtil;

import java.util.ArrayList;
import java.util.List;


public class MainPagerActivity extends ActionBarActivity implements EvaluationListFragment.EvaluationListFragmentListener, RiverListFragment.RiverListFragmentListener {
    static final int NUM_ITEMS = 2;
    static final String LOG = MainPagerActivity.class.getSimpleName();
    Context ctx;
    ProgressBar progressBar;
    RiverListFragment riverListFragment;
    EvaluationListFragment evaluationListFragment;
    List<PageFragment> pageFragmentList;
    ViewPager mPager;
    Menu mMenu;
    PagerAdapter adapter;
    Location mCurrentLocation;
    GoogleApiClient mLocationClient;
    private ResponseDTO response;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ctx = getApplicationContext();
        mPager = (ViewPager) findViewById(R.id.SITE_pager);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        // mPager.setOffscreenPageLimit(NUM_ITEMS - 1);

        PagerTitleStrip strip = (PagerTitleStrip) findViewById(R.id.pager_title_strip);
        strip.setVisibility(View.GONE);


    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("response", response);
        super.onSaveInstanceState(outState);
    }

    private void buildPages() {

        pageFragmentList = new ArrayList<>();
        riverListFragment = new RiverListFragment();
        Bundle data = new Bundle();
        data.putSerializable("response", response);
        data.putSerializable("evaluationSite", (java.io.Serializable) evaluationSite);

        riverListFragment.setArguments(data);
        evaluationListFragment = new EvaluationListFragment();
        evaluationListFragment.setArguments(data);


        pageFragmentList.add(riverListFragment);
        pageFragmentList.add(evaluationListFragment);
        initializeAdapter();

    }

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        mMenu = menu;
        getLocalData();
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
        if (id == R.id.action_settings) {
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

    private void getLocalData() {
        final WebCheckResult w = WebCheck.checkNetworkAvailability(ctx);
        CacheUtil.getCachedData(ctx, CacheUtil.CACHE_DATA, new CacheUtil.CacheUtilListener() {
            @Override
            public void onFileDataDeserialized(final ResponseDTO respond) {
                response = respond;
                if (response != null) {

                    buildPages();
                }
                if (w.isWifiConnected()) {
                    getData();
                }

            }

            @Override
            public void onDataCached() {

            }

            @Override
            public void onError() {
                if (w.isWifiConnected()) {
                    getData();
                }
            }
        });

    }

    public void getData() {
        RequestDTO req = new RequestDTO();
        req.setRequestType(RequestDTO.GET_DATA);

        try {

            WebSocketUtil.sendRequest(ctx, Statics.MINI_SASS_ENDPOINT, req, new WebSocketUtil.WebSocketListener() {
                @Override
                public void onMessage(final ResponseDTO r) {

                    Log.e(LOG, "## getStarterData responded...statusCode: " + r.getStatusCode());
                    if (!ErrorUtil.checkServerError(ctx, r)) {
                        return;
                    }
                    response = r;
                    CacheUtil.cacheData(ctx, r, CacheUtil.CACHE_DATA, new CacheUtil.CacheUtilListener() {
                        @Override
                        public void onFileDataDeserialized(final ResponseDTO resp) {


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


    private List<EvaluationSiteDTO> evaluationSite;

    @Override
    public void onRefreshEvaluation(List<EvaluationSiteDTO> siteList, int index) {
        Log.d(LOG, "onclick" + siteList.toString());
        evaluationSite = siteList;

        currentView = 0;
        initializeAdapter();
        // mPager.setCurrentItem(1);
        //  evaluationListFragment.setEvaluation(siteList);
    }

    @Override
    public void onRefreshTown(List<RiverTownDTO> riverTownList, int index) {

    }

    @Override
    public void onRefreshMap(RiverDTO river,int result) {
        startActivity(new Intent(MainPagerActivity.this, MapsActivity.class).putExtra("river",river));
    }

    @Override
    public void onCreateEvaluationRL(RiverDTO river) {

    }

    private int currentView;

    @Override
    public void onCreateEvaluation(ResponseDTO response) {

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

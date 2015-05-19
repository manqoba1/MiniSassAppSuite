package com.sifiso.codetribe.riverteamapp;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.common.api.GoogleApiClient;
import com.sifiso.codetribe.minisasslibrary.MiniSassApp;
import com.sifiso.codetribe.minisasslibrary.activities.EvaluationActivity;
import com.sifiso.codetribe.minisasslibrary.activities.GPSscanner;
import com.sifiso.codetribe.minisasslibrary.activities.MapsActivity;
import com.sifiso.codetribe.minisasslibrary.dto.EvaluationSiteDTO;
import com.sifiso.codetribe.minisasslibrary.dto.RiverDTO;
import com.sifiso.codetribe.minisasslibrary.dto.RiverTownDTO;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.RequestDTO;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.ResponseDTO;
import com.sifiso.codetribe.minisasslibrary.fragments.EvaluationListFragment;
import com.sifiso.codetribe.minisasslibrary.fragments.PageFragment;
import com.sifiso.codetribe.minisasslibrary.fragments.RiverListFragment;
import com.sifiso.codetribe.minisasslibrary.fragments.TownListFragment;
import com.sifiso.codetribe.minisasslibrary.services.CreateEvaluationListener;
import com.sifiso.codetribe.minisasslibrary.services.RequestSyncService;
import com.sifiso.codetribe.minisasslibrary.toolbox.WebCheck;
import com.sifiso.codetribe.minisasslibrary.toolbox.WebCheckResult;
import com.sifiso.codetribe.minisasslibrary.util.CacheUtil;
import com.sifiso.codetribe.minisasslibrary.util.ErrorUtil;
import com.sifiso.codetribe.minisasslibrary.util.Statics;
import com.sifiso.codetribe.minisasslibrary.util.TimerUtil;
import com.sifiso.codetribe.minisasslibrary.util.ToastUtil;
import com.sifiso.codetribe.minisasslibrary.util.Util;
import com.sifiso.codetribe.minisasslibrary.util.WebSocketUtil;
import com.sifiso.codetribe.minisasslibrary.R;
import com.sifiso.codetribe.minisasslibrary.viewsUtil.ZoomOutPageTransformerImpl;

import java.util.ArrayList;
import java.util.List;


public class MainPagerActivity extends ActionBarActivity implements CreateEvaluationListener {
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
    boolean mBound;
    RequestSyncService mService;

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(LOG,
                "#################### onStop");
        if (mBound) {

            unbindService(mConnection);
            mBound = false;
        }

    }


    static final int STATUS_CODE = 220;


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("response", response);
        super.onSaveInstanceState(outState);
    }

    private void buildPages() {

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
        super.onStart();
        TimerUtil.killFlashTimer();

        Intent intent = new Intent(MainPagerActivity.this, RequestSyncService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        Log.d(LOG,
                "#################### onStart");

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
        if (id == R.id.add_member) {
            /*Intent intent = new Intent(MainPagerActivity.this, TeamMemberActivity.class);
            startActivity(intent);*/
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
    public void onRefreshTown(List<RiverTownDTO> riverTownList, int index) {
        if (riverTownList.size() == 0) {
            return;
        }
        if (pageFragmentList.size() > 1) {
            pageFragmentList.remove(pageFragmentList.size() - 1);
            adapter.notifyDataSetChanged();
        }
        townListFragment = new TownListFragment();
        Bundle b = new Bundle();
        b.putSerializable("riverTown", (java.io.Serializable) riverTownList);
        b.putSerializable("response", response);
        currentView = 1;
        townListFragment.setArguments(b);
        pageFragmentList.add(townListFragment);
        initializeAdapter();
        adapter.notifyDataSetChanged();
        mPager.setCurrentItem(currentView);
    }

    @Override
    public void onRefreshMap(RiverDTO river, int result) {
        Intent intent = new Intent(MainPagerActivity.this, MapsActivity.class);
        intent.putExtra("river", river);
        intent.putExtra("displayType", result);
        startActivity(intent);
    }


    @Override
    public void onCreateEvaluationRL(RiverDTO river) {
        // ToastUtil.toast(ctx, river.getRiverName());
        Intent createEva = new Intent(MainPagerActivity.this, EvaluationActivity.class);
        createEva.putExtra("riverCreate", river);
        createEva.putExtra("response", response);
        createEva.putExtra("statusCode", CREATE_EVALUATION);
        startActivityForResult(createEva, CREATE_EVALUATION);
    }

    static final int CREATE_EVALUATION = 108;
    static final int RIVER_VIEW = 13;
    private int currentView;

    @Override
    public void onCreateEvaluation(ResponseDTO response) {
        Intent intent = new Intent(MainPagerActivity.this, EvaluationActivity.class);
        intent.putExtra("statusCode", CREATE_EVALUATION);
        startActivity(intent);
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

    private void getLocalData() {
        final WebCheckResult w = WebCheck.checkNetworkAvailability(ctx);
        CacheUtil.getCachedData(ctx, CacheUtil.CACHE_DATA, new CacheUtil.CacheUtilListener() {
            @Override
            public void onFileDataDeserialized(final ResponseDTO respond) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        response = respond;
                        if (response != null) {

                            buildPages();
                        }
                        if (w.isWifiConnected()) {
                            getData();
                        }
                    }
                });


            }

            @Override
            public void onDataCached() {

            }

            @Override
            public void onError() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (w.isWifiConnected()) {
                            getData();
                        }
                    }
                });

            }
        });

    }

    public void getData() {
        RequestDTO req = new RequestDTO();
        req.setRequestType(RequestDTO.GET_DATA);
        req.setCountryCode("ZA");
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
                                public void onFileDataDeserialized(final ResponseDTO resp) {

                                }

                                @Override
                                public void onDataCached() {
                                    /*Intent intent = new Intent(getApplicationContext(), RequestSyncService.class);
                                    startService(intent);*/
                                    // getData();
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
                public void onClose() {

                }

                @Override
                public void onError(final String message) {

                }
            });

        } catch (Exception e) {

        }
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
}

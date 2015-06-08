package com.sifiso.codetribe.riverteamapp;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
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
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.sifiso.codetribe.minisasslibrary.dto.GcmDeviceDTO;
import com.sifiso.codetribe.minisasslibrary.dto.TeamDTO;
import com.sifiso.codetribe.minisasslibrary.dto.TeamMemberDTO;
import com.sifiso.codetribe.minisasslibrary.dto.TownDTO;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.RequestDTO;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.ResponseDTO;
import com.sifiso.codetribe.minisasslibrary.fragments.PageFragment;
import com.sifiso.codetribe.minisasslibrary.fragments.RegisterFragment;
import com.sifiso.codetribe.minisasslibrary.fragments.SearchTownFragment;
import com.sifiso.codetribe.minisasslibrary.toolbox.WebCheck;
import com.sifiso.codetribe.minisasslibrary.toolbox.WebCheckResult;
import com.sifiso.codetribe.minisasslibrary.util.CacheUtil;
import com.sifiso.codetribe.minisasslibrary.util.ErrorUtil;
import com.sifiso.codetribe.minisasslibrary.util.Statics;
import com.sifiso.codetribe.minisasslibrary.util.Util;
import com.sifiso.codetribe.minisasslibrary.util.WebSocketUtil;
import com.sifiso.codetribe.minisasslibrary.R;

import java.util.ArrayList;
import java.util.List;


public class RegisterActivity extends ActionBarActivity implements SearchTownFragment.SearchTownFragmentListener, RegisterFragment.RegisterFragmentListener {

    Context ctx;
    Activity activity;
    List<PageFragment> pageFragmentList;
    RegisterFragment registerFragment;
    SearchTownFragment searchTownFragment;
    ViewPager mPager;
    Menu mMenu;
    PagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        activity = this;
        ctx = getApplicationContext();
        countryCode = "ZA";
        mPager = (ViewPager) findViewById(R.id.SITE_pager);


        PagerTitleStrip strip = (PagerTitleStrip) findViewById(R.id.pager_title_strip);
        strip.setVisibility(View.GONE);
        // buildPages();
    }

    private void buildPages() {

        pageFragmentList = new ArrayList<PageFragment>();
        registerFragment = new RegisterFragment();
        Bundle data = new Bundle();
        data.putSerializable("response", response);

        registerFragment.setArguments(data);
        searchTownFragment = new SearchTownFragment();
        searchTownFragment.setArguments(data);


        pageFragmentList.add(registerFragment);
        // pageFragmentList.add(searchTownFragment);
        initializeAdapter();

    }

    private void initializeAdapter() {
        try {
            adapter = new PagerAdapter(getSupportFragmentManager());
            mPager.setAdapter(adapter);
            mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageSelected(int arg0) {
                    // currentViewPager = arg0;
                    PageFragment pf = pageFragmentList.get(arg0);
                    if (pf instanceof RegisterFragment) {
                        isBack = false;
                        getSupportActionBar().setTitle("Sign up member");
                        pageFragmentList.remove(searchTownFragment);
                        adapter.notifyDataSetChanged();
                    } else if (pf instanceof SearchTownFragment) {
                        isBack = false;
                        getSupportActionBar().setTitle("Search town");
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
        getMenuInflater().inflate(R.menu.menu_register, menu);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Team member");
        getCachedData();
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
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean checkPlayServices() {
        Log.w(LOG, "checking GooglePlayServices .................");
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(ctx);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                // GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                //         PLAY_SERVICES_RESOLUTION_REQUEST).show();
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.google.android.gms")));
                return false;
            } else {
                Log.i(LOG, "This device is not supported.");
                throw new UnsupportedOperationException("GooglePlayServicesUtil resultCode: " + resultCode);
            }
        }
        return true;
    }


    static final String LOG = "RegistrationActivity";
    ResponseDTO response;

    private void getCachedData() {
        final WebCheckResult wcr = WebCheck.checkNetworkAvailability(ctx);

        CacheUtil.getCachedRegisterData(getApplicationContext(), CacheUtil.CACHE_REGISTER_DATA, new CacheUtil.CacheUtilListener() {

            @Override
            public void onFileDataDeserialized(ResponseDTO r) {
                Log.d(LOG, r.getTownList().toString() + "");

                response = r;
                buildPages();
                if (wcr.isWifiConnected()) {
                    getRegistrationData();
                }
            }

            @Override
            public void onDataCached(ResponseDTO r) {
                buildPages();
            }

            @Override
            public void onError() {

            }
        });

    }

    String countryCode;

    public void getRegistrationData() {

        RequestDTO req = new RequestDTO();
        req.setRequestType(RequestDTO.LIST_REGISTER_DATA);
        try {

            WebSocketUtil.sendRequest(ctx, Statics.MINI_SASS_ENDPOINT, req, new WebSocketUtil.WebSocketListener() {
                @Override
                public void onMessage(final ResponseDTO r) {

                    Log.e(LOG, "## getStarterData responded...statusCode: " + r.getStatusCode());
                    if (!ErrorUtil.checkServerError(ctx, r)) {
                        return;
                    }
                    CacheUtil.cacheRegisterData(ctx, r, CacheUtil.CACHE_REGISTER_DATA, new CacheUtil.CacheUtilListener() {
                        @Override
                        public void onFileDataDeserialized(final ResponseDTO resp) {


                        }

                        @Override
                        public void onDataCached(ResponseDTO r) {
                            response = r;
                            buildPages();
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

    @Override
    public void onRegistered() {
        Intent intent = new Intent(RegisterActivity.this, SignActivity.class);
        startActivity(intent);
        finish();
    }

    private int currentViewPager;

    @Override
    public void onTownRequest() {
        currentViewPager = 1;
        pageFragmentList.add(searchTownFragment);
        adapter.notifyDataSetChanged();
        mPager.setCurrentItem(currentViewPager, true);
    }

    @Override
    public void onTownSelected(TownDTO town) {

        pageFragmentList.remove(searchTownFragment);
        adapter.notifyDataSetChanged();
        currentViewPager = 0;
        isBack = true;
        mPager.setCurrentItem(currentViewPager, true);
    }

    boolean isBack = false;

    @Override
    public void onBackPressed() {
        currentViewPager = 0;
        mPager.setCurrentItem(currentViewPager, true);
        if (isBack) {
            super.onBackPressed();
        }
        isBack = true;
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

package com.sifiso.codetribe.riverteamapp;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.sifiso.codetribe.minisasslibrary.activities.MapsActivity;
import com.sifiso.codetribe.minisasslibrary.dto.EvaluationSiteDTO;
import com.sifiso.codetribe.minisasslibrary.dto.RiverDTO;
import com.sifiso.codetribe.minisasslibrary.dto.RiverTownDTO;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.RequestDTO;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.ResponseDTO;
import com.sifiso.codetribe.minisasslibrary.fragments.EvaluationListFragment;
import com.sifiso.codetribe.minisasslibrary.fragments.RiverListFragment;
import com.sifiso.codetribe.minisasslibrary.fragments.TownListFragment;
import com.sifiso.codetribe.minisasslibrary.toolbox.WebCheck;
import com.sifiso.codetribe.minisasslibrary.toolbox.WebCheckResult;
import com.sifiso.codetribe.minisasslibrary.util.CacheUtil;
import com.sifiso.codetribe.minisasslibrary.util.ErrorUtil;
import com.sifiso.codetribe.minisasslibrary.util.Statics;
import com.sifiso.codetribe.minisasslibrary.util.WebSocketUtil;

import java.util.List;


public class EvaluationView extends ActionBarActivity implements RiverListFragment.RiverListFragmentListener, EvaluationListFragment.EvaluationListFragmentListener {

    Menu mMenu;
    Context ctx;
    static final String LOG = EvaluationView.class.getSimpleName();
    private List<EvaluationSiteDTO> evaluationSite;
    private ResponseDTO response;
    RiverListFragment riverListFragment;
    EvaluationListFragment evaluationListFragment;
    TownListFragment townListFragment;
    private TextView RL_add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluation_view);
        ctx = getApplicationContext();
        RL_add = (TextView) findViewById(R.id.RL_add);
        RL_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent evaluateIntent = new Intent(EvaluationView.this, Evaluation.class);

                evaluateIntent.putExtra("response", response);
                startActivityForResult(evaluateIntent, STATUS_CODE);
            }
        });

    }

    @Override
    protected void onPause() {
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_evaluation_view, menu);
        mMenu = menu;
        getLocalData();
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
    public void onRefreshEvaluation(List<EvaluationSiteDTO> siteList, int index) {
        if (siteList.size() == 0) {
            return;
        }

        FragmentManager fm = getSupportFragmentManager();
        Log.d(LOG, "onclick+" + index);
        FragmentTransaction ft = fm.beginTransaction();
        evaluationListFragment = new EvaluationListFragment();
        Bundle b = new Bundle();
        b.putSerializable("evaluationSite", (java.io.Serializable) siteList);
        b.putSerializable("response", response);
        evaluationListFragment.setArguments(b);
        //setTitle(siteList.get(0).getRiverName()+" Evaluations");
        ft.replace(R.id.listcontainer, evaluationListFragment);

        ft.addToBackStack("listcontainer");
        ft.commit();
    }

    @Override
    public void onRefreshTown(List<RiverTownDTO> riverTownList, int index) {
        if (riverTownList.size() == 0) {
            return;
        }
        FragmentManager fm = getSupportFragmentManager();
        Log.d(LOG, "onclick" + index);
        FragmentTransaction ft = fm.beginTransaction();
        townListFragment = new TownListFragment();
        Bundle b = new Bundle();
        b.putSerializable("riverTown", (java.io.Serializable) riverTownList);
        b.putSerializable("response", response);
        townListFragment.setArguments(b);
        //setTitle(siteList.get(0).getRiverName()+" Evaluations");
        ft.replace(R.id.listcontainer, townListFragment);

        ft.addToBackStack("listcontainer");
        ft.commit();
    }

    @Override
    public void onRefreshMap(RiverDTO river, int result) {
        Intent intent = new Intent(EvaluationView.this, MapsActivity.class);
        intent.putExtra("river", river);
        intent.putExtra("displayType", result);
        startActivity(intent);

    }

    @Override
    public void onCreateEvaluationRL(RiverDTO river) {
        Intent createEva = new Intent(EvaluationView.this, Evaluation.class);
        createEva.putExtra("riverCreate", river);
        createEva.putExtra("response", response);
        createEva.putExtra("statusCode", CREATE_EVALUATION);
        startActivityForResult(createEva, CREATE_EVALUATION);
    }

    static final int CREATE_EVALUATION = 108;
    static final int RIVER_VIEW = 13;

    private void build() {
        FragmentManager fm = getSupportFragmentManager();

// add
        FragmentTransaction ft = fm.beginTransaction();
        riverListFragment = new RiverListFragment();
        Bundle b = new Bundle();
        b.putSerializable("response", response);
        riverListFragment.setArguments(b);
        ft.add(R.id.listcontainer, riverListFragment);
        ft.commit();
    }

    private void getLocalData() {
        final WebCheckResult w = WebCheck.checkNetworkAvailability(ctx);
        CacheUtil.getCachedData(ctx, CacheUtil.CACHE_DATA, new CacheUtil.CacheUtilListener() {
            @Override
            public void onFileDataDeserialized(final ResponseDTO respond) {
                response = respond;
                if (response != null) {
                    build();
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

    @Override
    public void onCreateEvaluation(ResponseDTO resp) {
        Intent evaluateIntent = new Intent(EvaluationView.this, Evaluation.class);
        if (response == null) {
            response = resp;
        }
        evaluateIntent.putExtra("response", response);
        startActivityForResult(evaluateIntent, STATUS_CODE);
    }

    static final int STATUS_CODE = 220;
}

package com.sifiso.codetribe.riverteamapp;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
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
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.gson.Gson;
import com.sifiso.codetribe.minisasslibrary.dialogs.GPSScannerDialog;
import com.sifiso.codetribe.minisasslibrary.dialogs.InsectSelectionDialog;
import com.sifiso.codetribe.minisasslibrary.dto.CategoryDTO;
import com.sifiso.codetribe.minisasslibrary.dto.ConditionsDTO;
import com.sifiso.codetribe.minisasslibrary.dto.EvaluationDTO;
import com.sifiso.codetribe.minisasslibrary.dto.EvaluationSiteDTO;
import com.sifiso.codetribe.minisasslibrary.dto.InsectDTO;
import com.sifiso.codetribe.minisasslibrary.dto.InsectImageDTO;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.RequestDTO;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.ResponseDTO;
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


public class Evaluation extends ActionBarActivity implements LocationListener, GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener, GPSScannerDialog.GPSScannerDialogListener {
    static final String LOG = Evaluation.class.getSimpleName();
    static final int ACCURACY_THRESHOLD = 5;
    static final int MAP_REQUESTED = 9007;
    Location mCurrentLocation;
    LocationClient mLocationClient;
    List<String> categoryStr;
    String catType = "Select category";
    double wc = 0.0, wt = 0.0, we = 0.0, wp = 0.0, wo = 0.0;
    ResponseDTO response;
    GPSScannerDialog gpsScannerDialog;
    Location location;
    LocationRequest mLocationRequest;
    InsectSelectionDialog insectSelectionDialog;
    boolean mBound;
    CachedSyncService mService;
    EvaluationDTO evaluationDTO;
    Integer teamMemberID, conditionID;
    private TextView WC_minus, WC_add, WT_minus, WT_add,
            WP_minus, WP_add, WO_minus, WO_add, WE_minus, WE_add;
    private EditText WC_score, WP_score, WT_score, WO_score, WE_score;
    private TextView TV_total_score, TV_average_score, TV_avg_score, TV_score_status;
    private ImageView IMG_score_icon, AE_pin_point;
    private EditText WT_sp_river, EDT_comment;
    private Button WT_gps_picker, SL_show_insect, AE_create;
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
                    }

                    @Override
                    public void onError(String message) {

                    }
                });
                getData();
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
    private Spinner WT_sp_category;
    private ViewStub viewStub;
    private Integer categoryID;
    private Integer riverID;
    private List<InsectImageDTO> insectImageList;
    private EvaluationSiteDTO evaluationSite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluation);
        ctx = getApplicationContext();
        mLocationClient = new LocationClient(ctx, this, this);
        setField();
        if (savedInstanceState != null) {
            response = (ResponseDTO) savedInstanceState.getSerializable("response");
        } else {
            response = (ResponseDTO) getIntent().getSerializableExtra("response");
        }
        addMinus();
        startGPSDialog();
    }

    private void setSpinner(final ResponseDTO resp) {
        if (resp.getCategoryList().isEmpty()) {
            Log.d(LOG, "******* getting cached data");
            return;
        }
        if (categoryStr == null) {
            categoryStr = new ArrayList<>();
        }
        categoryStr.add("Select category");
        for (int i = 0; i < resp.getCategoryList().size(); i++) {
            categoryStr.add(resp.getCategoryList().get(i).getCategoryName());
        }
        String[] cat = {"Select category", "Rocky", "Sandy"};
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.xsimple_spinner_item, categoryStr);
        WT_sp_category.setAdapter(categoryAdapter);
        WT_sp_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position > 0) {
                    categoryID = resp.getCategoryList().get(position - 1).getCategoryID();
                }
                catType = (String) parent.getItemAtPosition(position);
                Log.e(LOG, catType);
                //    Log.e(LOG, response.getCategoryList().get(position - 1).getCategoryID() + "");

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        WT_sp_river.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(LOG, "river size " + resp.getRiverList().size());
                Util.showPopupRiverWithHeroImage(ctx, Evaluation.this, resp.getRiverList(), WT_sp_river, "", new Util.UtilPopupListener() {
                    @Override
                    public void onItemSelected(int index) {
                        WT_sp_river.setTextColor(getResources().getColor(R.color.gray));
                        WT_sp_river.setText(resp.getRiverList().get(index).getRiverName());
                        riverID = response.getRiverList().get(index).getRiverID();
                    }
                });
            }
        });


    }

    private void setField() {
        WC_minus = (TextView) findViewById(R.id.WC_minus);
        WC_score = (EditText) findViewById(R.id.WC_score);
        WC_add = (TextView) findViewById(R.id.WC_add);
        WT_minus = (TextView) findViewById(R.id.WT_minus);
        WT_score = (EditText) findViewById(R.id.WT_score);
        WT_add = (TextView) findViewById(R.id.WT_add);
        WP_minus = (TextView) findViewById(R.id.WP_minus);
        WP_score = (EditText) findViewById(R.id.WP_score);
        WP_add = (TextView) findViewById(R.id.WP_add);
        WO_minus = (TextView) findViewById(R.id.WO_minus);
        WO_score = (EditText) findViewById(R.id.WO_score);
        WO_add = (TextView) findViewById(R.id.WO_add);
        WE_minus = (TextView) findViewById(R.id.WE_minus);
        WE_score = (EditText) findViewById(R.id.WE_score);
        WE_add = (TextView) findViewById(R.id.WE_add);

        TV_total_score = (TextView) findViewById(R.id.TV_total_score);
        TV_average_score = (TextView) findViewById(R.id.TV_average_score);
        TV_avg_score = (TextView) findViewById(R.id.TV_avg_score);
        TV_score_status = (TextView) findViewById(R.id.TV_score_status);
        IMG_score_icon = (ImageView) findViewById(R.id.IMG_score_icon);
        WT_sp_river = (EditText) findViewById(R.id.WT_sp_river);
        WT_sp_category = (Spinner) findViewById(R.id.WT_sp_category);
        EDT_comment = (EditText) findViewById(R.id.EDT_comment);
        AE_pin_point = (ImageView) findViewById(R.id.AE_pin_point);
        WT_gps_picker = (Button) findViewById(R.id.WT_gps_picker);
        SL_show_insect = (Button) findViewById(R.id.SL_show_insect);
        AE_create = (Button) findViewById(R.id.AE_create);

        viewStub = (ViewStub) findViewById(R.id.viewStub);
    }

    private void addMinus() {
        WO_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wo = Double.parseDouble(WO_score.getText().toString()) + 0.5;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        WO_score.setText(wo + "");
                    }
                });
            }
        });
        WO_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wo = Double.parseDouble(WO_score.getText().toString());
                if (wo <= 0.0) {

                } else {
                    wo = wo - 0.5;
                    if (wo < -0.0) {
                        wo = 0.0;
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        WO_score.setText(wo + "");
                    }
                });
            }
        });
        WP_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wp = Double.parseDouble(WP_score.getText().toString()) + 0.5;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        WP_score.setText(wp + "");
                    }
                });
            }
        });
        WP_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wp = Double.parseDouble(WP_score.getText().toString());
                if (wp <= 0.0) {

                } else {
                    wp = wp - 0.5;
                    if (wp < -0.0) {
                        wp = 0.0;
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        WP_score.setText(wp + "");
                    }
                });
            }
        });
        WT_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wt = Double.parseDouble(WT_score.getText().toString()) + 0.5;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        WT_score.setText(wt + "");
                    }
                });
            }
        });
        WT_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wt = Double.parseDouble(WT_score.getText().toString());
                if (wt <= 0.0) {

                } else {
                    wt = wt - 0.5;
                    if (wt < -0.0) {
                        wt = 0.0;
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        WT_score.setText(wt + "");
                    }
                });
            }
        });
        WE_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                we = Double.parseDouble(WE_score.getText().toString()) + 0.5;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        WE_score.setText(we + "");
                    }
                });
            }
        });
        WE_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                we = Double.parseDouble(WE_score.getText().toString());

                we = we - 0.5;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        WE_score.setText(we + "");
                    }
                });
            }
        });
        WC_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                wc = Double.parseDouble(WC_score.getText().toString()) + 0.5;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        WC_score.setText(wc + "");
                    }
                });
            }
        });
        WC_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                wc = Double.parseDouble(WC_score.getText().toString());
                if (wc <= 0.0) {

                } else {
                    wc = wc - 0.5;
                    if (wc < -0.0) {
                        wc = 0.0;
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        WC_score.setText(wc + "");
                    }
                });
            }
        });


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
        getMenuInflater().inflate(R.menu.menu_evaluation, menu);
        // WebCheckResult webCheckResult = WebCheck.checkNetworkAvailability(ctx);

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
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        switch (reqCode) {
            case MAP_REQUESTED:
                Log.w(LOG, "### map has returned with data?");
                if (resultCode == RESULT_OK) {
                    // evaluationSite = (EvaluationSiteDTO) data.getSerializableExtra("evaluationSite");
                    // projectSiteListFragment.updateSiteLocation(projectSite);
                }
                break;
        }
    }

    private void startGPSDialog() {
        gpsScannerDialog = new GPSScannerDialog();


        WT_gps_picker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (riverID != null) {
                    AE_pin_point.setVisibility(View.GONE);
                    gpsScannerDialog.show(getFragmentManager(), "GPS");
                } else {
                    ToastUtil.toast(Evaluation.this, "Please choose evaluated river");
                }
            }
        });

        AE_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                localSaveRequests();
            }
        });

    }

    private void startSelectionDialog() {


        for (InsectDTO i : response.getInsectList()) {
            if (insectImageList == null) {
                insectImageList = new ArrayList<>();
            }
            for (InsectImageDTO ii : i.getInsectImageList()) {
                insectImageList.add(ii);
            }
        }
        SL_show_insect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (catType.equalsIgnoreCase("Select category")) {
                    ToastUtil.toast(Evaluation.this, "Select category, before choosing insects");
                } else {
                    insectSelectionDialog = new InsectSelectionDialog();
                    insectSelectionDialog.setmSites(insectImageList);
                    insectSelectionDialog.setListener(new InsectSelectionDialog.InsectSelectionDialogListener() {
                        @Override
                        public void onSelectDone(final List<InsectImageDTO> insectImages) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (insectImages == null) {
                                        return;
                                    }

                                    TV_avg_score.setText(calculateAverage(insectImages) + "");
                                    TV_average_score.setText(((insectImages != null) ? insectImages.size() : 0.0) + "");

                                    statusScore(insectImages, catType);
                                    TV_total_score.setText(calculateScore(insectImages) + "");
                                    Log.e(LOG, calculateScore(insectImages) + "Yes");
                                }
                            });
                        }
                    });
                    insectSelectionDialog.show(getFragmentManager(), "");
                    insectSelectionDialog = null;
                }
            }
        });
    }

    private void getGPSCoordinates() {
        if (!mLocationClient.isConnected()) {
            mLocationClient.connect();
        }
        mCurrentLocation = mLocationClient.getLastLocation();
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setFastestInterval(1000);

        try {
            mLocationClient.requestLocationUpdates(mLocationRequest, this);
        } catch (IllegalStateException e) {
            Log.e(LOG, "---- mLocationClient.requestLocationUpdates ILLEGAL STATE", e);
        }
    }

    private void stopPeriodicUpdates() {
        mLocationClient.removeLocationUpdates(this);
        Log.e(LOG,
                "#################### stopPeriodicUpdates - removeLocationUpdates");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(LOG,
                "### onStart, binding RequestSyncService and PhotoUploadService");
        Intent intent = new Intent(this, CachedSyncService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        if (mLocationClient != null) {
            mLocationClient.connect();
            Log.i(LOG,
                    "### onStart - locationClient connecting ... ");
        }


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
        if (gpsScannerDialog != null) {
            Log.w(LOG, "### Passing location");
            gpsScannerDialog.setLocation(loc);
        }
        if (loc.getAccuracy() <= ACCURACY_THRESHOLD) {
            location = loc;
            Log.w(LOG, "### Passing location2");
            //gpsScannerDialog.dismiss();
            gpsScannerDialog.setLocation(loc);
            gpsScannerDialog.stopScan();
            mLocationClient.removeLocationUpdates(this);
            Log.e(LOG, "+++ best accuracy found: " + location.getAccuracy());
        }

    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(LOG,
                "### ---> PlayServices onConnected() - gotta start something! >>");
        location = mLocationClient.getLastLocation();
    }

    @Override
    public void onDisconnected() {
        Log.e(LOG,
                "### ---> PlayServices onDisconnected");
    }

    @Override
    public void onStartScanRequested() {
        AE_pin_point.setVisibility(View.GONE);
        getGPSCoordinates();
    }

    @Override
    public void onLocationConfirmed(EvaluationSiteDTO es) {
        Log.w(LOG, "## asking GPSScanner to process confirmed location for site");
        AE_pin_point.setVisibility(View.VISIBLE);
        evaluationSite = new EvaluationSiteDTO();
        evaluationSite.setLocationConfirmed(1);
        evaluationSite.setLatitude(es.getLatitude());
        evaluationSite.setLongitude(es.getLongitude());
        evaluationSite.setAccuracy(es.getAccuracy());
        evaluationSite.setDateRegistered(new Date().getTime());
        evaluationSite.setCategoryID(categoryID);
        evaluationSite.setRiverID(riverID);

        Log.w(LOG, "## Evaluation site created");
        gpsScannerDialog.dismiss();
        stopPeriodicUpdates();
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

    private Integer conditionIDFunc(List<CategoryDTO> categorys, String status, String type) {
        Integer cond = 0;
        Log.i(LOG, categorys.size() + " " + type);
        for (CategoryDTO c : categorys) {
            Log.e(LOG, categorys.size() + " " + type);
            if (type.equalsIgnoreCase(c.getCategoryName())) {
                Log.d(LOG, categorys.size() + " " + type);
                for (ConditionsDTO cd : c.getConditionsList()) {
                    Log.w(LOG, categorys.size() + " " + type + " ");
                    if (status.equalsIgnoreCase(cd.getConditionName())) {
                        Log.i(LOG, cd.getConditionName() + " " + cd.getConditionsID());
                        cond = cd.getConditionsID();
                    }

                }

            }
        }
        return cond;
    }

    private void statusScore(List<InsectImageDTO> dtos, String type) {
        String status = "";
        double average = 0.0, total = 0.0;
        for (InsectImageDTO ii : dtos) {
            total = total + ii.getSensitivityScore();
        }
        average = total / dtos.size();

        if (type.equalsIgnoreCase("Sandy Type")) {
            if (average > 6.9) {
                status = "Unmodified(NATURAL condition)";
                TV_score_status.setTextColor(getResources().getColor(R.color.purple));
                TV_avg_score.setTextColor(getResources().getColor(R.color.purple));
            } else if (average > 5.8 && average < 6.9) {
                status = "Largely natural/few modifications(GOOD condition)";
                TV_score_status.setTextColor(getResources().getColor(R.color.green));
                TV_avg_score.setTextColor(getResources().getColor(R.color.green));
            } else if (average > 4.9 && average < 5.8) {
                status = "Moderately modified(FAIR condition)";
                TV_score_status.setTextColor(getResources().getColor(R.color.yellow_dark));
                TV_avg_score.setTextColor(getResources().getColor(R.color.yellow_dark));
            } else if (average > 4.3 && average < 4.9) {
                status = "Largely modified(POOR condition)";
                TV_score_status.setTextColor(getResources().getColor(R.color.orange));
                TV_avg_score.setTextColor(getResources().getColor(R.color.orange));
            } else if (average < 4.3) {
                status = "Seriously/critically modified";
                TV_score_status.setTextColor(getResources().getColor(R.color.red));
                TV_avg_score.setTextColor(getResources().getColor(R.color.red));
            }
        } else if (type.equalsIgnoreCase("Rocky Type")) {
            if (average > 7.9) {
                status = "Unmodified(NATURAL condition)";
                TV_score_status.setTextColor(getResources().getColor(R.color.purple));
                TV_avg_score.setTextColor(getResources().getColor(R.color.purple));
            } else if (average > 6.8 && average < 7.9) {
                status = "Largely natural/few modifications(GOOD condition)";
                TV_score_status.setTextColor(getResources().getColor(R.color.green));
                TV_avg_score.setTextColor(getResources().getColor(R.color.green));
            } else if (average > 6.1 && average < 6.8) {
                status = "Moderately modified(FAIR condition)";
                TV_score_status.setTextColor(getResources().getColor(R.color.yellow_dark));
                TV_avg_score.setTextColor(getResources().getColor(R.color.yellow_dark));
            } else if (average > 5.1 && average < 6.1) {
                status = "Largely modified(POOR condition)";
                TV_score_status.setTextColor(getResources().getColor(R.color.orange));
                TV_avg_score.setTextColor(getResources().getColor(R.color.orange));
            } else if (average < 5.1) {
                status = "Seriously/critically modified";
                TV_avg_score.setTextColor(getResources().getColor(R.color.red));
                TV_score_status.setTextColor(getResources().getColor(R.color.red));
            }
        }

        TV_score_status.setText(status);
        conditionID = conditionIDFunc(response.getCategoryList(), status, type);
        Log.e(LOG, "lskdfsdf " + conditionIDFunc(response.getCategoryList(), status, type));
    }

    private double calculateScore(List<InsectImageDTO> dtos) {
        double score = 0.0;
        for (InsectImageDTO ii : dtos) {
            score = score + ii.getSensitivityScore();
        }
        return score;
    }

    private double calculateAverage(List<InsectImageDTO> dtos) {
        double average = 0.0, total = 0.0;

        for (InsectImageDTO ii : dtos) {
            total = total + ii.getSensitivityScore();
        }
        average = total / dtos.size();
        return average;
    }

    private void getLocalData() {
        //  Log.d(LOG, "******* getting cached data");
        CacheUtil.getCachedData(ctx, CacheUtil.CACHE_DATA, new CacheUtil.CacheUtilListener() {
            @Override
            public void onFileDataDeserialized(final ResponseDTO respond) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (respond.getStatusCode() > 0) {
                            Log.d(LOG, "** getting cached data");
                            return;
                        }
                        response = respond;
                        setSpinner(respond);
                        Log.d(LOG, "******* getting cached data");
                        startSelectionDialog();
                        WebCheckResult rc = WebCheck.checkNetworkAvailability(ctx);
                        if (rc.isMobileConnected()) {
                            getData();
                            return;
                        } else {
                            if (rc.isWifiConnected()) {
                                getData();
                                return;
                            }
                        }

                    }
                });


            }

            @Override
            public void onDataCached() {

            }

            @Override
            public void onError() {

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

    private void sendRequest(final RequestDTO request) {
        WebSocketUtil.sendRequest(ctx, Statics.MINI_SASS_ENDPOINT, request, new WebSocketUtil.WebSocketListener() {
            @Override
            public void onMessage(ResponseDTO response) {
                if (response.getStatusCode() > 0) {
                    addRequestToCache(request);
                }
            }

            @Override
            public void onClose() {

            }

            @Override
            public void onError(String message) {
                addRequestToCache(request);
            }
        });
    }

    private void addRequestToCache(RequestDTO request) {
        RequestCacheUtil.addRequest(ctx, request, new CacheUtil.CacheRequestListener() {
            @Override
            public void onDataCached() {
                if (evaluationSite == null) return;

                Log.e(LOG, "----onDataCached, onEndScanRequested - please stop scanning");


            }

            @Override
            public void onRequestCacheReturned(RequestCache cache) {

            }

            @Override
            public void onError() {

            }
        });
    }

    private void localSaveRequests() {
        RequestDTO w = new RequestDTO(RequestDTO.ADD_EVALUATION);

        evaluationDTO = new EvaluationDTO();

        evaluationDTO.setConditionsID(conditionID);
        evaluationDTO.setTeamMemberID(1);

        evaluationDTO.setEvaluationSite(evaluationSite);


        evaluationDTO.setRemarks(EDT_comment.getText().toString());
        evaluationDTO.setpH(Double.parseDouble(WP_score.getText().toString()));
        evaluationDTO.setOxygen(Double.parseDouble(WO_score.getText().toString()));
        evaluationDTO.setWaterClarity(Double.parseDouble(WC_score.getText().toString()));
        evaluationDTO.setWaterTemperature(Double.parseDouble(WT_score.getText().toString()));
        evaluationDTO.setElectricityConductivity(Double.parseDouble(WE_score.getText().toString()));
        evaluationDTO.setEvaluationDate(new Date().getTime());
        evaluationDTO.setScore(Double.parseDouble(TV_avg_score.getText().toString()));
        evaluationDTO.setLatitude(evaluationSite.getLatitude());
        evaluationDTO.setLongitude(evaluationSite.getLongitude());


        w.setEvaluation(evaluationDTO);

        Log.i(LOG, (new Gson()).toJson(w));
        WebCheckResult wcr = WebCheck.checkNetworkAvailability(ctx);
        if (evaluationDTO.getEvaluationSite() == null) {
            ToastUtil.errorToast(Evaluation.this, "Please make sure Site Evaluation is picked(hint: SITE GPS LOCATION)");
            return;
        }
        if (evaluationDTO.getConditionsID() == null) {
            ToastUtil.errorToast(Evaluation.this, "If you don't select insect group, you can't score evaluation(hint: SELECT INSECT GROUP)");
            return;
        }
        if (wcr.isWifiConnected()) {
            sendRequest(w);
        } else {
            addRequestToCache(w);
        }

    }
}

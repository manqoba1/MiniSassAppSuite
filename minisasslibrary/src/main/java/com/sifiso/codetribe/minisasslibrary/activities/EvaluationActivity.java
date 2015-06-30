package com.sifiso.codetribe.minisasslibrary.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.graphics.PorterDuff;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.ErrorDialogFragment;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.sifiso.codetribe.minisasslibrary.R;
import com.sifiso.codetribe.minisasslibrary.dto.CategoryDTO;
import com.sifiso.codetribe.minisasslibrary.dto.ConditionsDTO;
import com.sifiso.codetribe.minisasslibrary.dto.EvaluationDTO;
import com.sifiso.codetribe.minisasslibrary.dto.EvaluationImageDTO;
import com.sifiso.codetribe.minisasslibrary.dto.EvaluationSiteDTO;
import com.sifiso.codetribe.minisasslibrary.dto.InsectDTO;
import com.sifiso.codetribe.minisasslibrary.dto.InsectImageDTO;
import com.sifiso.codetribe.minisasslibrary.dto.RiverDTO;
import com.sifiso.codetribe.minisasslibrary.dto.RiverPartDTO;
import com.sifiso.codetribe.minisasslibrary.dto.RiverPointDTO;
import com.sifiso.codetribe.minisasslibrary.dto.TeamMemberDTO;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.RequestDTO;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.ResponseDTO;
import com.sifiso.codetribe.minisasslibrary.fragments.PageFragment;
import com.sifiso.codetribe.minisasslibrary.services.RequestCache;
import com.sifiso.codetribe.minisasslibrary.services.RequestSyncService;
import com.sifiso.codetribe.minisasslibrary.toolbox.BaseVolley;
import com.sifiso.codetribe.minisasslibrary.toolbox.WebCheck;
import com.sifiso.codetribe.minisasslibrary.toolbox.WebCheckResult;
import com.sifiso.codetribe.minisasslibrary.util.CacheUtil;
import com.sifiso.codetribe.minisasslibrary.util.Constants;
import com.sifiso.codetribe.minisasslibrary.util.RequestCacheUtil;
import com.sifiso.codetribe.minisasslibrary.util.SharedUtil;
import com.sifiso.codetribe.minisasslibrary.util.Statics;
import com.sifiso.codetribe.minisasslibrary.util.ToastUtil;
import com.sifiso.codetribe.minisasslibrary.util.Util;

import org.joda.time.DateTime;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;


public class EvaluationActivity extends ActionBarActivity implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    static final String LOG = EvaluationActivity.class.getSimpleName();
    private TextView WC_minus, WC_add, WT_minus, WT_add,
            WP_minus, WP_add, WO_minus, WO_add, WE_minus, WE_add;
    private EditText WC_score, WP_score, WT_score, WO_score, WE_score;
    private TextView TV_total_score, TV_average_score, TV_avg_score, TV_score_status;
    private ImageView IMG_score_icon, AE_pin_point;
    private TextView WT_sp_river, WT_sp_category, EDT_comment, AE_down_up;
    private Button WT_gps_picker, SL_show_insect, AE_create, AE_find_near_sites;

    //layouts
    RelativeLayout result2, result3;
    EvaluationDTO evaluationDTO;
    Integer teamMemberID, conditionID = null;
    double wc = 0.0, wt = 0.0, we = 0.0, wp = 0.0, wo = 0.0;
    private Activity activity;
    List<String> categoryStr;
    private ViewStub viewStub;
    private Integer categoryID;
    private Integer riverID;
    private TeamMemberDTO teamMember;
    String catType = "Select category";

    static final int ACCURACY_THRESHOLD = 10;
    static final int MAP_REQUESTED = 9007;
    static final int STATUS_CODE = 220;
    static final int INSECT_DATA = 103;
    public static final int CAPTURE_IMAGE = 1001;
    static final int CREATE_EVALUATION = 108;

    static final int GPS_DATA = 102;

    GoogleApiClient mGoogleApiClient;
    LocationRequest locationRequest;
    Location location;
    boolean mResolvingError;
    static final long ONE_MINUTE = 1000 * 60;
    static final long FIVE_MINUTES = 1000 * 60 * 5;
    private static final int REQUEST_RESOLVE_ERROR = 1001;
    // Unique tag for the error dialog fragment
    private static final String DIALOG_ERROR = "dialog_error";

    ViewPager mPager;
    Menu mMenu;
    List<PageFragment> pageFragmentList;
    ResponseDTO response;
    boolean mBound, mShowMore;
    RequestSyncService mService;

    private Context ctx;
    RelativeLayout GPS_layout4;
    private TextView txtAccuracy, txtLat, txtLng;

    private void setField() {
        GPS_layout4 = (RelativeLayout) findViewById(R.id.GPS_layout4);
        txtAccuracy = (TextView) findViewById(R.id.GPS_accuracy);
        txtLat = (TextView) findViewById(R.id.GPS_latitude);
        txtLng = (TextView) findViewById(R.id.GPS_longitude);
        Statics.setRobotoFontBold(ctx, txtLat);
        Statics.setRobotoFontBold(ctx, txtLng);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        //  WC_minus = (TextView) findViewById(R.id.WC_minus);
        WC_score = (EditText) findViewById(R.id.WC_score);
        //  WC_add = (TextView) findViewById(R.id.WC_add);
        //WT_minus = (TextView) findViewById(R.id.WT_minus);
        WT_score = (EditText) findViewById(R.id.WT_score);
        //WT_add = (TextView) findViewById(R.id.WT_add);
        //WP_minus = (TextView) findViewById(R.id.WP_minus);
        WP_score = (EditText) findViewById(R.id.WP_score);
        // WP_add = (TextView) findViewById(R.id.WP_add);
        //WO_minus = (TextView) findViewById(R.id.WO_minus);
        WO_score = (EditText) findViewById(R.id.WO_score);
        // WO_add = (TextView) findViewById(R.id.WO_add);
        // WE_minus = (TextView) findViewById(R.id.WE_minus);
        WE_score = (EditText) findViewById(R.id.WE_score);
        // WE_add = (TextView) findViewById(R.id.WE_add);
        result3 = (RelativeLayout) findViewById(R.id.result3);
        result3.setVisibility(View.GONE);
        result2 = (RelativeLayout) findViewById(R.id.result2);
        result2.setVisibility(View.VISIBLE);
        TV_total_score = (TextView) findViewById(R.id.TV_total_score);
        TV_average_score = (TextView) findViewById(R.id.TV_average_score);
        TV_avg_score = (TextView) findViewById(R.id.TV_avg_score);
        TV_score_status = (TextView) findViewById(R.id.TV_score_status);
        IMG_score_icon = (ImageView) findViewById(R.id.IMG_score_icon);
        WT_sp_river = (TextView) findViewById(R.id.WT_sp_river);
        WT_sp_category = (TextView) findViewById(R.id.WT_sp_category);
        EDT_comment = (EditText) findViewById(R.id.EDT_comment);
        AE_pin_point = (ImageView) findViewById(R.id.AE_pin_point);
        WT_gps_picker = (Button) findViewById(R.id.WT_gps_picker);
        SL_show_insect = (Button) findViewById(R.id.SL_show_insect);
        AE_create = (Button) findViewById(R.id.AE_create);
        AE_down_up = (TextView) findViewById(R.id.AE_down_up);
        AE_down_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mShowMore) {
                    mShowMore = false;
                    result2.setVisibility(View.VISIBLE);
                    // AE_down_up.setCompoundDrawables(ctx.getResources().getDrawable(android.R.drawable.arrow_down_float), null, null, null);
                    AE_down_up.setCompoundDrawablesWithIntrinsicBounds(0, 0, android.R.drawable.arrow_down_float, 0);
                } else {
                    mShowMore = true;
                    result2.setVisibility(View.GONE);
                    AE_down_up.setCompoundDrawablesWithIntrinsicBounds(0, 0, android.R.drawable.arrow_up_float, 0);
                }
            }
        });
        AE_find_near_sites = (Button) findViewById(R.id.AE_find_near_sites);
        AE_find_near_sites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (riverID == null) {
                    Toast.makeText(ctx, "Please choose the river first", Toast.LENGTH_SHORT).show();
                    return;
                }
                getEvaluationsByRiver();
            }
        });
        viewStub = (ViewStub) findViewById(R.id.viewStub);
        if (codeStatus == CREATE_EVALUATION) {
            river = (RiverDTO) getIntent().getSerializableExtra("riverCreate");
            WT_sp_river.setTextColor(getResources().getColor(R.color.gray));
            WT_sp_river.setText(river.getRiverName());
            riverID = river.getRiverID();
        }
        // buildUI();
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
                        // getData();
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
        getSupportActionBar().setSubtitle(Util.getLongerDate(new DateTime()));
        ctx = getApplicationContext();
        activity = this;
        teamMember = SharedUtil.getTeamMember(ctx);
        setTitle("Create Evaluations");

        setField();
        if (savedInstanceState != null) {
            response = (ResponseDTO) savedInstanceState.getSerializable("response");
            buildUI();
        }
        // (RiverDTO) data.getSerializableExtra("riverCreate");
        if (getIntent().getSerializableExtra("riverCreate") != null) {
            river = (RiverDTO) getIntent().getSerializableExtra("riverCreate");
            setRiverField(river);
        }

    }


    private int currentView;


    int codeStatus;
    RiverDTO river;


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("response", response);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_evaluation, menu);
        WebCheckResult wr = WebCheck.checkNetworkAvailability(ctx);
        if (wr.isMobileConnected() || wr.isWifiConnected()) {
            getRiversAroundMe();
        } else {

        }

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
            stopLocationUpdates();

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        switch (reqCode) {
            case MAP_REQUESTED:
                Log.w(LOG, "### map has returned with data?");
                if (resultCode == RESULT_OK) {
                    response = (ResponseDTO) data.getSerializableExtra("response");
                    teamMember = SharedUtil.getTeamMember(ctx);
                }
                break;
            case STATUS_CODE:
                Log.w(LOG, "### setting ui has returned with data?");
                if (resultCode == RESULT_OK) {
                    response = (ResponseDTO) data.getSerializableExtra("response");
                }
                break;
            case CREATE_EVALUATION:
                Log.w(LOG, "### setting ui has returned with data?");
                if (resultCode == CREATE_EVALUATION) {
                    RiverDTO riv = (RiverDTO) data.getSerializableExtra("riverCreate");
                    response = (ResponseDTO) data.getSerializableExtra("response");
                    setRiverField(riv);
                }
                break;
            case GPS_DATA:
                Log.w(LOG, "### gps ui has returned with data?");
                if (resultCode == GPS_DATA) {
                    Log.w(LOG, "### gps ui has returned with data?");

                    setEvaluationSite((EvaluationSiteDTO) data.getSerializableExtra("siteData"));
                    // evaluationFragment.setResponse(response);
                    teamMember = SharedUtil.getTeamMember(ctx);
                }
                break;
            case INSECT_DATA:
                Log.w(LOG, "### insects ui has returned with data?");
                if (resultCode == INSECT_DATA) {

                    insectImageList = (List<InsectImageDTO>) data.getSerializableExtra("overallInsect");

                    scoreUpdater((List<InsectImageDTO>) data.getSerializableExtra("selectedInsects"));
                    List<InsectImageDTO> list = (List<InsectImageDTO>) data.getSerializableExtra("selectedInsects");
                    //Log.w(LOG, "### insect ui has returned with data?" + list.size());
                    isInsectsPickerBack = true;
                    result3.setVisibility(View.VISIBLE);
                    teamMember = SharedUtil.getTeamMember(ctx);
                }
                break;
        }
    }

    public void setRiverField(RiverDTO r) {
        // ToastUtil.toast(ctx, "yes");
        riverID = r.getRiverID();
        WT_sp_river.setText(r.getRiverName());

    }

    public void setFieldsFromVisitedSites(EvaluationSiteDTO r) {
        riverID = r.getRiverID();
        WT_sp_river.setText(r.getRiver().getRiverName());
        WT_sp_river.setEnabled(false);
        categoryID = r.getCategoryID();
        WT_sp_category.setText(r.getCategory().getCategoryName());
        WT_sp_category.setEnabled(false);
    }

    static final DecimalFormat df = new DecimalFormat("###,###,###,###,##0.0");

    private String getDistance(float mf) {
        if (mf < 1000) {
            return df.format(mf) + " metres";
        }
        Double m = Double.parseDouble("" + mf);
        Double n = m / 1000;

        return df.format(n) + " kilometres";

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

    private void setSpinner() {

        calculateDistances();
        Log.d(LOG, "category size " + response.getCategoryList().size());
        WT_sp_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Util.showPopupCategoryBasicWithHeroImage(ctx, EvaluationActivity.this, response.getCategoryList(), WT_sp_category, "", new Util.UtilPopupListener() {
                    @Override
                    public void onItemSelected(int index) {
                        indexCat = index;
                        WT_sp_category.setText(response.getCategoryList().get(indexCat).getCategoryName());
                        categoryID = response.getCategoryList().get(indexCat).getCategoryId();
                        catType = (response.getCategoryList().get(indexCat).getCategoryName());
                       // evaluationSite.setCategoryID(categoryID);
                        Log.e(LOG, categoryID + "");
                    }
                });
            }
        });
        Log.d(LOG, "river size " + response.getRiverList().size());
        WT_sp_river.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Util.showPopupRiverWithHeroImage(ctx, EvaluationActivity.this, response.getRiverList(), WT_sp_river, "", new Util.UtilPopupListener() {
                    @Override
                    public void onItemSelected(int ind) {
                        // WT_sp_river.setTextColor(getResources().getColor(R.color.gray));
                        indexRiv = ind;
                        WT_sp_river.setText(response.getRiverList().get(indexRiv).getRiverName());
                        riverID = response.getRiverList().get(indexRiv).getRiverID();
                        //evaluationSite.setRiverID(riverID);
                        Log.e(LOG, "" + riverID);
                    }
                });
            }
        });


    }

    private int indexCat, indexRiv;

    private void startGPSDialog() {


        WT_gps_picker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (riverID != null) {
                    //mListener.onScanGpsRequest();
                    Log.d(LOG, "GPS Scanner");
                    Intent intent = new Intent(EvaluationActivity.this, GPSscanner.class);
                    startActivityForResult(intent, GPS_DATA);
                } else {
                    ToastUtil.toast(ctx, "Please choose river");
                }
            }
        });

        AE_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (riverID == null) {
                    ToastUtil.toast(ctx, "Please select river");
                    return;
                }
                if (categoryID == null) {
                    ToastUtil.toast(ctx, "Please select category");
                    return;
                }

                if (evaluationSite == null && accuracy == null) {
                    ToastUtil.toast(ctx, "Evaluation site not defined");
                    GPS_layout4.setVisibility(View.VISIBLE);
                    startLocationUpdates();
                    return;
                }
                if (selectedInsects == null) {
                    //selectedInsects = new ArrayList<>();
                    ToastUtil.toast(ctx, "Select insect group found, to score evaluation (Hint: Select insect group)");
                    return;
                }
                if (conditionID == null) {
                    ToastUtil.toast(ctx, "Condition not specified, Please select insect group found");
                    return;
                }

                localSaveRequests();
            }
        });

    }

    private void setLocationTextviews(Location loc) {
        txtLat.setText("" + loc.getLatitude());
        txtLng.setText("" + loc.getLongitude());
        txtAccuracy.setText("" + loc.getAccuracy());
    }

    private void startSelectionDialog() {


        for (InsectDTO i : response.getInsectList()) {

            if (insectImageList == null) {
                insectImageList = new ArrayList<InsectImageDTO>();
            }
            for (InsectImageDTO ii : i.getInsectimageDTOList()) {
                insectImageList.add(ii);
            }
        }
        SL_show_insect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (categoryID == null) {
                    ToastUtil.toast(ctx, "Select category, before choosing insects");
                } else {

                    Log.d(LOG, "Insect Select " + insectImageList.size());
                    Intent intent = new Intent(EvaluationActivity.this, InsectPicker.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.putExtra("insetImageList", (java.io.Serializable) insectImageList);
                    startActivityForResult(intent, INSECT_DATA);
                }
            }
        });
    }

    public void scoreUpdater(List<InsectImageDTO> insectImages) {
        selectedInsects = new ArrayList<>();
        if (insectImages == null) {
            result3.setVisibility(View.GONE);
            return;
        } else {
            result3.setVisibility(View.VISIBLE);
        }
        TV_avg_score.setText(calculateAverage(insectImages) + "");
        TV_average_score.setText(((insectImages != null) ? insectImages.size() : 0.0) + "");
        selectedInsects = insectImages;
        statusScore(insectImages, catType);
        TV_total_score.setText(calculateScore(insectImages) + "");
        Log.e(LOG, calculateScore(insectImages) + "Yes");
    }

    private List<InsectImageDTO> selectedInsects;

    private void cleanForm() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TV_total_score.setText("0.0");
                TV_average_score.setText("0.0");
                TV_avg_score.setText("0.0");
                IMG_score_icon.setImageDrawable(getResources().getDrawable(R.drawable.gray_crap));
                WT_sp_river.setText("What is the current river are you at?");
                WT_sp_category.setText("What environment are you at? Rocky or Sandy?");
                EDT_comment.setText("");
                AE_pin_point.setVisibility(View.GONE);
                evaluationSite =null;

                TV_score_status.setText("not specified");
                TV_score_status.setTextColor(getResources().getColor(R.color.gray));
                IMG_score_icon.setColorFilter(getResources().getColor(R.color.gray), PorterDuff.Mode.MULTIPLY);
                WE_score.setText("0.0");
                WO_score.setText("0.0");
                WC_score.setText("0.0");
                WP_score.setText("0.0");
                WT_score.setText("0.0");
                categoryID = null;
                riverID = null;
                conditionID = null;
                GPS_layout4.setVisibility(View.GONE);


            }
        });

    }

    private Integer conditionIDFunc(List<CategoryDTO> categorys, int statusCondition, int categoryID) {
        Integer cond = null;
        Log.i(LOG, categorys.size() + " " + categoryID);
        for (CategoryDTO c : categorys) {
            Log.e(LOG, categorys.size() + " " + categoryID);
            if (categoryID == c.getCategoryId()) {
                Log.d(LOG, categorys.size() + " " + categoryID);
                for (ConditionsDTO cd : c.getConditionsList()) {
                    Log.w(LOG, categorys.size() + " " + cd.getConditionName() + " ");
                    if (statusCondition == cd.getConditionsID()) {
                        Log.i(LOG, cd.getConditionName() + " " + cd.getConditionsID());
                        cond = cd.getConditionsID();
                    }

                }

            }
        }
        return cond;
    }


    private void statusScore(List<InsectImageDTO> dtos, String categoryName) {
        String status = "";
        int statusCondition = 0;
        int categoryID = 0;
        double average = 0.0, total = 0.0;
        if (dtos == null || dtos.size() == 0) {
            average = 0.0;

        } else {
            for (InsectImageDTO ii : dtos) {
                total = total + ii.getInsect().getSensitivityScore();
            }
            average = total / dtos.size();
        }
        average = Math.round(average * 100.0) / 100.0;
        Log.d(LOG, average + "");
        if (categoryName.equalsIgnoreCase("Sandy Type")) {
            categoryID = 8;
            if (average > 6.9) {
                status = "Unmodified(NATURAL condition)";
                statusCondition = Constants.UNMODIFIED_NATURAL_SAND;
                TV_score_status.setTextColor(getResources().getColor(R.color.dark_blue));
                TV_avg_score.setTextColor(getResources().getColor(R.color.dark_blue));
                IMG_score_icon.setImageDrawable(getResources().getDrawable(R.drawable.blue_crap));
                //IMG_score_icon.setColorFilter(getResources().getColor(R.color.purple), PorterDuff.Mode.MULTIPLY);
            } else if (average > 5.8 && average < 6.9) {
                status = "Largely natural/few modifications(GOOD condition)";
                statusCondition = Constants.LARGELY_NATURAL_SAND;
                TV_score_status.setTextColor(getResources().getColor(R.color.green));
                TV_avg_score.setTextColor(getResources().getColor(R.color.green));
                IMG_score_icon.setImageDrawable(getResources().getDrawable(R.drawable.green_crap));
                // IMG_score_icon.setColorFilter(getResources().getColor(R.color.green), PorterDuff.Mode.MULTIPLY);
            } else if (average > 4.9 && average < 5.8) {
                status = "Moderately modified(FAIR condition)";
                statusCondition = Constants.MODERATELY_MODIFIED_SAND;
                TV_score_status.setTextColor(getResources().getColor(R.color.orange));
                TV_avg_score.setTextColor(getResources().getColor(R.color.orange));
                IMG_score_icon.setImageDrawable(getResources().getDrawable(R.drawable.orange_crap));
                // IMG_score_icon.setColorFilter(getResources().getColor(R.color.yellow_dark), PorterDuff.Mode.MULTIPLY);
            } else if (average > 4.3 && average < 4.9) {
                status = "Largely modified(POOR condition)";
                statusCondition = Constants.LARGELY_MODIFIED_SAND;
                TV_score_status.setTextColor(getResources().getColor(R.color.red));
                TV_avg_score.setTextColor(getResources().getColor(R.color.red));
                IMG_score_icon.setImageDrawable(getResources().getDrawable(R.drawable.red_crap));
                // IMG_score_icon.setColorFilter(getResources().getColor(R.color.orange), PorterDuff.Mode.MULTIPLY);
            } else if (average < 4.3) {
                status = "Seriously/critically modified";
                statusCondition = Constants.CRITICALLY_MODIFIED_SAND;
                TV_score_status.setTextColor(getResources().getColor(R.color.purple));
                TV_avg_score.setTextColor(getResources().getColor(R.color.purple));
                IMG_score_icon.setImageDrawable(getResources().getDrawable(R.drawable.purple_crap));
                //IMG_score_icon.setColorFilter(getResources().getColor(R.color.red), PorterDuff.Mode.MULTIPLY);
            } else {
                status = "Not specified";
                statusCondition = Constants.NOT_SPECIFIED;
                TV_score_status.setTextColor(getResources().getColor(R.color.gray));
                TV_avg_score.setTextColor(getResources().getColor(R.color.gray));
                IMG_score_icon.setImageDrawable(getResources().getDrawable(R.drawable.gray_crap));
                //IMG_score_icon.setColorFilter(getResources().getColor(R.color.gray), PorterDuff.Mode.MULTIPLY);

            }
        } else if (categoryName.equalsIgnoreCase("Rocky Type")) {
            categoryID = 9;
            if (average > 7.9) {
                status = "Unmodified(NATURAL condition)";
                statusCondition = Constants.UNMODIFIED_NATURAL_ROCK;
                TV_score_status.setTextColor(getResources().getColor(R.color.dark_blue));
                TV_avg_score.setTextColor(getResources().getColor(R.color.dark_blue));
                IMG_score_icon.setImageDrawable(getResources().getDrawable(R.drawable.blue_crap));
                //IMG_score_icon.setColorFilter(getResources().getColor(R.color.purple), PorterDuff.Mode.MULTIPLY);
            } else if (average > 6.8 && average < 7.9) {
                status = "Largely natural/few modifications(GOOD condition)";
                statusCondition = Constants.LARGELY_NATURAL_ROCK;
                TV_score_status.setTextColor(getResources().getColor(R.color.green));
                TV_avg_score.setTextColor(getResources().getColor(R.color.green));
                IMG_score_icon.setImageDrawable(getResources().getDrawable(R.drawable.green_crap));
                // IMG_score_icon.setColorFilter(getResources().getColor(R.color.green), PorterDuff.Mode.MULTIPLY);
            } else if (average > 6.1 && average < 6.8) {
                status = "Moderately modified(FAIR condition)";
                statusCondition = Constants.MODERATELY_MODIFIED_ROCK;
                TV_score_status.setTextColor(getResources().getColor(R.color.orange));
                TV_avg_score.setTextColor(getResources().getColor(R.color.orange));
                IMG_score_icon.setImageDrawable(getResources().getDrawable(R.drawable.orange_crap));
                // IMG_score_icon.setColorFilter(getResources().getColor(R.color.yellow_dark), PorterDuff.Mode.MULTIPLY);
            } else if (average > 5.1 && average < 6.1) {
                status = "Largely modified(POOR condition)";
                statusCondition = Constants.LARGELY_MODIFIED_ROCK;
                TV_score_status.setTextColor(getResources().getColor(R.color.red));
                TV_avg_score.setTextColor(getResources().getColor(R.color.red));
                IMG_score_icon.setImageDrawable(getResources().getDrawable(R.drawable.red_crap));
                // IMG_score_icon.setColorFilter(getResources().getColor(R.color.orange), PorterDuff.Mode.MULTIPLY);
            } else if (average < 5.1) {
                status = "Seriously/critically modified";
                statusCondition = Constants.CRITICALLY_MODIFIED_ROCK;
                TV_score_status.setTextColor(getResources().getColor(R.color.purple));
                TV_avg_score.setTextColor(getResources().getColor(R.color.purple));
                IMG_score_icon.setImageDrawable(getResources().getDrawable(R.drawable.purple_crap));
                //IMG_score_icon.setColorFilter(getResources().getColor(R.color.red), PorterDuff.Mode.MULTIPLY);
            } else {
                status = "Not specified";
                statusCondition = Constants.NOT_SPECIFIED;
                TV_score_status.setTextColor(getResources().getColor(R.color.gray));
                TV_avg_score.setTextColor(getResources().getColor(R.color.gray));
                IMG_score_icon.setImageDrawable(getResources().getDrawable(R.drawable.gray_crap));
            }
        }
        TV_score_status.setText(status);


        conditionID = conditionIDFunc(response.getCategoryList(), statusCondition, categoryID);

        Log.e(LOG, "Check conditionID : " + conditionID);
    }

    private double calculateScore(List<InsectImageDTO> dtos) {
        double score = 0.0;

        if (dtos == null || dtos.size() == 0) {
            return 0.0;
        } else {
            for (InsectImageDTO ii : dtos) {

                score = score + ii.getInsect().getSensitivityScore();
            }
        }
        return score;
    }

    private double calculateAverage(List<InsectImageDTO> dtos) {
        double average = 0.0, total = 0.0;
        if (dtos == null || dtos.size() == 0) {
            return 0.0;
        }
        for (InsectImageDTO ii : dtos) {
            total = total + ii.getInsect().getSensitivityScore();
        }
        average = total / dtos.size();
        average = Math.round(average * 100.0) / 100.0;
        return average;
    }

    public void buildUI() {
        setSpinner();
        Log.d(LOG, "******* getting cached data");
        startSelectionDialog();
        //addMinus();
        startGPSDialog();
    }


    private void sendRequest(final RequestDTO request) {
        BaseVolley.getRemoteData(Statics.SERVLET_TEST, request, ctx, new BaseVolley.BohaVolleyListener() {
            @Override
            public void onResponseReceived(ResponseDTO response) {
                if (response.getStatusCode() > 0) {
                    addRequestToCache(request);
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // cleanForm();
                            showImageDialog();
                        }
                    });
                }
            }

            @Override
            public void onVolleyError(VolleyError error) {
                //addRequestToCache(request);
            }
        });
       /* WebSocketUtil.sendRequest(ctx, Statics.MINI_SASS_ENDPOINT, request, new WebSocketUtil.WebSocketListener() {
            @Override
            public void onMessage(ResponseDTO response) {
                if (response.getStatusCode() > 0) {
                    addRequestToCache(request);
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // cleanForm();
                            showImageDialog();
                        }
                    });
                }
            }

            @Override
            public void onClose() {

            }

            @Override
            public void onError(String message) {
                addRequestToCache(request);
            }
        });*/
    }

    private void addRequestToCache(RequestDTO request) {
        RequestCacheUtil.addRequest(ctx, request, new CacheUtil.CacheRequestListener() {
            @Override
            public void onDataCached() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        showImageDialog();
                    }
                });


            }

            @Override
            public void onRequestCacheReturned(RequestCache cache) {

            }

            @Override
            public void onError() {

            }
        });
    }

    public void setEvaluationSite(EvaluationSiteDTO site) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AE_pin_point.setVisibility(View.VISIBLE);
            }
        });
        evaluationSite = new EvaluationSiteDTO();
        evaluationSite = site;
        evaluationSite.setCategoryID(categoryID);
        evaluationSite.setRiverID(riverID);

    }

    GregorianCalendar c;

    private void localSaveRequests() {
        RequestDTO w = new RequestDTO(RequestDTO.ADD_EVALUATION);
        c = new GregorianCalendar();
        evaluationDTO = new EvaluationDTO();


        Log.d(LOG, "" + selectedInsects.size());


        evaluationDTO.setConditionsID(conditionID);
        evaluationDTO.setTeamMemberID(teamMember.getTeamMemberID());
        evaluationSite.setDateRegistered(new Date().getTime());
        evaluationDTO.setEvaluationSite(evaluationSite);


        evaluationDTO.setRemarks(EDT_comment.getText().toString());
        evaluationDTO.setpH(Double.parseDouble(WP_score.getText().toString()));
        evaluationDTO.setOxygen(Double.parseDouble(WO_score.getText().toString()));
        evaluationDTO.setWaterClarity(Double.parseDouble(WC_score.getText().toString()));
        evaluationDTO.setWaterTemperature(Double.parseDouble(WT_score.getText().toString()));
        evaluationDTO.setElectricityConductivity(Double.parseDouble(WE_score.getText().toString()));
        evaluationDTO.setEvaluationDate(new Date().getTime());
        evaluationDTO.setScore(Double.parseDouble(TV_avg_score.getText().toString()));
        Log.i(LOG, new Date(evaluationDTO.getEvaluationDate()).toString());
        evaluationDTO.setEvaluationimageList(takenImages);

        w.setInsectImages(selectedInsects);
        w.setEvaluation(evaluationDTO);

        //ToastUtil.errorToast(ctx, c.getTime().getTime() + " : " + c.getTime());
        //Log.i(LOG, (new Gson()).toJson(w));
        System.out.println((new Gson()).toJson(w));
        WebCheckResult wcr = WebCheck.checkNetworkAvailability(ctx);


        if (wcr.isWifiConnected() || wcr.isMobileConnected()) {
            sendRequest(w);
            //addRequestToCache(w);
        } else {
            addRequestToCache(w);
        }

    }

    List<EvaluationImageDTO> takenImages;

    public void setTakenImage(List<EvaluationImageDTO> takenImages) {
        if (takenImages == null) {
            takenImages = new ArrayList<>();
        }
        this.takenImages = takenImages;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.e(LOG, "################ onStart .... connect API and location clients ");
        if (!mResolvingError) {  // more about this later
            mGoogleApiClient.connect();
        }

        Log.i(LOG,
                "### onStart, binding RequestSyncService and PhotoUploadService");
        Intent intent = new Intent(this, RequestSyncService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        super.onStart();
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
    public void onStop() {
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


    EvaluationSiteDTO evaluationSite;


    List<InsectImageDTO> insectImageList;


    private void showImageDialog() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        EvaluationActivity.this);

                // set title
                alertDialogBuilder.setTitle("Evaluation Created");

                // set dialog message
                alertDialogBuilder
                        .setMessage("Create another one?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // Intent intent = new Intent(ctx, PictureActivity.class);
                                // intent.putExtra("takenImages", )
                                // startActivityForResult(intent, CAPTURE_IMAGE);
                                cleanForm();
                                dialog.cancel();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // if this button is clicked, just close
                                // the dialog box and do nothing
                                finish();
                                dialog.cancel();
                            }
                        });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();
            }
        });


    }

    private void showEvaluationDialog() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        EvaluationActivity.this);

                // set title
                alertDialogBuilder.setTitle("Upload Results");

                // set dialog message
                alertDialogBuilder
                        .setMessage("Create a new Evaluation!!")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // Intent intent = new Intent(ctx, PictureActivity.class);
                                // intent.putExtra("takenImages", )
                                // startActivityForResult(intent, CAPTURE_IMAGE);
                                cleanForm();
                                dialog.cancel();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // if this button is clicked, just close
                                // the dialog box and do nothing
                                finish();
                                dialog.cancel();
                            }
                        });
                alertDialogBuilder
                        .setMessage("Contribute to Existing Site!!")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // Intent intent = new Intent(ctx, PictureActivity.class);
                                // intent.putExtra("takenImages", )
                                // startActivityForResult(intent, CAPTURE_IMAGE);
                                cleanForm();
                                dialog.cancel();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // if this button is clicked, just close
                                        // the dialog box and do nothing
                                        finish();
                                        dialog.cancel();
                                    }
                                }

                        );

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();
            }
        });


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
                        buildUI();

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

                    }
                });

            }
        });

    }

    boolean isBusy;

    private void getRiversAroundMe() {
        if (location == null) {
            Toast.makeText(ctx, "Busy...getting rivers ...", Toast.LENGTH_SHORT).show();
            return;
        }
        if (isBusy) {
            Toast.makeText(ctx, "Busy...getting rivers ...", Toast.LENGTH_SHORT).show();
            return;
        }


        RequestDTO w = new RequestDTO();
        w.setRequestType(RequestDTO.LIST_DATA_WITH_RADIUS_RIVERS);
        w.setLatitude(location.getLatitude());
        w.setLongitude(location.getLongitude());
        w.setRadius(20);
        isBusy = true;

        BaseVolley.getRemoteData(Statics.SERVLET_ENDPOINT, w, ctx, new BaseVolley.BohaVolleyListener() {
            @Override
            public void onResponseReceived(ResponseDTO r) {
                isBusy = false;
//                progressBar.setVisibility(View.GONE);
                if (r.getStatusCode() > 0) {
                    Toast.makeText(ctx, r.getMessage(), Toast.LENGTH_LONG).show();
                    return;
                }
               /* if (r.getRiverList().isEmpty()) {
                    Toast.makeText(ctx, "No rivers found within 20 km", Toast.LENGTH_LONG).show();
                    return;
                }*/

                CacheUtil.cacheRiverData(ctx, r, CacheUtil.CACHE_RIVER, new CacheUtil.CacheUtilListener() {
                    @Override
                    public void onFileDataDeserialized(ResponseDTO response) {

                    }

                    @Override
                    public void onDataCached(ResponseDTO r) {
                        response = r;
                        Log.d(LOG, new Gson().toJson(r));
                        buildUI();
                    }

                    @Override
                    public void onError() {

                    }
                });
            }

            @Override
            public void onVolleyError(VolleyError error) {
                isBusy = false;
                //Toast.makeText(ctx, "Problem: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });


    }


    protected void stopLocationUpdates() {
        Log.e(LOG, "###### stopLocationUpdates - " + new Date().toString());
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);
        }
    }

    boolean mRequestingLocationUpdates;

    protected void startLocationUpdates() {
        Log.w(LOG, "###### startLocationUpdates: " + new Date().toString());
        if (mGoogleApiClient.isConnected()) {
            mRequestingLocationUpdates = true;
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, locationRequest, this);
        }
    }
    boolean isInsectsPickerBack = false;
    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
        setGPSLocation(location);
        if (location.getAccuracy() <= ACCURACY_LIMIT) {
            setGPSLocation(location);
           // stopLocationUpdates();
            if(!isInsectsPickerBack) {
                getCachedRiverData();
            }
        }
        Log.e(LOG, "####### onLocationChanged");
    }

    Float accuracy = null;

    private void setGPSLocation(Location loc) {
        if (evaluationSite == null) {
            evaluationSite = new EvaluationSiteDTO();
        }
        this.location = loc;

        evaluationSite.setLatitude(location.getLatitude());
        evaluationSite.setLongitude(location.getLongitude());
        evaluationSite.setAccuracy(location.getAccuracy());
        setLocationTextviews(loc);
        accuracy = location.getAccuracy();
        Log.w(LOG, "### Passing " + loc.getAccuracy());
        if (loc.getAccuracy() <= ACCURACY_THRESHOLD) {

            location = loc;
            setLocationTextviews(loc);
            Log.w(LOG, "### Passing location2");
            evaluationSite.setLatitude(location.getLatitude());
            evaluationSite.setLongitude(location.getLongitude());
            evaluationSite.setAccuracy(location.getAccuracy());
            accuracy = location.getAccuracy();
            stopLocationUpdates();
            setEvaluationSite(evaluationSite);

            //finish();
            Log.e(LOG, "+++ best accuracy found: " + location.getAccuracy());
        }
    }


    //getting evaluations by distance
    boolean isBusy2;


    private void getEvaluationsByRiver() {
        if (isBusy2) {
            Toast.makeText(ctx, "Busy...getting rivers ...", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestDTO w = new RequestDTO();
        w.setRequestType(RequestDTO.LIST_EVALUATION_BY_RIVER_ID);
        w.setRiverID(riverID);
        isBusy2 = true;

        BaseVolley.getRemoteData(Statics.SERVLET_ENDPOINT, w, ctx, new BaseVolley.BohaVolleyListener() {
            @Override
            public void onResponseReceived(ResponseDTO r) {
                isBusy2 = false;
//                progressBar.setVisibility(View.GONE);
                if (r.getStatusCode() > 0) {
                    Toast.makeText(ctx, r.getMessage(), Toast.LENGTH_LONG).show();
                    return;
                }

                setPopupList(r);

            }

            @Override
            public void onVolleyError(VolleyError error) {
                isBusy2 = false;
                //Toast.makeText(ctx, "Problem: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setPopupList(ResponseDTO r) {
        resp = r;
        calculateDistancesForSite();
        Util.showPopupEvaluationSite(ctx, activity, resp.getEvaluationSiteList(), AE_find_near_sites, "Near Evaluated Site", new Util.PopupSiteListener() {
            @Override
            public void onEvaluationClicked(EvaluationSiteDTO evaluation) {
                showEvaluationDialog();
                Log.i(LOG, new Gson().toJson(evaluation));
                setFieldsFromVisitedSites(evaluation);
            }
        });
    }

    ResponseDTO resp;

    private void calculateDistancesForSite() {
        if (location != null) {
            List<EvaluationSiteDTO> riverPoints = new ArrayList<>();
            Location spot = new Location(LocationManager.GPS_PROVIDER);

            for (EvaluationSiteDTO w : resp.getEvaluationSiteList()) {
                spot.setLatitude(w.getLatitude());
                spot.setLongitude(w.getLongitude());
                w.setDistanceFromMe(location.distanceTo(spot));
            }
            Collections.sort(resp.getEvaluationSiteList());
        }
    }

    static final int ACCURACY_LIMIT = 50;

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
            getRiversAroundMe();
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

    private void getCachedRiverData() {
        final WebCheckResult w = WebCheck.checkNetworkAvailability(ctx);
        CacheUtil.getCachedData(ctx, CacheUtil.CACHE_DATA, new CacheUtil.CacheUtilListener() {
            @Override
            public void onFileDataDeserialized(final ResponseDTO respond) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        response = respond;
                        buildUI();

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
}

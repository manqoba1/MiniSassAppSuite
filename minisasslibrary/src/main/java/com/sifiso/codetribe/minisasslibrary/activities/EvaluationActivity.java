package com.sifiso.codetribe.minisasslibrary.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.IBinder;
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
import android.widget.Spinner;
import android.widget.TextView;

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
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.RequestDTO;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.ResponseDTO;
import com.sifiso.codetribe.minisasslibrary.fragments.PageFragment;
import com.sifiso.codetribe.minisasslibrary.services.RequestCache;
import com.sifiso.codetribe.minisasslibrary.services.RequestSyncService;
import com.sifiso.codetribe.minisasslibrary.toolbox.WebCheck;
import com.sifiso.codetribe.minisasslibrary.toolbox.WebCheckResult;
import com.sifiso.codetribe.minisasslibrary.util.CacheUtil;
import com.sifiso.codetribe.minisasslibrary.util.ErrorUtil;
import com.sifiso.codetribe.minisasslibrary.util.RequestCacheUtil;
import com.sifiso.codetribe.minisasslibrary.util.Statics;
import com.sifiso.codetribe.minisasslibrary.util.ToastUtil;
import com.sifiso.codetribe.minisasslibrary.util.Util;
import com.sifiso.codetribe.minisasslibrary.util.WebSocketUtil;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;


public class EvaluationActivity extends ActionBarActivity {
    static final String LOG = EvaluationActivity.class.getSimpleName();
    private TextView WC_minus, WC_add, WT_minus, WT_add,
            WP_minus, WP_add, WO_minus, WO_add, WE_minus, WE_add;
    private EditText WC_score, WP_score, WT_score, WO_score, WE_score;
    private TextView TV_total_score, TV_average_score, TV_avg_score, TV_score_status;
    private ImageView IMG_score_icon, AE_pin_point;
    private TextView WT_sp_river, WT_sp_category, EDT_comment;
    private Button WT_gps_picker, SL_show_insect, AE_create;
    EvaluationDTO evaluationDTO;
    Integer teamMemberID, conditionID;
    double wc = 0.0, wt = 0.0, we = 0.0, wp = 0.0, wo = 0.0;
    private Activity activity;
    List<String> categoryStr;
    private ViewStub viewStub;
    private Integer categoryID;
    private Integer riverID;

    String catType = "Select category";

    static final int ACCURACY_THRESHOLD = 5;
    static final int MAP_REQUESTED = 9007;
    static final int STATUS_CODE = 220;
    static final int INSECT_DATA = 103;
    public static final int CAPTURE_IMAGE = 1001;
    static final int CREATE_EVALUATION = 108;

    static final int GPS_DATA = 102;

    ViewPager mPager;
    Menu mMenu;
    List<PageFragment> pageFragmentList;
    ResponseDTO response;
    boolean mBound;
    RequestSyncService mService;

    private Context ctx;
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
        //activity = EvaluationActivity.this;
        setTitle("Create Evaluations");


        if (savedInstanceState != null) {
            response = (ResponseDTO) savedInstanceState.getSerializable("response");
            buildUI();
        }
        setField();
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
        if (id == android.R.id.home) {
            finish();

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
                if (resultCode == RESULT_OK) {
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
                }
                break;
            case INSECT_DATA:
                Log.w(LOG, "### insects ui has returned with data?");
                if (resultCode == INSECT_DATA) {
                    Log.w(LOG, "### insect ui has returned with data?");
                    insectImageList = (List<InsectImageDTO>) data.getSerializableExtra("overallInsect");
                    scoreUpdater((List<InsectImageDTO>) data.getSerializableExtra("selectedInsects"));
                }
                break;
        }
    }

    public void setRiverField(RiverDTO r) {
        riverID = r.getRiverID();
        WT_sp_river.setText(r.getRiverName());

    }

    private void setSpinner(final ResponseDTO resp) {


        WT_sp_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(LOG, "category size " + resp.getCategoryList().size());
                Util.showPopupCategoryBasicWithHeroImage(ctx, EvaluationActivity.this, resp.getCategoryList(), WT_sp_category, "", new Util.UtilPopupListener() {
                    @Override
                    public void onItemSelected(int index) {

                        WT_sp_category.setText(resp.getCategoryList().get(index).getCategoryName());
                        categoryID = resp.getCategoryList().get(index).getCategoryID();
                        catType = (resp.getCategoryList().get(index).getCategoryName());
                        Log.e(LOG, catType);
                    }
                });
            }
        });

        WT_sp_river.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(LOG, "river size " + resp.getRiverList().size());

                Util.showPopupRiverWithHeroImage(ctx, EvaluationActivity.this, resp.getRiverList(), WT_sp_river, "", new Util.UtilPopupListener() {
                    @Override
                    public void onItemSelected(int index) {
                        // WT_sp_river.setTextColor(getResources().getColor(R.color.gray));
                        WT_sp_river.setText(resp.getRiverList().get(index).getRiverName());
                        riverID = resp.getRiverList().get(index).getRiverID();
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
        WT_sp_river = (TextView) findViewById(R.id.WT_sp_river);
        WT_sp_category = (TextView) findViewById(R.id.WT_sp_category);
        EDT_comment = (EditText) findViewById(R.id.EDT_comment);
        AE_pin_point = (ImageView) findViewById(R.id.AE_pin_point);
        WT_gps_picker = (Button) findViewById(R.id.WT_gps_picker);
        SL_show_insect = (Button) findViewById(R.id.SL_show_insect);
        AE_create = (Button) findViewById(R.id.AE_create);

        viewStub = (ViewStub) findViewById(R.id.viewStub);
        if (codeStatus == CREATE_EVALUATION) {
            river = (RiverDTO) getIntent().getSerializableExtra("riverCreate");
            WT_sp_river.setTextColor(getResources().getColor(R.color.gray));
            WT_sp_river.setText(river.getRiverName());
            riverID = river.getRiverID();
        }
        // buildUI();
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
                    ToastUtil.toast(ctx, "Please choose evaluated river");
                }
            }
        });

        AE_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (evaluationSite != null) {
                    localSaveRequests();
                } else {
                    ToastUtil.toast(ctx, "Please choose evaluated river");
                }
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
                    ToastUtil.toast(ctx, "Select category, before choosing insects");
                } else {

                    Log.d(LOG, "Insect Select " + insectImageList.size());
                    Intent intent = new Intent(EvaluationActivity.this, InsectPicker.class);
                    intent.putExtra("insetImageList", (java.io.Serializable) insectImageList);
                    startActivityForResult(intent, INSECT_DATA);
                }
            }
        });
    }

    public void scoreUpdater(List<InsectImageDTO> insectImages) {
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
                IMG_score_icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_insect));
                WT_sp_river.setText("");
                WT_sp_category.refreshDrawableState();
                EDT_comment.setText("");
                AE_pin_point.setVisibility(View.GONE);
                evaluationSite = new EvaluationSiteDTO();

                TV_score_status.setText("not specified");
                TV_score_status.setTextColor(getResources().getColor(R.color.red));
                IMG_score_icon.setColorFilter(getResources().getColor(R.color.red), PorterDuff.Mode.MULTIPLY);
                WE_score.setText("0.0");
                WO_score.setText("0.0");
                WC_score.setText("0.0");
                WP_score.setText("0.0");
                WT_score.setText("0.0");
            }
        });

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
        if (dtos == null || dtos.size() == 0) {
            average = 0.0;
        } else {
            for (InsectImageDTO ii : dtos) {
                total = total + ii.getSensitivityScore();
            }
            average = total / dtos.size();
        }


        if (type.equalsIgnoreCase("Sandy Type")) {
            if (average > 6.9) {
                status = "Unmodified(NATURAL condition)";
                TV_score_status.setTextColor(getResources().getColor(R.color.purple));
                TV_avg_score.setTextColor(getResources().getColor(R.color.purple));
                IMG_score_icon.setColorFilter(getResources().getColor(R.color.purple), PorterDuff.Mode.MULTIPLY);
            } else if (average > 5.8 && average < 6.9) {
                status = "Largely natural/few modifications(GOOD condition)";
                TV_score_status.setTextColor(getResources().getColor(R.color.green));
                TV_avg_score.setTextColor(getResources().getColor(R.color.green));
                IMG_score_icon.setColorFilter(getResources().getColor(R.color.green), PorterDuff.Mode.MULTIPLY);
            } else if (average > 4.9 && average < 5.8) {
                status = "Moderately modified(FAIR condition)";
                TV_score_status.setTextColor(getResources().getColor(R.color.yellow_dark));
                TV_avg_score.setTextColor(getResources().getColor(R.color.yellow_dark));
                IMG_score_icon.setColorFilter(getResources().getColor(R.color.yellow_dark), PorterDuff.Mode.MULTIPLY);
            } else if (average > 4.3 && average < 4.9) {
                status = "Largely modified(POOR condition)";
                TV_score_status.setTextColor(getResources().getColor(R.color.orange));
                TV_avg_score.setTextColor(getResources().getColor(R.color.orange));
                IMG_score_icon.setColorFilter(getResources().getColor(R.color.orange), PorterDuff.Mode.MULTIPLY);
            } else if (average < 4.3) {
                status = "Seriously/critically modified";
                TV_score_status.setTextColor(getResources().getColor(R.color.red));
                TV_avg_score.setTextColor(getResources().getColor(R.color.red));
                IMG_score_icon.setColorFilter(getResources().getColor(R.color.red), PorterDuff.Mode.MULTIPLY);
            } else {
                status = "Not specified";
                TV_score_status.setTextColor(getResources().getColor(R.color.gray));
                TV_avg_score.setTextColor(getResources().getColor(R.color.gray));
                IMG_score_icon.setColorFilter(getResources().getColor(R.color.gray), PorterDuff.Mode.MULTIPLY);
            }
        } else if (type.equalsIgnoreCase("Rocky Type")) {
            if (average > 7.9) {
                status = "Unmodified(NATURAL condition)";
                TV_score_status.setTextColor(getResources().getColor(R.color.purple));
                TV_avg_score.setTextColor(getResources().getColor(R.color.purple));
                IMG_score_icon.setColorFilter(getResources().getColor(R.color.purple), PorterDuff.Mode.MULTIPLY);
            } else if (average > 6.8 && average < 7.9) {
                status = "Largely natural/few modifications(GOOD condition)";
                TV_score_status.setTextColor(getResources().getColor(R.color.green));
                TV_avg_score.setTextColor(getResources().getColor(R.color.green));
                IMG_score_icon.setColorFilter(getResources().getColor(R.color.green), PorterDuff.Mode.MULTIPLY);
            } else if (average > 6.1 && average < 6.8) {
                status = "Moderately modified(FAIR condition)";
                TV_score_status.setTextColor(getResources().getColor(R.color.yellow_dark));
                TV_avg_score.setTextColor(getResources().getColor(R.color.yellow_dark));
                IMG_score_icon.setColorFilter(getResources().getColor(R.color.yellow_dark), PorterDuff.Mode.MULTIPLY);
            } else if (average > 5.1 && average < 6.1) {
                status = "Largely modified(POOR condition)";
                TV_score_status.setTextColor(getResources().getColor(R.color.orange));
                TV_avg_score.setTextColor(getResources().getColor(R.color.orange));
                IMG_score_icon.setColorFilter(getResources().getColor(R.color.orange), PorterDuff.Mode.MULTIPLY);
            } else if (average < 5.1) {
                status = "Seriously/critically modified";
                TV_avg_score.setTextColor(getResources().getColor(R.color.red));
                TV_score_status.setTextColor(getResources().getColor(R.color.red));
                IMG_score_icon.setColorFilter(getResources().getColor(R.color.red), PorterDuff.Mode.MULTIPLY);
            } else {

            }
        }

        TV_score_status.setText(status);
        conditionID = conditionIDFunc(response.getCategoryList(), status, type);
        Log.e(LOG, "lskdfsdf " + conditionIDFunc(response.getCategoryList(), status, type));
    }

    private double calculateScore(List<InsectImageDTO> dtos) {
        double score = 0.0;
        if (dtos == null || dtos.size() == 0) {
            return 0.0;
        } else {
            for (InsectImageDTO ii : dtos) {
                score = score + ii.getSensitivityScore();
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
            total = total + ii.getSensitivityScore();
        }
        average = total / dtos.size();
        return average;
    }

    public void buildUI() {
        setSpinner(response);
        Log.d(LOG, "******* getting cached data");
        startSelectionDialog();
        addMinus();
        startGPSDialog();
    }

    private void sendRequest(final RequestDTO request) {
        WebSocketUtil.sendRequest(ctx, Statics.MINI_SASS_ENDPOINT, request, new WebSocketUtil.WebSocketListener() {
            @Override
            public void onMessage(ResponseDTO response) {
                if (response.getStatusCode() > 0) {
                    addRequestToCache(request);
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            cleanForm();
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
        });
    }

    private void addRequestToCache(RequestDTO request) {
        RequestCacheUtil.addRequest(ctx, request, new CacheUtil.CacheRequestListener() {
            @Override
            public void onDataCached() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        cleanForm();
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
        if (selectedInsects == null) {
            selectedInsects = new ArrayList<>();
            ToastUtil.errorToast(ctx, "If you don't select insect group, you can't score evaluation(hint: SELECT INSECT GROUP)");
            return;
        }

        Log.d(LOG, "" + selectedInsects.size());


        evaluationDTO.setConditionsID(conditionID);
        evaluationDTO.setTeamMemberID(1);
        evaluationSite.setDateRegistered(c.getTime().getTime());
        evaluationDTO.setEvaluationSite(evaluationSite);


        evaluationDTO.setRemarks(EDT_comment.getText().toString());
        evaluationDTO.setpH(Double.parseDouble(WP_score.getText().toString()));
        evaluationDTO.setOxygen(Double.parseDouble(WO_score.getText().toString()));
        evaluationDTO.setWaterClarity(Double.parseDouble(WC_score.getText().toString()));
        evaluationDTO.setWaterTemperature(Double.parseDouble(WT_score.getText().toString()));
        evaluationDTO.setElectricityConductivity(Double.parseDouble(WE_score.getText().toString()));
        evaluationDTO.setEvaluationDate(c.getTime().getTime());
        evaluationDTO.setScore(Double.parseDouble(TV_avg_score.getText().toString()));
        evaluationDTO.setLatitude(evaluationSite.getLatitude());
        evaluationDTO.setLongitude(evaluationSite.getLongitude());

        evaluationDTO.setEvaluationImageList(takenImages);

        w.setInsectImages(selectedInsects);
        w.setEvaluation(evaluationDTO);

        //ToastUtil.errorToast(ctx, c.getTime().getTime() + " : " + c.getTime());
        Log.i(LOG, (new Gson()).toJson(w));
        WebCheckResult wcr = WebCheck.checkNetworkAvailability(ctx);
        if (evaluationDTO.getEvaluationSite() == null) {
            ToastUtil.errorToast(ctx, "Please make sure Site Evaluation is picked(hint: SITE GPS LOCATION)");
            return;
        }
        if (evaluationDTO.getConditionsID() == null) {
            ToastUtil.errorToast(ctx, "If you don't select insect group, you can't score evaluation(hint: SELECT INSECT GROUP)");
            return;
        }

        if (wcr.isWifiConnected()) {
            sendRequest(w);
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
        Log.i(LOG,
                "### onStart, binding RequestSyncService and PhotoUploadService");
        Intent intent = new Intent(this, RequestSyncService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
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


        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
        super.onStop();
    }


    EvaluationSiteDTO evaluationSite;


    @Override
    public void onPause() {
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        super.onPause();
    }


    List<InsectImageDTO> insectImageList;


    private void showImageDialog() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        EvaluationActivity.this);

                // set title
                alertDialogBuilder.setTitle("Create Evaluation Gallery?");

                // set dialog message
                alertDialogBuilder
                        .setMessage("Click no to exit!")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent(ctx, PictureActivity.class);
                                // intent.putExtra("takenImages", )
                                startActivityForResult(intent, CAPTURE_IMAGE);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // if this button is clicked, just close
                                // the dialog box and do nothing
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
}

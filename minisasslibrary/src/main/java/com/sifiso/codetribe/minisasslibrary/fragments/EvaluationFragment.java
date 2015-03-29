package com.sifiso.codetribe.minisasslibrary.fragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.sifiso.codetribe.minisasslibrary.services.RequestCache;
import com.sifiso.codetribe.minisasslibrary.toolbox.WebCheck;
import com.sifiso.codetribe.minisasslibrary.toolbox.WebCheckResult;
import com.sifiso.codetribe.minisasslibrary.util.CacheUtil;
import com.sifiso.codetribe.minisasslibrary.util.RequestCacheUtil;
import com.sifiso.codetribe.minisasslibrary.util.Statics;
import com.sifiso.codetribe.minisasslibrary.util.ToastUtil;
import com.sifiso.codetribe.minisasslibrary.util.Util;
import com.sifiso.codetribe.minisasslibrary.util.WebSocketUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class EvaluationFragment extends Fragment implements PageFragment {
    static final String LOG = EvaluationFragment.class.getSimpleName();
    private TextView WC_minus, WC_add, WT_minus, WT_add,
            WP_minus, WP_add, WO_minus, WO_add, WE_minus, WE_add;
    private EditText WC_score, WP_score, WT_score, WO_score, WE_score;
    private TextView TV_total_score, TV_average_score, TV_avg_score, TV_score_status;
    private ImageView IMG_score_icon, AE_pin_point;
    private TextView WT_sp_river, EDT_comment;
    private Button WT_gps_picker, SL_show_insect, AE_create;
    private Context ctx;
    EvaluationDTO evaluationDTO;
    Integer teamMemberID, conditionID;
    ResponseDTO response;
    InsectSelectionDialog insectSelectionDialog;
    double wc = 0.0, wt = 0.0, we = 0.0, wp = 0.0, wo = 0.0;
    private Activity activity;
    List<String> categoryStr;
    private Spinner WT_sp_category;
    private ViewStub viewStub;
    private Integer categoryID;
    private Integer riverID;
    private List<InsectImageDTO> insectImageList;
    private EvaluationSiteDTO evaluationSite;
    View v;
    int codeStatus;
    RiverDTO river;
    String catType = "Select category";
    EvaluationFragmentListener mListener;
    static final int CREATE_EVALUATION = 108;

    public EvaluationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    public void setRiverField(RiverDTO r) {
        riverID = r.getRiverID();
        WT_sp_river.setText(r.getRiverName());
        buildUI();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_evaluation, container, false);
        ctx = getActivity().getApplicationContext();
        activity = getActivity();
        response = (ResponseDTO) getArguments().get("response");
        setField();
        return v;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (EvaluationFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(ctx, R.layout.xsimple_spinner_item, categoryStr);
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

                Util.showPopupRiverWithHeroImage(ctx, activity, resp.getRiverList(), WT_sp_river, "", new Util.UtilPopupListener() {
                    @Override
                    public void onItemSelected(int index) {
                        // WT_sp_river.setTextColor(getResources().getColor(R.color.gray));
                        WT_sp_river.setText(resp.getRiverList().get(index).getRiverName());
                        riverID = response.getRiverList().get(index).getRiverID();
                    }
                });
            }
        });


    }

    private void setField() {
        WC_minus = (TextView) v.findViewById(R.id.WC_minus);
        WC_score = (EditText) v.findViewById(R.id.WC_score);
        WC_add = (TextView) v.findViewById(R.id.WC_add);
        WT_minus = (TextView) v.findViewById(R.id.WT_minus);
        WT_score = (EditText) v.findViewById(R.id.WT_score);
        WT_add = (TextView) v.findViewById(R.id.WT_add);
        WP_minus = (TextView) v.findViewById(R.id.WP_minus);
        WP_score = (EditText) v.findViewById(R.id.WP_score);
        WP_add = (TextView) v.findViewById(R.id.WP_add);
        WO_minus = (TextView) v.findViewById(R.id.WO_minus);
        WO_score = (EditText) v.findViewById(R.id.WO_score);
        WO_add = (TextView) v.findViewById(R.id.WO_add);
        WE_minus = (TextView) v.findViewById(R.id.WE_minus);
        WE_score = (EditText) v.findViewById(R.id.WE_score);
        WE_add = (TextView) v.findViewById(R.id.WE_add);

        TV_total_score = (TextView) v.findViewById(R.id.TV_total_score);
        TV_average_score = (TextView) v.findViewById(R.id.TV_average_score);
        TV_avg_score = (TextView) v.findViewById(R.id.TV_avg_score);
        TV_score_status = (TextView) v.findViewById(R.id.TV_score_status);
        IMG_score_icon = (ImageView) v.findViewById(R.id.IMG_score_icon);
        WT_sp_river = (TextView) v.findViewById(R.id.WT_sp_river);
        WT_sp_category = (Spinner) v.findViewById(R.id.WT_sp_category);
        EDT_comment = (EditText) v.findViewById(R.id.EDT_comment);
        AE_pin_point = (ImageView) v.findViewById(R.id.AE_pin_point);
        WT_gps_picker = (Button) v.findViewById(R.id.WT_gps_picker);
        SL_show_insect = (Button) v.findViewById(R.id.SL_show_insect);
        AE_create = (Button) v.findViewById(R.id.AE_create);

        viewStub = (ViewStub) v.findViewById(R.id.viewStub);
        if (codeStatus == CREATE_EVALUATION) {
            river = (RiverDTO) activity.getIntent().getSerializableExtra("riverCreate");
            WT_sp_river.setTextColor(getResources().getColor(R.color.gray));
            WT_sp_river.setText(river.getRiverName());
            riverID = river.getRiverID();
        }
        buildUI();
    }

    private void addMinus() {
        WO_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wo = Double.parseDouble(WO_score.getText().toString()) + 0.5;
                activity.runOnUiThread(new Runnable() {
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
                activity.runOnUiThread(new Runnable() {
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
                activity.runOnUiThread(new Runnable() {
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
                activity.runOnUiThread(new Runnable() {
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
                activity.runOnUiThread(new Runnable() {
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
                activity.runOnUiThread(new Runnable() {
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
                activity.runOnUiThread(new Runnable() {
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

                activity.runOnUiThread(new Runnable() {
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
                activity.runOnUiThread(new Runnable() {
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
                activity.runOnUiThread(new Runnable() {
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
                    mListener.onScanGpsRequest();
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
                    insectSelectionDialog = new InsectSelectionDialog();
                    insectSelectionDialog.setmSites(insectImageList);
                    insectSelectionDialog.setListener(new InsectSelectionDialog.InsectSelectionDialogListener() {
                        @Override
                        public void onSelectDone(final List<InsectImageDTO> insectImages) {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (insectImages == null) {
                                        return;
                                    }
                                    scoreUpdater(insectImages);

                                }
                            });
                        }
                    });
                    insectSelectionDialog.show(activity.getFragmentManager(), "");
                    insectSelectionDialog = null;
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
        TV_total_score.setText("");
        TV_average_score.setText("");
        TV_avg_score.setText("");
        IMG_score_icon.setImageDrawable(getResources().getDrawable(android.R.drawable.star_big_off));
        WT_sp_river.setText("");
        WT_sp_category.refreshDrawableState();
        EDT_comment.setText("");
        AE_pin_point.setVisibility(View.GONE);
        evaluationSite = new EvaluationSiteDTO();

        TV_score_status.setText("not specified");
        TV_score_status.setTextColor(getResources().getColor(R.color.gray));
        WE_score.setText("");
        WO_score.setText("");
        WC_score.setText("");
        WP_score.setText("");
        WT_score.setText("");
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
        for (InsectImageDTO ii : dtos) {
            score = score + ii.getSensitivityScore();
        }
        return score;
    }

    private double calculateAverage(List<InsectImageDTO> dtos) {
        double average = 0.0, total = 0.0;
        if (dtos.isEmpty()) {
            return 0.0;
        }
        for (InsectImageDTO ii : dtos) {
            total = total + ii.getSensitivityScore();
        }
        average = total / dtos.size();
        return average;
    }


    public void setResponse(ResponseDTO response) {
        this.response = response;
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
                    cleanForm();
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
                cleanForm();


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
        AE_pin_point.setVisibility(View.VISIBLE);
        evaluationSite = site;
        evaluationSite.setCategoryID(categoryID);
        evaluationSite.setRiverID(riverID);

    }

    private void localSaveRequests() {
        RequestDTO w = new RequestDTO(RequestDTO.ADD_EVALUATION);

        evaluationDTO = new EvaluationDTO();
        if (selectedInsects == null) {
            selectedInsects = new ArrayList<>();
            ToastUtil.errorToast(ctx, "If you don't select insect group, you can't score evaluation(hint: SELECT INSECT GROUP)");
            return;
        }

        Log.d(LOG, "" + selectedInsects.size());


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


        w.setInsectImages(selectedInsects);
        w.setEvaluation(evaluationDTO);


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

    @Override
    public void animateCounts() {

    }

    public interface EvaluationFragmentListener {
        // TODO: Update argument type and name
        public void onScanGpsRequest();

        public void onSelectInsectsRequest();

        public void onDoneEvaluationRequest();

    }

}

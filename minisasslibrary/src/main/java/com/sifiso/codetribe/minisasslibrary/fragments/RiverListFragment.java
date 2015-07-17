package com.sifiso.codetribe.minisasslibrary.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.sifiso.codetribe.minisasslibrary.R;
import com.sifiso.codetribe.minisasslibrary.activities.EvaluationActivity;
import com.sifiso.codetribe.minisasslibrary.adapters.RiverAdapter;
import com.sifiso.codetribe.minisasslibrary.dialogs.EvaluationListDialog;
import com.sifiso.codetribe.minisasslibrary.dto.EvaluationSiteDTO;
import com.sifiso.codetribe.minisasslibrary.dto.RiverDTO;
import com.sifiso.codetribe.minisasslibrary.dto.RiverTownDTO;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.ResponseDTO;
import com.sifiso.codetribe.minisasslibrary.services.CreateEvaluationListener;
import com.sifiso.codetribe.minisasslibrary.util.GPSTracker;
import com.sifiso.codetribe.minisasslibrary.util.Util;

import java.util.ArrayList;
import java.util.List;


public class RiverListFragment extends Fragment implements PageFragment {


    ResponseDTO response;
    RiverAdapter riverAdapter;
    boolean isFound;
    String searchText;
    CreateEvaluationListener mListener;
    private Context ctx;
    private Activity activity;
    private AutoCompleteTextView SLT_editSearch;
    private ImageView SLT_imgSearch2, SLT_hero;
    private ListView RL_riverList;


    public RiverListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

            response = (ResponseDTO) getArguments().getSerializable("response");
        }
    }

    View v;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.river_list, container, false);
        ctx = getActivity().getApplicationContext();
        activity = getActivity();
        getActivity().setTitle("MiniSASS Rivers");
        GPSTracker tracker = new GPSTracker(ctx);
        setField();
        return v;
    }

    private void setField() {
        SLT_editSearch = (AutoCompleteTextView) v.findViewById(R.id.SLT_editSearch);
        SLT_hero = (ImageView) v.findViewById(R.id.SLT_hero);

        SLT_imgSearch2 = (ImageView) v.findViewById(R.id.SLT_imgSearch2);
        // SLT_imgSearch2.setVisibility(View.GONE);
        // SLT_editSearch.setVisibility(View.GONE);

        SLT_imgSearch2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                searchRiver();
            }
        });
        RL_riverList = (ListView) v.findViewById(R.id.RL_riverList);
        SLT_hero.setImageDrawable(Util.getRandomHeroImage(ctx));

        setListView();
        riverToSearch();
    }

    List<String> stringRiver;

    private void riverToSearch() {
        stringRiver = new ArrayList<>();
        for (int i = 0; i < response.getRiverList().size(); i++) {
            RiverDTO riverText = response.getRiverList().get(i);
            stringRiver.add(riverText.getRiverName().trim());
        }
        ArrayAdapter<String> riverSearchAdapter = new ArrayAdapter<String>(ctx, R.layout.xsimple_spinner_dropdown_item, stringRiver);
        SLT_editSearch.setAdapter(riverSearchAdapter);
    }

    private void searchRiver() {
        if (SLT_editSearch.getText().toString().isEmpty()) {
            return;
        }
        int index = 0;
        searchText = SLT_editSearch.getText().toString();
        for (int i = 0; i < response.getRiverList().size(); i++) {
            RiverDTO searchRiver = response.getRiverList().get(i);
            String str = searchRiver.getRiverName() + " River";
            if (searchRiver.getRiverName().contains(searchText)) {
                isFound = true;
                break;
            }
            index++;
        }
        if (isFound) {
            RL_riverList.setSelection(index);


        } else {
            Util.showToast(ctx, ctx.getString(R.string.river_not_found) + " " + SLT_editSearch.getText().toString());
        }
        hideKeyboard();
    }

    void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) ctx
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(SLT_editSearch.getWindowToken(), 0);
    }

    public void startEvaluation() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(ctx, EvaluationActivity.class);
                intent.putExtra("response", response);
                startActivity(intent);
            }
        });

    }

    EvaluationListDialog evaluationListDialog;
    static String LOG = RiverListFragment.class.getSimpleName();


    private void setListView() {

        Log.d(LOG, "Mentor " + new Gson().toJson(response.getRiverList()));
        riverAdapter = new RiverAdapter(response.getRiverList(), ctx, new RiverAdapter.RiverAdapterListener() {
            @Override
            public void onDirection(Double latitude, Double longitude) {
                mListener.onDirection(latitude,longitude);
            }

            @Override
            public void onMapSiteRequest(List<EvaluationSiteDTO> siteList) {

            }

            @Override
            public void onEvaluationRequest(List<EvaluationSiteDTO> siteList) {

                mListener.onRefreshEvaluation(siteList, 1);
            }

            @Override
            public void onCreateEvaluation(RiverDTO river) {
                mListener.onCreateEvaluation(river);
            }

            @Override
            public void onMapRequest(RiverDTO river, int result) {
                mListener.onRefreshMap(river, result);
            }
        });
        LayoutInflater inf = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inf.inflate(R.layout.hero_layout, null);
        // RL_riverList.addHeaderView(v);
        RL_riverList.setAdapter(riverAdapter);
        RL_riverList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                mListener.onCreateEvaluation((RiverDTO) parent.getItemAtPosition(position));
                return false;
            }
        });
    }

    @Override
    public void animateCounts() {

    }


    static final int EVALUATION_VIEW = 12;
    static final int RIVER_VIEW = 13;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (CreateEvaluationListener) activity;
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
}

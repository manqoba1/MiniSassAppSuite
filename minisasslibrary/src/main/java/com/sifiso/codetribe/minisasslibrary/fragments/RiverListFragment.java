package com.sifiso.codetribe.minisasslibrary.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.wallet.fragment.BuyButtonAppearance;
import com.sifiso.codetribe.minisasslibrary.R;
import com.sifiso.codetribe.minisasslibrary.adapters.RiverAdapter;
import com.sifiso.codetribe.minisasslibrary.dialogs.EvaluationListDialog;
import com.sifiso.codetribe.minisasslibrary.dto.EvaluationSiteDTO;
import com.sifiso.codetribe.minisasslibrary.dto.RiverDTO;
import com.sifiso.codetribe.minisasslibrary.dto.RiverTownDTO;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.ResponseDTO;
import com.sifiso.codetribe.minisasslibrary.util.ToastUtil;
import com.sifiso.codetribe.minisasslibrary.util.Util;

import java.util.List;


public class RiverListFragment extends Fragment implements PageFragment {


    ResponseDTO response;
    RiverAdapter riverAdapter;
    boolean isFound;
    String searchText;
    RiverListFragmentListener mListener;
    private Context ctx;
    private EditText SLT_editSearch;
    private ImageView SLT_imgSearch2;
    private ListView RL_riverList;
    private TextView RL_add;


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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.river_list, container, false);
        ctx = getActivity().getApplicationContext();
        getActivity().setTitle("MiniSASS Rivers");
        SLT_editSearch = (EditText) v.findViewById(R.id.SLT_editSearch);

        SLT_imgSearch2 = (ImageView) v.findViewById(R.id.SLT_imgSearch2);
        SLT_imgSearch2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                searchRiver();
            }
        });
        RL_riverList = (ListView) v.findViewById(R.id.RL_riverList);
        setListView();
        return v;
    }

    private void searchRiver() {
        if (SLT_editSearch.getText().toString().isEmpty()) {
            return;
        }
        int index = 0;
        searchText = SLT_editSearch.getText().toString();
        for (int i = 0; i < response.getRiverList().size(); i++) {
            RiverDTO searchRiver = response.getRiverList().get(i);
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

    EvaluationListDialog evaluationListDialog;

    private void setListView() {
        riverAdapter = new RiverAdapter(response.getRiverList(), ctx, new RiverAdapter.RiverAdapterListener() {
            @Override
            public void onMapSiteRequest(List<EvaluationSiteDTO> siteList) {

            }

            @Override
            public void onEvaluationRequest(List<EvaluationSiteDTO> siteList) {
               /* evaluationListDialog = new EvaluationListDialog();
                Bundle b = new Bundle();
                b.putSerializable("evaluationSite", (java.io.Serializable) siteList);
                evaluationListDialog.setArguments(b);
                evaluationListDialog.show(getFragmentManager(),"");*/
                mListener.onRefreshEvaluation(siteList, 1);
            }

            @Override
            public void onTownshipRequest(List<RiverTownDTO> riverTownList) {
                mListener.onRefreshTown(riverTownList, 111);
            }

            @Override
            public void onMapRequest(RiverDTO river, int result) {
                mListener.onRefreshMap(river, result);
            }
        });
        LayoutInflater inf = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inf.inflate(R.layout.hero_layout, null);
        RL_riverList.addHeaderView(v);
        RL_riverList.setAdapter(riverAdapter);
        RL_riverList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                ToastUtil.toast(ctx, "Create Evaluation");
                mListener.onCreateEvaluationRL((RiverDTO) parent.getItemAtPosition(position));
                return false;
            }
        });
    }

    @Override
    public void animateCounts() {

    }

    public interface RiverListFragmentListener {

        public void onRefreshEvaluation(List<EvaluationSiteDTO> siteList, int index);

        public void onRefreshTown(List<RiverTownDTO> riverTownList, int index);

        public void onRefreshMap(RiverDTO river, int result);

        public void onCreateEvaluationRL(RiverDTO river);
    }

    static final int EVALUATION_VIEW = 12;
    static final int RIVER_VIEW = 13;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (RiverListFragmentListener) activity;
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

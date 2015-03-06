package com.sifiso.codetribe.minisasslibrary.fragments;

import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.sifiso.codetribe.minisasslibrary.R;
import com.sifiso.codetribe.minisasslibrary.adapters.RiverAdapter;
import com.sifiso.codetribe.minisasslibrary.dto.EvaluationSiteDTO;
import com.sifiso.codetribe.minisasslibrary.dto.RiverDTO;
import com.sifiso.codetribe.minisasslibrary.dto.RiverTownDTO;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.ResponseDTO;
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

    private void setListView() {
        riverAdapter = new RiverAdapter(response.getRiverList(), ctx, new RiverAdapter.RiverAdapterListener() {
            @Override
            public void onMapSiteRequest(List<EvaluationSiteDTO> siteList) {

            }

            @Override
            public void onEvaluationRequest(List<EvaluationSiteDTO> siteList) {

            }

            @Override
            public void onTownshipRequest(List<RiverTownDTO> riverTownList) {

            }

            @Override
            public void onMapRequest(RiverDTO river) {

            }
        });
        RL_riverList.setAdapter(riverAdapter);
    }

    @Override
    public void animateCounts() {

    }

    public interface RiverListFragmentListener {

        public void on(Uri uri);
    }

}

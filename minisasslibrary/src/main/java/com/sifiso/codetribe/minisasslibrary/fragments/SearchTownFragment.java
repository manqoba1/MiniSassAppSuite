package com.sifiso.codetribe.minisasslibrary.fragments;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.sifiso.codetribe.minisasslibrary.R;
import com.sifiso.codetribe.minisasslibrary.adapters.SearchTownAdapter;
import com.sifiso.codetribe.minisasslibrary.dto.TownDTO;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.ResponseDTO;

import java.util.ArrayList;
import java.util.List;


public class SearchTownFragment extends Fragment implements PageFragment{
    ImageView SLT_hero;
    EditText SLT_editSearch;
    ListView STF_list;
    SearchTownAdapter adapter;
    SearchTownFragmentListener mListener;
    Context ctx;
    Activity activity;
    View v;
    List<TownDTO> townList;
    ResponseDTO response;

    public SearchTownFragment() {
        // Required empty public constructor
    }

    private void setFields() {
        SLT_editSearch = (EditText) v.findViewById(R.id.SLT_editSearch);
        SLT_hero = (ImageView) v.findViewById(R.id.SLT_hero);
        STF_list = (ListView) v.findViewById(R.id.STF_list);
        setList();
    }

    private void setList() {
        if(townList == null){
            townList = new ArrayList<>();
        }
        townList = response.getTownList();
        adapter = new SearchTownAdapter(ctx, townList, new SearchTownAdapter.SearchTownAdapterListener() {
            @Override
            public void onTownClicked(TownDTO town) {

            }
        });
        STF_list.setAdapter(adapter);
        STF_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TownDTO town = (TownDTO) parent.getItemAtPosition(position);
                mListener.onTownSelected(town);
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_search_town, container, false);
        ctx = getActivity().getApplicationContext();
        response = (ResponseDTO) getArguments().getSerializable("response");
        activity = getActivity();
        setFields();
        return v;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (SearchTownFragmentListener) activity;
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

    @Override
    public void animateCounts() {

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface SearchTownFragmentListener {
        // TODO: Update argument type and name
        public void onTownSelected(TownDTO town);
    }

}

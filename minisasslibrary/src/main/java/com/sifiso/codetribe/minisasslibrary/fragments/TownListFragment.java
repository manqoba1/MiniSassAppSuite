package com.sifiso.codetribe.minisasslibrary.fragments;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.sifiso.codetribe.minisasslibrary.R;
import com.sifiso.codetribe.minisasslibrary.adapters.TownAdapter;
import com.sifiso.codetribe.minisasslibrary.dto.RiverTownDTO;
import com.sifiso.codetribe.minisasslibrary.dto.TeamDTO;
import com.sifiso.codetribe.minisasslibrary.dto.TownDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * create an instance of this fragment.
 */
public class TownListFragment extends Fragment implements PageFragment {
    ListView FTL_townList;

    public TownListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    private void setField() {
        FTL_townList = (ListView) v.findViewById(R.id.FTL_townList);
        setList();
    }

    View v;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_town_list, container, false);
        ctx = getActivity().getApplicationContext();
        Bundle b = getArguments();
        if (b != null) {
            riverTownList = (List<RiverTownDTO>) b.getSerializable("riverTown");

            getActivity().setTitle(riverTownList.get(0).getRiver().getRiverName() + " Towns");
        }
        setField();
        return v;
    }

    private List<RiverTownDTO> riverTownList;
    private List<TownDTO> townList;
    private TownAdapter adapter;
    Context ctx;

    private void setList() {
        for (RiverTownDTO rt : riverTownList) {
            if (townList == null) {
                townList = new ArrayList<>();
            }
            townList.add(rt.getTown());
        }
        adapter = new TownAdapter(ctx, townList, new TownAdapter.TownAdapterListener() {
            @Override
            public void onTeamClicked(List<TeamDTO> teamList) {

            }

            @Override
            public void onMapClicked(TownDTO team) {

            }
        });

        FTL_townList.setAdapter(adapter);
    }

    @Override
    public void animateCounts() {

    }
}

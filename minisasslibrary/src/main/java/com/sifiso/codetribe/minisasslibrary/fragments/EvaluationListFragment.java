package com.sifiso.codetribe.minisasslibrary.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.sifiso.codetribe.minisasslibrary.R;
import com.sifiso.codetribe.minisasslibrary.adapters.EvaluationAdapter;
import com.sifiso.codetribe.minisasslibrary.dto.EvaluationDTO;
import com.sifiso.codetribe.minisasslibrary.dto.EvaluationSiteDTO;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.ResponseDTO;
import com.sifiso.codetribe.minisasslibrary.util.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EvaluationListFragment.EvaluationListFragmentListener} interface
 * to handle interaction events.
 * Use the {@link EvaluationListFragment#} factory method to
 * create an instance of this fragment.
 */
public class EvaluationListFragment extends Fragment implements PageFragment {

    private EvaluationListFragmentListener mListener;
    private LinearLayout FEL_search;
    private ListView FEL_list;
    private ImageView SLT_imgSearch2, SLT_hero;
    private EditText SLT_editSearch;
    private TextView FEL_add;

    public EvaluationListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.evaluation_dialog_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void build() {
        FEL_list = (ListView) v.findViewById(R.id.FEL_list);
        FEL_search = (LinearLayout) v.findViewById(R.id.FEL_search);
        SLT_editSearch = (EditText) v.findViewById(R.id.SLT_editSearch);
        SLT_editSearch.setVisibility(View.GONE);
        SLT_hero = (ImageView) v.findViewById(R.id.SLT_hero);
        SLT_imgSearch2 = (ImageView) v.findViewById(R.id.SLT_imgSearch2);
        SLT_imgSearch2.setVisibility(View.GONE);
        SLT_hero.setImageDrawable(Util.getRandomHeroImage(ctx));
        setList();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // getActivity().setTheme(R.style.EvalListTheme);
    }

    View v;
    EvaluationAdapter adapter;

    private Context ctx;
    ResponseDTO response;
    private List<EvaluationDTO> evaluationList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_evaluation_list, container, false);
        Bundle b = getArguments();
        //setHasOptionsMenu(true);
        //  getActivity().setTheme(R.style.EvalListTheme);
        ctx = getActivity().getApplicationContext();
        if (b != null) {
            evaluationSiteList = (List<EvaluationSiteDTO>) b.getSerializable("evaluationSite");
            response = (ResponseDTO) b.getSerializable("response");
            for (EvaluationSiteDTO evalSite : evaluationSiteList) {

                for (EvaluationDTO eval : evalSite.getEvaluationList()) {
                    if (evaluationList == null) {
                        evaluationList = new ArrayList<>();
                    }
                    evaluationList.add(eval);
                }
            }
            getActivity().setTitle(evaluationSiteList.get(0).getRiverName() + " Evaluation");

        }
        build();
        return v;
    }

    public String getRiverName() {
        return evaluationSiteList.get(0).getRiverName();
    }

    public void setEvaluation(List<EvaluationDTO> evaluationList) {
        this.evaluationList = evaluationList;
        if (!evaluationList.isEmpty()) {
            setList();
        }

    }


    private void setList() {
        if (evaluationList == null) evaluationList = new ArrayList<>();
        LayoutInflater inf = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inf.inflate(R.layout.hero_layout, null);
        // FEL_list.addHeaderView(v);
        // FEL_list.addFooterView(v);
        adapter = new EvaluationAdapter(ctx, evaluationList, new EvaluationAdapter.EvaluationAdapterListener() {
            @Override
            public void onMapSiteRequest(List<EvaluationSiteDTO> siteList) {

            }

            @Override
            public void onEvaluationRequest(List<EvaluationSiteDTO> siteList) {

            }
        });
        FEL_list.setAdapter(adapter);
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (EvaluationListFragmentListener) activity;
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
    public interface EvaluationListFragmentListener {
        // TODO: Update argument type and name
        public void onCreateEvaluation(ResponseDTO response);

    }

    private List<EvaluationSiteDTO> evaluationSiteList;
}

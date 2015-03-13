package com.sifiso.codetribe.minisasslibrary.dialogs;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.sifiso.codetribe.minisasslibrary.R;
import com.sifiso.codetribe.minisasslibrary.adapters.InsectSelectionAdapter;
import com.sifiso.codetribe.minisasslibrary.dto.InsectImageDTO;
import com.sifiso.codetribe.minisasslibrary.util.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by CodeTribe1 on 2015-02-28.
 */
public class InsectSelectionDialog extends DialogFragment {
    static final String LOG = InsectSelectionDialog.class.getSimpleName();
    View view;
    Context ctx;
    double total = 0.0;
    private InsectSelectionAdapter adapter;
    private InsectSelectionDialogListener listener;
    private RecyclerView SD_list;
    private CheckBox SD_checkboxAll;
    private Button SD_done;
    private TextView textView;
    private List<InsectImageDTO> mSites;
    private List<InsectImageDTO> listCal;

    public InsectSelectionDialog() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.w(LOG, "###### onCreateView");

        view = inflater.inflate(R.layout.selection_dialog, container, false);

        ctx = getActivity();

        setFields();
        setList();
        return view;
    }

    private void setList() {

        adapter = new InsectSelectionAdapter(ctx, mSites, R.layout.insect_select_item, new InsectSelectionAdapter.InsectPopupAdapterListener() {
            @Override
            public void onInsectSelected(InsectImageDTO insect, int index) {

                collectCheckedInsects(insect);

            }
        });
        SD_list.setAdapter(adapter);
    }

    private void collectCheckedInsects(InsectImageDTO mDtos) {
        if (listCal == null) {
            listCal = new ArrayList<>(mSites.size());
        }


        if (mDtos.selected == true) {
            //if (listCal.contains(mDtos))
            listCal.add(mDtos);
            total = total + mDtos.getSensitivityScore();

        } else {
            mDtos.selected = true;
            listCal.remove(mDtos);
            total = total - mDtos.getSensitivityScore();
            mDtos.selected = false;
        }
        Log.e(LOG, listCal.size() + "");
        listener.onSelectDone(listCal);
    }

    private void setFields() {
        SD_list = (RecyclerView) view.findViewById(R.id.SD_list);
        SD_list.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

        SD_list.setItemAnimator(new DefaultItemAnimator());
        SD_list.addItemDecoration(new DividerItemDecoration(ctx, RecyclerView.VERTICAL));
        textView = (TextView) view.findViewById(R.id.textView);
        SD_done = (Button) view.findViewById(R.id.SD_done);

        SD_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                listener.onSelectDone(listCal);

                dismiss();
            }
        });
    }

   /* @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (InsectSelectionDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " - Host activity" + activity.getLocalClassName() + " must implement GPSScanFragmentListener");
        }
    }*/

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }


    public void setmSites(List<InsectImageDTO> mSites) {
        this.mSites = mSites;
    }

    public InsectSelectionDialogListener getListener() {
        return listener;
    }

    public void setListener(InsectSelectionDialogListener listener) {
        this.listener = listener;
    }

    public interface InsectSelectionDialogListener {
        public void onSelectDone(List<InsectImageDTO> insectImages);
    }
}

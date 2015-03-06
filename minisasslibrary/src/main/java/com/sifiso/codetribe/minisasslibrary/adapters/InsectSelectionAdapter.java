package com.sifiso.codetribe.minisasslibrary.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.sifiso.codetribe.minisasslibrary.R;
import com.sifiso.codetribe.minisasslibrary.dto.InsectImageDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by CodeTribe1 on 2015-02-27.
 */
public class InsectSelectionAdapter extends RecyclerView.Adapter<InsectSelectionAdapter.Holder> {

    private Context mContext;
    private List<InsectImageDTO> mList;
    private ArrayList<InsectImageDTO> selectedInsects;
    private int rowLayout;
    private InsectPopupAdapterListener listener;

    public InsectSelectionAdapter(Context mContext, List<InsectImageDTO> mList, int rowLayout, InsectPopupAdapterListener listener) {
        this.mContext = mContext;
        this.mList = mList;
        this.rowLayout = rowLayout;
        this.listener = listener;
    }

    public void selectInsect(InsectImageDTO insect, int index) {
        if (selectedInsects == null) {
            selectedInsects = new ArrayList<InsectImageDTO>();
        }

        selectedInsects.add(index, insect);
    }

    public void removeSelected(InsectImageDTO insect, int index) {
        if (selectedInsects == null) {
            selectedInsects = new ArrayList<InsectImageDTO>();
        }
        selectedInsects.remove(index);
    }

    public ArrayList<InsectImageDTO> getSelectedInsects() {

        return selectedInsects;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.insect_select_item, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(Holder h, final int position) {
        final InsectImageDTO insect = mList.get(position);

        int rID = mContext.getResources().getIdentifier(insect.getUri(), "drawable", mContext.getPackageName());

        h.INSC_image.setImageResource(rID);
        h.INSC_box.setText(insect.getGroupName());
        h.INSC_box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    insect.setSelected(isChecked);
                    listener.onInsectSelected(insect, position);
                } else {
                    insect.setSelected(isChecked);
                    listener.onInsectSelected(insect, position);
                }
            }
        });

        if (insect.isSelected()) {
            h.INSC_box.setChecked(true);
        } else {
            h.INSC_box.setChecked(false);
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    public interface InsectPopupAdapterListener {
        public void onInsectSelected(InsectImageDTO insect, int index);

    }

    public class Holder extends RecyclerView.ViewHolder {
        protected ImageView INSC_image;
        protected CheckBox INSC_box;

        public Holder(View itemView) {
            super(itemView);
            INSC_box = (CheckBox) itemView.findViewById(R.id.INSC_box);
            INSC_image = (ImageView) itemView.findViewById(R.id.INSC_image);
        }
    }
}

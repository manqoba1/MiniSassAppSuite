package com.sifiso.codetribe.minisasslibrary.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.sifiso.codetribe.minisasslibrary.R;
import com.sifiso.codetribe.minisasslibrary.dto.EvaluationDTO;
import com.sifiso.codetribe.minisasslibrary.dto.EvaluationSiteDTO;
import com.sifiso.codetribe.minisasslibrary.util.Util;

import java.util.Date;
import java.util.List;

/**
 * Created by CodeTribe1 on 2015-03-09.
 */
public class EvaluationAdapter extends BaseAdapter {
    Context mCtx;
    List<EvaluationDTO> mList;
    EvaluationAdapterListener mListener;

    public EvaluationAdapter(Context mCtx, List<EvaluationDTO> mList, EvaluationAdapterListener mListener) {
        this.mCtx = mCtx;
        this.mList = mList;
        this.mListener = mListener;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mList.get(position).getEvaluationSiteID();
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        Holder h;
        if (v == null) {
            h = new Holder();
            LayoutInflater inflater = (LayoutInflater) mCtx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.evaluation_list_item, parent, false);
            h.ELI_condition = (TextView) v.findViewById(R.id.ELI_condition);
            h.ELI_date = (TextView) v.findViewById(R.id.ELI_date);
            h.ELI_oxygen = (TextView) v.findViewById(R.id.ELI_oxygen);
            h.ELI_pH = (TextView) v.findViewById(R.id.ELI_pH);
            h.ELI_score = (TextView) v.findViewById(R.id.ELI_score);
            h.ELI_team = (TextView) v.findViewById(R.id.ELI_team);
            h.ELI_wc = (TextView) v.findViewById(R.id.ELI_wc);
            h.ELI_wt = (TextView) v.findViewById(R.id.ELI_wt);
            h.ELI_remarks = (TextView) v.findViewById(R.id.ELI_remarks);

            v.setTag(h);
        } else {
            h = (Holder) v.getTag();
        }
        EvaluationDTO site = mList.get(position);

        h.ELI_team.setText(site.getTeamName());

        h.ELI_date.setText(Util.getLongDate(new Date(site.getEvaluationDate())));
        h.ELI_score.setText(Math.round(site.getScore()) + "");
        h.ELI_oxygen.setText(site.getOxygen() + "");
        h.ELI_wc.setText(site.getWaterClarity() + "");
        h.ELI_wt.setText(site.getWaterTemperature() + "");
        h.ELI_pH.setText(site.getpH() + "");
        if (site.getRemarks() == null) {
            h.ELI_remarks.setVisibility(View.GONE);
        } else {
            h.ELI_remarks.setVisibility(View.VISIBLE);
        }
        h.ELI_remarks.setText(site.getRemarks());
        switch (site.getConditionsID()) {
            case 1:
                h.ELI_condition.setTextColor(mCtx.getResources().getColor(R.color.purple));
                h.ELI_score.setTextColor(mCtx.getResources().getColor(R.color.purple));
                break;
            case 6:
                h.ELI_condition.setTextColor(mCtx.getResources().getColor(R.color.green));
                h.ELI_score.setTextColor(mCtx.getResources().getColor(R.color.green));
                break;
            case 7:
                h.ELI_condition.setTextColor(mCtx.getResources().getColor(R.color.yellow));
                h.ELI_score.setTextColor(mCtx.getResources().getColor(R.color.yellow));
                break;
            case 8:
                h.ELI_condition.setTextColor(mCtx.getResources().getColor(R.color.orange));
                h.ELI_score.setTextColor(mCtx.getResources().getColor(R.color.orange));
                break;
            case 9:
                h.ELI_condition.setTextColor(mCtx.getResources().getColor(R.color.red));
                h.ELI_score.setTextColor(mCtx.getResources().getColor(R.color.red));
                break;

        }

        h.ELI_condition.setText(site.getConditionName());
        animateView(v);
        return v;
    }

    public void animateView(final View view) {
        Animation a = AnimationUtils.loadAnimation(mCtx, R.anim.grow_fade_in_center);
        a.setDuration(500);
        if (view == null)
            return;
        view.startAnimation(a);
    }

    class Holder {
        TextView ELI_team, ELI_date, ELI_wc, ELI_pH, ELI_wt, ELI_oxygen, ELI_score, ELI_condition, ELI_remarks;
    }

    public interface EvaluationAdapterListener {
        public void onMapSiteRequest(List<EvaluationSiteDTO> siteList);

        public void onEvaluationRequest(List<EvaluationSiteDTO> siteList);


    }
}

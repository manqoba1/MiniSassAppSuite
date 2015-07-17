package com.sifiso.codetribe.minisasslibrary.adapters;

import android.content.Context;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sifiso.codetribe.minisasslibrary.R;
import com.sifiso.codetribe.minisasslibrary.dto.EvaluationDTO;
import com.sifiso.codetribe.minisasslibrary.dto.EvaluationInsectDTO;
import com.sifiso.codetribe.minisasslibrary.dto.EvaluationSiteDTO;
import com.sifiso.codetribe.minisasslibrary.util.Constants;
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
        return mList.get(position).getEvaluationID();
    }

    class Holder {
        TextView ELI_txtCount, ELI_team, ELI_date, ELI_wc, ELI_pH, ELI_wt, ELI_oxygen, ELI_score, ELI_condition, ELI_remarks;
        ImageView ELI_condition_image, ELI_contribute, ELI_edit, ELI_directions;
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        final Holder h;
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
            h.ELI_condition_image = (ImageView) v.findViewById(R.id.ELI_condition_image);
            //h.ELI_contribute = (ImageView) v.findViewById(R.id.ELI_contribute);
            h.ELI_edit = (ImageView) v.findViewById(R.id.ELI_edit);
            h.ELI_directions = (ImageView) v.findViewById(R.id.ELI_directions);
            h.ELI_txtCount = (TextView) v.findViewById(R.id.ELI_txtCount);
            v.setTag(h);
        } else {
            h = (Holder) v.getTag();
        }
        final EvaluationDTO evaluation = mList.get(position);

        h.ELI_team.setText("Team "+evaluation.getTeamName());
        h.ELI_txtCount.setText((position+1)+"");
        h.ELI_date.setText(Util.getLongDate(new Date(evaluation.getEvaluationDate())));
        h.ELI_score.setText((Math.round(evaluation.getScore() * 100.00) / 100.00) + "");
        h.ELI_oxygen.setText(evaluation.getOxygen() + "");
        h.ELI_wc.setText(evaluation.getWaterClarity() + "");
        h.ELI_wt.setText(evaluation.getWaterTemperature() + "");
        h.ELI_pH.setText(evaluation.getpH() + "");
        if (evaluation.getRemarks() == null) {
            h.ELI_remarks.setVisibility(View.GONE);
        } else {
            h.ELI_remarks.setVisibility(View.VISIBLE);
        }
        h.ELI_score.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.flashOnce(h.ELI_score, 200, new Util.UtilAnimationListener() {
                    @Override
                    public void onAnimationEnded() {
                        mListener.onViewInsect(evaluation.getEvaluationinsectList());
                    }
                });

            }
        });
        h.ELI_condition_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.flashOnce(h.ELI_condition_image, 200, new Util.UtilAnimationListener() {
                    @Override
                    public void onAnimationEnded() {
                        mListener.onViewInsect(evaluation.getEvaluationinsectList());
                    }
                });

            }
        });

        h.ELI_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.flashOnce(h.ELI_edit, 200, new Util.UtilAnimationListener() {
                    @Override
                    public void onAnimationEnded() {
                        mListener.onEvaluationEdit(evaluation);
                    }
                });

            }
        });
        h.ELI_directions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.flashOnce(h.ELI_directions, 200, new Util.UtilAnimationListener() {
                    @Override
                    public void onAnimationEnded() {
                        mListener.onDirectionToSite(evaluation.getEvaluationSite());
                    }
                });

            }
        });
        h.ELI_remarks.setText(evaluation.getRemarks());
        switch (evaluation.getConditionsID()) {
            case Constants.UNMODIFIED_NATURAL_SAND:
                h.ELI_condition.setTextColor(mCtx.getResources().getColor(R.color.dark_blue));
                h.ELI_score.setTextColor(mCtx.getResources().getColor(R.color.dark_blue));
                h.ELI_condition_image.setImageDrawable(mCtx.getResources().getDrawable(R.drawable.blue_crap));
                h.ELI_condition_image.setColorFilter(mCtx.getResources().getColor(R.color.dark_blue), PorterDuff.Mode.MULTIPLY);
                break;
            case Constants.LARGELY_NATURAL_SAND:
                h.ELI_condition.setTextColor(mCtx.getResources().getColor(R.color.green));
                h.ELI_score.setTextColor(mCtx.getResources().getColor(R.color.green));
                h.ELI_condition_image.setImageDrawable(mCtx.getResources().getDrawable(R.drawable.green_crap));
                h.ELI_condition_image.setColorFilter(mCtx.getResources().getColor(R.color.green), PorterDuff.Mode.MULTIPLY);
                break;
            case Constants.MODERATELY_MODIFIED_SAND:
                h.ELI_condition.setTextColor(mCtx.getResources().getColor(R.color.orange));
                h.ELI_score.setTextColor(mCtx.getResources().getColor(R.color.orange));
                h.ELI_condition_image.setImageDrawable(mCtx.getResources().getDrawable(R.drawable.orange_crap));
                h.ELI_condition_image.setColorFilter(mCtx.getResources().getColor(R.color.orange), PorterDuff.Mode.MULTIPLY);
                break;
            case Constants.LARGELY_MODIFIED_SAND:
                h.ELI_condition.setTextColor(mCtx.getResources().getColor(R.color.red));
                h.ELI_score.setTextColor(mCtx.getResources().getColor(R.color.red));
                h.ELI_condition_image.setImageDrawable(mCtx.getResources().getDrawable(R.drawable.red_crap));
                h.ELI_condition_image.setColorFilter(mCtx.getResources().getColor(R.color.red), PorterDuff.Mode.MULTIPLY);

                break;
            case Constants.CRITICALLY_MODIFIED_SAND:
                h.ELI_condition.setTextColor(mCtx.getResources().getColor(R.color.purple));
                h.ELI_score.setTextColor(mCtx.getResources().getColor(R.color.purple));
                h.ELI_condition_image.setImageDrawable(mCtx.getResources().getDrawable(R.drawable.purple_crap));
                h.ELI_condition_image.setColorFilter(mCtx.getResources().getColor(R.color.purple), PorterDuff.Mode.MULTIPLY);
                break;
            case Constants.UNMODIFIED_NATURAL_ROCK:
                h.ELI_condition.setTextColor(mCtx.getResources().getColor(R.color.dark_blue));
                h.ELI_score.setTextColor(mCtx.getResources().getColor(R.color.dark_blue));
                h.ELI_condition_image.setImageDrawable(mCtx.getResources().getDrawable(R.drawable.blue_crap));
                h.ELI_condition_image.setColorFilter(mCtx.getResources().getColor(R.color.dark_blue), PorterDuff.Mode.MULTIPLY);
                break;
            case Constants.LARGELY_NATURAL_ROCK:
                h.ELI_condition.setTextColor(mCtx.getResources().getColor(R.color.green));
                h.ELI_score.setTextColor(mCtx.getResources().getColor(R.color.green));
                h.ELI_condition_image.setImageDrawable(mCtx.getResources().getDrawable(R.drawable.green_crap));
                h.ELI_condition_image.setColorFilter(mCtx.getResources().getColor(R.color.green), PorterDuff.Mode.MULTIPLY);
                break;
            case Constants.MODERATELY_MODIFIED_ROCK:
                h.ELI_condition.setTextColor(mCtx.getResources().getColor(R.color.orange));
                h.ELI_score.setTextColor(mCtx.getResources().getColor(R.color.orange));
                h.ELI_condition_image.setImageDrawable(mCtx.getResources().getDrawable(R.drawable.orange_crap));
                h.ELI_condition_image.setColorFilter(mCtx.getResources().getColor(R.color.orange), PorterDuff.Mode.MULTIPLY);
                break;
            case Constants.LARGELY_MODIFIED_ROCK:
                h.ELI_condition.setTextColor(mCtx.getResources().getColor(R.color.red));
                h.ELI_score.setTextColor(mCtx.getResources().getColor(R.color.red));
                h.ELI_condition_image.setImageDrawable(mCtx.getResources().getDrawable(R.drawable.red_crap));
                h.ELI_condition_image.setColorFilter(mCtx.getResources().getColor(R.color.red), PorterDuff.Mode.MULTIPLY);
                break;
            case Constants.CRITICALLY_MODIFIED_ROCK:
                h.ELI_condition.setTextColor(mCtx.getResources().getColor(R.color.purple));
                h.ELI_score.setTextColor(mCtx.getResources().getColor(R.color.purple));
                h.ELI_condition_image.setImageDrawable(mCtx.getResources().getDrawable(R.drawable.purple_crap));
                h.ELI_condition_image.setColorFilter(mCtx.getResources().getColor(R.color.purple), PorterDuff.Mode.MULTIPLY);
                break;
            case Constants.NOT_SPECIFIED:
                h.ELI_condition.setTextColor(mCtx.getResources().getColor(R.color.gray));
                h.ELI_score.setTextColor(mCtx.getResources().getColor(R.color.gray));
                h.ELI_condition_image.setImageDrawable(mCtx.getResources().getDrawable(R.drawable.gray_crap));
                h.ELI_condition_image.setColorFilter(mCtx.getResources().getColor(R.color.gray), PorterDuff.Mode.MULTIPLY);
                break;
        }

        h.ELI_condition.setText(evaluation.getConditionName());
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


    public interface EvaluationAdapterListener {
        public void onEvaluationContribute(EvaluationDTO evaluation);

        public void onDirectionToSite(EvaluationSiteDTO evaluationSite);

        public void onEvaluationEdit(EvaluationDTO evaluation);

        public void onViewInsect(List<EvaluationInsectDTO> insectImage);
    }
}

package com.sifiso.codetribe.minisasslibrary.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sifiso.codetribe.minisasslibrary.R;
import com.sifiso.codetribe.minisasslibrary.dto.EvaluationDTO;
import com.sifiso.codetribe.minisasslibrary.dto.EvaluationSiteDTO;
import com.sifiso.codetribe.minisasslibrary.dto.RiverDTO;
import com.sifiso.codetribe.minisasslibrary.dto.RiverTownDTO;
import com.sifiso.codetribe.minisasslibrary.util.ToastUtil;
import com.sifiso.codetribe.minisasslibrary.util.Util;
import com.sifiso.codetribe.minisasslibrary.viewsUtil.CircleTransform;

import java.util.Date;
import java.util.List;

/**
 * Created by CodeTribe1 on 2015-03-05.
 */
public class RiverAdapter extends BaseAdapter {
    Context mCtx;
    List<RiverDTO> mList;
    RiverAdapterListener mListener;

    public RiverAdapter(List<RiverDTO> mList, Context mCtx, RiverAdapterListener listener) {
        this.mList = mList;
        this.mCtx = mCtx;
        mListener = listener;
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
        return 0;
    }

    class Holder {
        ImageView AR_image_folder, AR_imgMap, AR_refresh, AR_imgDirections, AR_imgPicker;
        TextView AR_totalEvaluation, AR_txtRiverName, AR_totalStreams, AR_percOverallEva, AR_totalTrLabel,
                AR_eva_date, AR_Eva_score;

    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        final Holder h;
        if (v == null) {
            h = new Holder();
            LayoutInflater inflater = (LayoutInflater) mCtx
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.river_list_items, parent,
                    false);
            h.AR_image_folder = (ImageView) v.findViewById(R.id.AR_image_folder);
            h.AR_imgMap = (ImageView) v.findViewById(R.id.AR_imgMap);
            // h.AR_refresh = (ImageView) v.findViewById(R.id.AR_refresh);
            h.AR_imgDirections = (ImageView) v.findViewById(R.id.AR_imgDirections);
            h.AR_imgPicker = (ImageView) v.findViewById(R.id.AR_imgPicker);
            h.AR_totalEvaluation = (TextView) v.findViewById(R.id.AR_totalEvaluation);
            h.AR_txtRiverName = (TextView) v.findViewById(R.id.AR_txtRiverName);
            h.AR_totalStreams = (TextView) v.findViewById(R.id.AR_totalStreams);
            h.AR_percOverallEva = (TextView) v.findViewById(R.id.AR_percOverallEva);
            h.AR_totalTrLabel = (TextView) v.findViewById(R.id.AR_totalTrLabel);
            h.AR_eva_date = (TextView) v.findViewById(R.id.AR_eva_date);
            h.AR_Eva_score = (TextView) v.findViewById(R.id.AR_Eva_score);

            v.setTag(h);
        } else {
            h = (Holder) v.getTag();
        }
        final RiverDTO dto = mList.get(position);


        Log.d("RiverAdapter", "Mentor 2" + new Gson().toJson(dto.getEvaluationsiteList()));
        double perc = 0.0;
        double total = 0.0;
        for (EvaluationSiteDTO es : dto.getEvaluationsiteList()) {
            for (EvaluationDTO e : es.getEvaluationList()) {
                if (e.getScore() != 0) {
                    total = total + e.getScore();
                }
            }
        }
        h.AR_totalStreams.setText("" + dto.getStreamList().size());
        double percentage = total;//   Math.round((perc / dto.getEvaluationsiteList().size()) * 100.00) / 100.00; ;
        if (percentage > 0) {
            h.AR_percOverallEva.setText(percentage + "");
        } else {
            h.AR_percOverallEva.setText("0");
        }
        h.AR_imgDirections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Util.flashOnce(h.AR_imgDirections, 200, new Util.UtilAnimationListener() {
                    @Override
                    public void onAnimationEnded() {
                        mListener.onDirection(dto.getNearestLatitude(), dto.getNearestLongitude());
                    }
                });
            }
        });
        h.AR_txtRiverName.setText(dto.getRiverName().trim() + " River");
        h.AR_txtRiverName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.flashOnce(h.AR_txtRiverName, 200, new Util.UtilAnimationListener() {
                    @Override
                    public void onAnimationEnded() {
                        if (dto.getEvaluationsiteList() != null) {
                            mListener.onCreateEvaluation(dto);
                        } else {
                            ToastUtil.toast(mCtx, "No evaluations yet");
                        }
                    }
                });
            }
        });
        h.AR_totalEvaluation.setText("" + dto.getEvaluationsiteList().size());
        h.AR_totalEvaluation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ToastUtil.toast(mCtx,dto.getRiverName());
                Util.flashOnce(h.AR_totalEvaluation, 200, new Util.UtilAnimationListener() {
                    @Override
                    public void onAnimationEnded() {
                        if (dto.getEvaluationsiteList() != null) {
                            mListener.onEvaluationRequest(dto.getEvaluationsiteList());
                        } else {
                            ToastUtil.toast(mCtx, "No evaluations yet");
                        }
                    }
                });
            }
        });
        h.AR_totalTrLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ToastUtil.toast(mCtx,dto.getRiverName());
                Util.flashOnce(h.AR_totalTrLabel, 200, new Util.UtilAnimationListener() {
                    @Override
                    public void onAnimationEnded() {
                        if (dto.getEvaluationsiteList() != null) {
                            mListener.onEvaluationRequest(dto.getEvaluationsiteList());
                        } else {
                            ToastUtil.toast(mCtx, "No evaluations yet");
                        }
                    }
                });
            }

        });
        h.AR_imgMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.flashOnce(h.AR_imgMap, 200, new Util.UtilAnimationListener() {
                    @Override
                    public void onAnimationEnded() {
                        mListener.onMapRequest(dto, RIVER_VIEW);
                    }
                });

            }
        });

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

    public interface RiverAdapterListener {
        public void onMapSiteRequest(List<EvaluationSiteDTO> siteList);

        public void onEvaluationRequest(List<EvaluationSiteDTO> siteList);

        public void onCreateEvaluation(RiverDTO river);

        public void onMapRequest(RiverDTO river, int result);

        public void onDirection(Double latitude, Double longitude);
    }

    static final int EVALUATION_VIEW = 12;
    static final int RIVER_VIEW = 13;


}

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
import com.sifiso.codetribe.minisasslibrary.dto.EvaluationSiteDTO;
import com.sifiso.codetribe.minisasslibrary.dto.RiverDTO;
import com.sifiso.codetribe.minisasslibrary.dto.RiverTownDTO;
import com.sifiso.codetribe.minisasslibrary.util.ToastUtil;
import com.sifiso.codetribe.minisasslibrary.util.Util;
import com.sifiso.codetribe.minisasslibrary.viewsUtil.CircleTransform;

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

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        final Holder h;
        if (v == null) {
            h = new Holder();
            LayoutInflater inflater = (LayoutInflater) mCtx
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.river_list_items, parent,
                    false);
            h.RLI_evaluation = (TextView) v.findViewById(R.id.RLI_evaluation);
            h.RLI_image = (ImageView) v.findViewById(R.id.RLI_image);
            h.RLI_town = (TextView) v.findViewById(R.id.RLI_town);
            h.RLI_river_name = (TextView) v.findViewById(R.id.RLI_river_name);
            h.RLI_map_eval = (ImageView) v.findViewById(R.id.RLI_map_eval);
            h.RLI_end = (TextView) v.findViewById(R.id.RLI_end);
            h.RLI_start = (TextView) v.findViewById(R.id.RLI_start);
            v.setTag(h);
        } else {
            h = (Holder) v.getTag();
        }
        final RiverDTO dto = mList.get(position);
        h.RLI_river_name.setText(dto.getRiverName().trim()+ " River");
        // h.RLI_end.setText(dto.getEndCountryName());
        // h.RLI_start.setText(dto.getOriginCountryName());
        //h.RLI_town.setText("" + dto.getRiverTownList().size());
       /* h.RLI_town.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dto.getRiverTownList() != null) {
                    mListener.onTownshipRequest(dto.getRiverTownList());
                } else {
                    ToastUtil.toast(mCtx, "No town yet");
                }
            }
        });*/
        Log.d("RiverAdapter", "Mentor 2" + new Gson().toJson(dto.getEvaluationsiteList()));
        h.RLI_evaluation.setText("" + dto.getEvaluationsiteList().size());
        h.RLI_evaluation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ToastUtil.toast(mCtx,dto.getRiverName());
                if (dto.getEvaluationsiteList() != null) {
                    mListener.onEvaluationRequest(dto.getEvaluationsiteList());
                } else {
                    ToastUtil.toast(mCtx, "No evaluations yet");
                }
            }
        });
       /* if(dto.getImageUri() ==null){
            h.RLI_image.setImageDrawable(mCtx.getResources().getDrawable(R.drawable.greatfishriver));
        }else{
            int rID = mCtx.getResources().getIdentifier(dto.getImageUri(), "drawable", mCtx.getPackageName());
            h.RLI_image.setImageResource(rID);
        }*/

        // h.RLI_image.setImageDrawable(mCtx.getResources().getDrawable(R.drawable.greatfishriver));
        h.RLI_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.flashOnce(h.RLI_image, 200, new Util.UtilAnimationListener() {
                    @Override
                    public void onAnimationEnded() {
                        mListener.onMapRequest(dto, RIVER_VIEW);
                    }
                });

            }
        });
        CircleTransform transform = new CircleTransform();
        //Picasso.with(context).load(movie.getCast().get(position).getPhoto_url()).transform(transform).into(img);
        h.RLI_map_eval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.flashOnce(h.RLI_map_eval, 200, new Util.UtilAnimationListener() {
                    @Override
                    public void onAnimationEnded() {
                        mListener.onMapRequest(dto, EVALUATION_VIEW);
                    }
                });

            }
        });
        //animateView(v);
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

        public void onTownshipRequest(List<RiverTownDTO> riverTownList);

        public void onMapRequest(RiverDTO river, int result);
    }

    static final int EVALUATION_VIEW = 12;
    static final int RIVER_VIEW = 13;

    class Holder {
        ImageView RLI_image;
        TextView RLI_evaluation, RLI_town, RLI_river_name, RLI_end, RLI_start;
        ImageView RLI_map_eval;
    }
}

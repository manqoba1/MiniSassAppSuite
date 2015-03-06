package com.sifiso.codetribe.minisasslibrary.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.sifiso.codetribe.minisasslibrary.R;
import com.sifiso.codetribe.minisasslibrary.dto.EvaluationSiteDTO;
import com.sifiso.codetribe.minisasslibrary.dto.RiverDTO;
import com.sifiso.codetribe.minisasslibrary.dto.RiverTownDTO;

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
        Holder h;
        if (v == null) {
            h = new Holder();
            LayoutInflater inflater = (LayoutInflater) mCtx
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.river_list_items, parent,
                    false);
            h.RLI_evaluation = (TextView) v.findViewById(R.id.RLI_evaluation);
            h.RLI_river_map = (TextView) v.findViewById(R.id.RLI_river_map);
            h.RLI_image = (ImageView) v.findViewById(R.id.RLI_image);
            h.RLI_town = (TextView) v.findViewById(R.id.RLI_town);
            h.RLI_river_name = (TextView) v.findViewById(R.id.RLI_river_name);
            h.RLI_map_eval = (ImageButton) v.findViewById(R.id.RLI_map_eval);
            v.setTag(h);
        } else {
            h = (Holder) v.getTag();
        }
        final RiverDTO dto = mList.get(position);
        h.RLI_river_name.setText(dto.getRiverName());
        h.RLI_river_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onMapRequest(dto);
            }
        });
        h.RLI_town.setText("" + dto.getRiverTownList().size());
        h.RLI_town.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onTownshipRequest(dto.getRiverTownList());
            }
        });
        h.RLI_evaluation.setText("" + dto.getEvaluationSiteList().size());
        h.RLI_evaluation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onEvaluationRequest(dto.getEvaluationSiteList());
            }
        });
        h.RLI_image.setImageDrawable(mCtx.getResources().getDrawable(R.drawable.greatfishriver));
        h.RLI_map_eval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onMapRequest(dto);
            }
        });
        return v;
    }

    public interface RiverAdapterListener {
        public void onMapSiteRequest(List<EvaluationSiteDTO> siteList);

        public void onEvaluationRequest(List<EvaluationSiteDTO> siteList);

        public void onTownshipRequest(List<RiverTownDTO> riverTownList);

        public void onMapRequest(RiverDTO river);
    }

    class Holder {
        ImageView RLI_image;
        TextView RLI_evaluation, RLI_town, RLI_river_map, RLI_river_name;
        ImageButton RLI_map_eval;
    }
}

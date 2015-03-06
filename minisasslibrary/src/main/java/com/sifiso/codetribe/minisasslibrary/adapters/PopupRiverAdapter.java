package com.sifiso.codetribe.minisasslibrary.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sifiso.codetribe.minisasslibrary.R;
import com.sifiso.codetribe.minisasslibrary.dto.RiverDTO;
import com.sifiso.codetribe.minisasslibrary.util.Statics;

import java.util.List;

/**
 * Created by CodeTribe1 on 2015-02-27.
 */
public class PopupRiverAdapter extends ArrayAdapter<RiverDTO> {


    static final String LOG = PopupListAdapter.class.getSimpleName();
    private final LayoutInflater mInflater;
    private final int mLayoutRes;
    View view;
    private List<RiverDTO> mList;
    private String[] mStrings;
    private Context ctx;
    private String title;
    private boolean showAlternateIcon;


    public PopupRiverAdapter(Context context, int textViewResourceId,
                             List<RiverDTO> list, boolean showAlternateIcon) {

        super(context, textViewResourceId, list);
        this.mLayoutRes = textViewResourceId;
        mList = list;
        this.showAlternateIcon = showAlternateIcon;
        ctx = context;
        this.mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {
        ViewHolderItem item;
        if (convertView == null) {
            convertView = mInflater.inflate(mLayoutRes, null);
            item = new ViewHolderItem();
            item.txtString = (TextView) convertView
                    .findViewById(R.id.text1);

            item.image = (ImageView) convertView
                    .findViewById(R.id.image1);

            convertView.setTag(item);
        } else {
            item = (ViewHolderItem) convertView.getTag();
        }
        if (showAlternateIcon) {
            item.image.setImageDrawable(ctx.getResources().getDrawable(android.R.drawable.ic_dialog_map));

        } else {
            item.image.setImageDrawable(ctx.getResources().getDrawable(android.R.drawable.ic_dialog_email));

        }
        //final TownDTO p = mList.get(position);
        //String string = mStrings[position];
        final RiverDTO river = mList.get(position);
        item.txtString.setText(river.getRiverName());
        Statics.setRobotoFontLight(ctx, item.txtString);
        return (convertView);
    }

    static class ViewHolderItem {
        TextView txtString;
        ImageView image;
    }
}

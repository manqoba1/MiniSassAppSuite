package com.sifiso.codetribe.minisasslibrary.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListPopupWindow;
import android.widget.TextView;

import com.sifiso.codetribe.minisasslibrary.dto.EvaluationDTO;
import com.sifiso.codetribe.minisasslibrary.dto.EvaluationImageDTO;

import java.util.List;

public class EvaluationImageListFragment extends Fragment implements PageFragment{

    int index, newEvaluationComplete;
    List<String> list, currentSessionPhotos;
    ListPopupWindow actionsWindow;
    EvaluationImageDTO evaluationImage;
    EvaluationDTO evaluation;
   // EvaluationImageAdapter evaluationImageAdapter;
    Context ctx;
    TextView txtCount, txtName;
    Integer lastIndex;
    View view, topView, handle, searchLayout;
    ImageView imgSearch1, imgSearch2, heroImage;
    EditText editSearch;
    static final String LOG = EvaluationImageListFragment.class.getSimpleName();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }



    @Override
    public void animateCounts() {

    }

}

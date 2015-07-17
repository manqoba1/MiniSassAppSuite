package com.sifiso.codetribe.minisasslibrary.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.sifiso.codetribe.minisasslibrary.R;
import com.sifiso.codetribe.minisasslibrary.dto.EvaluationDTO;

/**
 * Created by sifiso on 2015-07-07.
 */
public class EditEvaluationDialog extends DialogFragment {
    static final String LOG = EditEvaluationDialog.class.getSimpleName();
    private EditText WC_score, WP_score, WT_score, WO_score, WE_score;
    private EditEvaluationDialogListener listener;
    private Button btnSave;
    private EvaluationDTO evaluation;
    View view;
    Context ctx;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.w(LOG, "###### onCreateView");

        view = inflater.inflate(R.layout.edit_evaluation_dialog, container, false);

        ctx = getActivity();

        setFields();
        return view;
    }

    private void setFields() {
        WC_score = (EditText) view.findViewById(R.id.WC_score);
        WT_score = (EditText) view.findViewById(R.id.WT_score);
        WP_score = (EditText) view.findViewById(R.id.WP_score);
        WO_score = (EditText) view.findViewById(R.id.WO_score);
        WE_score = (EditText) view.findViewById(R.id.WE_score);
        btnSave = (Button) view.findViewById(R.id.btn_save);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                evaluation.setOxygen(Double.parseDouble(WO_score.getText().toString()));
                evaluation.setWaterClarity(Double.parseDouble(WC_score.getText().toString()));
                evaluation.setpH(Double.parseDouble(WP_score.getText().toString()));
                evaluation.setWaterTemperature(Double.parseDouble(WT_score.getText().toString()));
                listener.onSaveUpdate(evaluation);
                dismiss();
            }
        });
        WC_score.setText(evaluation.getWaterClarity() + "");
        WT_score.setText(evaluation.getWaterTemperature() + "");
        WP_score.setText(evaluation.getpH() + "");
        WO_score.setText(evaluation.getOxygen() + "");


    }


    public interface EditEvaluationDialogListener {
        public void onSaveUpdate(EvaluationDTO evaluation);
    }

    public void setEvaluation(EvaluationDTO evaluation) {
        this.evaluation = evaluation;
    }

    public void setListener(EditEvaluationDialogListener listener) {
        this.listener = listener;
    }
}

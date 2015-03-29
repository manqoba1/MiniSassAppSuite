package com.sifiso.codetribe.minisasslibrary.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;

import com.sifiso.codetribe.minisasslibrary.R;
import com.sifiso.codetribe.minisasslibrary.dto.TeamMemberDTO;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.RequestDTO;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.ResponseDTO;
import com.sifiso.codetribe.minisasslibrary.util.ErrorUtil;
import com.sifiso.codetribe.minisasslibrary.util.SharedUtil;
import com.sifiso.codetribe.minisasslibrary.util.Statics;
import com.sifiso.codetribe.minisasslibrary.util.Util;
import com.sifiso.codetribe.minisasslibrary.util.WebSocketUtil;

public class TeamMemberFragment extends Fragment {

  private TeamFragmentListener listener;

    public TeamMemberFragment(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }
    View view;
    EditText editFirst, editLast, editCell;
    ImageView imgDelete;
    Button btnSave;
    TeamMemberDTO teamMember;
    Context ctx;
    ProgressBar progressBar;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.teammember_edit, container);
        ctx = getActivity();
        setFields();

        return view;
    }
    boolean isUpdate;
    private void sendData() {
        RequestDTO w = new RequestDTO();

        if (!isUpdate) {
            w.setRequestType(RequestDTO.REGISTER_TEAM);
            teamMember = new TeamMemberDTO();
            teamMember.setTeamID(SharedUtil.getImageLocation(ctx).getEvaluationImageID());
        } else {
            w.setRequestType(RequestDTO.UPDATE_TEAM_MEMBER);
        }
        if (editFirst.getText().toString().isEmpty()) {
            Util.showToast(ctx, ctx.getString(R.string.enter_firstname));
            return;
        }

        if (editLast.getText().toString().isEmpty()) {
            Util.showToast(ctx, ctx.getResources().getString(R.string.enter_lastname));
            return;
        }


        if (editCell.getText().toString().isEmpty()) {
            Util.showToast(ctx, ctx.getResources().getString(R.string.enter_cell));
            return;
        }


        teamMember.setFirstName(editFirst.getText().toString());
        teamMember.setLastName(editLast.getText().toString());
        teamMember.setCellphone(editCell.getText().toString());


        w.setTeamMember(teamMember);

        progressBar.setVisibility(View.VISIBLE);
        WebSocketUtil.sendRequest(ctx, Statics.MINI_SASS_ENDPOINT, w, new WebSocketUtil.WebSocketListener() {
            @Override
            public void onMessage(final ResponseDTO response) {
                if (!ErrorUtil.checkServerError(ctx, response)) {
                    return;
                }
                teamMember = response.getTeamMember();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        if (!ErrorUtil.checkServerError(ctx, response)) {
                            return;
                        }

                        if (isUpdate) {
                            listener.onTeamMemberUpdated(teamMember);
                        } else {
                            listener.onTeamMemberAdded(teamMember);
                        }
                    }
                });
            }

            @Override
            public void onClose() {

            }

            @Override
            public void onError(final String message) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        Util.showErrorToast(ctx, message);
                    }
                });

            }
        });

    }


    private void deleteStaff() {

    }

    public void setCompanyStaff(TeamMemberDTO teamMember) {
        this.teamMember = teamMember;
        if (teamMember == null) {
            isUpdate = false;
            return;
        } else {
            isUpdate = true;
        }
        editFirst.setText(teamMember.getFirstName());
        editLast.setText(teamMember.getLastName());
        editCell.setText(teamMember.getCellphone());

        imgDelete.setVisibility(View.VISIBLE);
    }

    private void setFields() {
        editFirst = (EditText) view.findViewById(R.id.ED_PSN_firstName);
        editLast = (EditText) view.findViewById(R.id.ED_PSN_lastName);
        editCell = (EditText) view.findViewById(R.id.ED_PSN_cellphone);
        btnSave = (Button) view.findViewById(R.id.ED_PSN_btnSave);


        imgDelete = (ImageView) view.findViewById(R.id.ED_PSN_imgDelete);
        imgDelete.setVisibility(View.GONE);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.flashOnce(btnSave, 100, new Util.UtilAnimationListener() {
                    @Override
                    public void onAnimationEnded() {
                        sendData();
                    }
                });
            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (TeamFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement teamFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public interface TeamFragmentListener {
        public void onTeamMemberAdded(TeamMemberDTO team);

        public void onTeamMemberUpdated(TeamMemberDTO TeamMember);
    }

}

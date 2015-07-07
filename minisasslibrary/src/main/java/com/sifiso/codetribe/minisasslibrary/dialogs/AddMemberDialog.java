package com.sifiso.codetribe.minisasslibrary.dialogs;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.sifiso.codetribe.minisasslibrary.R;
import com.sifiso.codetribe.minisasslibrary.dto.TeamMemberDTO;

/**
 * Created by CodeTribe1 on 2015-07-07.
 */
public class AddMemberDialog extends DialogFragment {
    static final String LOG = AddMemberDialog.class.getSimpleName();
    View v;
    Context ctx;
    Activity activity;
    Button rsRegister;
    EditText rsMemberName, rsMemberSurname;
    EditText rsCellphone, rsPin;
    CheckBox cbMoreMember;
    AutoCompleteTextView rsMemberEmail;

    private TeamMemberDTO teamMember;
    private AddMemberDialogListener listener;


    public interface AddMemberDialogListener {
        public void membersToBeRegistered(TeamMemberDTO teamMember);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.w(LOG, "###### onCreateView");

        v = inflater.inflate(R.layout.add_member_layout, container, false);
        ctx = getActivity().getApplicationContext();
        //activity = this;
        setFields();
        return v;
    }


    private void setFields() {
        rsMemberName = (EditText) v.findViewById(R.id.edtMemberName);
        rsMemberSurname = (EditText) v.findViewById(R.id.edtMemberLastNAme);
        rsPin = (EditText) v.findViewById(R.id.edtPassword);
        rsMemberEmail = (AutoCompleteTextView) v.findViewById(R.id.edtMemberEmail);
        rsCellphone = (EditText) v.findViewById(R.id.edtMemberPhone);

        cbMoreMember = (CheckBox) v.findViewById(R.id.cbMoreMember);
        cbMoreMember.setVisibility(View.GONE);
        rsRegister = (Button) v.findViewById(R.id.btnReg);
        rsRegister.setText("Join team " + teamMember.getTeam().getTeamName());
        rsRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (rsMemberName.getText().toString().isEmpty()) {
                    rsMemberName.setError("Enter first name");
                    // Toast.makeText(ctx, "Enter Last Name", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (rsMemberEmail.getText().toString().isEmpty()) {
                    rsMemberEmail.setError("Enter email address");
                    //Toast.makeText(ctx, "Enter Email Address", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (rsPin.getText().toString().isEmpty()) {
                    rsPin.setError("Enter pin");
                    //Toast.makeText(ctx, "Enter Email Address", Toast.LENGTH_SHORT).show();
                    return;
                }
                TeamMemberDTO member = new TeamMemberDTO();
                member.setActiveFlag(0);
                member.setCellphone(rsCellphone.getText().toString());
                member.setEmail(rsMemberEmail.getText().toString());
                member.setFirstName(rsMemberName.getText().toString());
                member.setLastName(rsMemberSurname.getText().toString());
                member.setPin(rsPin.getText().toString());
                member.setTeamID(teamMember.getTeam().getTeamID());
                member.setTeamMemberID(teamMember.getTeamMemberID());
                listener.membersToBeRegistered(member);
                dismiss();
            }
        });
    }

    public void setTeamMember(TeamMemberDTO teamMember) {
        this.teamMember = teamMember;
    }

    public void setListener(AddMemberDialogListener listener) {
        this.listener = listener;
    }
}

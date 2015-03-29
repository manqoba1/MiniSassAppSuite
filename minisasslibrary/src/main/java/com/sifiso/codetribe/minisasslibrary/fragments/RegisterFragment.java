package com.sifiso.codetribe.minisasslibrary.fragments;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sifiso.codetribe.minisasslibrary.R;
import com.sifiso.codetribe.minisasslibrary.dto.GcmDeviceDTO;
import com.sifiso.codetribe.minisasslibrary.dto.TeamDTO;
import com.sifiso.codetribe.minisasslibrary.dto.TeamMemberDTO;
import com.sifiso.codetribe.minisasslibrary.dto.TownDTO;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.RequestDTO;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.ResponseDTO;
import com.sifiso.codetribe.minisasslibrary.util.ErrorUtil;
import com.sifiso.codetribe.minisasslibrary.util.SharedUtil;
import com.sifiso.codetribe.minisasslibrary.util.Statics;
import com.sifiso.codetribe.minisasslibrary.util.Util;
import com.sifiso.codetribe.minisasslibrary.util.WebSocketUtil;

import java.util.ArrayList;
import java.util.List;

public class RegisterFragment extends Fragment implements PageFragment {
    public interface RegisterFragmentListener {
        // TODO: Update argument type and name
        public void onRegistered();

        public void onTownRequest();
    }

    Context ctx;
    Activity activity;
    Button rsRegister;
    EditText rsTeamName, rsMemberName, rsMemberSurname;
    EditText rsCellphone, rsPin;
    TextView rsTown;
    ViewStub viewStub;
    View v;
    GcmDeviceDTO gcmDevice;
    ProgressBar reg_progress;
    String email, strTown;
    TownDTO town;
    Integer townID;
    AutoCompleteTextView rsMemberEmail;

    private RegisterFragmentListener mListener;


    public RegisterFragment() {
        // Required empty public constructor
    }

    static String LOG = RegisterFragment.class.getSimpleName();

    public void updateTown(TownDTO t) {
        Log.d(LOG, t.getTownID() + " : " + t.getTownName());
        if (rsTown == null) {
            rsTown = (TextView) v.findViewById(R.id.edtTown);
        }
        rsTown.setText(t.getTownName());
        townID = t.getTownID();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_register, container, false);
        activity = getActivity();
        ctx = getActivity().getApplicationContext();
        setFields();
        getEmail();

        return v;
    }

    List townList = new ArrayList<TownDTO>();


    public void setFields() {
        rsRegister = (Button) v.findViewById(R.id.btnReg);

        rsTeamName = (EditText) v.findViewById(R.id.edtRegTeamName);
        rsTown = (TextView) v.findViewById(R.id.edtTown);
        rsMemberName = (EditText) v.findViewById(R.id.edtMemberName);
        rsMemberSurname = (EditText) v.findViewById(R.id.edtMemberLastNAme);
        rsMemberEmail = (AutoCompleteTextView) v.findViewById(R.id.edtMemberEmail);
        rsCellphone = (EditText) v.findViewById(R.id.edtMemberPhone);
        viewStub = (ViewStub) v.findViewById(R.id.vTub);
        rsTown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onTownRequest();
            }
        });
        rsRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRegistration();
            }
        });

    }

    public void sendRegistration() {

        if (rsTeamName.getText() == null) {
            Toast.makeText(ctx, "Enter Team Name", Toast.LENGTH_SHORT).show();
            return;
        }

        if (rsTown.getText() == null && townID == null) {

            Toast.makeText(ctx, "Select Towmn", Toast.LENGTH_SHORT).show();
            return;
        }


        if (rsMemberSurname.getText() == null) {
            Toast.makeText(ctx, "Enter Last Name", Toast.LENGTH_SHORT).show();
            return;
        }
        if (rsMemberEmail.getText().toString().isEmpty()) {
            Toast.makeText(ctx, "Enter Enail Address", Toast.LENGTH_SHORT).show();
            return;
        }


        TeamMemberDTO t = new TeamMemberDTO();
        t.setEmail(rsMemberEmail.getText().toString());
        t.setFirstName(rsMemberName.getText().toString());
        t.setLastName(rsMemberSurname.getText().toString());
        t.setCellphone(rsCellphone.getText().toString());
        t.setActiveFlag(0);


        final TeamDTO g = new TeamDTO();
        g.setTeamName(rsTeamName.getText().toString());
        g.setTownID(townID);
        g.getTeamMemberList().add(t);

        RequestDTO r = new RequestDTO();
        r.setRequestType(RequestDTO.REGISTER_TEAM);
        r.setTeam(g);

        WebSocketUtil.sendRequest(ctx, Statics.MINI_SASS_ENDPOINT, r, new WebSocketUtil.WebSocketListener() {
            @Override
            public void onMessage(final ResponseDTO resp) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!ErrorUtil.checkServerError(ctx, resp)) {
                            return;
                        }
                        SharedUtil.storeEmail(ctx, rsMemberEmail.getText().toString());
                        mListener.onRegistered();
                    }
                });
            }

            @Override
            public void onClose() {

            }

            @Override
            public void onError(String message) {

            }
        });

    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (RegisterFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    List<String> emailAccountList;

    public void getEmail() {
        AccountManager am = AccountManager.get(ctx);
        Account[] accts = am.getAccounts();
        if (accts.length == 0) {
            Toast.makeText(ctx, "No Accounts found. Please create one and try again", Toast.LENGTH_LONG).show();

            return;
        }

        emailAccountList = new ArrayList<String>();
        if (accts != null) {
            for (int i = 0; i < accts.length; i++) {
                emailAccountList.add(accts[i].name);
            }
            ArrayAdapter adapter = new ArrayAdapter<String>(ctx, R.layout.xsimple_spinner_item, emailAccountList);


            rsMemberEmail.setAdapter(adapter);
            rsMemberEmail.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    email = emailAccountList.get(position);
                    rsMemberEmail.setText(email);
                }
            });
        }

    }

    @Override
    public void animateCounts() {

    }


}

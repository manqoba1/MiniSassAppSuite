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
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.sifiso.codetribe.minisasslibrary.R;
import com.sifiso.codetribe.minisasslibrary.adapters.MemberToBeAddedAdapter;
import com.sifiso.codetribe.minisasslibrary.dto.CountryDTO;
import com.sifiso.codetribe.minisasslibrary.dto.GcmDeviceDTO;
import com.sifiso.codetribe.minisasslibrary.dto.OrganisationtypeDTO;
import com.sifiso.codetribe.minisasslibrary.dto.TeamDTO;
import com.sifiso.codetribe.minisasslibrary.dto.TeamMemberDTO;
import com.sifiso.codetribe.minisasslibrary.dto.TownDTO;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.RequestDTO;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.ResponseDTO;
import com.sifiso.codetribe.minisasslibrary.util.ErrorUtil;
import com.sifiso.codetribe.minisasslibrary.util.SharedUtil;
import com.sifiso.codetribe.minisasslibrary.util.Statics;
import com.sifiso.codetribe.minisasslibrary.util.ToastUtil;
import com.sifiso.codetribe.minisasslibrary.util.Util;
import com.sifiso.codetribe.minisasslibrary.util.WebSocketUtil;

import java.util.ArrayList;
import java.util.Date;
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
    CheckBox cbMoreMember;
    Spinner sp_org_type, sp_country;
    ViewStub viewStub;
    View v;
    String email;
    Integer countryID, orgaTypeID;
    AutoCompleteTextView rsMemberEmail;
    ResponseDTO response;
    ListView lsMember;
    LinearLayout llMember;
    TextView textView13;
    private RegisterFragmentListener mListener;


    public RegisterFragment() {
        // Required empty public constructor
    }

    static String LOG = RegisterFragment.class.getSimpleName();

   /* public void updateTown(TownDTO t) {
        Log.d(LOG, t.getTownID() + " : " + t.getTownName());
        if (rsTown == null) {
            rsTown = (TextView) v.findViewById(R.id.edtTown);
        }
        rsTown.setText(t.getTownName());
        townID = t.getTownID();
    }*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_register, container, false);
        activity = getActivity();
        ctx = getActivity().getApplicationContext();
        response = (ResponseDTO) getArguments().getSerializable("response");
        setFields();
        getEmail();

        return v;
    }

    List<String> countrySpinner, orgTypeSpinner;

    private void setSpinners() {
        if (countrySpinner == null && orgTypeSpinner == null) {
            countrySpinner = new ArrayList<>();
            orgTypeSpinner = new ArrayList<>();
        }
        countrySpinner.add("Choose country");
        for (CountryDTO c : response.getCountryList()) {
            countrySpinner.add(c.getCountryName());
        }
        orgTypeSpinner.add("Choose organisation type");
        for (OrganisationtypeDTO c : response.getOrganisationtypeList()) {
            orgTypeSpinner.add(c.getOrganisationName());
        }
        ArrayAdapter<String> countryAdapter = new ArrayAdapter<String>(ctx, R.layout.xxsimple_spinner_dropdown_item, countrySpinner);
        ArrayAdapter<String> orgtypeAdapter = new ArrayAdapter<String>(ctx, R.layout.xxsimple_spinner_dropdown_item, orgTypeSpinner);
        sp_org_type.setAdapter(orgtypeAdapter);
        sp_org_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String oName = "";
                if (position > 0) {
                    orgaTypeID = response.getOrganisationtypeList().get(position - 1).getOrganisationTypeID();
                    oName = response.getOrganisationtypeList().get(position - 1).getOrganisationName();
                } else {
                    orgaTypeID = null;
                }
                Log.d(LOG, " the org id " + orgaTypeID + " : " + oName);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        sp_country.setAdapter(countryAdapter);
        sp_country.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String cName = "";
                if (position > 0) {
                    countryID = response.getCountryList().get(position - 1).getCountryID();
                    cName = response.getCountryList().get(position - 1).getCountryName();
                } else {
                    countryID = null;
                }
                Log.d(LOG, " the country id " + countryID + " : " + cName);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    boolean isMoreMember;
    MemberToBeAddedAdapter memberToBeAddedAdapter;

    public void setFields() {
        rsRegister = (Button) v.findViewById(R.id.btnReg);
        textView13 = (TextView) v.findViewById(R.id.textView13);
        llMember = (LinearLayout) v.findViewById(R.id.llMember);
        rsTeamName = (EditText) v.findViewById(R.id.edtRegTeamName);
        sp_country = (Spinner) v.findViewById(R.id.sp_country);
        sp_org_type = (Spinner) v.findViewById(R.id.sp_org_type);
        rsMemberName = (EditText) v.findViewById(R.id.edtMemberName);
        rsMemberSurname = (EditText) v.findViewById(R.id.edtMemberLastNAme);
        rsPin = (EditText) v.findViewById(R.id.edtPassword);
        rsMemberEmail = (AutoCompleteTextView) v.findViewById(R.id.edtMemberEmail);
        rsCellphone = (EditText) v.findViewById(R.id.edtMemberPhone);
        viewStub = (ViewStub) v.findViewById(R.id.vTub);
        lsMember = (ListView) v.findViewById(R.id.lsMember);
        lsMember.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return (event.getAction() == MotionEvent.ACTION_MOVE);
            }
        });
        cbMoreMember = (CheckBox) v.findViewById(R.id.cbMoreMember);
        cbMoreMember.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isMoreMember = isChecked;
                    onCreateOptionsMenu(menu, inflater);
                    disableWhenMoreIsChecked();
                } else {
                    isMoreMember = isChecked;
                    onCreateOptionsMenu(menu, inflater);
                    enableWhenMoreIsNotChecked();
                }
            }
        });
        rsRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendRegistration();

            }
        });
        setSpinners();
    }

    private void disableWhenMoreIsChecked() {
        rsTeamName.setEnabled(false);
        sp_country.setEnabled(false);
        sp_org_type.setEnabled(false);
        textView13.setText(rsTeamName.getText().toString().toUpperCase() + " Team members");
        rsRegister.setText("Add Member");
    }

    private void enableWhenMoreIsNotChecked() {
        rsTeamName.setEnabled(true);
        sp_country.setEnabled(true);
        sp_org_type.setEnabled(true);
        rsRegister.setText("REGISTER");
    }

    public void sendRegistration() {

        if (rsTeamName.getText().toString().isEmpty()) {
            //Toast.makeText(ctx, "Enter Team Name", Toast.LENGTH_SHORT).show();
            rsTeamName.setError("Enter team name");
            return;
        }

        if (countryID == null) {
            Util.showErrorToast(ctx, "Choose a country");
            //Toast.makeText(ctx, "Select Town", Toast.LENGTH_SHORT).show();
            return;
        }

        if (orgaTypeID == null) {
            Util.showErrorToast(ctx, "Choose a Organisation type");
            //Toast.makeText(ctx, "Select Town", Toast.LENGTH_SHORT).show();
            return;
        }
        if (rsMemberName.getText().toString().isEmpty()) {
            rsMemberName.setError("Enter last name");
            // Toast.makeText(ctx, "Enter Last Name", Toast.LENGTH_SHORT).show();
            return;
        }
        if (rsMemberEmail.getText().toString().isEmpty()) {
            rsMemberEmail.setError("Enter email address");
            //Toast.makeText(ctx, "Enter Email Address", Toast.LENGTH_SHORT).show();
            return;
        }
        if (rsPin.getText().toString().isEmpty()) {
            rsPin.setError("Enter password");
            //Toast.makeText(ctx, "Enter Email Address", Toast.LENGTH_SHORT).show();
            return;
        }

        TeamMemberDTO t = new TeamMemberDTO();
        t.setEmail(rsMemberEmail.getText().toString());
        t.setFirstName(rsMemberName.getText().toString());
        t.setLastName(rsMemberSurname.getText().toString());
        t.setCellphone(rsCellphone.getText().toString());
        t.setPin(rsPin.getText().toString());
        t.setActiveFlag(0);
        t.setDateRegistered(new Date().getTime());
        memberToBeRegistered.add(0, t);
        if (isMoreMember) {
            ToastUtil.toast(ctx, "Member is add to " + rsTeamName.getText().toString().toUpperCase() + ".");
            llMember.setVisibility(View.VISIBLE);
            memberToBeAddedAdapter = new MemberToBeAddedAdapter(ctx, memberToBeRegistered, new MemberToBeAddedAdapter.MemberToBeAddedAdapterListener() {
                @Override
                public void onRemoveMember(TeamMemberDTO dto) {
                    memberToBeRegistered.remove(dto);
                    memberToBeAddedAdapter.notifyDataSetChanged();
                    if (memberToBeRegistered.size() == 0) {
                        llMember.setVisibility(View.GONE);
                    }
                }
            });
            lsMember.setAdapter(memberToBeAddedAdapter);
            clearMemberInput();
            return;
        }
        registerRequest(memberToBeRegistered);

    }

    private void clearMemberInput() {
        ToastUtil.toast(ctx, "Next member please.");
        rsMemberEmail.setText(null);
        rsMemberName.setText(null);
        rsMemberSurname.setText(null);
        rsCellphone.setText(null);
        rsPin.setText(null);
    }

    private List<TeamMemberDTO> memberToBeRegistered = new ArrayList<>();

    private void registerRequest(List<TeamMemberDTO> t) {
        final TeamDTO g = new TeamDTO();
        g.setTeamName(rsTeamName.getText().toString());
        g.setCountryID(countryID);
        g.setOrganisationTypeID(orgaTypeID);
        g.setTeammemberList(t);
        g.setDateRegistered(new Date().getTime());

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

    Menu menu;
    MenuInflater inflater;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        this.menu = menu;
        this.inflater = inflater;
        if (isMoreMember) {
            inflater.inflate(R.menu.menu_registration, menu);
        } else {
            menu.clear();
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void checkEmpty() {
        if (memberToBeRegistered.size() == 0) {
            ToastUtil.toast(ctx, "Add member to group");
            return;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.doneAddingMember) {
            checkEmpty();
            registerRequest(memberToBeRegistered);
            return true;
        }
        return super.onOptionsItemSelected(item);
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

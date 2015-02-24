package com.sifiso.codetribe.minisasslibrary.activities;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.sifiso.codetribe.minisasslibrary.R;
import com.sifiso.codetribe.minisasslibrary.dto.GcmDeviceDTO;
import com.sifiso.codetribe.minisasslibrary.dto.TeamDTO;
import com.sifiso.codetribe.minisasslibrary.dto.TeamMemberDTO;
import com.sifiso.codetribe.minisasslibrary.dto.TownDTO;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.RequestDTO;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.ResponseDTO;
import com.sifiso.codetribe.minisasslibrary.util.Statics;
import com.sifiso.codetribe.minisasslibrary.util.Util;
import com.sifiso.codetribe.minisasslibrary.util.WebSocketUtil;

import java.util.ArrayList;
import java.util.List;


public class Registration extends ActionBarActivity implements GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener {
    Context ctx;
    Activity activity;
    Button bsRegister, bsSignin, rsCancel, rsRegister, tsNext;
    EditText esPin, rsTeamName, rsMemberName, rsMemberSurname;
    EditText rsCellphone, rsPin;
    TextView rsTown;
    View regLayout, SignLayout, regMiddlelay, reg_teamLay;
    GcmDeviceDTO gcmDevice;
    ProgressBar reg_progress, sign_progress;
    String email, strTown;
    TownDTO town;
    AutoCompleteTextView rsMemberEmail, esEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_activity);
        activity = this;
        ctx = getApplicationContext();
        getSupportActionBar().setTitle("Sign in");
        setFields();
        getEmail();
        getTown();

    }


    public boolean checkPlayServices() {
        Log.w(LOG, "checking GooglePlayServices .................");
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(ctx);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                // GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                //         PLAY_SERVICES_RESOLUTION_REQUEST).show();
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.google.android.gms")));
                return false;
            } else {
                Log.i(LOG, "This device is not supported.");
                throw new UnsupportedOperationException("GooglePlayServicesUtil resultCode: " + resultCode);
            }
        }
        return true;
    }


    public void sendRegistration() {

        if (rsTeamName.getText().toString().isEmpty()) {
            Toast.makeText(ctx, "Enter Team Name", Toast.LENGTH_SHORT).show();
            return;
        }

        if (rsTown == null) {

            Toast.makeText(ctx, "Select Towmn", Toast.LENGTH_SHORT).show();
            return;
        }


        if (rsMemberSurname.getText().toString().isEmpty()) {
            Toast.makeText(ctx, "Enter Last Name", Toast.LENGTH_SHORT).show();
            return;
        }
        if (rsMemberEmail.getText().toString().isEmpty()) {
            Toast.makeText(ctx, "Enter Enail Address", Toast.LENGTH_SHORT).show();
            return;
        }

        if (rsPin.getText().toString().isEmpty()) {
            Toast.makeText(ctx, "invalid pin", Toast.LENGTH_SHORT).show();
            return;
        }


        TeamMemberDTO t = new TeamMemberDTO();
        t.setEmail(email);
        t.setFirstName(rsMemberName.getText().toString());
        t.setLastName(rsMemberSurname.getText().toString());
        t.setCellphone(rsCellphone.getText().toString());
        t.setPin(rsPin.getText().toString());

        final TeamDTO g = new TeamDTO();
        g.setTeamName(rsTeamName.getText().toString());
        g.setTownName(strTown);

        RequestDTO r = new RequestDTO();
        RequestDTO p = new RequestDTO();

        r.setRequestType(RequestDTO.REGISTER_TEAM);


        r.setTeam(g);
        reg_progress.setVisibility(View.VISIBLE);

        WebSocketUtil.sendRequest(ctx, Statics.MINI_SASS_ENDPOINT, r, new WebSocketUtil.WebSocketListener() {
            @Override
            public void onMessage(final ResponseDTO response) {

            }

            @Override
            public void onClose() {

            }

            @Override
            public void onError(String message) {

            }
        });

    }

    public void sendSignIn() {

        if (esEmail == null) {
            Toast.makeText(ctx, "Enter Email", Toast.LENGTH_SHORT).show();
            return;
        }

        if (esPin.getText().toString().isEmpty()) {
            Toast.makeText(ctx, "Invalid Pin", Toast.LENGTH_SHORT).show();
            return;
        }

        sign_progress.setVisibility(View.VISIBLE);

    }

    public void setFields() {
        bsRegister = (Button) findViewById(R.id.btnLogReg);
        bsSignin = (Button) findViewById(R.id.btnLogSubmit);
        rsCancel = (Button) findViewById(R.id.btnCancel);
        rsRegister = (Button) findViewById(R.id.btnReg);
        regLayout = (View) findViewById(R.id.reg_layed);
        SignLayout = (View) findViewById(R.id.signLay);
        esEmail = (AutoCompleteTextView) findViewById(R.id.edtLogEmail);
        esPin = (EditText) findViewById(R.id.edtLogPassword);
        rsTeamName = (EditText) findViewById(R.id.edtRegTeamName);
        rsTown = (TextView) findViewById(R.id.txtTown);
        rsMemberName = (EditText) findViewById(R.id.edtMemberName);
        rsMemberSurname = (EditText) findViewById(R.id.edtMemberLastNAme);
        rsMemberEmail = (AutoCompleteTextView) findViewById(R.id.edtMemberEmail);
        rsCellphone = (EditText) findViewById(R.id.edtMemberPhone);
        rsPin = (EditText) findViewById(R.id.edtMemberPassword);
        regMiddlelay = (View) findViewById(R.id.middleLayout);
        reg_teamLay = (View) findViewById(R.id.regTeamLayout);
        reg_progress = (ProgressBar) findViewById(R.id.regProgress);
        sign_progress = (ProgressBar) findViewById(R.id.signProgress);
        tsNext = (Button) findViewById(R.id.btnnext);
        bsRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                regMiddlelay.setVisibility(View.GONE);
                regLayout.setVisibility(View.GONE);
                SignLayout.setVisibility(View.GONE);
                reg_teamLay.setVisibility(View.VISIBLE);
                getSupportActionBar().setTitle("Add Team");

            }
        });


        tsNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reg_teamLay.setVisibility(View.GONE);
                getSupportActionBar().setHomeAsUpIndicator(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setTitle("Team Member");
                regLayout.setVisibility(View.VISIBLE);
                getSupportActionBar().show();
            }
        });

        rsCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignLayout.setVisibility(View.VISIBLE);
                regLayout.setVisibility(View.GONE);
                regMiddlelay.setVisibility(View.VISIBLE);
                SignLayout.setVisibility(View.VISIBLE);
                getSupportActionBar().setTitle("Sign in");
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                if (rsTeamName.getText().toString().isEmpty()) {
                    rsRegister.setEnabled(false);
                } else {
                    rsRegister.setEnabled(true);
                }

            }
        });
        rsRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRegistration();
            }
        });

        bsSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSignIn();
            }
        });

    }

    List<String> emailAccountList;

    public void getEmail() {
        AccountManager am = AccountManager.get(getApplicationContext());
        Account[] accts = am.getAccounts();
        if (accts.length == 0) {
            Toast.makeText(ctx, "No Accounts found. Please create one and try again", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        emailAccountList = new ArrayList<String>();
        if (accts != null) {
            for (int i = 0; i < accts.length; i++) {
                emailAccountList.add(accts[i].name);
            }
            ArrayAdapter adapter = new ArrayAdapter<String>(ctx, R.layout.xsimple_spinner_item, emailAccountList);

            esEmail.setAdapter(adapter);
            esEmail.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    email = emailAccountList.get(position);
                }
            });
            rsMemberEmail.setAdapter(adapter);
            rsMemberEmail.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    email = emailAccountList.get(position);
                }
            });
        }

    }


    //town
    List townList = new ArrayList<TownDTO>();

    public void getTown() {
        rsTown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Util.flashOnce(rsTown, 100, new Util.UtilAnimationListener() {
                    @Override
                    public void onAnimationEnded() {
                        Util.showPopupBasicWithHeroImage(ctx, activity, townList, rsTeamName, "Select nearest Town", new Util.UtilPopupListener() {
                            @Override
                            public void onItemSelected(int index) {
                                rsTown.setText((CharSequence) townList.get(index));
                                town = (TownDTO) townList.get(index);
                            }
                        });
                    }
                });

            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_registration, menu);

        startActivity(new Intent(this, SplashActivity.class));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.addMember) {
            reg_teamLay.setVisibility(View.GONE);
            SignLayout.setVisibility(View.GONE);
            regMiddlelay.setVisibility(View.GONE);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Team Member");
            regLayout.setVisibility(View.VISIBLE);
            return true;
        }
        if (id == R.id.addTeam) {
            regMiddlelay.setVisibility(View.GONE);
            regLayout.setVisibility(View.GONE);
            SignLayout.setVisibility(View.GONE);
            reg_teamLay.setVisibility(View.VISIBLE);
            getSupportActionBar().setTitle("Add Team");
            return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onDisconnected() {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    static final String LOG = "RegistrationActivity";
}

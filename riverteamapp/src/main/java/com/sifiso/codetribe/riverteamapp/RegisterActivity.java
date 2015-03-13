package com.sifiso.codetribe.riverteamapp;

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
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.sifiso.codetribe.minisasslibrary.dto.GcmDeviceDTO;
import com.sifiso.codetribe.minisasslibrary.dto.TeamDTO;
import com.sifiso.codetribe.minisasslibrary.dto.TeamMemberDTO;
import com.sifiso.codetribe.minisasslibrary.dto.TownDTO;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.RequestDTO;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.ResponseDTO;
import com.sifiso.codetribe.minisasslibrary.toolbox.WebCheck;
import com.sifiso.codetribe.minisasslibrary.toolbox.WebCheckResult;
import com.sifiso.codetribe.minisasslibrary.util.CacheUtil;
import com.sifiso.codetribe.minisasslibrary.util.ErrorUtil;
import com.sifiso.codetribe.minisasslibrary.util.Statics;
import com.sifiso.codetribe.minisasslibrary.util.Util;
import com.sifiso.codetribe.minisasslibrary.util.WebSocketUtil;

import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.common.GooglePlayServicesClient;


public class RegisterActivity extends ActionBarActivity implements GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener {

    Context ctx;
    Activity activity;
    Button bsRegister, bsSignin, rsRegister, tsNext;
    EditText esPin, rsTeamName, rsMemberName, rsMemberSurname;
    EditText rsCellphone, rsPin;
    EditText rsTown;
    ViewStub viewStub;
    View regMemberLayout,/* SignLayout, regMiddlelay,*/
            regTeamLayout;
    GcmDeviceDTO gcmDevice;
    ProgressBar reg_progress;
    String email, strTown;
    TownDTO town;
    AutoCompleteTextView rsMemberEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        activity = this;
        ctx = getApplicationContext();
        getSupportActionBar().setTitle("Register team");
        setFields();
        getEmail();
        getTown();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register, menu);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Team Member");
        getCachedData();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
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

    public void setFields() {
        rsRegister = (Button) findViewById(R.id.btnReg);

        rsTeamName = (EditText) findViewById(R.id.edtRegTeamName);
        rsTown = (EditText) findViewById(R.id.edtTown);
        rsMemberName = (EditText) findViewById(R.id.edtMemberName);
        rsMemberSurname = (EditText) findViewById(R.id.edtMemberLastNAme);
        rsMemberEmail = (AutoCompleteTextView) findViewById(R.id.edtMemberEmail);
        viewStub = (ViewStub) findViewById(R.id.vTub);

        rsRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRegistration();
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


            rsMemberEmail.setAdapter(adapter);
            rsMemberEmail.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    email = emailAccountList.get(position);
                }
            });
        }

    }


    List townList = new ArrayList<TownDTO>();

    public void getTown() {
        rsTown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Util.flashOnce(rsTown, 100, new Util.UtilAnimationListener() {
                    @Override
                    public void onAnimationEnded() {
                        Util.showPopupBasicTown(ctx, activity, townList, viewStub, "Select nearest Town", new Util.UtilPopupListener() {
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
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onDisconnected() {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    static final String LOG = "RegistrationActivity";

    private void getCachedData() {
        final WebCheckResult wcr = WebCheck.checkNetworkAvailability(ctx);

        CacheUtil.getCachedData(getApplicationContext(), CacheUtil.CACHE_DATA, new CacheUtil.CacheUtilListener() {

            @Override
            public void onFileDataDeserialized(ResponseDTO r) {
                Log.d(LOG, r.getCountryList().toString() + "");

                townList = r.getTownList();
                if (wcr.isWifiConnected()) {
                    getData();
                }
            }

            @Override
            public void onDataCached() {

            }

            @Override
            public void onError() {

            }
        });

    }

    public void getData() {
        RequestDTO req = new RequestDTO();
        req.setRequestType(RequestDTO.GET_DATA);

        try {

            WebSocketUtil.sendRequest(ctx, Statics.MINI_SASS_ENDPOINT, req, new WebSocketUtil.WebSocketListener() {
                @Override
                public void onMessage(final ResponseDTO r) {

                    Log.e(LOG, "## getStarterData responded...statusCode: " + r.getStatusCode());
                    if (!ErrorUtil.checkServerError(ctx, r)) {
                        return;
                    }
                    CacheUtil.cacheData(ctx, r, CacheUtil.CACHE_DATA, new CacheUtil.CacheUtilListener() {
                        @Override
                        public void onFileDataDeserialized(final ResponseDTO resp) {


                        }

                        @Override
                        public void onDataCached() {
                            //finish();
                        }

                        @Override
                        public void onError() {

                        }
                    });


                }

                @Override
                public void onClose() {

                }

                @Override
                public void onError(final String message) {

                }
            });
        } catch (Exception e) {

        }
    }
}

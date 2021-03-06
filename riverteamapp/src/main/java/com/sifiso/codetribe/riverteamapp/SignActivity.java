package com.sifiso.codetribe.riverteamapp;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.gson.Gson;
import com.sifiso.codetribe.minisasslibrary.R;
import com.sifiso.codetribe.minisasslibrary.dialogs.AddTeamsDialog;
import com.sifiso.codetribe.minisasslibrary.dto.GcmDeviceDTO;
import com.sifiso.codetribe.minisasslibrary.dto.TeamDTO;
import com.sifiso.codetribe.minisasslibrary.dto.TeamMemberDTO;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.RequestDTO;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.ResponseDTO;
import com.sifiso.codetribe.minisasslibrary.util.DataUtil;
import com.sifiso.codetribe.minisasslibrary.util.ErrorUtil;
import com.sifiso.codetribe.minisasslibrary.util.GCMUtil;
import com.sifiso.codetribe.minisasslibrary.util.SharedUtil;
import com.sifiso.codetribe.minisasslibrary.util.Statics;
import com.sifiso.codetribe.minisasslibrary.util.TimerUtil;
import com.sifiso.codetribe.minisasslibrary.util.ToastUtil;
import com.sifiso.codetribe.minisasslibrary.util.Util;
import com.sifiso.codetribe.minisasslibrary.util.WebSocketUtil;
import com.sifiso.codetribe.minisasslibrary.viewsUtil.RandomPics;

import java.util.ArrayList;
import java.util.List;


public class SignActivity extends ActionBarActivity {
    Context ctx;
    Button bsRegister, bsSignin;
    EditText esPin;
    String email, townList;
    ProgressBar sign_progress;
    AutoCompleteTextView esEmail;
    TextView SI_app, SI_welcome;
    ImageView SI_banner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);
        ctx = getApplicationContext();

        setFields();

        getEmail();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sign, menu);
        //getCachedData();
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Team member sign in");
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
            Intent intent = new Intent(SignActivity.this, Welcome.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void setFields() {

        SI_app = (TextView) findViewById(R.id.SI_app);
        SI_banner = (ImageView) findViewById(R.id.SI_banner);
        SI_banner.setImageDrawable(Util.getRandomHeroImage(ctx));
        SI_banner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.flashOnce(SI_banner, 100, new Util.UtilAnimationListener() {
                    @Override
                    public void onAnimationEnded() {
                        SI_banner.setImageDrawable(Util.getRandomHeroImage(ctx));
                    }
                });
            }
        });
        SI_app.setText("MiniSASS App Sign in");
        SI_welcome = (TextView) findViewById(R.id.SI_welcome);
        bsSignin = (Button) findViewById(R.id.btnLogSubmit);
        bsSignin.setText("Sign In");
        esEmail = (AutoCompleteTextView) findViewById(R.id.SI_txtEmail);
        esPin = (EditText) findViewById(R.id.SI_pin);
        sign_progress = (ProgressBar) findViewById(R.id.progressBar);


        bsSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.flashOnce(bsSignin, 100, new Util.UtilAnimationListener() {
                    @Override
                    public void onAnimationEnded() {
                        sendSignIn();
                    }
                });

              /*  Intent intentEva = new Intent(SignActivity.this, EvaluationView.class);
                startActivity(intentEva);
                finish();*/
            }
        });

        checkVirgin();
        if (SharedUtil.getEmail(ctx) != null) {
            esEmail.setText(SharedUtil.getEmail(ctx));
        }
    }

    private void checkVirgin() {
        //SharedUtil.clearTeam(ctx);
        TeamMemberDTO dto = SharedUtil.getTeamMember(ctx);
        if (dto != null) {
            Log.i(LOG, "++++++++ Not a virgin anymore ...checking GCM registration....");
            String id = SharedUtil.getRegistrationId(getApplicationContext());
            if (id == null) {
                registerGCMDevice();
            }

            Intent intent = new Intent(ctx, MainPagerActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        registerGCMDevice();
    }

    GcmDeviceDTO gcmDevice;

    private void registerGCMDevice() {
        boolean ok = checkPlayServices();

        if (ok) {
            Log.e(LOG, "############# Starting Google Cloud Messaging registration");
            GCMUtil.startGCMRegistration(getApplicationContext(), new GCMUtil.GCMUtilListener() {
                @Override
                public void onDeviceRegistered(String id) {
                    Log.e(LOG, "############# GCM - we cool, cool.....: " + id);
                    SharedUtil.storeRegistrationId(ctx, id);
                    gcmDevice = new GcmDeviceDTO();
                    gcmDevice.setManufacturer(Build.MANUFACTURER);
                    gcmDevice.setModel(Build.MODEL);
                    gcmDevice.setSerialNumber(Build.SERIAL);
                    gcmDevice.setProduct(Build.PRODUCT);
                    gcmDevice.setAndroidVersion(Build.VERSION.RELEASE);
                    gcmDevice.setRegistrationID(id);

                }

                @Override
                public void onGCMError() {
                    Log.e(LOG, "############# onGCMError --- we got GCM problems");

                }
            });
        }
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

    AddTeamsDialog addTeamDialog;

    public void sendSignIn() {

        if (esEmail.getText() == null) {
            Toast.makeText(ctx, "Enter Email", Toast.LENGTH_SHORT).show();
            return;
        }

        if (esPin.getText().toString().isEmpty()) {
            Toast.makeText(ctx, "Invalid Pin", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestDTO w = new RequestDTO(RequestDTO.SIGN_IN_MEMBER);
        w.setEmail(esEmail.getText().toString());
        w.setPassword(esPin.getText().toString());
        w.setGcmDevice(gcmDevice);
        Log.d(LOG, new Gson().toJson(w));
        WebSocketUtil.sendRequest(ctx, Statics.MINI_SASS_ENDPOINT, w, new WebSocketUtil.WebSocketListener() {
            @Override
            public void onMessage(final ResponseDTO resp) {


                // Log.d(LOG,resp.getTeamMember().getEmail());
                if (!ErrorUtil.checkServerError(ctx, resp)) {
                    return;
                }
                // Log.d(LOG,resp.getTeamMember().getEmail());
                if (resp.getTeamMember().getTeam() == null) {
                    addTeamDialog = new AddTeamsDialog();
                    addTeamDialog.setTeamData(resp);
                    addTeamDialog.setListener(new AddTeamsDialog.AddTeamDialogListener() {
                        @Override
                        public void onAddTeamToMember(TeamDTO team) {
                            DataUtil.addTeam(team, new DataUtil.DataUtilInterface() {
                                @Override
                                public void onResponse(ResponseDTO r) {
                                    if (!ErrorUtil.checkServerError(ctx, r)) {
                                        return;
                                    }
                                    ToastUtil.toast(ctx, r.getMessage());
                                    resp.getTeamMember().setTeam(r.getTeam());
                                    SharedUtil.saveTeamMember(ctx, resp.getTeamMember());
                                    SharedUtil.storeEmail(ctx, esEmail.getText().toString());
                                    Intent intent = new Intent(SignActivity.this, MainPagerActivity.class);
                                    startActivity(intent);
                                    finish();
                                }

                                @Override
                                public void onError(String error) {

                                }
                            });
                        }
                    });
                    addTeamDialog.show(getFragmentManager(), " ");
                    return;
                }
                SharedUtil.saveTeamMember(ctx, resp.getTeamMember());
                SharedUtil.storeEmail(ctx, esEmail.getText().toString());
                Intent intent = new Intent(SignActivity.this, MainPagerActivity.class);
                startActivity(intent);
                finish();

            }

            @Override
            public void onClose() {

            }

            @Override
            public void onError(final String message) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e(LOG, message);
                        Util.showErrorToast(ctx, "Check your network connectivity:");
                    }
                });

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
                    esEmail.setText(email);
                }
            });
            // esEmail.setText(email);

        }

    }

    @Override
    protected void onPause() {
        TimerUtil.killFlashTimer();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        TimerUtil.killFlashTimer();
        super.onDestroy();
    }

    static final String LOG = "SigninActivity";


}

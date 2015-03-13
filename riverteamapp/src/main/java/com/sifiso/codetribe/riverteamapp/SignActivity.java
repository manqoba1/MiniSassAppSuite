package com.sifiso.codetribe.riverteamapp;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
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
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.ResponseDTO;
import com.sifiso.codetribe.minisasslibrary.toolbox.WebCheck;
import com.sifiso.codetribe.minisasslibrary.toolbox.WebCheckResult;
import com.sifiso.codetribe.minisasslibrary.util.CacheUtil;

import java.util.ArrayList;
import java.util.List;


public class SignActivity extends ActionBarActivity implements GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener {
    Context ctx;
    Button bsRegister, bsSignin;
    EditText esPin;
    String email, townList;
    ProgressBar sign_progress;
    AutoCompleteTextView esEmail;

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
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void setFields() {


        bsSignin = (Button) findViewById(R.id.btnLogSubmit);
        esEmail = (AutoCompleteTextView) findViewById(R.id.edtLogEmail);
        esPin = (EditText) findViewById(R.id.edtLogPassword);
        sign_progress = (ProgressBar) findViewById(R.id.signProgress);


        bsSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // sendSignIn();
                Intent intentEva = new Intent(SignActivity.this,EvaluationView.class);
                startActivity(intentEva);
                finish();
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


        }

    }


    static final String LOG = "SigninActivity";

    private void getCachedData() {
        final WebCheckResult wcr = WebCheck.checkNetworkAvailability(ctx);

        CacheUtil.getCachedData(getApplicationContext(), CacheUtil.CACHE_DATA, new CacheUtil.CacheUtilListener() {

            @Override
            public void onFileDataDeserialized(ResponseDTO r) {
                Log.d(LOG, r.getCountryList().toString() + "");


            }

            @Override
            public void onDataCached() {

            }

            @Override
            public void onError() {

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
}

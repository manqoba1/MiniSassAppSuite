package com.sifiso.codetribe.minisasslibrary.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.sifiso.codetribe.minisasslibrary.R;
import com.sifiso.codetribe.minisasslibrary.adapters.TeamMemberAdapter;
import com.sifiso.codetribe.minisasslibrary.dialogs.AddMemberDialog;
import com.sifiso.codetribe.minisasslibrary.dto.TeamMemberDTO;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.RequestDTO;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.ResponseDTO;
import com.sifiso.codetribe.minisasslibrary.toolbox.BaseVolley;
import com.sifiso.codetribe.minisasslibrary.toolbox.WebCheck;
import com.sifiso.codetribe.minisasslibrary.toolbox.WebCheckResult;
import com.sifiso.codetribe.minisasslibrary.util.ErrorUtil;
import com.sifiso.codetribe.minisasslibrary.util.SharedUtil;
import com.sifiso.codetribe.minisasslibrary.util.Statics;

public class ProfileActivity extends ActionBarActivity {

    TextView P_name, P_phone, P_email, P_EVN_count, P_TNAME;
    ImageView AP_PP, P_ICON, P_edit;
    ListView P_membersList;
    Button P_add_member;
    Context ctx;
    private TeamMemberDTO teamMember;
    private TeamMemberAdapter teamMemberAdapter;
    static String LOG = ProfileActivity.class.getSimpleName();
    private AddMemberDialog addMemberDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ctx = getApplicationContext();
        teamMember = SharedUtil.getTeamMember(ctx);
        Log.d(LOG, new Gson().toJson(teamMember));
        setFields();

    }

    private void checkConnection() {
        WebCheckResult wcr = WebCheck.checkNetworkAvailability(ctx, true);
        if (wcr.isWifiConnected()) {
            P_ICON.setVisibility(View.VISIBLE);

        }
    }

    private void setFields() {
        P_name = (TextView) findViewById(R.id.P_name);
        P_phone = (TextView) findViewById(R.id.P_phone);
        P_email = (TextView) findViewById(R.id.P_email);
        P_EVN_count = (TextView) findViewById(R.id.P_EVN_count);
        AP_PP = (ImageView) findViewById(R.id.AP_PP);
        P_TNAME = (TextView) findViewById(R.id.P_TNAME);
        P_edit = (ImageView) findViewById(R.id.P_edit);
        P_ICON = (ImageView) findViewById(R.id.P_ICON);
        P_membersList = (ListView) findViewById(R.id.P_membersList);
        P_add_member = (Button) findViewById(R.id.P_add_member);

        P_name.setText(teamMember.getFirstName() + " " + teamMember.getLastName());
        P_phone.setText((teamMember.getCellphone().equals("") ? "cell not specified" : teamMember.getCellphone()));
        P_email.setText(teamMember.getEmail());
        P_EVN_count.setText(teamMember.getEvaluationCount() + "");
        P_TNAME.setText(teamMember.getTeam().getTeamName());
        if (teamMember.getActiveFlag() == 0) {
            P_add_member.setVisibility(View.GONE);
        } else {
            P_add_member.setVisibility(View.VISIBLE);
        }
        P_add_member.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMemberDialog = new AddMemberDialog();
                addMemberDialog.show(getFragmentManager(), LOG);
                addMemberDialog.setTeamMember(teamMember);
                addMemberDialog.setListener(new AddMemberDialog.AddMemberDialogListener() {
                    @Override
                    public void membersToBeRegistered(TeamMemberDTO tm) {
                        registerMember(tm);
                    }
                });
            }
        });
        setTeamMemberList();
    }

    private void setTeamMemberList() {

        teamMemberAdapter = new TeamMemberAdapter(ctx, R.layout.team_member_item, teamMember.getTeam().getTeammemberList());
        P_membersList.setAdapter(teamMemberAdapter);
    }

    private void registerMember(TeamMemberDTO dto) {
        RequestDTO w = new RequestDTO();
        w.setRequestType(RequestDTO.REGISTER_TEAM_MEMBER);
        w.setTeamMember(dto);

        BaseVolley.getRemoteData(Statics.SERVLET_TEST, w, ctx, new BaseVolley.BohaVolleyListener() {
            @Override
            public void onResponseReceived(ResponseDTO response) {
                if (ErrorUtil.checkServerError(ctx, response)) {

                }
                teamMember = response.getTeamMember();
                SharedUtil.saveTeamMember(ctx, response.getTeamMember());
                setTeamMemberList();
            }

            @Override
            public void onVolleyError(VolleyError error) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
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

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        overridePendingTransition(com.sifiso.codetribe.minisasslibrary.R.anim.slide_in_left, com.sifiso.codetribe.minisasslibrary.R.anim.slide_out_right);
        //  TimerUtil.killFlashTimer();
        super.onDestroy();
    }

    @Override
    public void onPause() {
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        super.onPause();
    }
}

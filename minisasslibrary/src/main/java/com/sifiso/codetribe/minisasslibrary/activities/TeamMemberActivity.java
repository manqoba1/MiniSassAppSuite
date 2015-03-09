package com.sifiso.codetribe.minisasslibrary.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.sifiso.codetribe.minisasslibrary.R;
import com.sifiso.codetribe.minisasslibrary.dto.TeamMemberDTO;
import com.sifiso.codetribe.minisasslibrary.fragments.TeamMemberFragment;

public class TeamMemberActivity extends ActionBarActivity implements TeamMemberFragment.TeamFragmentListener {
    TeamMemberFragment teamMemberFragment;
    TeamMemberDTO teamMember;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teammember);
        teamMemberFragment = (TeamMemberFragment)getFragmentManager().findFragmentById(R.id.fragment);
        teamMember  = (TeamMemberDTO) getIntent().getSerializableExtra("teamMember");
        teamMemberFragment.setCompanyStaff(teamMember);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_teammember, menu);
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
    public void onTeamMemberAdded(TeamMemberDTO team) {
        this.teamMember = teamMember;
        onBackPressed();
    }

    @Override
    public void onTeamMemberUpdated(TeamMemberDTO TeamMember)
         {
            onBackPressed();
        }
        public void onBackPressed(){
            if (teamMember != null) {
                Intent i = new Intent();
                i.putExtra("teamMember",teamMember);
                setResult(RESULT_OK, i);
            } else {
                setResult(RESULT_CANCELED);
            }
            finish();
        }
    }

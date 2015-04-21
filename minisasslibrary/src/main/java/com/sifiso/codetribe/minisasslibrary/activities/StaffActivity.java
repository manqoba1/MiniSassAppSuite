package com.sifiso.codetribe.minisasslibrary.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.sifiso.codetribe.minisasslibrary.R;
import com.sifiso.codetribe.minisasslibrary.dto.TeamMemberDTO;
import com.sifiso.codetribe.minisasslibrary.fragments.StaffFragment;
import com.sifiso.codetribe.minisasslibrary.fragments.StaffListFragment;

import java.util.List;

public class StaffActivity extends ActionBarActivity implements StaffFragment.StaffFragmentListener,
        StaffListFragment.CompanyStaffListListener {
    Context cxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_staff);
        //setContentView(R.layout.fragment_staff_list);
        staffFragment = (StaffFragment)getFragmentManager().findFragmentById(R.id.fragment);
        TeamMemberDTO staff = (TeamMemberDTO) getIntent().getSerializableExtra("companyStaff");
        staffFragment.setCompanyStaff(staff);
    }


    StaffFragment staffFragment;
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
    public void onPause() {
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        super.onPause();
    }

    TeamMemberDTO companyStaff;
    @Override
    public void onStaffAdded(TeamMemberDTO companyStaff) {
        this.companyStaff = companyStaff;
        onBackPressed();
    }

    @Override
    public void onStaffUpdated(TeamMemberDTO companyStaff) {
        onBackPressed();
    }
    @Override
    public void onBackPressed() {
        if (companyStaff != null) {
            Intent i = new Intent();
            i.putExtra("companyStaff",companyStaff);
            setResult(RESULT_OK, i);
        } else {
            setResult(RESULT_CANCELED);
        }
        finish();
    }

    @Override
    public void onCompanyStaffClicked(TeamMemberDTO companyStaff) {

    }

    @Override
    public void onCompanyStaffInvitationRequested(List<TeamMemberDTO> companyStaffList, int index) {

    }

    @Override
    public void onCompanyStaffPictureRequested(TeamMemberDTO companyStaff) {

    }

    @Override
    public void onCompanyStaffEditRequested(TeamMemberDTO companyStaff) {

    }
}

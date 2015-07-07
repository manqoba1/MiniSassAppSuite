package com.sifiso.codetribe.minisasslibrary.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.sifiso.codetribe.minisasslibrary.R;
import com.sifiso.codetribe.minisasslibrary.dto.TeamMemberDTO;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.ResponseDTO;
import com.sifiso.codetribe.minisasslibrary.fragments.TeamMemberListFragment;
import com.sifiso.codetribe.minisasslibrary.interfaces.TeamMemberAddedListener;
import com.sifiso.codetribe.minisasslibrary.listeners.BusyListener;

import java.util.ArrayList;
import java.util.List;

public class TeamMemberActivity extends FragmentActivity implements
		BusyListener, TeamMemberAddedListener {

	TeamMemberListFragment teamMemberListFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_team_member);
		teamMemberListFragment = (TeamMemberListFragment) getSupportFragmentManager()
				.findFragmentById(R.id.teamMemberListFragment);
		response = (ResponseDTO) getIntent().getSerializableExtra("response");
		teamMemberListFragment.setTeam(response.getTeam());
		setTitle(response.getTeam().getTeamName());
        getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	ResponseDTO response;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_teammember, menu);
		mMenu = menu;
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int i = item.getItemId();
		if (i == R.id.action_bar) {
			onBackPressed();
			return true;
		} else if (i == android.R.id.home) {
			finish();
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
	}

	public void setRefreshActionButtonState(final boolean refreshing) {
		if (mMenu != null) {
			final MenuItem refreshItem = mMenu.findItem(R.id.menu_back);
			if (refreshItem != null) {
				if (refreshing) {
					refreshItem.setActionView(R.layout.action_bar_logo);
				} else {
					refreshItem.setActionView(null);
				}
			}
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
	}

	@Override
	public void setBusy() {
		setRefreshActionButtonState(true);

	}

	@Override
	public void setNotBusy() {
		setRefreshActionButtonState(false);

	}

	private List<TeamMemberDTO> teamMemberList = new ArrayList<TeamMemberDTO>();
	Menu mMenu;

	@Override
	public void onTeamMemberAdded(List<TeamMemberDTO> list) {
		Log.w(LOG, "onTeamMemberAdded fired, memebers: " + list.size());
		teamMemberList = list;
        teamMemberListFragment.refresh(list);
	}
	@Override
	public void onBackPressed() {
		Log.w(LOG, "onBackPressed------------");
		if (teamMemberList.isEmpty()) {
			setResult(Activity.RESULT_CANCELED);
		} else {
			Intent i = new Intent();
			ResponseDTO r = new ResponseDTO();
			r.setTeamMemberList(teamMemberList);
			i.putExtra("response", r);
			setResult(Activity.RESULT_OK, i);
			Log.w(LOG, "have setResult with teamMembers added: " + r.getTeamMemberList().size());
		}
		finish();
		
		super.onBackPressed();
	}
	static final String LOG = "TeamMemberActivity";
}

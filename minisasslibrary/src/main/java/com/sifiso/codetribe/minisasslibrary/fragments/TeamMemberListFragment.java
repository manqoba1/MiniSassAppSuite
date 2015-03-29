package com.sifiso.codetribe.minisasslibrary.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListPopupWindow;
import android.widget.ListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.sifiso.codetribe.minisasslibrary.R;
import com.sifiso.codetribe.minisasslibrary.adapters.TeamMemberAdapter;
import com.sifiso.codetribe.minisasslibrary.dto.EvaluationImageDTO;
import com.sifiso.codetribe.minisasslibrary.dto.TeamMemberDTO;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.ResponseDTO;
import com.sifiso.codetribe.minisasslibrary.util.Statics;
import com.sifiso.codetribe.minisasslibrary.util.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by CodeTribe1 on 2015-03-09.
 */
public class TeamMemberListFragment extends Fragment
        implements PageFragment {
    private TeamMemberListListener mListener;

    /**
     * The fragment's ListView/GridView.
     */
    private ListView mListView;

    public TeamMemberListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    Context ctx;
    TextView txtCount, txtName;
    View view, topView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_teammember_list, container, false);
        ctx = getActivity();
        Bundle b = getArguments();
        if (b != null) {
            ResponseDTO r = (ResponseDTO) b.getSerializable("response");
            teamMemberList = r.getTeamMemberList();
        }

        txtCount = (TextView) view.findViewById(R.id.STAFF_LIST_staffCount);
        txtName = (TextView) view.findViewById(R.id.STAFF_LIST_label);
        topView = view.findViewById(R.id.STAFF_LIST_top);
        mListView = (ListView) view.findViewById(R.id.STAFF_LIST_list);

        Statics.setRobotoFontLight(ctx, txtName);
        txtCount.setText("" + teamMemberList.size());

        setList();
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (TeamMemberListListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Host " + activity.getLocalClassName()
                    + " must implement CompanyStaffListListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    ListPopupWindow staffActionsWindow;
    List<String> list;

    private void setList() {

        if (teamMemberList == null) {
            Log.w("TeamMemberListFragment", "-- TeamMmeberList is null, quittin...");
            return;
        }
        teamMemberAdapter = new TeamMemberAdapter(ctx, R.layout.teammember_card,
                teamMemberList, new TeamMemberAdapter.TeamAdapterListener() {
            @Override
            public void onPictureRequested(TeamMemberDTO team) {
                mListener.onTeamMemberPictureRequested(team);
            }

            @Override
            public void onStatusUpdatesRequested(TeamMemberDTO team) {

            }
        });

        mListView.setAdapter(teamMemberAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (null != mListener) {
                    TeamMemberDTO teamMemeber = teamMemberList.get(position);
                    mListener.onTeamMemberClicked(teamMemeber);
                    list = new ArrayList<>();
                    /*list.add(ctx.getString(R.string.get_status));
                    list.add(ctx.getString(R.string.take_picture));
                    list.add(ctx.getString(R.string.send_app_link));*/
                    list.add(ctx.getString(R.string.edit_staff));
                    View v = Util.getHeroView(ctx, "Select Action");

                    //Util.showPopupRiverWithHeroImage(ctx,);
                    Util.showPopupWithHeroImage(ctx, getActivity(), list, txtName, "select ", new Util.UtilPopupListener() {
                        TeamMemberDTO team;

                        @Override
                        public void onItemSelected(int index) {
                            switch (index) {
                                case 0:
                                    //get status
                                    break;
                                case 1:
                                    mListener.onTeamMemberPictureRequested(team);
                                    break;
                                case 2:
                                    int index2 = 0;
                                    for (TeamMemberDTO s : teamMemberList) {
                                        if (s.getTeamMemberID().intValue() == team.getTeamMemberID().intValue()) {
                                            break;
                                        }
                                        index2++;
                                    }
                                    mListener.onTeamMemberInvitationRequested(teamMemberList, index2);
                                    break;
                                case 3:
                                    mListener.onTeamMemberEditRequested(team);
                                    break;
                            }
                        }
                    });
                }
            }
        });
    }

    TeamMemberDTO teamMemberDTO;

    public TeamMemberDTO getTeamMemberDTO() {
        return teamMemberDTO;
    }

    /**
     * The default content for this Fragment has a TextView that is shown when
     * the taskStatusList is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
    public void setEmptyText(CharSequence emptyText) {
        View emptyView = mListView.getEmptyView();

        if (emptyText instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
        }
    }

    @Override
    public void animateCounts() {
        Util.animateRotationY(txtCount, 500);

    }

    public void addTeamStaff(TeamMemberDTO team) {
        if (teamMemberList == null) {
            teamMemberList = new ArrayList<>();
        }
        teamMemberList.add(team);
        //Collections.sort(teamMemberList);
        teamMemberAdapter.notifyDataSetChanged();
        txtCount.setText("" + teamMemberList.size());
        Util.pretendFlash(txtCount, 300, 4, new Util.UtilAnimationListener() {
            @Override
            public void onAnimationEnded() {
                Util.animateRotationY(txtCount, 500);
            }
        });
        int index = 0;
        for (TeamMemberDTO s : teamMemberList) {
            if (s.getTeamMemberID().intValue() == team.getTeamMemberID().intValue()) {
                break;
            }
            index++;
        }
        mListView.setSelection(index);

    }

    public void refreshList(TeamMemberDTO team) {
        ImageLoader.getInstance().clearDiskCache();
        ImageLoader.getInstance().clearMemoryCache();
        setList();

        int index = 0;
        for (TeamMemberDTO c : teamMemberList) {
            if (team.getTeamMemberID() == c.getTeamMemberID()) {
                break;
            }
            index++;
        }
        if (index < teamMemberList.size()) {
            mListView.setSelection(index);
        }
    }

    public interface TeamMemberListListener {
        public void onTeamMemberClicked(TeamMemberDTO teamMember);

        public void onTeamMemberInvitationRequested(List<TeamMemberDTO> teamMemberList, int index);

        public void onTeamMemberPictureRequested(TeamMemberDTO teamMember);

        public void onTeamMemberEditRequested(TeamMemberDTO teamMember);
    }

    EvaluationImageDTO evaluation;
    List<TeamMemberDTO> teamMemberList;
    TeamMemberAdapter teamMemberAdapter;
}


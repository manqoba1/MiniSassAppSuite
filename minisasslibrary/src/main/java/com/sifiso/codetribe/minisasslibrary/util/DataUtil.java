package com.sifiso.codetribe.minisasslibrary.util;

import android.content.Context;

import com.sifiso.codetribe.minisasslibrary.dto.TeamDTO;
import com.sifiso.codetribe.minisasslibrary.dto.TeamMemberDTO;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.RequestDTO;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.ResponseDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by CodeTribe1 on 2015-02-16.
 */
public class DataUtil {
    private static List<TeamMemberDTO> teamMembers;
    private DataUtilInterface listener;

    public interface DataUtilInterface {
        public void onResponse(ResponseDTO r);

        public void onError(String error);

    }

    public static void AddTeamMembers(TeamMemberDTO teamMember) {
        if (teamMembers == null) {
            teamMembers = new ArrayList<TeamMemberDTO>();
        }
        teamMembers.add(teamMember);
    }

    public static List<TeamMemberDTO> getTeamMembers() {
        return teamMembers;
    }


    public static void registerTeamMember(Context ctx, String sufix, TeamDTO team, final DataUtilInterface listener) {

        RequestDTO req = new RequestDTO();
        req.setTeam(team);
        req.setRequestType(RequestDTO.REGISTER_TEAM);

        try {
            WebSocketUtil.sendRequest(ctx, sufix, req, new WebSocketUtil.WebSocketListener() {
                @Override
                public void onMessage(final ResponseDTO response) {

                    listener.onResponse(response);

                }

                @Override
                public void onClose() {

                }

                @Override
                public void onError(String message) {
                    listener.onError(message);
                }
            });
        } catch (Exception e) {

        }

    }

    public static void login(Context ctx, String sufix, TeamMemberDTO teamMember, final DataUtilInterface listener) {
        RequestDTO req = new RequestDTO();
        req.setTeamMember(teamMember);
        req.setRequestType(RequestDTO.SIGN_IN_MEMBER);

        try {
            WebSocketUtil.sendRequest(ctx, sufix, req, new WebSocketUtil.WebSocketListener() {
                @Override
                public void onMessage(final ResponseDTO response) {

                    listener.onResponse(response);

                }

                @Override
                public void onClose() {

                }

                @Override
                public void onError(String message) {
                    listener.onError(message);
                }
            });
        } catch (Exception e) {

        }
    }
    public static void getData(Context ctx, String sufix, TeamMemberDTO teamMember, final DataUtilInterface listener) {
        RequestDTO req = new RequestDTO();
        req.setTeamMember(teamMember);
        req.setRequestType(RequestDTO.SIGN_IN_MEMBER);

        try {
            WebSocketUtil.sendRequest(ctx, sufix, req, new WebSocketUtil.WebSocketListener() {
                @Override
                public void onMessage(final ResponseDTO response) {

                    listener.onResponse(response);

                }

                @Override
                public void onClose() {

                }

                @Override
                public void onError(String message) {
                    listener.onError(message);
                }
            });
        } catch (Exception e) {

        }
    }


}

package com.sifiso.codetribe.minisasslibrary.util;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.sifiso.codetribe.minisasslibrary.dto.EvaluationDTO;
import com.sifiso.codetribe.minisasslibrary.dto.GcmDeviceDTO;
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
    static DataUtilInterface dataUtilInterface;

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
        dataUtilInterface = listener;
        RequestDTO req = new RequestDTO();
        req.setTeam(team);
        req.setRequestType(RequestDTO.REGISTER_TEAM);

        try {
            WebSocketUtil.sendRequest(ctx, sufix, req, new WebSocketUtil.WebSocketListener() {
                @Override
                public void onMessage(final ResponseDTO response) {

                    dataUtilInterface.onResponse(response);

                }

                @Override
                public void onClose() {

                }

                @Override
                public void onError(String message) {
                    dataUtilInterface.onError(message);
                }
            });
        } catch (Exception e) {

        }

    }

    public static void login(Context ctx, String sufix, String email, String pin, GcmDeviceDTO dto, DataUtilInterface listener) {

        dataUtilInterface = listener;
        RequestDTO req = new RequestDTO();
        req.setEmail(email);
        req.setPassword(pin);
        req.setGcmDevice(dto);
        req.setRequestType(RequestDTO.SIGN_IN_MEMBER);

        try {
            WebSocketUtil.sendRequest(ctx, sufix, req, new WebSocketUtil.WebSocketListener() {
                @Override
                public void onMessage(final ResponseDTO response) {

                    dataUtilInterface.onResponse(response);

                }

                @Override
                public void onClose() {

                }

                @Override
                public void onError(String message) {
                    dataUtilInterface.onError(message);
                }
            });
        } catch (Exception e) {

        }
    }

    private static String LOG = DataUtil.class.getSimpleName();
    static GCMRegisteredListener gcmRegisteredListener;

    public interface GCMRegisteredListener {
        public void onDeviceRegistered(String c);
    }

    public void registerGCMDevice(Context ctx, Activity act, GCMRegisteredListener registeredListener) {
        gcmRegisteredListener = registeredListener;
        GCMUtil.checkPlayServices(ctx, act);

        GCMUtil.startGCMRegistration(ctx, new GCMUtil.GCMUtilListener() {
            @Override
            public void onGCMError() {
                Log.e(LOG, "Error registering device for gcm");
            }

            @Override
            public void onDeviceRegistered(String regID) {
                gcmRegisteredListener.onDeviceRegistered(regID);
            }
        });


    }


    public static void getData(Context ctx, String sufix, DataUtilInterface listener) {
        dataUtilInterface = listener;
        RequestDTO req = new RequestDTO();
        req.setRequestType(RequestDTO.GET_DATA);

        try {
            WebSocketUtil.sendRequest(ctx, sufix, req, new WebSocketUtil.WebSocketListener() {
                @Override
                public void onMessage(final ResponseDTO response) {

                    dataUtilInterface.onResponse(response);

                }

                @Override
                public void onClose() {

                }

                @Override
                public void onError(String message) {
                    dataUtilInterface.onError(message);
                }
            });
        } catch (Exception e) {

        }
    }

    public static void createEvaluation(Context ctx, String sufix, EvaluationDTO dto, DataUtilInterface listener) {
        dataUtilInterface = listener;
        RequestDTO req = new RequestDTO();
        req.setEvaluation(dto);
        try {
            WebSocketUtil.sendRequest(ctx, sufix, req, new WebSocketUtil.WebSocketListener() {
                @Override
                public void onMessage(final ResponseDTO response) {

                    dataUtilInterface.onResponse(response);

                }

                @Override
                public void onClose() {

                }

                @Override
                public void onError(String message) {
                    dataUtilInterface.onError(message);
                }
            });
        } catch (Exception e) {

        }
    }

}
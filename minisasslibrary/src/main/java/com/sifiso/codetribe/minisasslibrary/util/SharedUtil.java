package com.sifiso.codetribe.minisasslibrary.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;
import com.sifiso.codetribe.minisasslibrary.dto.EvaluationSiteDTO;
import com.sifiso.codetribe.minisasslibrary.dto.TeamDTO;
import com.sifiso.codetribe.minisasslibrary.dto.TeamMemberDTO;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by aubreyM on 2014/10/12.
 */
public class SharedUtil {
    static final Gson gson = new Gson();
    public static final String
            TEAM_MEMBER_JSON = "teamMember",
            TEAM_JSON = "team",
            GCM_REGISTRATION_ID = "gcm",
            SESSION_ID = "sessionID",
            SITE_LOCATION = "siteLocation",

    LOG = "SharedUtil",
            APP_VERSION = "appVersion";

    /**
     * Stores the registration ID and app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param context application's context.
     * @param regId   registration ID
     */
    public static void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        int appVersion = getAppVersion(context);
        Log.i(LOG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(GCM_REGISTRATION_ID, regId);
        editor.putInt(APP_VERSION, appVersion);
        editor.commit();
        Log.e(LOG, "GCM registrationId saved in prefs! Yebo!!!");
    }

    /**
     * Gets the current registration ID for application on GCM service.
     * <p/>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     * registration ID.
     */
    public static String getRegistrationId(Context context) {
        final SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        String registrationId = prefs.getString(GCM_REGISTRATION_ID, null);
        if (registrationId == null) {
            Log.i(LOG, "GCM Registration ID not found on device.");
            return null;
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = SharedUtil.getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(LOG, "App version changed.");
            return null;
        }
        return registrationId;
    }

    public static void saveSessionID(Context ctx, String sessionID) {

        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor ed = sp.edit();
        ed.putString(SESSION_ID, sessionID);
        ed.commit();
        Log.e("SharedUtil", "%%%%% SessionID: " + sessionID + " saved in SharedPreferences");
    }

    public static String getSessionID(Context ctx) {

        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(ctx);
        return sp.getString(SESSION_ID, null);
    }

    public static void saveCompanyStaff(Context ctx, TeamMemberDTO dto) {

        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(ctx);
        String x = gson.toJson(dto);
        SharedPreferences.Editor ed = sp.edit();
        ed.putString(TEAM_MEMBER_JSON, x);
        ed.commit();
        Log.e("SharedUtil", "%%%%% CompanyStaff: " + dto.getFirstName() + " " + dto.getLastName() + " saved in SharedPreferences");
    }

    public static TeamMemberDTO getTeamMember(Context ctx) {

        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(ctx);
        String adm = sp.getString(TEAM_MEMBER_JSON, null);
        TeamMemberDTO golfGroup = null;
        if (adm != null) {
            golfGroup = gson.fromJson(adm, TeamMemberDTO.class);

        }
        return golfGroup;
    }

    public static void saveCompany(Context ctx, TeamDTO dto) {

        TeamDTO xx = new TeamDTO();
        xx.setTeamName(dto.getTeamName());
        xx.setTeamID(dto.getTeamID());
        xx.setTownID(dto.getTownID());
        xx.setTeamImage(dto.getTeamImage());

        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(ctx);
        String x = gson.toJson(xx);
        SharedPreferences.Editor ed = sp.edit();
        ed.putString(TEAM_JSON, x);
        ed.commit();
        Log.e("SharedUtil", "%%%%% Company: " + dto.getTeamName() + " saved in SharedPreferences");
    }

    public static TeamDTO getTeam(Context ctx) {

        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(ctx);
        String adm = sp.getString(TEAM_JSON, null);
        TeamDTO co = null;
        if (adm != null) {
            co = gson.fromJson(adm, TeamDTO.class);

        }
        return co;
    }

    public static void saveEvaluationSiteLocation(Context ctx, EvaluationSiteDTO dto, Location loc) {
        EvaluationSiteDTO sl = new EvaluationSiteDTO();
        sl.setLatitude(loc.getLatitude());
        sl.setLongitude(loc.getLongitude());
        sl.setRiverID(dto.getRiverID());
        sl.setCategoryID(dto.getCategoryID());
        sl.setDateRegistered(new Date().getTime());


        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor ed = sp.edit();
        ed.putString(SITE_LOCATION, gson.toJson(sl));
        ed.commit();
        Log.e("SharedUtil", "%%%%% Location, site: " + dto.getCategoryName() + " saved in SharedPreferences");
    }

    public static EvaluationSiteDTO getEvaluationSiteLocation(Context ctx) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(ctx);
        String s = sp.getString(SITE_LOCATION, null);
        if (s == null) {
            return null;
        }
        return gson.fromJson(s, EvaluationSiteDTO.class);
    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    public static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }


}

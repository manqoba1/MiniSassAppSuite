package com.sifiso.codetribe.minisasslibrary.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;
import com.sifiso.codetribe.minisasslibrary.dto.EvaluationDTO;
import com.sifiso.codetribe.minisasslibrary.dto.EvaluationImageDTO;
import com.sifiso.codetribe.minisasslibrary.dto.TeamMemberDTO;

import java.util.Date;

/**
 * Created by Chris on 2015-02-20.
 */
public class SharedUtil {

    static final Gson gson = new Gson();

    public static final String
        TEAM_MEMBER_JSON = "teamMember",
        TEAM_JSON = "team",
        EVALUATION_IMAGE_ID = "evaluationImageID",
        EVALUATION_JSON = ".evaluation",
        EVALUATION_ID = "evaluationID",
        GCM_REGISTRAION_ID = "gcm",
        IMAGE_LOCATION = "imageLocation",
        LOG = "SharedUtil",
        REMINDER_TIME = "reminderTime",
      APP_VERSION = "appVersion";

    public static ImageLocation getImageLocation(Context ctx) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(ctx);
        String s = sp.getString(IMAGE_LOCATION, null);
        if (s == null) {
            return null;
        }
        return gson.fromJson(s,ImageLocation.class);
    }

    public static EvaluationDTO getEvaluation(Context ctx) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
        String st = sp.getString(EVALUATION_JSON, null);
        EvaluationDTO e = null;
        if (st != null){
            e = gson.fromJson(st, EvaluationDTO.class);
        }
        return e;
    }

    public static TeamMemberDTO getTeamMember(Context ctx) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
        String st = sp.getString(TEAM_MEMBER_JSON, null);
        TeamMemberDTO tm =null;
        if (st != null) {
            tm = gson.fromJson(st, TeamMemberDTO.class);
        }
        return tm;
    }

    public static void saveLastEvaluationImageID(Context ctx, Integer evaluationImageID) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor ed = sp.edit();
        ed.putInt(EVALUATION_IMAGE_ID, evaluationImageID);
        ed.commit();
        Log.e("SharedUtil", "evaluationImageID:" + evaluationImageID + "save in SharedPreferences");
    }

    public static Integer getLastEvaluationImageID(Context ctx) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(ctx);
        int id = sp.getInt(EVALUATION_IMAGE_ID, 0);
        return id;

    }

    public static void saveImageLocation(Context ctx, EvaluationImageDTO evi, Location loc) {
        ImageLocation il = new ImageLocation();
        il.setLongitude(loc.getLongitude());
        il.setAccuracy(loc.getAccuracy());
        il.setLatitude(loc.getLatitude());
        il.setDateTaken(new Date());

        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor ed = sp.edit();
        ed.putString(IMAGE_LOCATION, gson.toJson(il));
        ed.commit();
        Log.e(LOG, "SharedUtil, LOCATION IMAGE: " + evi.getFileName() + " SAVED IN SharedPreferences");

        }


}

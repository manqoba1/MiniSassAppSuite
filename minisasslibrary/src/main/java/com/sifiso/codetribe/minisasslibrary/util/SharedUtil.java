package com.sifiso.codetribe.minisasslibrary.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;
import com.sifiso.codetribe.minisasslibrary.dto.EvaluationImageDTO;

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

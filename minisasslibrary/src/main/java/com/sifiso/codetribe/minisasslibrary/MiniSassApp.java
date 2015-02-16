package com.sifiso.codetribe.minisasslibrary;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.utils.L;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.sifiso.codetribe.minisasslibrary.dto.RiverDTO;
import com.sifiso.codetribe.minisasslibrary.toolbox.BitmapLruCache;
import com.sifiso.codetribe.minisasslibrary.util.Statics;

import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.annotation.ReportsCrashes;

import java.io.File;

/**
 * Created by CodeTribe1 on 2015-02-16.
 */
@ReportsCrashes(
        formKey = "",
        formUri = Statics.CRASH_REPORTS_URL,
        customReportContent = {ReportField.APP_VERSION_NAME, ReportField.APP_VERSION_CODE,
                ReportField.ANDROID_VERSION, ReportField.PHONE_MODEL, ReportField.BRAND, ReportField.STACK_TRACE,
                ReportField.PACKAGE_NAME,
                ReportField.CUSTOM_DATA,
                ReportField.LOGCAT},
        socketTimeout = 10000
)
public class MiniSassApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        StringBuilder sb = new StringBuilder();
        sb.append("\n\n\n#######################################\n");
        sb.append("#######################################\n");
        sb.append("###\n");
        sb.append("###  Monitor App has started\n");
        sb.append("###\n");
        sb.append("#######################################\n\n");

        Log.d(LOG, sb.toString());
//
        ACRA.init(this);
        /*RiverDTO river = SharedUtil.getCompany(getApplicationContext());
        if (company != null) {
            ACRA.getErrorReporter().putCustomData("companyID", "" + company.getCompanyID());
            ACRA.getErrorReporter().putCustomData("companyName", company.getCompanyName());
        }*/
        Log.e(LOG, "###### ACRA Crash Reporting has been initiated");
        initializeVolley(getApplicationContext());

        DisplayImageOptions defaultOptions =
                new DisplayImageOptions.Builder()
                        .cacheInMemory(true)
                        .cacheOnDisk(true)
                        .showImageOnFail(getApplicationContext().getResources().getDrawable(R.drawable.under_construction))
                        .showImageOnLoading(getApplicationContext().getResources().getDrawable(R.drawable.under_construction))
                        .build();

        File cacheDir = StorageUtils.getCacheDirectory(this, true);
        Log.d(LOG, "## onCreate, ImageLoader cacheDir, files: " + cacheDir.listFiles().length);
        //
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .denyCacheImageMultipleSizesInMemory()
                .diskCache(new UnlimitedDiscCache(cacheDir))
                .memoryCache(new LruMemoryCache(16 * 1024 * 1024))
                .defaultDisplayImageOptions(defaultOptions)
                .build();

        ImageLoader.getInstance().init(config);
        L.writeDebugLogs(false);
        L.writeLogs(false);

        Log.w(LOG, "###### ImageLoaderConfiguration has been initialised");
    }

    /**
     * Set up Volley Networking; create RequestQueue and ImageLoader
     *
     * @param context
     */
    public void initializeVolley(Context context) {
        Log.e(LOG, "initializing Volley Networking ...");
        requestQueue = Volley.newRequestQueue(context);
        int memClass = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE))
                .getMemoryClass();

        // Use 1/8th of the available memory for this memory cache.
        int cacheSize = 1024 * 1024 * memClass / 8;
        bitmapLruCache = new BitmapLruCache(cacheSize);
        // imageLoader = new ImageLoader(requestQueue, bitmapLruCache);
        Log.i(LOG, "********** Yebo! Volley Networking has been initialized, cache size: " + (cacheSize / 1024) + " KB");

        // Create global configuration and initialize ImageLoader with this configuration
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .defaultDisplayImageOptions(defaultOptions)
                .build();

        ImageLoader.getInstance().init(config);
    }
    public RequestQueue getRequestQueue() {
        return requestQueue;
    }

    public BitmapLruCache getBitmapLruCache() {
        return bitmapLruCache;
    }

    RequestQueue requestQueue;
    BitmapLruCache bitmapLruCache;
    static String LOG = MiniSassApp.class.getSimpleName();
}

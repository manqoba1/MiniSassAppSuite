package com.sifiso.codetribe.minisasslibrary.fragments;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.sifiso.codetribe.minisasslibrary.R;
import com.sifiso.codetribe.minisasslibrary.dto.EvaluationSiteDTO;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.RequestDTO;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.ResponseDTO;
import com.sifiso.codetribe.minisasslibrary.services.RequestCache;
import com.sifiso.codetribe.minisasslibrary.toolbox.WebCheck;
import com.sifiso.codetribe.minisasslibrary.toolbox.WebCheckResult;
import com.sifiso.codetribe.minisasslibrary.util.CacheUtil;
import com.sifiso.codetribe.minisasslibrary.util.ErrorUtil;
import com.sifiso.codetribe.minisasslibrary.util.RequestCacheUtil;
import com.sifiso.codetribe.minisasslibrary.util.Statics;
import com.sifiso.codetribe.minisasslibrary.util.Util;
import com.sifiso.codetribe.minisasslibrary.util.WebSocketUtil;

import java.text.DecimalFormat;


public class GPSScanFragment extends Fragment implements PageFragment {

    static final DecimalFormat df = new DecimalFormat("###,###,###,##0.00");
    static final String LOG = GPSScanFragment.class.getSimpleName();
    TextView desiredAccuracy, txtLat, txtLng, txtAccuracy;
    Button btnScan, btnSave;
    View view;
    SeekBar seekBar;
    boolean isScanning;
    EvaluationSiteDTO evaluationSite;
    ImageView imgLogo, hero;
    Context ctx;
    ObjectAnimator logoAnimator;
    long start, end;
    Chronometer chronometer;
    boolean pleaseStop;
    Location location;
    private GPSScanFragmentListener listener;

    public GPSScanFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment GPSScanFragment.
     */
    public static GPSScanFragment newInstance() {
        GPSScanFragment fragment = new GPSScanFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void animateCounts() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.w(LOG, "###### onCreateView");

        view = inflater.inflate(R.layout.fragment_gps, container, false);
        ctx = getActivity();

        setFields();
        return view;
    }

    public void startScan() {
        listener.onStartScanRequested();
        txtAccuracy.setText("0.00");
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.start();
        isScanning = true;
        btnScan.setText(ctx.getString(R.string.stop_scan));
    }

    private void setFields() {

        desiredAccuracy = (TextView) view.findViewById(R.id.GPS_desiredAccuracy);
        txtAccuracy = (TextView) view.findViewById(R.id.GPS_accuracy);
        txtLat = (TextView) view.findViewById(R.id.GPS_latitude);
        txtLng = (TextView) view.findViewById(R.id.GPS_longitude);
        btnSave = (Button) view.findViewById(R.id.GPS_btnSave);
        btnScan = (Button) view.findViewById(R.id.GPS_btnStop);
        seekBar = (SeekBar) view.findViewById(R.id.GPS_seekBar);
        imgLogo = (ImageView) view.findViewById(R.id.GPS_imgLogo);
        hero = (ImageView) view.findViewById(R.id.GPS_hero);
        chronometer = (Chronometer) view.findViewById(R.id.GPS_chrono);

        btnSave.setVisibility(View.GONE);
        Statics.setRobotoFontBold(ctx, txtLat);
        Statics.setRobotoFontBold(ctx, txtLng);

        imgLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.flashOnce(imgLogo, 100, new Util.UtilAnimationListener() {
                    @Override
                    public void onAnimationEnded() {
                        listener.onMapRequested(evaluationSite);
                    }
                });
            }
        });

        txtAccuracy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.flashOnce(txtAccuracy, 100, new Util.UtilAnimationListener() {
                    @Override
                    public void onAnimationEnded() {
                        if (evaluationSite.getAccuracy() == null) return;
                        listener.onMapRequested(evaluationSite);
                    }
                });

            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                desiredAccuracy.setText("" + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.flashOnce(btnScan, 100, new Util.UtilAnimationListener() {
                    @Override
                    public void onAnimationEnded() {
                        if (isScanning) {
                            listener.onEndScanRequested();
                            isScanning = false;
                            btnScan.setText(ctx.getString(R.string.start_scan));
                            chronometer.stop();
                        } else {
                            listener.onStartScanRequested();
                            isScanning = true;
                            btnScan.setText(ctx.getString(R.string.stop_scan));
                            chronometer.setBase(SystemClock.elapsedRealtime());
                            chronometer.start();
                            Util.collapse(btnSave, 300, null);
                        }
                    }
                });

            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.flashOnce(btnSave, 100, new Util.UtilAnimationListener() {
                    @Override
                    public void onAnimationEnded() {

                    }
                });

            }
        });
    }

    private void confirmLocation() {
        RequestDTO w = new RequestDTO(RequestDTO.CONFIRM_LOCATION);

        w.setEvaluationSiteID(evaluationSite.getEvaluationSiteID());
        w.setLatitude(evaluationSite.getLatitude());
        w.setLongitude(evaluationSite.getLongitude());
        w.setAccuracy(evaluationSite.getAccuracy());


        WebCheckResult wcr = WebCheck.checkNetworkAvailability(ctx);
        if (wcr.isWifiConnected()) {
            sendRequest(w);
        } else {
            addRequestToCache(w);
        }
    }

    private void sendRequest(final RequestDTO request) {
        WebSocketUtil.sendRequest(ctx, Statics.MINI_SASS_ENDPOINT, request, new WebSocketUtil.WebSocketListener() {
            @Override
            public void onMessage(ResponseDTO response) {
                if (response.getStatusCode() > 0) {
                    addRequestToCache(request);
                }
            }

            @Override
            public void onClose() {

            }

            @Override
            public void onError(String message) {
                addRequestToCache(request);
            }
        });
    }

    private void addRequestToCache(RequestDTO request) {
        RequestCacheUtil.addRequest(ctx, request, new CacheUtil.CacheRequestListener() {
            @Override
            public void onDataCached() {
                if (evaluationSite == null) return;
                evaluationSite.setLocationConfirmed(1);
                Log.e(LOG, "----onDataCached, onEndScanRequested - please stop scanning");
                listener.onEndScanRequested();
                listener.onLocationConfirmed(evaluationSite);

            }

            @Override
            public void onRequestCacheReturned(RequestCache cache) {

            }

            @Override
            public void onError() {

            }
        });
    }

    public void resetLogo() {
        logoAnimator = ObjectAnimator.ofFloat(imgLogo, "rotation", 0, 360);
        logoAnimator.setDuration(200);
        logoAnimator.start();
    }

    private void sendGPSData() {

        RequestDTO w = new RequestDTO(RequestDTO.UPDATE_EVALUATION_SITE);
        final EvaluationSiteDTO site = new EvaluationSiteDTO();
        site.setEvaluationSiteID(evaluationSite.getEvaluationSiteID());
        site.setLatitude(location.getLatitude());
        site.setLongitude(location.getLongitude());
        site.setAccuracy(location.getAccuracy());
        w.setEvaluationSite(site);

        WebSocketUtil.sendRequest(ctx, Statics.MINI_SASS_ENDPOINT, w, new WebSocketUtil.WebSocketListener() {
            @Override
            public void onMessage(final ResponseDTO response) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!ErrorUtil.checkServerError(ctx, response)) {
                            return;
                        }
                        listener.onEndScanRequested();
                        listener.onLocationConfirmed(site);
                        Log.w(LOG, "++++++++++++ project site location updated");
                    }
                });
            }

            @Override
            public void onClose() {

            }

            @Override
            public void onError(final String message) {
                Log.e(LOG, "---- ERROR websocket - " + message);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Util.showErrorToast(ctx, message);
                    }
                });
            }
        });
    }

    public void setLocation(Location location) {
        if (evaluationSite == null) return;
        this.location = location;
        txtLat.setText("" + location.getLatitude());
        txtLng.setText("" + location.getLongitude());
        txtAccuracy.setText("" + location.getAccuracy());

        if (location.getAccuracy() == seekBar.getProgress()
                || location.getAccuracy() < seekBar.getProgress()) {
            isScanning = false;
            chronometer.stop();
            resetLogo();
            btnScan.setText(ctx.getString(R.string.start_scan));
            evaluationSite.setLatitude(location.getLatitude());
            evaluationSite.setLongitude(location.getLongitude());
            evaluationSite.setAccuracy(location.getAccuracy());
            //confirmLocation();
            return;
        }
        Util.flashSeveralTimes(hero, 200, 2, null);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (GPSScanFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " - Host activity" + activity.getLocalClassName() + " must implement GPSScanFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public EvaluationSiteDTO getProjectSite() {
        return evaluationSite;
    }

    public void setProjectSite(EvaluationSiteDTO evaluationSite) {
        this.evaluationSite = evaluationSite;

    }

    public interface GPSScanFragmentListener {
        public void onStartScanRequested();

        public void onLocationConfirmed(EvaluationSiteDTO projectSite);

        public void onEndScanRequested();

        public void onMapRequested(EvaluationSiteDTO projectSite);


    }
}

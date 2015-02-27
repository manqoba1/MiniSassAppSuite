package com.sifiso.codetribe.minisasslibrary.activities;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sifiso.codetribe.minisasslibrary.R;
import com.sifiso.codetribe.minisasslibrary.dto.EvaluationDTO;
import com.sifiso.codetribe.minisasslibrary.dto.EvaluationImageDTO;
import com.sifiso.codetribe.minisasslibrary.util.Statics;
import com.sifiso.codetribe.minisasslibrary.util.Util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MinisassMapsActivity extends ActionBarActivity implements LocationListener,
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener {

  ///  LocationClient mLocationClient;
    GoogleMap googleMap;
    GoogleApiClient mGoogleApiClient;
    LocationRequest locationRequest;
    Location location;
    Context ctx;
    List<Marker> markers = new ArrayList<Marker>();
    static final String LOG = MinisassMapsActivity.class.getSimpleName();
    boolean mResolvingError;
    static final long ONE_MINUTE = 1000 * 60;
    static final long FIVE_MINUTES = 1000 * 60 * 5;
    //unique error dialog
    private static final String DIALOG_ERROR = "dialog_error";
    EvaluationImageDTO evaluationImage;
    EvaluationDTO evaluation;
    int index;
    TextView text, txtCount;
    View topLayout;
    ProgressBar progressBar;
    static final Locale loc = Locale.getDefault();
    static final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yy HH:mm");


    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctx.getApplicationContext();
        try {
            setContentView(R.layout.activity_minisass_maps);
        } catch (Exception e) {
            Log.e(LOG, "FAILED to set ContentView", e);
        }
        evaluationImage = (EvaluationImageDTO) getIntent().getSerializableExtra("evaluationImage");
        evaluation = (EvaluationDTO) getIntent().getSerializableExtra("evaluation");
        index = getIntent().getIntExtra("index", 0);

      //  mLocationClient = new LocationClient(getApplicationContext(), this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        text = (TextView) findViewById(R.id.text1);
     //   txtCount = (TextView) findViewById(R.id.count);
        txtCount.setText("0");
     //   progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        Statics.setRobotoFontBold(ctx, text);

        topLayout = findViewById(R.id.top);

        if (evaluationImage != null) {
            txtCount.setText("1");
            text.setText(getString(R.string.evaluation_image_map));
        }
        if (evaluation != null) {
            text.setText(getString(R.string.evaluation_map));
        }
        googleMap = mapFragment.getMap();
        if (googleMap == null) {
            Util.showToast(ctx, getString(R.string.map_unavailable));
            finish();
            return;
        }

       // setGoogleMap;
    }

   /* private void setGoogleMap() {
        googleMap.setMyLocationEnabled(true);
        googleMap.setBuildingsEnabled(true);
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                location.setLatitude(latLng.latitude);
                location.setLongitude(latLng.longitude);
                Log.i(LOG, "_OnMapClickListener");
            }
        });

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                LatLng latLng = marker.getPosition();
                Location loc = new Location(location);
                loc.setLatitude(latLng.latitude);
                loc.setLongitude(latLng.longitude);
                if (evaluation != null) {
                    for (EvaluationImageDTO image : evaluation.getEvaluationImageList()) {
                        if (image.getFileName().equalsIgnoreCase(marker.getTitle())) {
                            evaluationImage = image;
                        }
                    }
                }
                float f = location.distanceTo(loc);
                Log.w(LOG, "distance" + f);

                showPopup(latLng.latitude, latLng.longitude, marker.getTitle() + "\n" + marker.getSnippet());
                return true;
            }
        });
        if (evaluationImage == null) {
            if (evaluation.getLatitude() == null) {
                Util.showToast(ctx, "evaluation location coordinates, failed to put on site");
                finish();
            } else {
                setOneMarker();
                if (evaluation.getLo)
            }
        }
    }
*/

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onDisconnected() {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}

package com.sifiso.codetribe.minisasslibrary.activities;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.sifiso.codetribe.minisasslibrary.R;
import com.sifiso.codetribe.minisasslibrary.dto.EvaluationDTO;
import com.sifiso.codetribe.minisasslibrary.dto.EvaluationImageDTO;
import com.sifiso.codetribe.minisasslibrary.dto.EvaluationSiteDTO;
import com.sifiso.codetribe.minisasslibrary.dto.RiverDTO;
import com.sifiso.codetribe.minisasslibrary.util.Constants;
import com.sifiso.codetribe.minisasslibrary.util.DistanceCalculator;
import com.sifiso.codetribe.minisasslibrary.util.Statics;
import com.sifiso.codetribe.minisasslibrary.util.Util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements LocationListener
        , GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener {

    GoogleMap googleMap;
    GoogleApiClient mGoogleApiClient;
    LocationRequest locationRequest;
    LocationClient mLocationClient;
    Location location;
    Context ctx;
    List<Marker> markers = new ArrayList<Marker>();
    static final String LOG = MapsActivity.class.getSimpleName();
    boolean mResolvingError;
    static final long ONE_MINUTE = 1000 * 60;
    static final long FIVE_MINUTES = 1000 * 60 * 5;
    private static final int REQUEST_RESOLVE_ERROR = 1001;
    //unique error dialog
    private static final String DIALOG_ERROR = "dialog_error";
    EvaluationImageDTO evaluationImage;
    EvaluationDTO evaluation;
    RiverDTO river;
    int index;
    TextView /*text,*/ txtCount, textMap;
    View topLayout;
    ProgressBar progressBar;
    static final Locale loc = Locale.getDefault();
    static final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yy HH:mm");


    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        ctx = MapsActivity.this;

        evaluationImage = (EvaluationImageDTO) getIntent().getSerializableExtra("evaluationImage");
        evaluation = (EvaluationDTO) getIntent().getSerializableExtra("evaluation");
        river = (RiverDTO) getIntent().getSerializableExtra("river");
        index = getIntent().getIntExtra("index", 0);
        int displayType = getIntent().getIntExtra("displayType", EVALUATION_VIEW);
        mLocationClient = new LocationClient(getApplicationContext(), this,
                this);
        //txtCount = (TextView) findViewById(R.id.count);
        //textMap = (TextView) findViewById(R.id.textMap);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
//        text = (TextView) findViewById(R.id.textMap);

        // textMap.setText(river.getRiverName());
        //  txtCount.setText(river.getEvaluationSiteList().size() + "");

//        progressBar.setVisibility(View.GONE);
        //Statics.setRobotoFontBold(ctx, text);

          topLayout = findViewById(R.id.top);


        googleMap = mapFragment.getMap();
        if (googleMap == null) {
            Util.showToast(ctx, getString(R.string.map_unavailable));
            // finish();
            return;
        }


        if (displayType == EVALUATION_VIEW) {
            setEvaluationMarkers();
        } else if (displayType == RIVER_VIEW) {
            MeasureRiverLength();
        }

        setGoogleMap();
    }

    static final int EVALUATION_VIEW = 12;
    static final int RIVER_VIEW = 13;

    private List<Polyline> polylines = new ArrayList<Polyline>();
    private List<LatLng> iterateList = new ArrayList<LatLng>();

    private void MeasureRiverLength() {
        googleMap.clear();
        if (river != null) {
            iterateList.add(new LatLng(river.getOriginLatitude(), river.getOriginLongitude()));
            iterateList.add(new LatLng(river.getEndLatitude(), river.getEndLongitude()));
            final PolylineOptions options = new PolylineOptions();
            options.width(5);
            options.color(getResources().getColor(R.color.maroon));
            options.addAll(iterateList);
            double distance = DistanceCalculator.distance(new LatLng(river.getOriginLatitude(), river.getOriginLongitude()), new LatLng(river.getEndLatitude(), river.getEndLongitude()), "");

            Marker marker1 = googleMap.addMarker(new MarkerOptions().position(new LatLng(river.getOriginLatitude(),
                    river.getOriginLongitude())).title(river.getRiverName()));
            marker1.setSnippet("The distance of " + river.getRiverName() + " is : " + Math.round(distance) + " Km");
            marker1.showInfoWindow();

            Marker marker2 = googleMap.addMarker(new MarkerOptions().position(new LatLng(river.getEndLatitude(),
                    river.getEndLongitude())).title(river.getRiverName()));
            markers.add(marker2);
            markers.add(marker1);

            googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {
                    //ensure that all markers in bounds
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();

                    for (Marker marker : markers) {
                        builder.include(marker.getPosition());
                    }

                    LatLngBounds bounds = builder.build();
                    int padding = 5; // offset from edges of the map in pixels
                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                    googleMap.addPolyline(options);
                    txtCount.setVisibility(View.GONE);
                    //googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 1.0f));
                    googleMap.animateCamera(cu);
                    setTitle(river.getRiverName());
                }
            });
        }
    }


    private void setEvaluationMarkers() {
        googleMap.clear();

        if (!river.getEvaluationSiteList().isEmpty()) {
            int index = 0;
            for (EvaluationSiteDTO es : river.getEvaluationSiteList()) {
                BitmapDescriptor desc = BitmapDescriptorFactory.fromResource(R.drawable.dot_black);
                Integer conditionColor = null;
                for (EvaluationDTO eva : es.getEvaluationList()) {
                    if (eva.getConditionsID() != null) {
                        conditionColor = es.getEvaluationList().get(0).getConditionsID();
                        switch (eva.getConditionsID()) {
                            case Constants.UNMODIFIED_NATURAL_SAND:

                                desc = BitmapDescriptorFactory.fromResource(R.drawable.purple_crap);
                                break;
                            case Constants.LARGELY_NATURAL_SAND:

                                desc = BitmapDescriptorFactory.fromResource(R.drawable.green_crap);
                                break;
                            case Constants.MODERATELY_MODIFIED_SAND:

                                desc = BitmapDescriptorFactory.fromResource(R.drawable.blue_crap);
                                break;
                            case Constants.LARGELY_MODIFIED_SAND:

                                desc = BitmapDescriptorFactory.fromResource(R.drawable.orange_crap);
                                break;
                            case Constants.CRITICALLY_MODIFIED_SAND:

                                desc = BitmapDescriptorFactory.fromResource(R.drawable.red_crap);
                                break;
                            case Constants.UNMODIFIED_NATURAL_ROCK:

                                desc = BitmapDescriptorFactory.fromResource(R.drawable.purple_crap);
                                break;
                            case Constants.LARGELY_NATURAL_ROCK:

                                desc = BitmapDescriptorFactory.fromResource(R.drawable.green_crap);
                                break;
                            case Constants.MODERATELY_MODIFIED_ROCK:

                                desc = BitmapDescriptorFactory.fromResource(R.drawable.blue_crap);
                                break;
                            case Constants.LARGELY_MODIFIED_ROCK:

                                desc = BitmapDescriptorFactory.fromResource(R.drawable.orange_crap);
                                break;
                            case Constants.CRITICALLY_MODIFIED_ROCK:

                                desc = BitmapDescriptorFactory.fromResource(R.drawable.red_crap);
                                break;

                        }

                    }
                    index++;
                    Log.d(LOG, "" + index);
                    final Marker m = googleMap.addMarker(new MarkerOptions().position(new LatLng(eva.getEvaluationSite().getLatitude(), eva.getEvaluationSite().getLongitude())).icon(desc)
                            .snippet(eva.getRemarks()));
                    m.showInfoWindow();

                    markers.add(m);
                }

            }
            googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {
                    //ensure that all markers in bounds
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    for (Marker marker : markers) {
                        builder.include(marker.getPosition());
                    }

                    LatLngBounds bounds = builder.build();
                    int padding = 10; // offset from edges of the map in pixels
                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                    //   txtCount.setText("" + markers.size());
                    //googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 1.0f));
                    googleMap.animateCamera(cu);
                    setTitle(river.getRiverName());
                }
            });
        }
    }

    private void setGoogleMap() {
        googleMap.setMyLocationEnabled(true);
        googleMap.setBuildingsEnabled(true);
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                location.setLatitude(latLng.latitude);
                location.setLongitude(latLng.longitude);
                Log.w(LOG, "********* onMapClick");
            }
        });
        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.showInfoWindow();
                LatLng latLng = marker.getPosition();
                Location loc = new Location(location);
                loc.setLatitude(latLng.latitude);
                loc.setLongitude(latLng.longitude);

                float f = location.distanceTo(loc);
                Log.w(LOG, "distance" + f);
                try {
                    showPopup(latLng.latitude, latLng.longitude, marker.getTitle() + "\n" + marker.getSnippet());
                } catch (Exception e) {
                    Log.w(LOG, "{0}",e);
                }
                return true;
            }
        });

    }

    List<String> list;

    private void showPopup(final double lat, final double lng, String title) {
        list = new ArrayList<>();
        list.add("Directions");
        list.add("Status Report");

        Util.showPopupBasicWithHeroImage(ctx, this, list, topLayout, ctx.getString(R.string.select_action), new Util.UtilPopupListener() {
            @Override
            public void onItemSelected(int index) {
                switch (index) {
                    case 0:
                        startDirectionsMap(lat, lng);
                        break;
                    case 1:
                        //Util.showToast(ctx, ctx.getString(R.string.under_cons));
                        break;
                    case 2:

                        break;

                }
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        // setUpMapIfNeeded();
    }

    private void startDirectionsMap(double lat, double lng) {
        Log.i(LOG, "startDirectionsMap ..........");
        String url = "http://maps.google.com/maps?saddr="
                + location.getLatitude() + "," + location.getLongitude()
                + "&daddr=" + lat + "," + lng + "&mode=driving";
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(url));
        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
        startActivity(intent);
    }


    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMarker();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMarker() {
        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
    }

    @Override
    public void onLocationChanged(Location location) {

    }


    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!mResolvingError) {  // more about this later
            //mGoogleApiClient.connect();
            mLocationClient.connect();
        }

    }

    @Override
    protected void onStop() {
        try {
            //mGoogleApiClient.disconnect();
            mLocationClient.disconnect();
        } catch (Exception e) {
            Log.e(LOG, "Failed to Stop something", e);
        }
        super.onStop();
    }

    @Override
    public void onConnected(Bundle bundle) {
        location = mLocationClient.getLastLocation();

        locationRequest = new LocationRequest();
        locationRequest.setFastestInterval(FIVE_MINUTES);
        locationRequest.setInterval(ONE_MINUTE);

        mLocationClient.requestLocationUpdates(locationRequest, this);
    }

    @Override
    public void onDisconnected() {

    }


    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (mResolvingError) {
            // Already attempting to resolve an error.
            return;
        } else if (result.hasResolution()) {
            try {
                mResolvingError = true;
                result.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) {
                // There was an error with the resolution intent. Try again.
                mGoogleApiClient.connect();
            }
        } else {
            // Show dialog using GooglePlayServicesUtil.getErrorDialog()

            mResolvingError = true;
        }
    }
}

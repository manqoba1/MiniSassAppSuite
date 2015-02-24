package com.sifiso.codetribe.minisasslibrary.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Location;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.internal.no;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sifiso.codetribe.minisasslibrary.R;
import com.sifiso.codetribe.minisasslibrary.dto.EvaluationDTO;
import com.sifiso.codetribe.minisasslibrary.dto.EvaluationImageDTO;
import com.sifiso.codetribe.minisasslibrary.dto.TeamDTO;
import com.sifiso.codetribe.minisasslibrary.dto.TeamMemberDTO;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.ImagesDTO;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.ResponseDTO;
import com.sifiso.codetribe.minisasslibrary.util.ImageLocation;
import com.sifiso.codetribe.minisasslibrary.util.ImageUtil;
import com.sifiso.codetribe.minisasslibrary.util.PMException;
import com.sifiso.codetribe.minisasslibrary.util.PhotoCacheUtil;
import com.sifiso.codetribe.minisasslibrary.util.PhotoUploadService;
import com.sifiso.codetribe.minisasslibrary.util.SharedUtil;
import com.sifiso.codetribe.minisasslibrary.util.Util;

import org.acra.ACRA;
import org.joda.time.DateTime;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PictureActivity extends ActionBarActivity implements LocationListener,
        GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener{

    LocationRequest mLocationRequest;
    LocationClient mLocationClient;
    LayoutInflater inflater;
    File photoFile, currentThumbFile;
    Uri thumbUri, fileUri;
    Bitmap bitmapForScreen;
    Menu mMenu;
    int type;
    boolean pictureChanged;
    Context ctx;
    public static final int CAPTURE_IMAGE = 1001;
    Location location;
    ImageLocation imageLocation;
    static final float ACCURACY_THRESHOLD = 25;
    ImagesDTO images;
    EvaluationImageDTO evaluationImage;
    EvaluationDTO evaluation;
    TeamMemberDTO teamMember;
    TeamDTO team;
    static final long ONE_MINUTE = 1000 * 60 * 60;


    boolean isUploaded, mBound;
    String mCurrentPhotoPath;
    public final String LOG = PictureActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG, "onCreate FIRED");
        ctx = getApplicationContext();
        inflater = getLayoutInflater();
        setContentView(R.layout.camera);
        setFields();
        mLocationClient = new LocationClient(getApplicationContext(), this, this);
        //getting objects
        if (savedInstanceState != null) {
            Log.e(LOG, "savedInstanceState, is now LOADED");
            type = savedInstanceState.getInt("type", 0);
            evaluationImage = (EvaluationImageDTO) savedInstanceState.getSerializable("evaluationImage");
            evaluation = (EvaluationDTO) savedInstanceState.getSerializable("Evaluation");
            String path = savedInstanceState.getString("photoFile");

            if (path != null) {
                photoFile = new File(path);
            }
            double lat = savedInstanceState.getDouble("latitude");
            double lng = savedInstanceState.getDouble("longitude");
            float acc = savedInstanceState.getFloat("accuracy");
            location = new Location(LocationManager.GPS_PROVIDER);
            location.setLatitude(lat);
            location.setLongitude(lng);
            location.setAccuracy(acc);
            Log.w(LOG, "location accuracy saved:" + acc);
        } else {
            type = getIntent().getIntExtra("type", 0);
            evaluation = (EvaluationDTO) getIntent().getSerializableExtra("evaluation");
            evaluationImage = (EvaluationImageDTO) getIntent().getSerializableExtra("evaluationImage");
            }
        setTitle("Evaluation Image");
        if (evaluationImage != null) {
            getSupportActionBar().setSubtitle(evaluationImage.getFileName());
            if (evaluation != null) {
                getSupportActionBar().setSubtitle(evaluation.getRemarks());
            }
        }

    }
    ActionBarActivity activity;
    Button btnStart;
    View gpsStatus, topLayout;
    TextView txtMsg, txtAccuracy;
    Chronometer chrono;
    LinearLayout imageContainerLayout;
    ProgressBar progressBar;
    ImageView imgCamera;
    Bitmap getBitmapForScreen;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //Intent is handled by camera activity
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.e(LOG, "FAILED to createImageFile");
                Util.showErrorToast(ctx, getString(R.string.file_error));
                return;
            }
            if (photoFile != null) {
                Log.w(LOG, "start picture intent (dispatchTakePictureIntent)");
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, CAPTURE_IMAGE);
            }

        }

    }

    private void setFields() {
        activity = this;
        gpsStatus = findViewById(R.id.CAM_gpsStatus);
        chrono = (Chronometer) findViewById(R.id.CAM_chrono);
        txtMsg = (TextView) findViewById(R.id.CAM_message);
        progressBar = (ProgressBar) findViewById(R.id.CAM_progressBar);
        imageContainerLayout = (LinearLayout) findViewById(R.id.CAM_imageContainer);
        txtAccuracy = (TextView) findViewById(R.id.CAM_accuracy);
        btnStart = (Button) findViewById(R.id.CAM_btnStart);
        topLayout =  findViewById(R.id.CAM_topLayout);
        imgCamera = (ImageView) findViewById(R.id.CAM_imgCamera);
        imgCamera.setVisibility(View.GONE);
        topLayout.setVisibility(View.GONE);
        gpsStatus.setVisibility(View.GONE);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.flashOnce(btnStart, 200, new Util.UtilAnimationListener() {
                    @Override
                    public void onAnimationEnded() {
                        showReminderDialog();
                        btnStart.setVisibility(View.GONE);
                    }
                });

            }
        });

        imgCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.flashOnce(imgCamera, 200, new Util.UtilAnimationListener() {
                    @Override
                    public void onAnimationEnded() {
                        topLayout.setVisibility(View.GONE);
                        gpsStatus.setVisibility(View.GONE);
                        dispatchTakePictureIntent();
                    }
                });

            }
        });
    }



    private File createImageFile() throws IOException {
        //creating image file names

        String imageFileName = "pic" + System.currentTimeMillis();
        switch (type) {
            case ImagesDTO.EVALUATION_IMAGE:
                imageFileName = "evaluationImage" + evaluationImage.getFileName();
                break;
            case ImagesDTO.TEAM_MEMBER_IMAGE:
                imageFileName = teamMember.getFirstName() +
                        teamMember.getLastName();
                break;
            case ImagesDTO.TEAM_IMAGE:
                imageFileName = team.getTeamName();
                break;

        }
        imageFileName = imageFileName + "_" + System.currentTimeMillis();
        File root;
        if (Util.hasStorage(true)) {
            Log.i(LOG, "fetch file from getExternalStoragePublicDirectory");
            root = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES
            );
        } else {
            Log.i(LOG, "fetch file from getDataDirectory");
            root = Environment.getDataDirectory();
        }
        File pics = new File(root, "minisass_app");
        if (!pics.exists()) {
            pics.mkdir();
        }
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                pics
        );

        //Saving file:path for use with Action_VIEW intents
        mCurrentPhotoPath = "file: " + image.getAbsolutePath();
        return image;
    }

    boolean confirmedStandingAtLocation;




    private void showReminderDialog() {
        AlertDialog.Builder d = new AlertDialog.Builder(this);
        d.setTitle("Image Location Reminder")
                .setMessage("Are you ready to start taking images at this location?")
                .setPositiveButton(ctx.getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        imgCamera.setVisibility(View.GONE);
                        topLayout.setVisibility(View.VISIBLE);
                        gpsStatus.setVisibility(View.VISIBLE);
                        confirmedStandingAtLocation = true;
                        chrono.start();
                        try {
                            if (mLocationClient.isConnected()) {
                                mLocationClient.requestLocationUpdates(mLocationRequest, (LocationListener) activity);
                            }
                        } catch (Exception e) {
                            Log.e(LOG, "could not connect");
                        }
                    }
                })
                .setNegativeButton(ctx.getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .show();
    }
    PhotoUploadService mService;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.w(LOG, "PhotoUploadService ServiceConnection onServiceConnected");
            PhotoUploadService.LocalBinder binder = (PhotoUploadService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
            mService.UploadCachedPhotos(new PhotoUploadService.UploadListener() {
                @Override
                public void onUploadsComplete(int count) {
                    Log.w(LOG, " onUploadsComplete, list: " + count);
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName argO) {
            Log.w(LOG, "PhotoUploadService onServiceDisconnected");
            mBound = false;
        }
    };

    List<String> currentSessionPhotos = new ArrayList<>();

    @Override
    public void onPause() {
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        if (isUploaded) {
            Log.d(LOG, "picture has been uploaded, onBackPressed");
            ResponseDTO r = new ResponseDTO();
            r.setEvaluationImageFileName(currentSessionPhotos);
            Intent i = new Intent();
          //  i.putExtra("response", r);
            setResult(RESULT_OK, i);
        } else {
            Log.d(LOG, "onBackPressed has been cancelled");
            setResult(RESULT_CANCELED);
        }
        finish();
    }

    @Override
    public void onSaveInstanceState(Bundle b) {
        Log.e(LOG, "onSaveInstanceState");
        b.putInt("type", type);
        if (currentThumbFile != null){
            b.putString("thumbPath", currentThumbFile.getAbsolutePath());
        }
        if (photoFile != null) {
            b.putString("photoFile", photoFile.getAbsolutePath());
        }
        if (evaluationImage != null) {
            b.putSerializable("evaluationImage", evaluationImage);
        }
        if (evaluation != null){
        b.putSerializable("evaluation", evaluation);
        }
        if (location != null) {
            b.putDouble("latitude", location.getLatitude());
            b.putDouble("longitude", location.getLongitude());
            b.putFloat("accuracy", location.getAccuracy());
        }
        super.onSaveInstanceState(b);
    }

    class PhotoTask extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... voids) {
            Log.w(LOG, "starting PhotoTask doInBackground, file length: " + photoFile.length());
            pictureChanged = false;
            ExifInterface exif = null;
            if (photoFile == null || photoFile.length() == 0) {
                Log.e(LOG, "photoFile is null or length = 0, leaving");
                return 99;
            }
            fileUri = Uri.fromFile(photoFile);
            if (fileUri != null) {
                try {
                    exif = new ExifInterface(photoFile.getAbsolutePath());
                    String orient = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
                    Log.i(LOG, "ORIENTATION: " + orient);
                    float rotate = 0f;
                    if (orient.equalsIgnoreCase("6")) {
                        rotate = 90f;
                        Log.i(LOG, "picture rotate: " + rotate);
                    }
                    try {
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inSampleSize = 2;
                        Bitmap bm = BitmapFactory.decodeFile(photoFile.getAbsolutePath(), options);
                        getLog(bm, "Raw Camera - sample size = 2");
                        Matrix matrixThumbnail = new Matrix();
                        matrixThumbnail.postScale(0.4f, 0.4f);
                        Bitmap thumb = Bitmap.createBitmap
                                (bm, 0, 0, bm.getWidth(), bm.getHeight(), matrixThumbnail, true);
                        getLog(thumb, "thumb");

                        thumb = ImageUtil.drawTextToBitmap(ctx, thumb, location);
                        //appending date & gps co-ordinates to bitmap

                        currentThumbFile = ImageUtil.getFileFromBitmap(thumb, "t" + System.currentTimeMillis() + ".jpg");
                        bitmapForScreen = ImageUtil.getBitmapFromUri(ctx, Uri.fromFile(currentThumbFile));

                        thumbUri = Uri.fromFile(currentThumbFile);

                        //writing exif data
                        Util.writeLocationToExif(currentThumbFile.getAbsolutePath(), location);
                        boolean del = photoFile.delete();
                        Log.i(LOG, "Thumbnail file length: " + currentThumbFile.length() +
                        " image file deleted: " + del);
                    } catch (Exception e) {
                        Log.e(LOG, "unable to process bitmap", e);
                        return 9;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return 1;
                }
            }
            return 0;
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (result > 0) {
                Util.showErrorToast(ctx, getString(R.string.camera_error));
                return;
            }
            if (thumbUri != null) {
                pictureChanged = true;
                try {
                    isUploaded = true;
                    currentSessionPhotos.add(Uri.fromFile(currentThumbFile).toString());
                    addImageToScroller();

                } catch (Exception e){
                    e.printStackTrace();
                }

            }
        }
    }

    private void addImageToScroller() {
        Log.i(LOG, "addImageToScroller");
        if (currentSessionPhotos.size() == 1) {
            imageContainerLayout.removeAllViews();
        }
        View v = inflater.inflate(R.layout.scroller_image_template, null);
        ImageView img = (ImageView) v.findViewById(R.id.image);
        TextView num = (TextView) v.findViewById(R.id.number);
        num.setText(" "+ currentSessionPhotos.size());
        Uri uri = Uri.fromFile(currentThumbFile);
        ImageLoader.getInstance().displayImage(uri.toString(), img);
        imageContainerLayout.addView(v, 0);
        btnStart.setVisibility(View.VISIBLE);
        imgCamera.setVisibility(View.VISIBLE);
        uploadPhotos();
    }

    private void getLog(Bitmap bm, String which) {
        if (bm == null) return;
        Log.e(LOG, which + " - bitmap: width: "
                + bm.getWidth() + " height: "
                + bm.getHeight() + " rowBytes: "
                + bm.getRowBytes());
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.e(LOG, "onRestoreInstanceState" + savedInstanceState);
        type = savedInstanceState.getInt("type", 0);
        evaluationImage = (EvaluationImageDTO) savedInstanceState.getSerializable("evaluationImage");
        evaluation = (EvaluationDTO) savedInstanceState.getSerializable("evaluation");
        String path = savedInstanceState.getString("photoFile");
        if (path != null) {
            photoFile = new File(path);
        }

        double lat = savedInstanceState.getDouble("latitude");
        double lng = savedInstanceState.getDouble("longitude");
        float acc = savedInstanceState.getFloat("accuracy");
        location = new Location(LocationManager.GPS_PROVIDER);
        location.setLatitude(lat);
        location.setLongitude(lng);
        location.setAccuracy(acc);
        Log.w(LOG, "location accuracy saved: " + acc);
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onResume() {
        Log.d(LOG, "onResume FIRED");
        imageLocation = SharedUtil.getImageLocation(ctx);
        if (imageLocation != null) {
            Log.e(LOG, "image: " + imageLocation.getEvaluationImageID() + " " +
                    imageLocation.getAccuracy() + " _ " + imageLocation.getDateTaken().toString());
            if (evaluationImage.getEvaluationImageID().intValue() != evaluationImage.getEvaluationImageID().intValue()) {
                imageLocation = null;
            } else {
                DateTime dateTime = new DateTime();
                DateTime then = new DateTime(imageLocation.getDateTaken().getTime());
                long time = dateTime.toDate().getTime() - then.toDate().getTime();
                if (time > ONE_MINUTE) {
                    imageLocation = null;
                }
            }
        }
        super.onResume();
    }

    private void uploadPhotos() {
        Log.e(LOG,"uploadPhoto accuracy: " + location.getAccuracy());
        switch (type) {
            case ImagesDTO.TEAM_IMAGE:
                addTeamPicture( new CacheListener() {
                    @Override
                    public void onCachingDone() {
                        mService.UploadCachedPhotos(null);
                    }
                });
                break;
            case ImagesDTO.EVALUATION_IMAGE:
                addEvaluationImagePicture(new CacheListener() {
                    @Override
                    public void onCachingDone() {
                        mService.UploadCachedPhotos(null);
                    }
                });
                break;
            case ImagesDTO.TEAM_MEMBER_IMAGE:
                addTeamMemberPicture( new CacheListener() {
                    @Override
                    public void onCachingDone() {
                        mService.UploadCachedPhotos(null);
                    }
                });
                break;
        }
    }
    private interface CacheListener {
        public void onCachingDone();
    }

    private ImagesDTO getObject() {
        ImagesDTO img = new ImagesDTO();
        img.setEvaluationID(evaluation.getEvaluationID());
        img.setDateTaken(new Date());
        img.setLongitude(location.getLongitude());
        img.setLatitude(location.getLatitude());
        img.setAccuracy(location.getAccuracy());
        img.setThumbFlag(1);

        return img;

    }

    private void addTeamMemberPicture(final CacheListener listener) {
        Log.w(LOG, "attempting to addTeamMemberEvaluation");
        final ImagesDTO img = new ImagesDTO();
        img.setTeamMemberID(teamMember.getTeamMemberID());
        img.setPictureType(ImagesDTO.TEAM_MEMBER_IMAGE);
        img.setThumbFilePath(currentThumbFile.getAbsolutePath());
        img.setTeamMemberPicture(true);
        PhotoCacheUtil.cachePhoto(ctx, images, new PhotoCacheUtil.PhotoCacheListener() {
            @Override
            public void onFileDataDeserialized(ResponseDTO response) {

            }

            @Override
            public void onDataCached() {
                Log.w(LOG, "photo cached");
                listener.onCachingDone();
            }

            @Override
            public void onError() {
                Util.showErrorToast(ctx, getString(R.string.photo_error));
            }
        });
    }

    public void addEvaluationImagePicture(final CacheListener listener) {
        Log.w(LOG, "attempting to addEvaluationImagePicture");
        final ImagesDTO img = new ImagesDTO();
        img.setEvaluationImageID(evaluationImage.getEvaluationImageID());
        img.setPictureType(ImagesDTO.EVALUATION_IMAGE);
        img.setThumbFilePath(currentThumbFile.getAbsolutePath());
        img.setEvaluationImagePicture(true);
        PhotoCacheUtil.cachePhoto(ctx, images, new PhotoCacheUtil.PhotoCacheListener() {
            @Override
            public void onFileDataDeserialized(ResponseDTO response) {

            }

            @Override
            public void onDataCached() {
            Log.w(LOG, "photo cached");
                listener.onCachingDone();
            }

            @Override
            public void onError() {
                Util.showErrorToast(ctx, getString(R.string.photo_error));
            }
        });
    }

    public void addTeamPicture(final CacheListener listener) {
        Log.w(LOG, "attempting to addTeamPicture");
        final ImagesDTO img = getObject();
        img.setTeamID(team.getTeamID());
        img.setPictureType(ImagesDTO.TEAM_IMAGE);
        img.setThumbFilePath(currentThumbFile.getAbsolutePath());
        img.setTeamPicture(true);
        PhotoCacheUtil.cachePhoto(ctx, images, new PhotoCacheUtil.PhotoCacheListener() {
            @Override
            public void onFileDataDeserialized(ResponseDTO response) {

            }

            @Override
            public void onDataCached() {
            Log.w(LOG, "photo cached: ");
                listener.onCachingDone();
            }

            @Override
            public void onError() {
            Util.showErrorToast(ctx, getString(R.string.photo_error));
            }
        });
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode,
                                 final Intent data) {
        Log.e(LOG, "onActivityResult requestCode: " + requestCode + "resultCode" + requestCode);
        switch (requestCode) {
            case CAPTURE_IMAGE:
                if (resultCode == Activity.RESULT_OK) {
                    if (photoFile != null) {
                        Log.e(LOG, "photo file length: " + photoFile.length());
                        new PhotoTask().execute();
                    }
                }
                break;

        }
    pictureChanged = true;


    }

    @Override
    public void onStart() {
        Log.i(LOG, "onStart - Connecting locationClient ");
        if (mLocationClient != null) {
            if (location == null) {
                mLocationClient.connect();
            }
        }
        Log.i(LOG, "onStart Bind to PhotoUploadService");
        Intent intent = new Intent(this, PhotoUploadService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();

        if (mLocationClient != null) {
            mLocationClient.disconnect();
            Log.e(LOG, "onStop - LocationClient disconnecting");
        }
        Log.e(LOG, "onStop unBind from PhotoUploadService");
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.camera, menu);
        mMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.


        if (item.getItemId() == R.id.menu_camera) {
            dispatchTakePictureIntent();
            return true;
        }
       if (item.getItemId() == R.id.menu_gallery) {
            Util.showToast(ctx, "Still constructing");
            return true;
        }

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return false;
    }


    @Override
    public void onConnected(Bundle bundle) {
        Log.i(LOG,
                "+++ LocationClient onConnected() -  requestLocationUpdates ...");
        if (imageLocation != null) {
            Log.w(LOG,"## already have a good location, returning");
            location = mLocationClient.getLastLocation();
            location.setAccuracy(imageLocation.getAccuracy());
            location.setLatitude(imageLocation.getLatitude());
            location.setLongitude(imageLocation.getLongitude());
            topLayout.setVisibility(View.GONE);
            gpsStatus.setVisibility(View.GONE);
            imgCamera.setVisibility(View.VISIBLE);
            btnStart.setVisibility(View.GONE);
            dispatchTakePictureIntent();
            return;
        }
        Log.w(LOG,"## requesting location updates ....");
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setFastestInterval(500);

        location = mLocationClient.getLastLocation();
        mLocationClient.requestLocationUpdates(mLocationRequest, this);


    }

    @Override
    public void onDisconnected() {
        Log.e(LOG, "onDisconnected FIRED");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        ACRA.getErrorReporter().handleSilentException(new PMException(
                "Google LocationClient onConnectionFailed: " + connectionResult.getErrorCode()
        ));
    }

    @Override
    public void onLocationChanged(Location locat) {
        Log.d(LOG, "onLocationChanged accuracy:" + locat.getAccuracy());
        txtAccuracy.setText(" " + locat.getAccuracy());
        Util.flashSeveralTimes(txtAccuracy, 200, 2, null);
        if (this.location == null) {
            this.location = locat;
        }
        if (locat.getAccuracy() <= ACCURACY_THRESHOLD) {
            this.location = locat;
            mLocationClient.removeLocationUpdates(this);
            chrono.stop();
            SharedUtil.saveImageLocation(ctx, evaluationImage, location);
            if (confirmedStandingAtLocation) {
                progressBar.setVisibility(View.GONE);
                txtMsg.setText(getString(R.string.gps_complete));
                imgCamera.setVisibility(View.VISIBLE);
                Util.flashSeveralTimes(imgCamera, 200, 3, null);
            }
        }

    }
}

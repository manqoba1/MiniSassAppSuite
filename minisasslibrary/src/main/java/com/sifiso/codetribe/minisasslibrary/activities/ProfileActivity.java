package com.sifiso.codetribe.minisasslibrary.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.graphics.Palette;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.sifiso.codetribe.minisasslibrary.R;
import com.sifiso.codetribe.minisasslibrary.adapters.TeamMemberAdapter;
import com.sifiso.codetribe.minisasslibrary.dialogs.AddMemberDialog;
import com.sifiso.codetribe.minisasslibrary.dto.TeamMemberDTO;
import com.sifiso.codetribe.minisasslibrary.dto.TmemberDTO;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.RequestDTO;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.ResponseDTO;
import com.sifiso.codetribe.minisasslibrary.listeners.BitmapListener;
import com.sifiso.codetribe.minisasslibrary.toolbox.BaseVolley;
import com.sifiso.codetribe.minisasslibrary.toolbox.WebCheck;
import com.sifiso.codetribe.minisasslibrary.toolbox.WebCheckResult;
import com.sifiso.codetribe.minisasslibrary.util.Bitmaps;
import com.sifiso.codetribe.minisasslibrary.util.CloudinaryUtil;
import com.sifiso.codetribe.minisasslibrary.util.ErrorUtil;
import com.sifiso.codetribe.minisasslibrary.util.ImageTask;
import com.sifiso.codetribe.minisasslibrary.util.ImageUtil;
import com.sifiso.codetribe.minisasslibrary.util.PictureUtil;
import com.sifiso.codetribe.minisasslibrary.util.SharedUtil;
import com.sifiso.codetribe.minisasslibrary.util.Statics;
import com.sifiso.codetribe.minisasslibrary.util.ToastUtil;
import com.sifiso.codetribe.minisasslibrary.util.Util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProfileActivity extends ActionBarActivity {

    TextView P_TNAME, P_name, P_phone, P_email, P_EVN_count, textView7;
    Spinner P_sp_team;
    ImageView AP_PP, P_ICON, P_edit;
    ListView P_membersList;
    Button P_add_member, P_inviteMember;
    Context ctx;
    private TeamMemberDTO teamMember;
    private TeamMemberAdapter teamMemberAdapter;
    static String LOG = ProfileActivity.class.getSimpleName();
    private AddMemberDialog addMemberDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ctx = getApplicationContext();
        teamMember = SharedUtil.getTeamMember(ctx);
        Log.d(LOG, new Gson().toJson(teamMember));
        setFields();
        //updateProfilePicture(teamMember);
    }

    private void checkConnection() {
        WebCheckResult wcr = WebCheck.checkNetworkAvailability(ctx, true);
        if (wcr.isWifiConnected()) {
            P_ICON.setVisibility(View.VISIBLE);

        }
    }

    private void setFields() {
        P_name = (TextView) findViewById(R.id.P_name);
        textView7 = (TextView) findViewById(R.id.textView7);
        P_phone = (TextView) findViewById(R.id.P_phone);
        P_email = (TextView) findViewById(R.id.P_email);
        P_EVN_count = (TextView) findViewById(R.id.P_EVN_count);
        AP_PP = (ImageView) findViewById(R.id.AP_PP);
        AP_PP.setDrawingCacheEnabled(true);
        P_TNAME = (TextView) findViewById(R.id.P_TNAME);
        P_edit = (ImageView) findViewById(R.id.P_edit);
        P_ICON = (ImageView) findViewById(R.id.P_ICON);
        P_membersList = (ListView) findViewById(R.id.P_membersList);
        P_add_member = (Button) findViewById(R.id.P_add_member);
        P_inviteMember = (Button) findViewById(R.id.P_inviteMember);
        P_sp_team = (Spinner) findViewById(R.id.P_sp_team);

        P_name.setText(teamMember.getFirstName() + " " + teamMember.getLastName());
        P_phone.setText((teamMember.getCellphone().equals("") ? "cell not specified" : teamMember.getCellphone()));
        P_email.setText(teamMember.getEmail());
        P_EVN_count.setText(teamMember.getEvaluationCount() + "");
        P_TNAME.setText("Team " + teamMember.getTeam().getTeamName());
        P_TNAME.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.flashOnce(P_TNAME, 200, new Util.UtilAnimationListener() {
                    @Override
                    public void onAnimationEnded() {
                        setTeamMemberList(teamMember.getTeam().getTeammemberList());
                        // ToastUtil.toast(ctx,"My team members");
                        setSpinner();
                    }
                });

            }
        });
        setSpinner();

        if (teamMember.getTeamMemberImage() == null) {
            AP_PP.setImageDrawable(ctx.getResources().getDrawable(R.drawable.boy));
        } else {
            ImageLoader.getInstance().displayImage(teamMember.getTeamMemberImage(), AP_PP);
            ImageLoader.getInstance().displayImage(teamMember.getTeamMemberImage(), AP_PP, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String s, View view) {

                }

                @Override
                public void onLoadingFailed(String s, View view, FailReason failReason) {

                }

                @Override
                public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                    Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                        @Override
                        public void onGenerated(Palette palette) {
                            Palette.Swatch aSwitch = palette.getMutedSwatch();
                            try {
                                textView7.setTextColor(aSwitch.getRgb());
                            } catch (Exception e) {

                            }
                        }
                    });
                }

                @Override
                public void onLoadingCancelled(String s, View view) {

                }
            });
        }

        P_add_member.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addMemberDialog = new AddMemberDialog();
                addMemberDialog.show(getFragmentManager(), LOG);
                addMemberDialog.setTeamMember(teamMember);
                addMemberDialog.setFlag(false);
                addMemberDialog.setListener(new AddMemberDialog.AddMemberDialogListener() {
                    @Override
                    public void membersToBeRegistered(TeamMemberDTO tm) {
                        registerMember(tm);
                    }
                });
            }
        });
        P_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMemberDialog = new AddMemberDialog();
                addMemberDialog.show(getFragmentManager(), LOG);
                addMemberDialog.setTeamMember(teamMember);
                addMemberDialog.setFlag(true);
                addMemberDialog.setListener(new AddMemberDialog.AddMemberDialogListener() {
                    @Override
                    public void membersToBeRegistered(TeamMemberDTO tm) {
                        updateProfilePicture(tm);
                    }
                });
            }
        });
        P_inviteMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtil.errorToast(ctx, "Site Under Construction");
            }
        });
        AP_PP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImageSource();
            }
        });

        setTeamMemberList(teamMember.getTeam().getTeammemberList());
    }

    private void setSpinner() {
        List<String> tNames = new ArrayList<>();
        tNames.add("Tap to see teams you invited to");
        for (TmemberDTO t : teamMember.getTmemberList()) {
            tNames.add(t.getTeam().getTeamName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(ctx, R.layout.xxsimple_spinner_dropdown_item, tNames);
        P_sp_team.setAdapter(adapter);
        P_sp_team.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    return;
                }
            //    setTeamMemberList(teamMember.getTmemberList().get(position - 1).getTeam().getTeammemberList());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void updateProfilePicture(TeamMemberDTO teamMember) {
        Log.e(LOG, new Gson().toJson(teamMember));
        RequestDTO w = new RequestDTO();
        w.setRequestType(RequestDTO.UPDATE_PROFILE);
        w.setTeamMember(teamMember);
        BaseVolley.getRemoteData(Statics.SERVLET_TEST, w, ctx, new BaseVolley.BohaVolleyListener() {
            @Override
            public void onResponseReceived(final ResponseDTO r) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.i(LOG + "Check", new Gson().toJson(r));
                        SharedUtil.saveTeamMember(ctx, r.getTeamMember());
                    }
                });
            }

            @Override
            public void onVolleyError(VolleyError error) {

            }
        });
    }

    private void setTeamMemberList(List<TeamMemberDTO> teamMemberList) {
        teamMemberAdapter = new TeamMemberAdapter(ctx, R.layout.team_member_item, teamMemberList);
        P_membersList.setAdapter(teamMemberAdapter);
    }

    private void registerMember(TeamMemberDTO dto) {
        RequestDTO w = new RequestDTO();
        w.setRequestType(RequestDTO.REGISTER_TEAM_MEMBER);
        w.setTeamMember(dto);

        BaseVolley.getRemoteData(Statics.SERVLET_TEST, w, ctx, new BaseVolley.BohaVolleyListener() {
            @Override
            public void onResponseReceived(ResponseDTO response) {
                if (ErrorUtil.checkServerError(ctx, response)) {

                }
                SharedUtil.saveTeamMember(ctx, response.getTeamMember());
                teamMember = response.getTeamMember();
                setTeamMemberList(teamMember.getTeam().getTeammemberList());
            }

            @Override
            public void onVolleyError(VolleyError error) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        overridePendingTransition(com.sifiso.codetribe.minisasslibrary.R.anim.slide_in_left, com.sifiso.codetribe.minisasslibrary.R.anim.slide_out_right);
        //  TimerUtil.killFlashTimer();
        super.onDestroy();
    }

    @Override
    public void onPause() {
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        super.onPause();
    }

    /**
     * Start the device camera
     */
    private void startCameraIntent() {
        fileUri = PictureUtil.getImageFileUri();
        Intent cameraIntent = PictureUtil.getCameraIntent(AP_PP, fileUri);
        startActivityForResult(cameraIntent, CAPTURE_IMAGE);
    }

    private void startGalleryIntent() {
        fileUri = PictureUtil.getImageFileUri();
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE);
    }

    private void uploadProfileImageToCDN(final TeamMemberDTO teamMember, File imgUrl) {
        Log.e(LOG, imgUrl.toString());
        CloudinaryUtil.uploadImagesToCDN(ctx, imgUrl, new CloudinaryUtil.CloudinaryUtilListner() {
            @Override
            public void onSuccessUpload(Map uploadResult) {
                pictureChanged = false;
                String url = (String) uploadResult.get("url");
                teamMember.setTeamMemberImage(url);
                SharedUtil.saveTeamMember(ctx, teamMember);
                updateProfilePicture(teamMember);
            }

            @Override
            public void onError() {

            }

            @Override
            public void onProgress(Integer upload) {

            }
        });
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode,
                                 final Intent data) {
        switch (requestCode) {
            case CAPTURE_IMAGE:
                if (resultCode == Activity.RESULT_OK) {
                    ImageTask.getResizedBitmaps(fileUri, ctx, new BitmapListener() {
                        @Override
                        public void onError() {
                            ToastUtil.errorToast(
                                    ctx,
                                    ctx.getResources().getString(
                                            R.string.error_image_get));
                        }

                        @Override
                        public void onBitmapsResized(Bitmaps bitmaps) {
                            AP_PP.setImageBitmap(bitmaps.getLargeBitmap());
                            // Bitmap bitmap = AP_PP.getDrawingCache();
                            Palette.from(bitmaps.getLargeBitmap()).generate(new Palette.PaletteAsyncListener() {
                                @Override
                                public void onGenerated(Palette palette) {
                                    Palette.Swatch aSwitch = palette.getMutedSwatch();

                                    try {
                                        textView7.setTextColor(aSwitch.getRgb());
                                    } catch (Exception e) {

                                    }
                                }
                            });
                            try {
                                fImage = ImageUtil.getFileFromBitmap(bitmaps.getLargeBitmap(), "picM" + System.currentTimeMillis() + ".jpg");
                                uploadProfileImageToCDN(teamMember, fImage);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    pictureChanged = true;
                }
                break;
            case PICK_IMAGE:
                if (resultCode == Activity.RESULT_OK) {
                    final Uri imageUri = data.getData();
                    ImageTask.getResizedBitmaps(imageUri, ctx,
                            new BitmapListener() {

                                @Override
                                public void onError() {
                                    ToastUtil.errorToast(ctx, ctx.getResources()
                                            .getString(R.string.error_image_get));
                                }

                                @Override
                                public void onBitmapsResized(Bitmaps bitmaps) {
                                    AP_PP.setImageBitmap(bitmaps.getLargeBitmap());
                                    Palette.from(bitmaps.getLargeBitmap()).generate(new Palette.PaletteAsyncListener() {
                                        @Override
                                        public void onGenerated(Palette palette) {
                                            Palette.Swatch aSwitch = palette.getMutedSwatch();

                                            try {
                                                textView7.setTextColor(aSwitch.getRgb());
                                            } catch (Exception e) {

                                            }
                                        }
                                    });
                                    try {
                                        fImage = ImageUtil.getFileFromBitmap(bitmaps.getLargeBitmap(), "picM" + System.currentTimeMillis() + ".jpg");
                                        uploadProfileImageToCDN(teamMember, fImage);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                }
                            });

                    pictureChanged = true;

                } else {
                    ToastUtil.toast(
                            ctx,
                            ctx.getResources().getString(
                                    R.string.image_pick_cancelled));
                }
                break;
        }
        //TODO not belonging here


        super.onActivityResult(requestCode, resultCode, data);
    }

    File fImage;

    private void selectImageSource() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
        builder.setTitle(ctx.getResources().getString(R.string.image_source));
        builder.setItems(
                new CharSequence[]{
                        ctx.getResources().getString(R.string.gallery),
                        ctx.getResources().getString(R.string.camera)},
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                startGalleryIntent();
                                break;
                            case 1:
                                startCameraIntent();
                                break;

                            default:
                                break;
                        }

                    }
                });

        builder.show();
    }

    boolean pictureChanged;
    static final int CAPTURE_IMAGE = 3, PICK_IMAGE = 4;
    private static Uri fileUri;
}

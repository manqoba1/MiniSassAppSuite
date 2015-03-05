package com.sifiso.codetribe.minisasslibrary.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.sifiso.codetribe.minisasslibrary.R;
import com.sifiso.codetribe.minisasslibrary.adapters.PictureRecyclerAdapter;
import com.sifiso.codetribe.minisasslibrary.dto.EvaluationDTO;
import com.sifiso.codetribe.minisasslibrary.dto.EvaluationImageDTO;
import com.sifiso.codetribe.minisasslibrary.dto.EvaluationSiteDTO;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.ImagesDTO;
import com.sifiso.codetribe.minisasslibrary.util.DividerItemDecoration;
import com.sifiso.codetribe.minisasslibrary.util.Util;

import java.util.ArrayList;
import java.util.List;

public class PictureRecyclerGridActivity extends ActionBarActivity {

    RecyclerView list;
    TextView title;
    Context ctx;
    EvaluationImageDTO evaluationImage;
    EvaluationDTO evaluation;
    EvaluationSiteDTO evaluationSite;
    Button RCVbtn;
    PictureRecyclerAdapter adapter;
   // List<ImagesDTO> imagesList;
    List<EvaluationImageDTO> imagesList;
    Activity activity;
    private List<ImagesDTO> imagesForDeletion = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctx = getApplicationContext();
        activity = this;
        setContentView(R.layout.activity_picture_recycler_grid);
        list = (RecyclerView) findViewById(R.id.FI_recyclerView);
        title = (TextView) findViewById(R.id.RCV_title);
        RCVbtn = (Button) findViewById(R.id.RCV_btn);

        list.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        list.setItemAnimator(new DefaultItemAnimator());
        list.addItemDecoration(new DividerItemDecoration(ctx, RecyclerView.VERTICAL));


        evaluation = (EvaluationDTO) getIntent().getSerializableExtra("evaluation");
        if (evaluation != null) {
            if (evaluation.getImagesList() == null || evaluation.getImagesList().isEmpty()) {
                Util.showErrorToast(ctx, getString(R.string.no_photos));
                finish();
            }

            adapter = new PictureRecyclerAdapter(evaluation.getImagesList(), 1, ctx, new PictureRecyclerAdapter.PictureListener() {
                @Override
                public void onPictureClicked(int position) {
                    Log.e(LOG, "picture has been clicked,  position: " + position);
                    Intent i = new Intent(getApplicationContext(), FullPhotoActivity.class);
                    i.putExtra("evaluationImage", evaluationImage);
                    startActivity(i);
                }
            });
            list.setAdapter(adapter);
        }
        RCVbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i;
                if (evaluationSite != null) {
                    i = new Intent(ctx, ImageActivity.class);
                    i.putExtra("evaluationSite", evaluationSite);
                    startActivity(i);
                }
            }
        });

    }

    private class TouchListener implements RecyclerView.OnItemTouchListener, View.OnTouchListener {
        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            Log.w(LOG, "FIRED onInterceptTouchEvent: " + e.toString());
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
            Log.w(LOG, "FIRED onTouchEvent: " + e.toString());

        }

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            Log.w(LOG, "FIRED onTouch: " + motionEvent.toString());
            return false;
        }
    }

        @Override
        public boolean onCreateOptionsMenu (Menu menu){
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.menu_picture_recycler_grid, menu);
            return true;
        }

        @Override
        public boolean onOptionsItemSelected (MenuItem item){
            // Handle action bar item clicks here. The action bar will
            // automatically handle clicks on the Home/Up button, so long
            // as you specify a parent activity in AndroidManifest.xml.
            int id = item.getItemId();

            //noinspection SimplifiableIfStatement
            if (id == R.id.action_settings) {
                return true;
            }
            if (id == R.id.action_gallery) {


           /*   PhotoCacheUtil.getCachedPhotos(ctx, new PhotoCacheUtil.PhotoCacheListener() {
                    @Override
                    public void onFileDataDeserialized(ResponseDTO response) {

                    }

                    @Override
                    public void onDataCached() {

                    }

                    @Override
                    public void onError() {

                    }
                }

                );
                Intent i = new Intent(ctx, FullPhotoActivity.class);
                i.putExtra("evaluationImage", evaluationImage);
                startActivity(i);
               */ return true;

            }

            return super.onOptionsItemSelected(item);
        }




    static final String LOG = PictureRecyclerGridActivity.class.getSimpleName();
}

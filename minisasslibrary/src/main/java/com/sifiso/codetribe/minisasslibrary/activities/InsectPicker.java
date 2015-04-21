package com.sifiso.codetribe.minisasslibrary.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.sifiso.codetribe.minisasslibrary.R;
import com.sifiso.codetribe.minisasslibrary.adapters.InsectSelectionAdapter;
import com.sifiso.codetribe.minisasslibrary.dto.InsectImageDTO;
import com.sifiso.codetribe.minisasslibrary.util.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

public class InsectPicker extends ActionBarActivity {
    static final String LOG = InsectPicker.class.getSimpleName();
    Context ctx;
    double total = 0.0;
    private InsectSelectionAdapter adapter;
    private RecyclerView SD_list;

    private List<InsectImageDTO> mSites;
    private List<InsectImageDTO> listCal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insect_picker);
        ctx = getApplicationContext();
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Log.i(LOG,"onCreate select insect");
        mSites = (List<InsectImageDTO>) getIntent().getSerializableExtra("insetImageList");
        Log.i(LOG,"onCreate select insect " + mSites.size());
        setFields();
        setList();
    }

    private void setList() {

        adapter = new InsectSelectionAdapter(ctx, mSites, R.layout.insect_select_item, new InsectSelectionAdapter.InsectPopupAdapterListener() {
            @Override
            public void onInsectSelected(InsectImageDTO insect, int index) {

                collectCheckedInsects(insect);

            }
        });
        SD_list.setAdapter(adapter);
    }

    private void collectCheckedInsects(InsectImageDTO mDtos) {
        if (listCal == null) {
            listCal = new ArrayList<>(mSites.size());
        }


        if (mDtos.selected == true) {
            //if (listCal.contains(mDtos))
            listCal.add(mDtos);
            total = total + mDtos.getSensitivityScore();

        } else {
            mDtos.selected = true;
            listCal.remove(mDtos);
            total = total - mDtos.getSensitivityScore();
            mDtos.selected = false;
        }
        Log.e(LOG, listCal.size() + "");
        intent = new Intent(InsectPicker.this, EvaluationActivity.class);
        intent.putExtra("overallInsect", (java.io.Serializable) mSites);
        intent.putExtra("selectedInsects", (java.io.Serializable) listCal);
        setResult(INSECT_DATA, intent);
        //listener.onSelectDone(listCal);
    }

    Intent intent;
    static final int INSECT_DATA = 103;

    @Override
    public void onBackPressed() {
        intent = new Intent(InsectPicker.this, EvaluationActivity.class);
        intent.putExtra("overallInsect", (java.io.Serializable) mSites);
        intent.putExtra("selectedInsects", (java.io.Serializable) listCal);
        setResult(INSECT_DATA, intent);
        super.onBackPressed();
    }

    private void setFields() {
        SD_list = (RecyclerView) findViewById(R.id.SD_list);
        SD_list.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

        SD_list.setItemAnimator(new DefaultItemAnimator());
        SD_list.addItemDecoration(new DividerItemDecoration(ctx, RecyclerView.HORIZONTAL));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_insect_picker, menu);
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
        if (id == android.R.id.home) {
            intent = new Intent(InsectPicker.this, EvaluationActivity.class);
            intent.putExtra("overallInsect", (java.io.Serializable) mSites);
            intent.putExtra("selectedInsects", (java.io.Serializable) listCal);
            setResult(INSECT_DATA, intent);
        }
        return super.onOptionsItemSelected(item);
    }
}

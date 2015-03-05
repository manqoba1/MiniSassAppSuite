package com.sifiso.codetribe.riverteamapp;

import android.content.res.Configuration;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.sifiso.codetribe.minisasslibrary.dto.tranfer.ResponseDTO;


public class Evaluation extends ActionBarActivity {
    private TextView WC_minus, WC_add, WT_minus, WT_add,
            WP_minus, WP_add, WO_minus, WO_add, WE_minus, WE_add;
    private EditText WC_score, WP_score, WT_score, WO_score, WE_score;
    private TextView TV_total_score, TV_average_score, TV_avg_score, TV_score_status;
    private ImageView IMG_score_icon;
    private EditText WT_sp_river, WT_sp_category, EDT_comment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluation);
        setField();
        addMinus();
    }

    int wc = 0, wt = 0, we = 0, wp = 0, wo = 0;

    private void addMinus() {
        WO_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wo = wo + 1;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        WO_score.setText(wo);
                    }
                });
            }
        });
        WO_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (wo <= 0) {

                } else {
                    wo = wo - 1;
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        WO_score.setText(wo);
                    }
                });
            }
        });
        WP_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wp = wp + 1;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        WP_score.setText(wp);
                    }
                });
            }
        });
        WP_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (wp <= 0) {

                } else {
                    wp = wp - 1;
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        WP_score.setText(wp);
                    }
                });
            }
        });
        WT_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wt = wt + 1;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        WT_score.setText(wt);
                    }
                });
            }
        });
        WT_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (wt <= 0) {

                } else {
                    wt = wt - 1;
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        WT_score.setText(wt);
                    }
                });
            }
        });
        WE_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                we = we + 1;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        WE_score.setText(we);
                    }
                });
            }
        });
        WE_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (we <= 0) {

                } else {
                    we = we - 1;
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        WE_score.setText(we);
                    }
                });
            }
        });
        WC_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wc = wc + 1;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        WC_score.setText(wc);
                    }
                });
            }
        });
        WC_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (wc <= 0) {

                } else {
                    wc = wc - 1;
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        WC_score.setText(wc);
                    }
                });
            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    ResponseDTO response;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("response", response);
        super.onSaveInstanceState(outState);
    }

    private void setField() {
        WC_minus = (TextView) findViewById(R.id.WC_minus);
        WC_score = (EditText) findViewById(R.id.WC_score);
        WC_add = (TextView) findViewById(R.id.WC_add);
        WT_minus = (TextView) findViewById(R.id.WT_minus);
        WT_score = (EditText) findViewById(R.id.WT_score);
        WT_add = (TextView) findViewById(R.id.WT_add);
        WP_minus = (TextView) findViewById(R.id.WP_minus);
        WP_score = (EditText) findViewById(R.id.WP_score);
        WP_add = (TextView) findViewById(R.id.WP_add);
        WO_minus = (TextView) findViewById(R.id.WO_minus);
        WO_score = (EditText) findViewById(R.id.WO_score);
        WO_add = (TextView) findViewById(R.id.WO_add);
        WE_minus = (TextView) findViewById(R.id.WE_minus);
        WE_score = (EditText) findViewById(R.id.WE_score);
        WE_add = (TextView) findViewById(R.id.WE_add);

        TV_total_score = (TextView) findViewById(R.id.TV_total_score);
        TV_average_score = (TextView) findViewById(R.id.TV_average_score);
        TV_avg_score = (TextView) findViewById(R.id.TV_avg_score);
        TV_score_status = (TextView) findViewById(R.id.TV_score_status);
        IMG_score_icon = (ImageView) findViewById(R.id.IMG_score_icon);
        WT_sp_river = (EditText) findViewById(R.id.WT_sp_river);
        WT_sp_category = (EditText) findViewById(R.id.WT_sp_category);
        EDT_comment = (EditText) findViewById(R.id.EDT_comment);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_evaluation, menu);
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
}

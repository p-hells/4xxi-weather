package com.example.home.fourxxiweather.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.home.fourxxiweather.R;
import com.example.home.fourxxiweather.utils.SharedPreferencesHelper;

public class IdLoaderActivity extends AppCompatActivity {

    Button btnLoadIds;
    TextView tvState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ids_loader);

        btnLoadIds = (Button) findViewById(R.id.btnLoadIds);
        tvState = (TextView) findViewById(R.id.tvState);

        int state = SharedPreferencesHelper.getIdLoadingState(this);
        switch (state) {
            case 0:
                btnLoadIds.setEnabled(true);
                tvState.setText("");
                break;
            case 1:
                btnLoadIds.setEnabled(false);
                tvState.setText(getString(R.string.textview_loading_in_progress));
                break;
            case 2:
                btnLoadIds.setEnabled(false);
                tvState.setText(getString(R.string.textview_already_loaded));
                break;
        }

        btnLoadIds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startIdsLoading();
            }
        });
    }

    private void startIdsLoading() {
        SharedPreferencesHelper.setIdLoadingState(this, 1);
        finish();
    }
}

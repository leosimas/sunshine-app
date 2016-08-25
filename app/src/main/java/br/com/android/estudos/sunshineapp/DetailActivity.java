package br.com.android.estudos.sunshineapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final Intent intent = this.getIntent();
        if (intent != null) {
            final String forecastStr = intent.getStringExtra(Intent.EXTRA_TEXT);
            if (forecastStr != null) {
                TextView textView = (TextView) this.findViewById(R.id.textView);
                textView.setText( forecastStr );
            }
        }

    }

}

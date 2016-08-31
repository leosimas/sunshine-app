package br.com.android.estudos.sunshineapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private static final String DETAILFRAGMENT_TAG = "DFTAG";

    private String mLocation;
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mLocation = Utility.getPreferredLocation( this );

        if (findViewById(R.id.weather_detail_container) != null) {
            // The detail container view will be present only in the large-screen layouts
            // (res/layout-sw600dp). If this view is present, then the activity should be
            // in two-pane mode.
            mTwoPane = true;
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.weather_detail_container, new DetailFragment(), DETAILFRAGMENT_TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        final String preferredLocation = Utility.getPreferredLocation(this);
        if ( ! mLocation.equals( preferredLocation ) ) {
            ForecastFragment forecastFragment = (ForecastFragment) this.getSupportFragmentManager().findFragmentById(R.id.fragment_forecast);
            forecastFragment.onLocationChanged();
            mLocation = preferredLocation;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        final int id = item.getItemId();
        switch (id) {

            case R.id.action_settings:
                this.startActivity( new Intent(this, SettingsActivity.class) );
                return true;

            case R.id.action_view_map:
                final String location = Utility.getPreferredLocation(this);

                Uri uri = Uri.parse( "geo:0,0").buildUpon()
                        .appendQueryParameter("q", location)
                        .build();

                Intent intent = new Intent( Intent.ACTION_VIEW );
                intent.setData( uri );

                if ( intent.resolveActivity( this.getPackageManager() ) != null ) {
                    this.startActivity(intent);
                } else {
                    final String errorMsg = getString( R.string.error_map_intent_not_found );
                    Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show();
                }

                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

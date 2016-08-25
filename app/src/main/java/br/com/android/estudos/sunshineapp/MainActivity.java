package br.com.android.estudos.sunshineapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
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
                final String location = SharedPrefs.getLocationPreference(this);

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

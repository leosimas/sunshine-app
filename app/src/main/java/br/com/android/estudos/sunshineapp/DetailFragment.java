package br.com.android.estudos.sunshineapp;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import br.com.android.estudos.sunshineapp.data.WeatherContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailFragment extends Fragment {

    private static final String[] FORECAST_COLUMNS = {
            WeatherContract.WeatherEntry.TABLE_NAME + "."+ WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID
    };

    static final int COL_WEATHER_ID = 0;
    static final int COL_WEATHER_DATE = 1;
    static final int COL_WEATHER_DESC = 2;
    static final int COL_WEATHER_MAX_TEMP = 3;
    static final int COL_WEATHER_MIN_TEMP = 4;
    static final int COL_WEATHER_CONDITION_ID = 5;

    private static final String FORECAST_SHARE_HASHTAG = " #SunshineApp";
    private static final int LOADER_ID = 200;

    private ShareActionProvider mShareActionProvider;
    private TextView textView;
    private String forecastString;

    public DetailFragment() {
        this.setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.detail_fragment, menu);

        // Locate MenuItem with ShareActionProvider
        MenuItem item = menu.findItem(R.id.action_share);

        // Fetch and store ShareActionProvider
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
    }

    private void setShareIntent() {
        if (mShareActionProvider != null) {
            Intent shareIntent = new Intent();
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT,
                forecastString + FORECAST_SHARE_HASHTAG);
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                this.startActivity( new Intent(getActivity(), SettingsActivity.class) );
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        textView = (TextView) rootView.findViewById(R.id.textView);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getActivity().getSupportLoaderManager().initLoader(LOADER_ID, null, new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                final Intent intent = getActivity().getIntent();
                if (intent == null) {
                    return null;
                }

                String uriString = intent.getDataString();

                return new CursorLoader(getActivity(),
                        Uri.parse(uriString),
                        FORECAST_COLUMNS,
                        null,
                        null,
                        null
                        );
            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
                if ( ! cursor.moveToFirst() ) {
                    return;
                }

                String dateStr = Utility.formatDate( cursor.getLong(COL_WEATHER_DATE) );
                String description = cursor.getString(COL_WEATHER_DESC);

                boolean isMetric = Utility.isMetric(getActivity());
                String high = Utility.formatTemperature( cursor.getDouble(COL_WEATHER_MAX_TEMP), isMetric );
                String low = Utility.formatTemperature( cursor.getDouble(COL_WEATHER_MIN_TEMP), isMetric );

                forecastString = String.format("%s - %s - %s/%s", dateStr, description, high, low);
                textView.setText( forecastString );

                setShareIntent();
            }

            @Override
            public void onLoaderReset(Loader<Cursor> loader) {
                // what about this one?
            }
        });

    }

}

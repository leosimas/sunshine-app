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
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import br.com.android.estudos.sunshineapp.data.WeatherContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment {

    private static final String LOG_TAG = ForecastFragment.class.getSimpleName();
    private static final int LOADER_ID = 100;
    private ForecastAdapter mForecastAdapter;

    private static final String[] FORECAST_COLUMNS = {
            // In this case the id needs to be fully qualified with a table name, since
            // the content provider joins the location & weather tables in the background
            // (both have an _id column)
            // On the one hand, that's annoying.  On the other, you can search the weather table
            // using the location set by the user, which is only in the Location table.
            // So the convenience is worth it.
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
            WeatherContract.LocationEntry.COLUMN_COORD_LAT,
            WeatherContract.LocationEntry.COLUMN_COORD_LONG
    };

    // These indices are tied to FORECAST_COLUMNS.  If FORECAST_COLUMNS changes, these
    // must change.
    static final int COL_WEATHER_ID = 0;
    static final int COL_WEATHER_DATE = 1;
    static final int COL_WEATHER_DESC = 2;
    static final int COL_WEATHER_MAX_TEMP = 3;
    static final int COL_WEATHER_MIN_TEMP = 4;
    static final int COL_LOCATION_SETTING = 5;
    static final int COL_WEATHER_CONDITION_ID = 6;
    static final int COL_COORD_LAT = 7;
    static final int COL_COORD_LONG = 8;

    public ForecastFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // inflating view:
        View view = inflater.inflate(R.layout.fragment_forecast, container, false);

        mForecastAdapter = new ForecastAdapter(getActivity(), null, 0 );

        ListView listView = (ListView) view.findViewById(R.id.listview_forecast);
        listView.setAdapter(mForecastAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // TODO do it later:
//                final String forecastStr = mForecastAdapter.getItem( i );
//
//                Intent intent = new Intent(getActivity(), DetailActivity.class)
//                        .putExtra(Intent.EXTRA_TEXT, forecastStr);
//                getActivity().startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        this.updateWeather();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getActivity().getSupportLoaderManager().initLoader(
                LOADER_ID, null, new LoaderManager.LoaderCallbacks<Cursor>() {
                    @Override
                    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                        // set adapter:
                        String locationSetting = Utility.getPreferredLocation(getActivity());

                        // Sort order:  Ascending, by date.
                        String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC";
                        Uri weatherForLocationUri = WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(
                                locationSetting, System.currentTimeMillis());

                        return new CursorLoader(getActivity(),
                                weatherForLocationUri,
                                FORECAST_COLUMNS,
                                null,
                                null,
                                sortOrder);
                    }

                    @Override
                    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                        mForecastAdapter.swapCursor(data);
                    }

                    @Override
                    public void onLoaderReset(Loader<Cursor> loader) {
                        mForecastAdapter.swapCursor(null);
                    }
                });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecast_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int id = item.getItemId();
        switch (id) {
            case R.id.action_refresh:
                this.updateWeather();
                return true;

            case R.id.action_settings:
                this.startActivity( new Intent(getActivity(), SettingsActivity.class) );
                return true;

            case R.id.action_view_map:
                final String location = SharedPrefs.getLocationPreference(getActivity());

                Uri uri = Uri.parse( "geo:0,0?q=" + TextUtils.htmlEncode( location ) );
                Intent intent = new Intent( Intent.ACTION_VIEW );
                intent.setData( uri );

                if ( intent.resolveActivity( getActivity().getPackageManager() ) != null ) {
                    this.startActivity(intent);
                } else {
                    final String errorMsg = getString( R.string.error_map_intent_not_found );
                    Toast.makeText(ForecastFragment.this.getActivity(), errorMsg, Toast.LENGTH_SHORT).show();
                }

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateWeather() {
        final String location = SharedPrefs.getLocationPreference(getActivity());
        new FetchWeatherTask(getActivity()).execute( location );
    }

}

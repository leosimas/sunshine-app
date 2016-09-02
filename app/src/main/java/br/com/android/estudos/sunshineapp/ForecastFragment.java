package br.com.android.estudos.sunshineapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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

import java.util.Date;

import br.com.android.estudos.sunshineapp.data.WeatherContract;
import br.com.android.estudos.sunshineapp.service.SunshineService;
import br.com.android.estudos.sunshineapp.sync.SunshineSyncAdapter;

/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment {

    private static final String LOG_TAG = ForecastFragment.class.getSimpleName();
    private static final int LOADER_ID = 100;
    private static final String KEY_SELECTED_POSITION = "KEY_SELECTED_POSITION";
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

    private LoaderManager.LoaderCallbacks<Cursor> mLoaderCallbacks = new LoaderManager.LoaderCallbacks<Cursor>() {
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
            updateSelectedItem();

            // cant do this on 'onLoadFinished', why?
//            new Handler().post(new Runnable() {
//                @Override
//                public void run() {
//                    showDetails((Cursor) mForecastAdapter.getItem(mPosition));
//                }
//            });
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            mForecastAdapter.swapCursor(null);
        }
    };

    private int mPosition;
    private ListView mListView;
    private boolean mUseTodayLayout;

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

        mListView = (ListView) view.findViewById(R.id.listview_forecast);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // CursorAdapter returns a cursor at the correct position for getItem(), or null
                // if it cannot seek to that position.
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                showDetails(cursor);
                mPosition = position;
            }
        });

        mForecastAdapter = new ForecastAdapter(getActivity(), null, 0 );
        mForecastAdapter.setUseTodayLayout(mUseTodayLayout);
        mListView.setAdapter(mForecastAdapter);

        if ( savedInstanceState != null && savedInstanceState.containsKey(KEY_SELECTED_POSITION) ) {
            mPosition = savedInstanceState.getInt(KEY_SELECTED_POSITION);
        } else {
            mPosition = 0;
        }

        this.updateSelectedItem();

        return view;
    }

    private void showDetails(Cursor cursor) {
        if (cursor != null && cursor.getCount() > 0) {
            String locationSetting = Utility.getPreferredLocation(getActivity());

            Callback callback = (Callback) getActivity();
            callback.onItemSelected(WeatherContract.WeatherEntry.buildWeatherLocationWithDate(
                            locationSetting, cursor.getLong(COL_WEATHER_DATE)
                    ));
        }
    }

    private void updateSelectedItem() {
        if ( mPosition != ListView.INVALID_POSITION ) {
            mListView.setItemChecked(mPosition, true);
            mListView.smoothScrollToPosition( mPosition );
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if ( mPosition != ListView.INVALID_POSITION ) {
            outState.putInt(KEY_SELECTED_POSITION, mPosition);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(LOADER_ID, null, mLoaderCallbacks);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecast_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                this.startActivity( new Intent(getActivity(), SettingsActivity.class) );
                return true;

            case R.id.action_view_map:
                this.openMapLocation();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void openMapLocation() {
//                final String location = Utility.getPreferredLocation(getActivity());
//                Uri uri = Uri.parse( "geo:0,0?q=" + TextUtils.htmlEncode( location ) );

        if ( mForecastAdapter == null ) {
            return;
        }

        Cursor cursor = mForecastAdapter.getCursor();
        if (cursor == null) {
            return;
        }

        if ( !cursor.moveToFirst() ) {
            return;
        }

        final Double lat = cursor.getDouble(COL_COORD_LAT);
        final Double lon = cursor.getDouble(COL_COORD_LONG);

        Uri uri = Uri.parse( "geo:" + lat + "," + lon );

        Intent intent = new Intent( Intent.ACTION_VIEW );
        intent.setData( uri );

        if ( intent.resolveActivity( getActivity().getPackageManager() ) != null ) {
            this.startActivity(intent);
        } else {
            final String errorMsg = getString( R.string.error_map_intent_not_found );
            Toast.makeText(ForecastFragment.this.getActivity(), errorMsg, Toast.LENGTH_SHORT).show();
        }
    }

    private void updateWeather() {
        SunshineSyncAdapter.syncImmediately( this.getActivity() );

//        final String location = Utility.getPreferredLocation(getActivity());
//
//        Intent intent = new Intent(getActivity(), SunshineService.AlarmReceiver.class)
//                .putExtra(SunshineService.EXTRA_LOCATION, location);
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 0, intent, PendingIntent.FLAG_ONE_SHOT);
//
//        long triggerTime = System.currentTimeMillis() + 5 * 1000;
//
//        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
//        alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);

    }

    public void onLocationChanged() {
        this.updateWeather();
        getLoaderManager().restartLoader(LOADER_ID, null, mLoaderCallbacks);
    }

    public void setUseTodayLayout(boolean useTodayLayout) {
        this.mUseTodayLayout = useTodayLayout;
        if ( this.mForecastAdapter != null ) {
            this.mForecastAdapter.setUseTodayLayout( useTodayLayout );
        }
    }

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        void onItemSelected(Uri dateUri);
    }

}

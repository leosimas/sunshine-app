package br.com.android.estudos.sunshineapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment {

    private static final String LOG_TAG = ForecastFragment.class.getSimpleName();
    private ArrayAdapter<String> arrayAdapter;
    private List<String> forecastList;

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

        forecastList = new ArrayList<>();

        // inflating view:
        View view = inflater.inflate(R.layout.fragment_forecast, container, false);

        // set adapter:
        arrayAdapter = new ArrayAdapter<>(getActivity(), R.layout.list_item_forecast, R.id.textview_forecast, forecastList);

        ListView listView = (ListView) view.findViewById(R.id.listview_forecast);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final String forecastStr = arrayAdapter.getItem( i );
                //Toast.makeText(ForecastFragment.this.getActivity(), forecastStr, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), DetailActivity.class)
                        .putExtra(Intent.EXTRA_TEXT, forecastStr);
                getActivity().startActivity(intent);
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
        new FetchWeatherTask().execute( location );
    }

    public class FetchWeatherTask extends AsyncTask<String, Void, String[]> {

        @Override
        protected String[] doInBackground(String... params) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String forecastJsonStr = null;
            String result[] = null;
            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast

                Uri uri = new Uri.Builder()
                        .scheme("http")
                        .authority("api.openweathermap.org")
                        .appendEncodedPath( "data/2.5/forecast/daily" )
                        .appendQueryParameter("appid", "e97690e93a6a314bcd82b2c6bc489e42")
                        .appendQueryParameter("mode", "json")
                        .appendQueryParameter("units", "metric")
                        .appendQueryParameter("q", params[0])
                        .build();

                String builtUrl = uri.toString();

//                Log.v(LOG_TAG, "builtUrl = " + builtUrl);

                URL url = new URL( builtUrl );

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder buffer = new StringBuilder();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                forecastJsonStr = buffer.toString();
//                Log.v(LOG_TAG, forecastJsonStr);

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }

                if ( forecastJsonStr != null ) {
                    try {
                        result = WeatherDataParser.getWeatherDataFromJson(getActivity(), forecastJsonStr);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }

            return result;
        }

        @Override
        protected void onPostExecute(String[] strings) {
            if (strings == null) {
                Log.e(LOG_TAG, "no data");
                return;
            }
            forecastList.clear();
            forecastList.addAll( Arrays.asList(strings) );

            arrayAdapter.notifyDataSetChanged();
        }
    }
}

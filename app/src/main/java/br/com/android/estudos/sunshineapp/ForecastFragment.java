package br.com.android.estudos.sunshineapp;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment {

    private static final String LOG_TAG = ForecastFragment.class.getSimpleName();

    public ForecastFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // mock data:
        String[] forecastArray = new String[]{
                "Today - Sunny - 88 / 63",
                "Tomorrow - Sunny - 88 / 63",
                "After Tomorrow - Sunny - 88 / 63",
                "After 3  - Sunny - 88 / 63",
                "After 4 - Sunny - 88 / 63",
                "After 5 - Sunny - 88 / 63",
                "Last one - Sunny - 88 / 63"
        };

        List<String> forecastList = Arrays.asList( forecastArray );

        // inflating view:
        View view = inflater.inflate(R.layout.fragment_forecast, container, false);

        // set adapter:
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(), R.layout.list_item_forecast, R.id.textview_forecast, forecastList);

        ListView listView = (ListView) view.findViewById(R.id.listview_forecast);
        listView.setAdapter(arrayAdapter);

        return view;
    }

    public class FetchWeatherTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                URL url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7");

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
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
            }

            return forecastJsonStr;
        }
    }
}

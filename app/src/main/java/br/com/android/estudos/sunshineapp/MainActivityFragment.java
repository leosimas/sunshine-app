package br.com.android.estudos.sunshineapp;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.Arrays;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    public MainActivityFragment() {
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
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        // set adapter:
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(), R.layout.list_item_forecast, R.id.textview_forecast, forecastList);

        ListView listView = (ListView) view.findViewById(R.id.listview_forecast);
        listView.setAdapter(arrayAdapter);

        return view;
    }
}

package br.com.android.estudos.sunshineapp;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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


        return inflater.inflate(R.layout.fragment_main, container, false);
    }
}

package com.example.richard.sunshine;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by richard on 7/3/2016.
 */
public class ForecastFragment extends Fragment {

    public ForecastFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup group, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, group, false);

        String[] forecastArray = {
                "Today - Sunny - 88/63",
                "Tomorrow - Foggy - 70/46",
                "Weds - Cloudy - 72/63",
                "Thurs - Rainy - 64/51",
                "Fri - Foggy - 70/46",
                "Sat - Sunny - 76/68",
                "Sun - Sunny - 74/70"
        };

        List<String> weekForcast = new ArrayList<>(Arrays.asList(forecastArray));

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getContext(),
                R.layout.list_item_forecast,
                R.id.list_item_textView,
                weekForcast
        );

        ListView listView = (ListView) rootView.findViewById(R.id.listView_forecast);
        listView.setAdapter(adapter);

        new FetchWeatherTask().execute("https://api.openweathermap.org/data/2.5/forecast/daily?q=60618&cnt=16&APPID=604579dd75ea230cf9cf861961d6bbf8");


        return rootView;
    }

    public class FetchWeatherTask extends AsyncTask<String, Void, String> {

        private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();

        @Override
        protected String doInBackground(String... urls) {
            HttpsURLConnection urlConnection = null;
            BufferedReader reader = null;

            String forecastJsonStr = null;

            try {
                // Construct query for OpenWeather API
                URL url = new URL(urls[0]);

                // Create request and connection to the api
                urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read input into a string
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // lol. nothing to do
                    return null;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null){
                    buffer.append(line + '\n');
                }

                if (buffer.length() == 0) {
                    // empty buffer!
                    return null;
                }

                forecastJsonStr = buffer.toString();

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // no point in parsing data if there was an error
                forecastJsonStr = null;
            } finally {
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
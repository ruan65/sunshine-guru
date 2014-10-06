package com.engstuff.sunshineguru.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.engstuff.sunshineguru.DetailActivity;
import com.engstuff.sunshineguru.R;
import com.engstuff.sunshineguru.data.DataRequest;
import com.engstuff.sunshineguru.data.WeatherDataParser;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ForecastFragment extends Fragment {

    ArrayAdapter<String> mForecastAdapter;
    Context ctx;

    public ForecastFragment() {
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ctx = getActivity();
        setHasOptionsMenu(true);
        mForecastAdapter = new ArrayAdapter<String>(
                ctx,
                R.layout.list_item_forcast_tv,
                R.id.list_item_forecast_tv,
                new ArrayList<String>(Arrays.asList(ctx.getString(R.string.wrong_location))));

        View rootView = inflater.inflate(R.layout.f_main, container, false);
        ListView forecastLv = (ListView) rootView.findViewById(R.id.lv_forecast);
        forecastLv.setAdapter(mForecastAdapter);
        forecastLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(ctx, DetailActivity.class)
                                .putExtra(Intent.EXTRA_TEXT, mForecastAdapter.getItem(i));
                startActivity(intent);
            }
        });
        return rootView;
    }

    private void fetchWeather() {
        ConnectivityManager connMngr = (ConnectivityManager) ctx
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connMngr.getActiveNetworkInfo();
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            new DownloadDataJson().execute(
                            PreferenceManager.getDefaultSharedPreferences(getActivity())
                            .getString(getString(R.string.pref_location_key),
                                    getString(R.string.pref_location_default)
                            ));
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        fetchWeather();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecastfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_refresh:
                fetchWeather();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class DownloadDataJson extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... args) {

            return new DataRequest().getDaysForecast(args[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {


                try {
                    updateSharedPref(result);
                    List<String> newData = Arrays.asList(new WeatherDataParser(ctx).getWeatherDataFromJson(result, 7));
                    mForecastAdapter.clear();

                    for (String day : newData) mForecastAdapter.add(day);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void updateSharedPref(String result) throws JSONException {
        SharedPreferences shPref = ctx.getSharedPreferences(ctx.getString(R.string.shared_preference_file_key), ctx.MODE_PRIVATE);
        SharedPreferences.Editor editor = shPref.edit();
        editor.putString(ctx.getString(R.string.last_weather_request), result);
        editor.putString(ctx.getString(R.string.curent_geo_coord), WeatherDataParser.getGeoFromResponse(result));
        editor.commit();
    }
}

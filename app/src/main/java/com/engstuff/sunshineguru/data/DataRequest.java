package com.engstuff.sunshineguru.data;

import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class DataRequest {

    public final static String BASE_API_URL = "http://api.openweathermap.org/data/2.5/forecast/daily?",
                              GET = "GET", QUERY = "q", MODE = "mode", DAYS = "cnt", UNITS = "units";

    String mode = "json", unints = "metric";
    int days = 7;
    HttpURLConnection urlConnection;
    BufferedReader reader;
    StringBuffer buffer;

    private URL makeQueryUrl(String postal) throws MalformedURLException {

        Uri uri = Uri.parse(BASE_API_URL).buildUpon()
                .appendQueryParameter(QUERY, postal)
                .appendQueryParameter(MODE, mode)
                .appendQueryParameter(DAYS, Integer.toString(days))
                .appendQueryParameter(UNITS, unints)
                .build();

        return new URL(uri.toString());
    }

    public String getDaysForecast(String place) {
        try {
            URL url = makeQueryUrl(place);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod(GET);
            urlConnection.connect();

            InputStream is = urlConnection.getInputStream();
            buffer = new StringBuffer();

            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

        } catch (MalformedURLException e) {
            Log.e("DataRequest", "Error: ", e);
        } catch (IOException e) {
            Log.e("DataRequest", "Error: ", e);
        } finally {
            if (urlConnection != null) urlConnection.disconnect();

            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e("DataRequest", "Error closing stream: ", e);
                }
            }
        }

        return buffer.toString();
    }
}




















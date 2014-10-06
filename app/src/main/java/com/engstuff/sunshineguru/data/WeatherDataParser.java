package com.engstuff.sunshineguru.data;

import android.content.Context;
import android.preference.PreferenceManager;
import com.engstuff.sunshineguru.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class WeatherDataParser {

    /* The date/time conversion code is going to be moved outside the asynctask later,
     * so for convenience we're breaking it out into its own method now.
     */

    private Context ctx;

    public WeatherDataParser(Context context) {
        this.ctx = context;
    }


    private String getReadableDateString(long time){
        // Because the API returns a unix timestamp (measured in seconds),
        // it must be converted to milliseconds in order to be converted to valid date.
        Date date = new Date(time * 1000);
        SimpleDateFormat format = new SimpleDateFormat("E, MMM d");
        return format.format(date).toString();
    }

    /**
     * Prepare the weather high/lows for presentation.
     */
    private String formatHighLows(double high, double low) {
        // For presentation, assume the user doesn't care about tenths of a degree.
        long roundedHigh = Math.round(high);
        long roundedLow = Math.round(low);

        String highLowStr = roundedHigh + "/" + roundedLow;
        return highLowStr;
    }

    /**
     * Take the String representing the complete forecast in JSON Format and
     * pull out the data we need to construct the Strings needed for the wireframes.
     *
     * Fortunately parsing is easy:  constructor takes the JSON string and converts it
     * into an Object hierarchy for us.
     */
    public String[] getWeatherDataFromJson(String forecastJsonStr, int numDays)
            throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String OWM_LIST = "list";
        final String OWM_WEATHER = "weather";
        final String OWM_TEMPERATURE = "temp";
        final String OWM_MAX = "max";
        final String OWM_MIN = "min";
        final String OWM_DATETIME = "dt";
        final String OWM_DESCRIPTION = "main";

        JSONObject forecastJson = new JSONObject(forecastJsonStr);
        JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);

        String[] resultStrs = new String[numDays];
        for(int i = 0; i < weatherArray.length(); i++) {
            // For now, using the format "Day, description, hi/low"
            String day;
            String description;
            String highAndLow;

            // Get the JSON object representing the day
            JSONObject dayForecast = weatherArray.getJSONObject(i);

            // The date/time is returned as a long.  We need to convert that
            // into something human-readable, since most people won't read "1400356800" as
            // "this saturday".
            long dateTime = dayForecast.getLong(OWM_DATETIME);
            day = getReadableDateString(dateTime);

            // description is in a child array called "weather", which is 1 element long.
            JSONObject weatherObject = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
            description = weatherObject.getString(OWM_DESCRIPTION);

            // Temperatures are in a child object called "temp".  Try not to name variables
            // "temp" when working with temperature.  It confuses everybody.
            JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
            double high = temperatureObject.getDouble(OWM_MAX);
            double low = temperatureObject.getDouble(OWM_MIN);
            if (
                    PreferenceManager.getDefaultSharedPreferences(ctx)
                            .getString(ctx.getString(R.string.temp_unit_key), ctx.getString(R.string.temp_unit_default))
                            .equals(ctx.getString(R.string.temp_unit_far))
                    ) {
                high = convertToFahrenheit(high);
                low = convertToFahrenheit(low);
            }


            highAndLow = formatHighLows(high, low);
            resultStrs[i] = day + " - " + description + " - " + highAndLow;
        }

        return resultStrs;
    }

    private double convertToFahrenheit(double x) {
        return x * 9 / 5 + 32;
    }

    public static String getGeoFromResponse(String weatherJsonString) throws JSONException {
        JSONObject coord = new JSONObject(weatherJsonString)
                .getJSONObject("city")
                .getJSONObject("coord");
        double lon = coord.getDouble("lon");
        double lat = coord.getDouble("lat");

        return "geo:" + Double.toString(lat) + "," + Double.toString(lon);
    }

    public static double getMaxTemperatureForDay(String weatherJsonString, int dayIndex) throws JSONException {
        return new JSONObject(weatherJsonString)
                .getJSONArray("list")
                .getJSONObject(dayIndex)
                .getJSONObject("temp")
                .getDouble("max");
    }
}

package com.engstuff.sunshineguru;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.engstuff.sunshineguru.fragments.ForecastFragment;


public class Main extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.a_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new ForecastFragment())
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case R.id.menu_show_location:
                Uri uri = null;
                String geo = getCurrentGeoCoords(this);
                if (geo != null) uri = Uri.parse(geo);

                if (uri != null) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(uri);
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivity(intent);
                    }
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static String getCurrentGeoCoords(Context context) {

        return context.getSharedPreferences(
                context.getString(R.string.shared_preference_file_key), Context.MODE_PRIVATE)
                .getString(context.getString(R.string.curent_geo_coord), null);
    }

}























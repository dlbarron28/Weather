package com.davidbarron.weather;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity implements WeatherList.LocationListFragmentListener, AddLocationDialog.AddLocationistener, WeatherDetail.DetailsFragmentListener {
    private WeatherList weatherList;
    public static final String ROW_ID = "row_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        weatherList = new WeatherList();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.add(R.id.fragment_container, weatherList);
        transaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_exit) {
            finish();
            return true;
        }
        else if(id == R.id.action_settings) {
            getFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new SettingsFragment())
                .addToBackStack(null)
                .commit();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getFragmentManager();
        if(fm.getBackStackEntryCount() != 0) {
            fm.popBackStack();
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    public void onLocationSelected(long rowID) {
        WeatherDetail weatherDetail = new WeatherDetail();
        Bundle arguments = new Bundle();
        arguments.putLong(ROW_ID, rowID);
        weatherDetail.setArguments(arguments);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, weatherDetail);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onAddLocation() {
        DialogFragment dialog = new AddLocationDialog();
        dialog.show(getFragmentManager(), "AddLocationDialog");
    }

    @Override
    public void onAddEditCompleted(String city, String state, boolean home_boolean) {
        DatabaseDriver databaseDriver = new DatabaseDriver(this);
        databaseDriver.insertLocation(city,state,home_boolean);
        databaseDriver.close();
        weatherList.updateLocationList();
    }

    @Override
    public void onDeleteLocation() {
        FragmentManager fm = getFragmentManager();
        if(fm.getBackStackEntryCount() != 0) {
            fm.popBackStack();
        }
        weatherList.updateLocationList();
    }
}

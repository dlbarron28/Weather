package com.davidbarron.weather;

import android.app.Activity;
import android.app.ListFragment;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class WeatherList extends ListFragment {
    private LocationListFragmentListener listener;
    private ListView locationList;
    private CursorAdapter cursorAdapter;


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        setRetainInstance(true);
        setHasOptionsMenu(true);
        setEmptyText(getResources().getString(R.string.no_locations));
        locationList = getListView();
        locationList.setOnItemClickListener(viewLocationListener);
        locationList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        cursorAdapter = new SimpleCursorAdapter(getActivity(),R.layout.row_layout,null,new String[] {"city","state"},new int[] {R.id.cityTextView,R.id.stateTextView},0);
        setListAdapter(cursorAdapter);
    }
    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        listener = (LocationListFragmentListener) activity;
    }

    // remove ContactListFragmentListener when Fragment detached
    @Override
    public void onDetach()
    {
        super.onDetach();
        listener = null;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        new GetContactsTask().execute((Object[]) null);
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_menu, menu);
    }

    // handle choice from options menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_add:
                listener.onAddLocation();
                return true;
        }

        return super.onOptionsItemSelected(item); // call super's method
    }

    public void updateLocationList()
    {
        new GetContactsTask().execute((Object[]) null);
    }


    AdapterView.OnItemClickListener viewLocationListener = new AdapterView.OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View view,int position, long id)
        {
            listener.onLocationSelected(id);
        }
    };
    private class GetContactsTask extends AsyncTask<Object, Object, Cursor>
    {
        DatabaseDriver databaseDriver = new DatabaseDriver(getActivity());

        // open database and return Cursor for all contacts
        @Override
        protected Cursor doInBackground(Object... params)
        {
            databaseDriver.open();
            return databaseDriver.getAllLocations();
        }

        // use the Cursor returned from the doInBackground method
        @Override
        protected void onPostExecute(Cursor result)
        {
            cursorAdapter.changeCursor(result); // set the adapter's Cursor
            databaseDriver.close();
        }
    }
    public interface LocationListFragmentListener
    {
        public void onLocationSelected(long rowID);
        public void onAddLocation();
    }


}

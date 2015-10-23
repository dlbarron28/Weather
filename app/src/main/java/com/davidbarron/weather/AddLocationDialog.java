package com.davidbarron.weather;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.CheckBox;
import android.widget.EditText;

public class AddLocationDialog  extends DialogFragment {

    private AddLocationistener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add a location");
        LayoutInflater inflater = getActivity().getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.dialog_location, null));
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                EditText cityname = (EditText) AddLocationDialog.this.getDialog().findViewById(R.id.city_name);
                EditText statename = (EditText) getDialog().findViewById(R.id.state_name);
                CheckBox home_checkbox = (CheckBox) getDialog().findViewById(R.id.home_checkbox);
                listener.onAddEditCompleted(cityname.getText().toString(), statename.getText().toString(),home_checkbox.isChecked());
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                AddLocationDialog.this.getDialog().cancel();
            }
        });
        return builder.create();
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        listener = (AddLocationistener) activity;
    }
    @Override
    public void onDetach()
    {
        super.onDetach();
        listener = null;
    }
    public interface AddLocationistener
    {
        public void onAddEditCompleted(String city, String state, boolean home_boolean);
    }
}

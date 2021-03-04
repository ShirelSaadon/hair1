package com.example.myapplication5;



import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;


public class SpinnerType {
    private static final String TAG = "Spinner";
    private Spinner spinner;
    private String type = " ";
    private Context context;

    public SpinnerType(Spinner spinner, Context context) {
        this.spinner = spinner;
        this.context = context;
    }

    public void  initSpinner() {
        Log.d(TAG, "initSpinner: initing spinner");
        ArrayList<String> types = new ArrayList<>();

        types.add("type");
        types.add("hair color");
        types.add("haircut");
        types.add("hairdo");
        types.add("hair straightenin");

        //create an ArrayAdapter from the String Array
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, types);
        Log.d(TAG, "initSpinner: " + dataAdapter.toString());
        //set the view for the Drop down list
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //set the ArrayAdapter to the spinner
        spinner.setAdapter(dataAdapter);
        //attach the listener to the spinner


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d(TAG, "onItemSelected: Selected: " + types.get(i));
                type = types.get(i);
                Log.d(TAG, "onItemSelected: " + type);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        Log.d(TAG, "initSpinner: ");
    }

    public String getType(){return  type;}

    public Object getSelectedItem() {

        return spinner.getSelectedItem();


    }

    public Object getSelectedView() {
        return spinner.getSelectedView();
    }
}

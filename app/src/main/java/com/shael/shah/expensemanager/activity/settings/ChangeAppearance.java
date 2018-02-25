package com.shael.shah.expensemanager.activity.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.shael.shah.expensemanager.R;

public class ChangeAppearance extends AppCompatActivity {

    private static final String SHAREDPREF_DISPLAY_OPTION = "com.shael.shah.expensemanager.SHAREDPREF_DISPLAY_OPTION";

    private Spinner animationSpinner;
    private ArrayAdapter<String> animationArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_appearance);

        animationSpinner = findViewById(R.id.animationSpinner);
        createAnimationSpinnerRows();
        animationSpinner.setSelection(animationArrayAdapter.getPosition(getDisplayOptionFromSharedPreferences()));

        animationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), animationArrayAdapter.getItem(position), Toast.LENGTH_LONG).show();
                setDisplayOptionSharedPreference(animationArrayAdapter.getItem(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void createAnimationSpinnerRows() {
        String animationItems[] = new String[]{"Circle", "Bars"};
        animationArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, animationItems);
        animationArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        animationSpinner.setAdapter(animationArrayAdapter);
    }

    private String getDisplayOptionFromSharedPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        return sharedPreferences.getString(SHAREDPREF_DISPLAY_OPTION, "CIRCLE");
    }

    private void setDisplayOptionSharedPreference(String displayExpensesOption) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor prefEditor = sharedPreferences.edit();

        prefEditor.putString(SHAREDPREF_DISPLAY_OPTION, displayExpensesOption);
        prefEditor.apply();
    }
}

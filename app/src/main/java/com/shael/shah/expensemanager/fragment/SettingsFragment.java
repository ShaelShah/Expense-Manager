package com.shael.shah.expensemanager.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shael.shah.expensemanager.R;
import com.shael.shah.expensemanager.activity.BackupRestoreActivity;
import com.shael.shah.expensemanager.activity.CategoryUpdate;
import com.shael.shah.expensemanager.activity.ChangeAppearance;

public class SettingsFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        TextView backupRestoreTextView = view.findViewById(R.id.backupRestoreTextView);
        backupRestoreTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backupRestoreData();
            }
        });

        TextView appearanceTextView = view.findViewById(R.id.appearanceTextView);
        appearanceTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeAppearance();
            }
        });

        TextView dateRangeTextView = view.findViewById(R.id.dateRangeTextView);
        dateRangeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateRangeUpdate();
            }
        });

        TextView categoriesTextView = view.findViewById(R.id.categoryTextView);
        categoriesTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryUpdate();
            }
        });
        return view;
    }

    private void backupRestoreData() {
        Intent intent = new Intent(getActivity(), BackupRestoreActivity.class);
        startActivity(intent);
    }

    private void changeAppearance() {
        Intent intent = new Intent(getActivity(), ChangeAppearance.class);
        startActivity(intent);
    }

    private void dateRangeUpdate() {
//        Intent intent = new Intent(getActivity(), DateRangeUpdate.class);
//        startActivity(intent);
    }

    private void categoryUpdate() {
        Intent intent = new Intent(getActivity(), CategoryUpdate.class);
        startActivity(intent);
    }
}

package com.shael.shah.expensemanager.fragment.display;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shael.shah.expensemanager.R;
import com.shael.shah.expensemanager.activity.settings.BackupRestoreActivity;

public class SettingsFragment extends Fragment {

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
        return view;
    }

    private void backupRestoreData() {
        Intent intent = new Intent(getActivity(), BackupRestoreActivity.class);
        startActivity(intent);
    }
}

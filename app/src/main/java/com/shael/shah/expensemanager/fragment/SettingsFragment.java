package com.shael.shah.expensemanager.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.shael.shah.expensemanager.R;
import com.shael.shah.expensemanager.model.Category;
import com.shael.shah.expensemanager.model.Expense;
import com.shael.shah.expensemanager.utils.DataSingleton;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SettingsFragment extends Fragment {

    private static final int GET_FILE_RESULT_CODE = 1;

    private LinearLayout settingsLinearLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        settingsLinearLayout = (LinearLayout) view.findViewById(R.id.settingsLinearLayout);
        TextView backupRestoreTextView = (TextView) view.findViewById(R.id.backupRestoreTextView);

        backupRestoreTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addBackupRestoreOptions();
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (requestCode == GET_FILE_RESULT_CODE && resultCode == Activity.RESULT_OK) {
            if (resultData != null) {
                Uri uri = resultData.getData();
                CSVToExpenses(uri);
            }
        }
    }

    private void addBackupRestoreOptions() {
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        LinearLayout linearLayout = (LinearLayout) layoutInflater.inflate(R.layout.backup_restore_row_layout, null);
        settingsLinearLayout.addView(linearLayout, 4);

        final TextView backup = (TextView) linearLayout.findViewById(R.id.backupTextView);
        backup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backupToCSV();
            }
        });

        TextView restore = (TextView) linearLayout.findViewById(R.id.restoreTextView);
        restore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restoreFromCSV();
            }
        });
    }

    private void backupToCSV() {
        List<Expense> expenses = DataSingleton.getInstance().getExpenses();

        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }

        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath() + File.separator + "Expense Manager";
        String filename = "Backup - " + Calendar.getInstance().getTime().toString() + ".csv";

        try {
            File filepath = new File(path);
            File file = new File(path, filename);
            filepath.mkdirs();

            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write("Date,Amount,Category,Location,Note,Recurring,Income,Recurring Period,Payment Method\n");
            fileWriter.append("Incomes\n");
            for (Expense e : expenses) {
                if (e.isIncome())
                    fileWriter.append(e.toCSV() + "\n");
            }

            fileWriter.append("Expenses\n");
            for (Expense e : expenses) {
                if (!e.isIncome())
                    fileWriter.append(e.toCSV() + "\n");
            }

            fileWriter.flush();
            fileWriter.close();

            MediaScannerConnection.scanFile(getActivity(), new String[]{file.getAbsolutePath()}, null, null);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Could not backup to CSV", Toast.LENGTH_LONG).show();
            return;
        }

        Toast.makeText(getActivity(), "Backup completed", Toast.LENGTH_LONG).show();
    }

    private void restoreFromCSV() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/csv");
        startActivityForResult(intent, GET_FILE_RESULT_CODE);
    }

    private void CSVToExpenses(Uri uri) {
        DataSingleton.getInstance().reset();

        try {
            List<Category> categories = null;
            BufferedReader bufferedReader = new BufferedReader(new FileReader(uri.getPath()));

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (!line.equals("Date,Amount,Category,Location,Note,Recurring,Income,Recurring Period,Payment Method") && !line.equals("Incomes") && !line.equals("Expenses")) {
                    String[] parts = line.split(",");

                    if (parts[6].equalsIgnoreCase("false")) {
                        DataSingleton.getInstance().addCategory(parts[2]);
                        categories = DataSingleton.getInstance().getCategories();
                    }

                    Category category = null;
                    if (categories != null) {
                        for (Category c : categories) {
                            if (c.getType().equals(parts[2])) {
                                category = c;
                                break;
                            }
                        }
                    }

                    Expense expense = new Expense.Builder(new Date(parts[0]), new BigDecimal(parts[1]), category, parts[3])
                            .note(parts[4])
                            .recurring(Boolean.parseBoolean(parts[5]))
                            .income(Boolean.parseBoolean(parts[6]))
                            .recurringPeriod(parts[7])
                            .paymentMethod(parts[8])
                            .build();
                    DataSingleton.getInstance().addExpense(expense);
                }
            }
        } catch (IOException e) {
            Toast.makeText(getActivity(), "Could not restore from CSV", Toast.LENGTH_LONG).show();
            return;
        }

        Toast.makeText(getActivity(), "Restored from CSV", Toast.LENGTH_LONG).show();
    }

}

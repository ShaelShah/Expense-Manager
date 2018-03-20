package com.shael.shah.expensemanager.fragment.display;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
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
import android.widget.Toast;

import com.shael.shah.expensemanager.R;
import com.shael.shah.expensemanager.activity.settings.category.EditCategoriesActivity;
import com.shael.shah.expensemanager.model.Category;
import com.shael.shah.expensemanager.model.Expense;
import com.shael.shah.expensemanager.model.Income;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        view.findViewById(R.id.backupTextView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backupData();
            }
        });

        view.findViewById(R.id.restoreTextView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restoreData();
            }
        });

        view.findViewById(R.id.updateCategoriesTextView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editCategories();
            }
        });

        view.findViewById(R.id.resetDataTextView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetData();
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

    private void backupData() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }

        DataSingleton instance = DataSingleton.getInstance();
        List<Income> incomes = instance.getIncomes();
        List<Expense> expenses = instance.getExpenses();

        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath() + File.separator + "Expense Manager";
        String filename = "Backup - " + Calendar.getInstance().getTime().toString() + ".csv";

        try {
            File filepath = new File(path);
            File file = new File(path, filename);
            filepath.mkdirs();

            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write("Incomes\n");
            fileWriter.append("Date,Amount,Location,Note,Recurring Period\n");

            for (Income i : incomes)
                fileWriter.append(i.toCSV()).append("\n");

            fileWriter.append("Expenses\n");
            fileWriter.append("Date,Amount,Category,Location,Note,Recurring Period,Payment Method\n");
            for (Expense e : expenses)
                fileWriter.append(e.toCSV()).append("\n");

            fileWriter.flush();
            fileWriter.close();

            MediaScannerConnection.scanFile(getActivity().getApplicationContext(), new String[]{file.getAbsolutePath()}, null, null);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Could not backup to CSV", Toast.LENGTH_LONG).show();
            return;
        }

        Toast.makeText(getActivity(), "Backup completed", Toast.LENGTH_LONG).show();
    }

    private void restoreData() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/csv");
        startActivityForResult(intent, GET_FILE_RESULT_CODE);
    }

    private void CSVToExpenses(Uri uri) {
        DataSingleton instance = DataSingleton.getInstance();
        instance.reset();

        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(uri.getPath()));

            boolean income = true;
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.equals("Incomes")) {
                    income = true;
                    continue;
                }

                if (line.equals("Expenses")) {
                    income = false;
                    continue;
                }

                if (line.equals("Date,Amount,Location,Note,Recurring Period") || line.equals("Date,Amount,Category,Location,Note,Recurring Period,Payment Method")) {
                    continue;
                }

                String[] parts = line.split(",");

                if (income) {
                    Income newIncome = new Income.Builder(new Date(parts[0]), new BigDecimal(parts[1]), parts[2])
                            .note(parts[3])
                            .recurringPeriod(parts[4])
                            .build();
                    instance.addIncome(newIncome);
                } else {
                    instance.addCategory(parts[2]);

                    List<Category> categories = instance.getCategories();
                    Category category = null;
                    if (categories != null) {
                        for (Category c : categories) {
                            if (c.getType().equals(parts[2])) {
                                category = c;
                                break;
                            }
                        }
                    }

                    Expense newExpense = new Expense.Builder(new Date(parts[0]), new BigDecimal(parts[1]), category, parts[3])
                            .note(parts[4])
                            .recurringPeriod(parts[5])
                            .paymentMethod(parts[6])
                            .build();
                    instance.addExpense(newExpense);
                }
            }

            //instance.updateDatabase();
            instance.updateSettings();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Could not restore from CSV", Toast.LENGTH_LONG).show();
            return;
        }

        Toast.makeText(getActivity(), "Restored from CSV", Toast.LENGTH_LONG).show();
    }

    private void editCategories() {
        Intent intent = new Intent(getActivity(), EditCategoriesActivity.class);
        startActivity(intent);
    }

    private void resetData() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Confirm Reset");
        builder.setMessage("Are you sure you want to reset? All data will be removed");

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
            }
        });

        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                DataSingleton instance = DataSingleton.getInstance();
                instance.reset();

                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}

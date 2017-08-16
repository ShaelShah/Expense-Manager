package com.shael.shah.expensemanager.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
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

public class BackupRestoreActivity extends Activity {

    private static final int GET_FILE_RESULT_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup_restore);
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

    public void backupData(View view) {
        backupToCSV();
    }

    public void restoreData(View view) {
        restoreFromCSV();
    }

    private void backupToCSV() {
        List<Expense> expenses = DataSingleton.getInstance().getExpenses();

        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
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

            MediaScannerConnection.scanFile(this, new String[]{file.getAbsolutePath()}, null, null);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Could not backup to CSV", Toast.LENGTH_LONG).show();
            return;
        }

        Toast.makeText(this, "Backup completed", Toast.LENGTH_LONG).show();
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
            Toast.makeText(this, "Could not restore from CSV", Toast.LENGTH_LONG).show();
            return;
        }

        Toast.makeText(this, "Restored from CSV", Toast.LENGTH_LONG).show();
    }
}



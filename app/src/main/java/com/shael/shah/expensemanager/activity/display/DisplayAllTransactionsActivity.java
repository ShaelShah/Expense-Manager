package com.shael.shah.expensemanager.activity.display;

import android.content.Intent;

import com.shael.shah.expensemanager.activity.update.UpdateExpenseActivity;
import com.shael.shah.expensemanager.activity.update.UpdateIncomeActivity;
import com.shael.shah.expensemanager.model.Transaction;

import java.util.ArrayList;
import java.util.List;

public class DisplayAllTransactionsActivity extends DisplayTransactionsActivity {

    @Override
    protected String getTitleText() {
        return "All Transactions";
    }

    @Override
    protected List<Transaction> getTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        transactions.addAll(instance.getExpenses());
        transactions.addAll(instance.getIncomes());

        return transactions;
    }

    @Override
    protected Intent getTransactionIntent(int transactionID, boolean income) {
        Intent intent = income ? new Intent(this, UpdateIncomeActivity.class) : new Intent(this, UpdateExpenseActivity.class);
        intent.putExtra(EXTRA_EXPENSE_ID, transactionID);

        return intent;
    }
}

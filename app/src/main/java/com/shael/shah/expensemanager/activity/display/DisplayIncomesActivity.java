package com.shael.shah.expensemanager.activity.display;

import android.content.Intent;

import com.shael.shah.expensemanager.activity.update.UpdateIncomeActivity;
import com.shael.shah.expensemanager.model.Transaction;

import java.util.ArrayList;
import java.util.List;

public class DisplayIncomesActivity extends DisplayTransactionsActivity
{

    /*****************************************************************
     * Private Variables
     *****************************************************************/

    private static final String EXTRA_TRANSACTION_ID = "com.shael.shah.expensemanager.EXTRA_TRANSACTION_ID";

    /*****************************************************************
     * Abstract Methods
     *****************************************************************/

    @Override
    protected String getTitleText()
    {
        return "Incomes";
    }

    @Override
    protected List<Transaction> getTransactions()
    {
        return new ArrayList<Transaction>(instance.getIncomes());
    }

    @Override
    protected Intent getTransactionIntent(int transactionID, boolean income)
    {
        Intent intent = new Intent(this, UpdateIncomeActivity.class);
        intent.putExtra(EXTRA_TRANSACTION_ID, transactionID);

        return intent;
    }
}

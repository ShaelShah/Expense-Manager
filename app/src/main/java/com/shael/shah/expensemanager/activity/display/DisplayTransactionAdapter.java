package com.shael.shah.expensemanager.activity.display;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.shael.shah.expensemanager.R;
import com.shael.shah.expensemanager.model.Expense;
import com.shael.shah.expensemanager.model.Income;
import com.shael.shah.expensemanager.model.Transaction;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class DisplayTransactionAdapter extends BaseAdapter
{

    private Context context;
    private LayoutInflater inflater;
    private List<Transaction> dataSource;

    public DisplayTransactionAdapter(Context context, List<Transaction> transactions)
    {
        this.dataSource = transactions;
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount()
    {
        return dataSource.size();
    }

    @Override
    public Object getItem(int position)
    {
        return dataSource.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder viewHolder;

        if (convertView == null)
        {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.display_expenses_view, parent, false);

            viewHolder.colorView = convertView.findViewById(R.id.categoryColorView);
            viewHolder.dateTextView = convertView.findViewById(R.id.expenseDateTextView);
            viewHolder.locationTextView = convertView.findViewById(R.id.expenseLocationTextView);
            viewHolder.amountTextView = convertView.findViewById(R.id.expensesAmountTextView);

            convertView.setTag(viewHolder);
        } else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.CANADA);
        Transaction transaction = (Transaction) getItem(position);
        int color = transaction instanceof Income ? ContextCompat.getColor(context, R.color.green) : ((Expense) transaction).getCategory().getColor();
        viewHolder.colorView.setBackgroundColor(color);
        viewHolder.dateTextView.setText(sdf.format(transaction.getDate()));
        viewHolder.locationTextView.setText(transaction.getLocation());
        viewHolder.amountTextView.setText(context.getResources().getString(R.string.currency, transaction.getAmount()));

        return convertView;
    }

    private static class ViewHolder
    {
        private View colorView;
        private TextView dateTextView;
        private TextView locationTextView;
        private TextView amountTextView;
    }
}

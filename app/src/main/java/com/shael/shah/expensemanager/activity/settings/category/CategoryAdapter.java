package com.shael.shah.expensemanager.activity.settings.category;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.shael.shah.expensemanager.R;
import com.shael.shah.expensemanager.model.Category;

import java.util.List;

public class CategoryAdapter extends BaseAdapter
{

    private LayoutInflater inflater;
    private List<Category> dataSource;

    public CategoryAdapter(Context context, List<Category> categories)
    {
        this.dataSource = categories;
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
            convertView = inflater.inflate(R.layout.category_edit_view, parent, false);

            viewHolder.colorView = convertView.findViewById(R.id.colorView);
            viewHolder.typeTextView = convertView.findViewById(R.id.categoryTextView);

            convertView.setTag(viewHolder);
        } else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Category category = (Category) getItem(position);
        viewHolder.colorView.setBackgroundColor(category.getColor());
        viewHolder.typeTextView.setText(category.getType());

        return convertView;
    }

    private static class ViewHolder
    {
        private View colorView;
        private TextView typeTextView;
    }
}

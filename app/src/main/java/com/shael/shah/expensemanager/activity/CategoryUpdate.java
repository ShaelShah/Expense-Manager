package com.shael.shah.expensemanager.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.shael.shah.expensemanager.R;
import com.shael.shah.expensemanager.model.Category;
import com.shael.shah.expensemanager.utils.DataSingleton;

import java.util.List;

public class CategoryUpdate extends Activity {

    private List<Category> categories;
    private LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_update_layout);

        linearLayout = (LinearLayout) findViewById(R.id.categoryUpdateLinearLayout);
//        categories = DataSingleton.getInstance().getCategories();

        createCategoryRows();
    }

    private void createCategoryRows() {
        for (int i = 0; i < categories.size(); i++) {
            //TODO: Look into View.inflate method (specifically the 3rd parameter)
            View item = View.inflate(this, R.layout.category_select_row_layout, null);

            View colorBox = item.findViewById(R.id.colorView);
            colorBox.setBackgroundColor(categories.get(i).getColor());

            linearLayout.addView(item);
            linearLayout.addView(createSeparatorView());
        }
    }

    private View createSeparatorView() {
        View line = new View(this);
        line.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
        line.setBackgroundColor(Color.LTGRAY);

        return line;
    }
}

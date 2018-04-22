package com.shael.shah.expensemanager.activity.settings.category;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.shael.shah.expensemanager.R;
import com.shael.shah.expensemanager.model.Category;
import com.shael.shah.expensemanager.utils.DataSingleton;

import java.util.List;

public class EditCategoriesActivity extends Activity
{
    private DataSingleton instance;

    private ListView categoryListView;
    private List<Category> categories;

    @Override
    protected void onCreate(Bundle savedInstance)
    {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_edit_categories);

        instance = DataSingleton.getInstance();
        categories = instance.getCategories();

        Toolbar toolbar = findViewById(R.id.editCategoriesToolbar);
        setActionBar(toolbar);

        final CategoryAdapter adapter = new CategoryAdapter(this, categories);
        categoryListView = findViewById(R.id.categoryListView);
        categoryListView.setAdapter(adapter);
        categoryListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        categoryListView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener()
        {

            @Override
            public void onItemCheckedStateChanged(ActionMode actionMode, int i, long l, boolean b)
            {
                int checkedItems = categoryListView.getCheckedItemCount();
                actionMode.setTitle(String.valueOf(checkedItems) + " Selected");
                actionMode.invalidate();
            }

            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu)
            {
                MenuInflater inflater = actionMode.getMenuInflater();
                inflater.inflate(R.menu.edit_category_context, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu)
            {
                int checkedItems = categoryListView.getCheckedItemCount();
                MenuItem item = menu.findItem(R.id.edit_category);
                if (checkedItems == 1)
                {
                    item.setVisible(true).setEnabled(true);
                }
                else
                {
                    item.setVisible(false).setEnabled(false);
                }

                return true;
            }

            @Override
            public boolean onActionItemClicked(final ActionMode actionMode, MenuItem menuItem)
            {
                switch (menuItem.getItemId())
                {
                    case R.id.edit_category:
                        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) menuItem.getMenuInfo();
                        final Category category = (Category) adapter.getItem(menuInfo.position);
                        updateCategory(category);
                        actionMode.finish(); // Action picked, so close the CAB
                        return true;

                    case R.id.delete_category:
                        AlertDialog.Builder builder = new AlertDialog.Builder(EditCategoriesActivity.this);
                        builder.setTitle("Confirm Delete");
                        builder.setMessage("Are you sure you wanted to delete the selected categories? All expenses under those categories will also be deleted.");

                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int ID)
                            {
                                dialog.dismiss();
                            }
                        });

                        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int ID)
                            {
                                SparseBooleanArray selectedItems = categoryListView.getCheckedItemPositions();
                                for (int i = categoryListView.getCount() - 1; i >= 0; i--)
                                {
                                    if (selectedItems.get(i))
                                    {
                                        Category cat = categories.get(i);
                                        if (!instance.deleteAllExpensesFromCategory(cat))
                                        {
                                            // TODO: Proper error handling
                                        }
                                        if (!instance.deleteCategory(cat))
                                        {
                                            Toast.makeText(EditCategoriesActivity.this, "Could not delete Category", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                }

                                dialog.dismiss();
                                adapter.notifyDataSetChanged();
                                actionMode.finish();
                            }
                        });

                        AlertDialog dialog = builder.create();
                        dialog.show();

                    default:
                        return false;
                }
            }

            @Override
            public void onDestroyActionMode(ActionMode actionMode)
            {
            }
        });

        categoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                final Category category = (Category) adapterView.getItemAtPosition(i);
                updateCategory(category);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.edit_categories_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.add_category:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setView(R.layout.add_category_dialog);
                builder.setTitle(R.string.add_category);

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int ID)
                    {
                        dialog.dismiss();
                    }
                });

                builder.setPositiveButton("Add", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int ID)
                    {
                        AlertDialog categoryDialog = (AlertDialog) dialog;

                        EditText categoryNameEditText = categoryDialog.findViewById(R.id.categoryNameEditText);
                        int color = ((ColorDrawable) categoryDialog.findViewById(R.id.categoryColorView).getBackground()).getColor();
                        Category category = instance.addCategory(categoryNameEditText.getText().toString(), color);
                        if (category != null)
                            Toast.makeText(getApplicationContext(), "Category Added", Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(getApplicationContext(), "Category Already Exists", Toast.LENGTH_LONG).show();

                        dialog.dismiss();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();

                View categoryColorView = dialog.findViewById(R.id.categoryColorView);
                categoryColorView.setBackgroundColor(instance.getCurrentColor());

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateCategory(final Category category)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(EditCategoriesActivity.this);
        builder.setView(R.layout.add_category_dialog);
        builder.setTitle(R.string.add_category);

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int ID)
            {
                dialog.dismiss();
            }
        });

        builder.setPositiveButton("Update", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int ID)
            {
                AlertDialog categoryDialog = (AlertDialog) dialog;

                EditText categoryNameEditText = categoryDialog.findViewById(R.id.categoryNameEditText);
                int color = ((ColorDrawable) categoryDialog.findViewById(R.id.categoryColorView).getBackground()).getColor();
                if (instance.updateCategory(category, categoryNameEditText.getText().toString(), color))
                    Toast.makeText(getApplicationContext(), "Category Updated", Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(getApplicationContext(), "Category could not be Updated", Toast.LENGTH_LONG).show();

                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

        EditText categoryNameEditText = dialog.findViewById(R.id.categoryNameEditText);
        View categoryColorView = dialog.findViewById(R.id.categoryColorView);
        categoryNameEditText.setText(category.getType());
        categoryColorView.setBackgroundColor(category.getColor());
    }
}

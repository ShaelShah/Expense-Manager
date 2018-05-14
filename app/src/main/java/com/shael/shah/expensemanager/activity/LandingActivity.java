package com.shael.shah.expensemanager.activity;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toolbar;

import com.shael.shah.expensemanager.R;
import com.shael.shah.expensemanager.activity.add.AddExpenseActivity;
import com.shael.shah.expensemanager.activity.add.AddIncomeActivity;
import com.shael.shah.expensemanager.fragment.display.OverviewFragment;
import com.shael.shah.expensemanager.fragment.display.SettingsFragment;
import com.shael.shah.expensemanager.utils.DataSingleton;
import com.shael.shah.expensemanager.utils.DataSingleton.LandingFragment;

public class LandingActivity extends Activity
{

    /*****************************************************************
     * Constants
     ******************************************************************/

    private static final int REQUEST_ADD = 1;

    /*****************************************************************
     * Private Variables
     ******************************************************************/

    private DataSingleton instance;
    private LandingFragment currentFragmentID;
    private Fragment currentFragment;
    private ActionBarDrawerToggle drawerToggle;
    private DrawerLayout drawerLayout;

    /*****************************************************************
     * Lifecycle Methods
     *****************************************************************/

    /*
     *  Initial method called by the system during app startup.
     *  Responsible for getting a copy of all expenses and categories.
     *  Also responsible for setting up of the initial GUI.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        // TODO: Do this asynchronously
        instance = DataSingleton.init(this);

        //Setup toolbar
        Toolbar toolbar = findViewById(R.id.mainActivityToolbar);
        setActionBar(toolbar);

        //Find views to work with during this activity
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.navigationView);

        //Helper functions
        setupDrawer();
        if (getActionBar() != null)
        {
            getActionBar().setDisplayHomeAsUpEnabled(true);
            getActionBar().setHomeButtonEnabled(true);
        }

        //Action listeners
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener()
        {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item)
            {
                int id = item.getItemId();

                switch (id)
                {
                    case R.id.overview:
                        if (currentFragmentID != LandingFragment.OVERVIEW)
                        {
                            currentFragment = new OverviewFragment();
                            currentFragmentID = LandingFragment.OVERVIEW;
                        }
                        break;

                    case R.id.history:
                        break;

                    case R.id.settings:
                        if (currentFragmentID != LandingFragment.SETTINGS)
                        {
                            currentFragment = new SettingsFragment();
                            currentFragmentID = LandingFragment.SETTINGS;
                        }
                        break;
                }

                if (currentFragment != null)
                    getFragmentManager().beginTransaction().replace(R.id.fragmentFrameLayout, currentFragment).commit();

                DrawerLayout drawer = findViewById(R.id.drawer_layout);
                drawer.closeDrawer(Gravity.START);
                return true;
            }
        });

        //Setup initial fragment
        if (savedInstanceState == null)
        {
            currentFragmentID = LandingFragment.OVERVIEW;
            currentFragment = new OverviewFragment();
            getFragmentManager().beginTransaction().add(R.id.fragmentFrameLayout, currentFragment).commit();
        }
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        instance.updateSettings();
        DataSingleton.destroyInstance();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == REQUEST_ADD)
        {
            if (resultCode == Activity.RESULT_OK)
            {
                if (currentFragmentID == LandingFragment.OVERVIEW)
                {
                    ((OverviewFragment) currentFragment).updateDisplay();

                    final Snackbar snackbar = Snackbar.make(findViewById(R.id.fragmentOverviewRoot), "Transaction Added", Snackbar.LENGTH_LONG);
                    snackbar.setAction("Dismiss", new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View view)
                        {
                            snackbar.dismiss();
                        }
                    });
                    snackbar.show();
                }
            }
        }
    }

    /*****************************************************************
     * Menu Methods
     *****************************************************************/

    /*
     *  Method called by Android to handle all menu operations.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        if (drawerToggle.onOptionsItemSelected(item))
        {
            return true;
        }

        switch (item.getItemId())
        {
            case R.id.add_expense:
                Intent addExpenseIntent = new Intent(this, AddExpenseActivity.class);
                startActivityForResult(addExpenseIntent, REQUEST_ADD);
                return true;

            case R.id.add_income:
                Intent addIncomeIntent = new Intent(this, AddIncomeActivity.class);
                startActivityForResult(addIncomeIntent, REQUEST_ADD);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*
     *  Method called by Android to set up layout for the toolbar.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    /*****************************************************************
     * GUI Setup Methods
     *****************************************************************/

    private void setupDrawer()
    {
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_open, R.string.drawer_close);
        drawerToggle.setDrawerIndicatorEnabled(true);
        drawerLayout.addDrawerListener(drawerToggle);
    }
}

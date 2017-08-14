package com.shael.shah.expensemanager.activity;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toolbar;

import com.shael.shah.expensemanager.R;
import com.shael.shah.expensemanager.fragment.OverviewFragment;
import com.shael.shah.expensemanager.fragment.SettingsFragment;

public class LandingActivity extends Activity {

    /*****************************************************************
     * Private Variables
     ******************************************************************/

    private static final String EXTRA_EXPENSE_TYPE = "com.shael.shah.expensemanager.EXTRA_EXPENSE_TYPE";

    private static String currentFragment = "OVERVIEW";

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.landing_activity);

        //Setup toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.mainActivityToolbar);
        setActionBar(toolbar);

        //Find views to work with during this activity
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigationView);

        //Helper functions
        setupDrawer();
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        //Action listeners
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                int id = item.getItemId();

                Fragment fragment = null;
                switch (id) {
                    case R.id.overview:
                        if (!currentFragment.equals("OVERVIEW")) {
                            fragment = new OverviewFragment();
                            currentFragment = "OVERVIEW";
                        }
                        break;
                    case R.id.history:
                        if (!currentFragment.equals("HISTORY")) {
                            //fragment = new HistoryFragment();
                            currentFragment = "HISTORY";
                        }
                        break;
                    case R.id.settings:
                        if (!currentFragment.equals("SETTINGS")) {
                            fragment = new SettingsFragment();
                            currentFragment = "SETTINGS";
                        }
                        break;
                }

                if (fragment != null)
                    getFragmentManager().beginTransaction().replace(R.id.fragmentFrameLayout, fragment).commit();

                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(Gravity.START);
                return true;
            }
        });

        //Setup initial fragment
        getFragmentManager().beginTransaction().add(R.id.fragmentFrameLayout, new OverviewFragment()).commit();
    }

    /*****************************************************************
     * Menu Methods
     *****************************************************************/

    /*
     *  Method called by Android to handle all menu operations.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent addExpenseIntent = new Intent(this, AddExpenseActivity.class);

        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId()) {

            case R.id.add_expense:
                addExpenseIntent.putExtra(EXTRA_EXPENSE_TYPE, "Normal");
                startActivity(addExpenseIntent);
                return true;

            case R.id.add_recurring_expense:
                addExpenseIntent.putExtra(EXTRA_EXPENSE_TYPE, "Recurring");
                startActivity(addExpenseIntent);
                return false;

            case R.id.add_income:
                addExpenseIntent.putExtra(EXTRA_EXPENSE_TYPE, "Income");
                startActivity(addExpenseIntent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*
     *  Method called by Android to set up layout for the toolbar.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    /*****************************************************************
     * GUI Setup Methods
     *****************************************************************/

    private void setupDrawer() {
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_open, R.string.drawer_close);
        drawerToggle.setDrawerIndicatorEnabled(true);
        drawerLayout.setDrawerListener(drawerToggle);
    }
}
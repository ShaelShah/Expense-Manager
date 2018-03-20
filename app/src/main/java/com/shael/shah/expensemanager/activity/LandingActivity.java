package com.shael.shah.expensemanager.activity;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toolbar;

import com.shael.shah.expensemanager.R;
import com.shael.shah.expensemanager.activity.add.AddExpenseActivity;
import com.shael.shah.expensemanager.activity.add.AddIncomeActivity;
import com.shael.shah.expensemanager.fragment.display.OverviewFragment;
import com.shael.shah.expensemanager.fragment.display.SettingsFragment;
import com.shael.shah.expensemanager.utils.DataSingleton;
import com.shael.shah.expensemanager.utils.DataSingleton.LandingFragment;

public class LandingActivity extends Activity {

    /*****************************************************************
     * Private Variables
     ******************************************************************/

    private DataSingleton instance;
    private LandingFragment currentFragment;
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
        setContentView(R.layout.activity_landing);

        //TODO: Do this asynchronously
        instance = DataSingleton.init(this);

        //Setup toolbar
        Toolbar toolbar = findViewById(R.id.mainActivityToolbar);
        setActionBar(toolbar);

        //Find views to work with during this activity
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.navigationView);

        //Helper functions
        setupDrawer();
        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
            getActionBar().setHomeButtonEnabled(true);
        }

        //Action listeners
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                Fragment fragment = null;
                switch (id) {
                    case R.id.overview:
                        if (currentFragment != LandingFragment.OVERVIEW) {
                            fragment = new OverviewFragment();
                            currentFragment = LandingFragment.OVERVIEW;
                        }
                        break;

                    case R.id.history:
                        break;

                    case R.id.settings:
                        if (currentFragment != LandingFragment.SETTINGS) {
                            fragment = new SettingsFragment();
                            currentFragment = LandingFragment.SETTINGS;
                        }
                        break;
                }

                if (fragment != null)
                    getFragmentManager().beginTransaction().replace(R.id.fragmentFrameLayout, fragment).commit();

                DrawerLayout drawer = findViewById(R.id.drawer_layout);
                drawer.closeDrawer(Gravity.START);
                return true;
            }
        });

        //Setup initial fragment
        if (savedInstanceState == null)
            getFragmentManager().beginTransaction().add(R.id.fragmentFrameLayout, new OverviewFragment()).commit();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //instance.updateDatabase();
        instance.updateSettings();
        DataSingleton.destroyInstance();
    }

    /*****************************************************************
     * Menu Methods
     *****************************************************************/

    /*
     *  Method called by Android to handle all menu operations.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (drawerToggle.onOptionsItemSelected(item))
            return true;

        switch (item.getItemId()) {
            case R.id.add_expense:
                Intent addExpenseIntent = new Intent(this, AddExpenseActivity.class);
                startActivity(addExpenseIntent);
                return true;

            case R.id.add_income:
                Intent addIncomeIntent = new Intent(this, AddIncomeActivity.class);
                startActivity(addIncomeIntent);
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
        drawerLayout.addDrawerListener(drawerToggle);
    }
}

/*
 * Copyright (c) 2016, University of Oslo
 *
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.client.sdk.ui.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.NavigationView.OnNavigationItemSelectedListener;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.hisp.dhis.client.sdk.ui.R;
import org.hisp.dhis.client.sdk.ui.fragments.AboutFragment;
import org.hisp.dhis.client.sdk.ui.fragments.HelpFragment;
import org.hisp.dhis.client.sdk.ui.fragments.WrapperFragment;

import static org.hisp.dhis.client.sdk.ui.utils.Preconditions.isNull;


public abstract class AbsHomeActivity extends AppCompatActivity
        implements OnNavigationItemSelectedListener, DrawerListener, INavigationCallback {

    private static final int APPS_GROUP_ID = 234253562;
    private static final int APPS_DATA_CAPTURE_ID = 234534541;
    private static final int APPS_EVENT_CAPTURE_ID = 777221321;
    private static final int APPS_TRACKER_CAPTURE_ID = 88234512;
    private static final int APPS_DASHBOARD_ID = 45345124;

    private static final int APPS_DATA_CAPTURE_ORDER = 100;
    private static final int APPS_EVENT_CAPTURE_ORDER = 101;
    private static final int APPS_TRACKER_CAPTURE_ORDER = 102;
    private static final int APPS_DASHBOARD_ORDER = 103;

    private static final String APPS_DATA_CAPTURE_PACKAGE = "org.dhis2.mobile";
    private static final String APPS_EVENT_CAPTURE_PACKAGE = "org.hisp.dhis.android.eventcapture";
    private static final String APPS_TRACKER_CAPTURE_PACKAGE = "org.hisp.dhis.android.trackercapture";
    private static final String APPS_DASHBOARD_PACKAGE = "org.hisp.dhis.android.dashboard";

    // Drawer layout
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    // Showing information about user in navigation drawer
    private TextView usernameLetter;
    private TextView username;
    private TextView userInfo;

    // Delaying attachment of fragment
    // in order to avoid animation lag
    private Runnable pendingRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerLayout.setDrawerListener(this);

        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.inflateMenu(R.menu.menu_drawer_default);

        ViewGroup navigationHeader = (ViewGroup) getLayoutInflater()
                .inflate(R.layout.navigation_header, navigationView, false);
        usernameLetter = (TextView) navigationHeader.findViewById(R.id.textview_username_letter);
        username = (TextView) navigationHeader.findViewById(R.id.textview_username);
        userInfo = (TextView) navigationHeader.findViewById(R.id.textview_user_info);

        navigationView.addHeaderView(navigationHeader);

        addAppsToMenu();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        boolean isSelected = false;
        int menuItemId = menuItem.getItemId();

        if (menuItemId == APPS_DASHBOARD_ID) {
            isSelected = openApp(APPS_DASHBOARD_PACKAGE);
        } else if (menuItemId == APPS_DATA_CAPTURE_ID) {
            isSelected = openApp(APPS_DATA_CAPTURE_PACKAGE);
        } else if (menuItemId == APPS_EVENT_CAPTURE_ID) {
            isSelected = openApp(APPS_EVENT_CAPTURE_PACKAGE);
        } else if (menuItemId == APPS_TRACKER_CAPTURE_ID) {
            isSelected = openApp(APPS_TRACKER_CAPTURE_PACKAGE);
        } else if (menuItemId == R.id.drawer_item_profile) {
            attachFragmentDelayed(getProfileFragment());
            isSelected = true;
        } else if (menuItemId == R.id.drawer_item_settings) {
            attachFragmentDelayed(getSettingsFragment());
            isSelected = true;
        } else if (menuItemId == R.id.drawer_item_help) {
            attachFragment(getHelpFragment());
            isSelected = true;
        } else if (menuItemId == R.id.drawer_item_about) {
            attachFragment(getAboutFragment());
            isSelected = true;
        }

        isSelected = onItemSelected(menuItem) || isSelected;
        if (isSelected) {
            drawerLayout.closeDrawers();
            navigationView.setCheckedItem(menuItem.getItemId());
        }

        return isSelected;
    }

    @Override
    public void onDrawerOpened(View drawerView) {
        pendingRunnable = null;
    }

    @Override
    public void onDrawerClosed(View drawerView) {
        if (pendingRunnable != null) {
            new Handler().post(pendingRunnable);
        }

        pendingRunnable = null;
    }

    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {
        // stub implementation
    }

    @Override
    public void onDrawerStateChanged(int newState) {
        // stub implementation
    }

    @Override
    public void toggleNavigationDrawer() {
        if (drawerLayout.isDrawerOpen(navigationView)) {
            drawerLayout.closeDrawer(navigationView);
        } else {
            drawerLayout.openDrawer(navigationView);
        }
    }

    protected void attachFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commit();
    }

    protected void attachFragmentDelayed(final Fragment fragment) {
        isNull(fragment, "Fragment must not be null");

        pendingRunnable = new Runnable() {

            @Override
            public void run() {
                attachFragment(fragment);
            }
        };
    }

    private void addAppsToMenu() {
        if (getAppsMenu() != null) {
            if (isAppInstalled(APPS_DASHBOARD_PACKAGE)) {
                MenuItem menuItem = getAppsMenu().add(APPS_GROUP_ID, APPS_DASHBOARD_ID,
                        APPS_DASHBOARD_ORDER, R.string.drawer_item_app_dashboard);
                menuItem.setIcon(R.drawable.ic_dashboard);
            }

            if (isAppInstalled(APPS_EVENT_CAPTURE_PACKAGE)) {
                MenuItem menuItem = getAppsMenu().add(APPS_GROUP_ID, APPS_EVENT_CAPTURE_ID,
                        APPS_EVENT_CAPTURE_ORDER, R.string.drawer_item_app_event_capture);
                menuItem.setIcon(R.drawable.ic_event_capture);
            }

            if (isAppInstalled(APPS_TRACKER_CAPTURE_PACKAGE)) {
                MenuItem menuItem = getAppsMenu().add(APPS_GROUP_ID, APPS_TRACKER_CAPTURE_ID,
                        APPS_TRACKER_CAPTURE_ORDER, R.string.drawer_item_app_tracker_capture);
                menuItem.setIcon(R.drawable.ic_tracker_capture);
            }

            if (isAppInstalled(APPS_DATA_CAPTURE_PACKAGE)) {
                MenuItem menuItem = getAppsMenu().add(APPS_GROUP_ID, APPS_DATA_CAPTURE_ID,
                        APPS_DATA_CAPTURE_ORDER, R.string.drawer_item_app_data_capture);
                menuItem.setIcon(R.drawable.ic_data_capture);
            }
        }
    }

    private boolean isAppInstalled(String packageName) {
        String currentApp = getApplicationContext().getPackageName();
        if (currentApp.equals(packageName)) {
            return false;
        }

        PackageManager packageManager = getBaseContext().getPackageManager();
        try {
            // using side effect of calling getPackageInfo() method
            packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private Menu getAppsMenu() {
        Menu menu = getNavigationView().getMenu();
        if (menu == null) {
            return null;
        }

        for (int index = 0; index < menu.size(); index++) {
            MenuItem item = menu.getItem(index);
            if (item.getItemId() == R.id.drawer_section_apps) {
                return item.getSubMenu();
            }
        }

        return null;
    }

    private boolean openApp(String packageName) {
        Intent intent = getBaseContext().getPackageManager()
                .getLaunchIntentForPackage(packageName);
        if (intent != null) {
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            getBaseContext().startActivity(intent);
            return true;
        }

        return false;
    }

    @NonNull
    protected NavigationView getNavigationView() {
        return navigationView;
    }

    @NonNull
    protected DrawerLayout getDrawerLayout() {
        return drawerLayout;
    }

    @NonNull
    protected TextView getUsernameTextView() {
        return username;
    }

    @NonNull
    protected TextView getUserInfoTextView() {
        return userInfo;
    }

    @NonNull
    protected TextView getUsernameLetterTextView() {
        return usernameLetter;
    }

    @NonNull
    protected Fragment getHelpFragment() {
        return WrapperFragment.newInstance(HelpFragment.class,
                getString(R.string.drawer_item_help));
    }

    @NonNull
    protected Fragment getAboutFragment() {
        return WrapperFragment.newInstance(AboutFragment.class,
                getString(R.string.drawer_item_about));
    }

    @NonNull
    protected abstract Fragment getProfileFragment();

    @NonNull
    protected abstract Fragment getSettingsFragment();

    protected abstract boolean onItemSelected(@NonNull MenuItem item);
}
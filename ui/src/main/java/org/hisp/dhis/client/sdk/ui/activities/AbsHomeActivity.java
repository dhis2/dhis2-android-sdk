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
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.NavigationView;
import android.support.design.widget.NavigationView.OnNavigationItemSelectedListener;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.hisp.dhis.client.sdk.ui.R;
import org.hisp.dhis.client.sdk.ui.SettingPreferences;
import org.hisp.dhis.client.sdk.ui.fragments.HelpFragment;
import org.hisp.dhis.client.sdk.ui.fragments.InformationFragment;
import org.hisp.dhis.client.sdk.ui.fragments.WrapperFragment;

import static org.hisp.dhis.client.sdk.utils.Preconditions.isNull;

// TODO add support for custom applications in navigation drawer
public abstract class AbsHomeActivity extends BaseActivity
        implements OnNavigationItemSelectedListener, DrawerListener, NavigationCallback,
        OnBackPressedFromFragmentCallback {

    private static final String APPS_DASHBOARD_PACKAGE =
            "org.hisp.dhis.android.dashboard";
    private static final String APPS_DATA_CAPTURE_PACKAGE =
            "org.dhis2.mobile";
    private static final String APPS_EVENT_CAPTURE_PACKAGE =
            "org.hisp.dhis.android.eventcapture";
    private static final String APPS_TRACKER_CAPTURE_PACKAGE =
            "org.hisp.dhis.android.trackercapture";
    private static final String APPS_TRACKER_CAPTURE_REPORTS_PACKAGE =
            "org.hispindia.bidtrackerreports";

    private static final int DEFAULT_ORDER_IN_CATEGORY = 100;

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
        SettingPreferences.init(getApplicationContext());
        setContentView(R.layout.activity_home);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerLayout.addDrawerListener(this);

        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.inflateMenu(R.menu.menu_drawer_default);

        ViewGroup navigationHeader = (ViewGroup) getLayoutInflater()
                .inflate(R.layout.navigation_header, navigationView, false);
        usernameLetter = (TextView) navigationHeader.findViewById(R.id.textview_username_letter);
        username = (TextView) navigationHeader.findViewById(R.id.textview_username);
        userInfo = (TextView) navigationHeader.findViewById(R.id.textview_user_info);

        navigationView.addHeaderView(navigationHeader);

        /* configuring visibility of apps in navigation view */
        navigationView.getMenu().findItem(R.id.drawer_item_dashboard).setVisible(
                isAppInstalled(APPS_DASHBOARD_PACKAGE));
        navigationView.getMenu().findItem(R.id.drawer_item_data_capture).setVisible(
                isAppInstalled(APPS_DATA_CAPTURE_PACKAGE));
        navigationView.getMenu().findItem(R.id.drawer_item_event_capture).setVisible(
                isAppInstalled(APPS_EVENT_CAPTURE_PACKAGE));
        navigationView.getMenu().findItem(R.id.drawer_item_tracker_capture).setVisible(
                isAppInstalled(APPS_TRACKER_CAPTURE_PACKAGE));
        navigationView.getMenu().findItem(R.id.drawer_item_tracker_capture_reports).setVisible(
                isAppInstalled(APPS_TRACKER_CAPTURE_REPORTS_PACKAGE));
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        boolean isSelected = false;
        int menuItemId = menuItem.getItemId();

        if (menuItemId == R.id.drawer_item_dashboard) {
            isSelected = openApp(APPS_DASHBOARD_PACKAGE);
        } else if (menuItemId == R.id.drawer_item_data_capture) {
            isSelected = openApp(APPS_DATA_CAPTURE_PACKAGE);
        } else if (menuItemId == R.id.drawer_item_event_capture) {
            isSelected = openApp(APPS_EVENT_CAPTURE_PACKAGE);
        } else if (menuItemId == R.id.drawer_item_tracker_capture) {
            isSelected = openApp(APPS_TRACKER_CAPTURE_PACKAGE);
        } else if (menuItemId == R.id.drawer_item_tracker_capture_reports) {
            isSelected = openApp(APPS_TRACKER_CAPTURE_REPORTS_PACKAGE);
        } else if (menuItemId == R.id.drawer_item_profile) {
            attachFragmentDelayed(getProfileFragment());
            isSelected = true;
        } else if (menuItemId == R.id.drawer_item_settings) {
            attachFragmentDelayed(getSettingsFragment());
            isSelected = true;
        } else if (menuItemId == R.id.drawer_item_information) {
            attachFragment(getInformationFragment());
            isSelected = true;
        }
        /*else if (menuItemId == R.id.drawer_item_help) {
            attachFragment(getHelpFragment());
            isSelected = true;
        } */
        isSelected = onItemSelected(menuItem) || isSelected;
        if (isSelected) {
            navigationView.setCheckedItem(menuItemId);
            drawerLayout.closeDrawers();
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

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
            return;
        }

        super.onBackPressed();
    }

    @Override
    public boolean onBackPressedFromFragment() {
        // When back button is pressed from a fragment, show the first menu item
        MenuItem firstMenuItem = navigationView.getMenu().getItem(0);
        onNavigationItemSelected(firstMenuItem);
        return true;
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

    protected boolean openApp(String packageName) {
        Intent intent = getBaseContext().getPackageManager()
                .getLaunchIntentForPackage(packageName);
        if (intent != null) {
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            getBaseContext().startActivity(intent);
            return true;
        }

        return false;
    }

    protected MenuItem addMenuItem(int menuItemId, @DrawableRes int icon, @StringRes int title) {
        return addMenuItem(menuItemId, ContextCompat.getDrawable(this, icon), getString(title));
    }

    protected MenuItem addMenuItem(int menuItemId, Drawable icon, CharSequence title) {
        MenuItem menuItem = navigationView.getMenu().add(
                R.id.drawer_group_main, menuItemId, DEFAULT_ORDER_IN_CATEGORY, title);
        menuItem.setIcon(icon);
        menuItem.setCheckable(true);
        return menuItem;
    }

    protected boolean removeMenuItem(int menuItemId) {
        MenuItem menuItem = getNavigationView().getMenu().findItem(menuItemId);
        if (menuItem != null) {
            getNavigationView().getMenu().removeItem(menuItem.getItemId());
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

    protected void setSynchronizedMessage(@NonNull CharSequence message) {
        String formattedMessage = String.format(getString(
                R.string.drawer_item_synchronized), message);
        navigationView.getMenu().findItem(R.id.drawer_item_synchronized)
                .setTitle(formattedMessage);
    }

    protected Fragment getInformationFragment() {
        return WrapperFragment.newInstance(InformationFragment.class,
                getString(R.string.drawer_item_information),
                new Bundle());
    }

    @NonNull
    protected Fragment getHelpFragment() {
        return WrapperFragment.newInstance(HelpFragment.class,
                getString(R.string.drawer_item_help));
    }

    @NonNull
    protected abstract Fragment getProfileFragment();

    @NonNull
    protected abstract Fragment getSettingsFragment();

    protected abstract boolean onItemSelected(@NonNull MenuItem item);
}
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

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.MenuRes;
import android.support.design.widget.NavigationView;
import android.support.design.widget.NavigationView.OnNavigationItemSelectedListener;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.hisp.dhis.client.sdk.ui.R;

import static org.hisp.dhis.client.sdk.ui.utils.Preconditions.isNull;


public abstract class AbsHomeActivity extends AppCompatActivity
        implements OnNavigationItemSelectedListener, DrawerListener, INavigationCallback {

    // Drawer layout
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    // Showing information about user in navigation drawer
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
        navigationView.inflateMenu(getNavigationMenu());

        ViewGroup navigationHeader = (ViewGroup) getLayoutInflater()
                .inflate(R.layout.navigation_header, navigationView, false);
        username = (TextView) navigationHeader.findViewById(R.id.drawer_user_name);
        userInfo = (TextView) navigationHeader.findViewById(R.id.drawer_user_info);

        navigationView.addHeaderView(navigationHeader);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        boolean isSelected = onItemSelected(menuItem);

        if (isSelected) {
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
        if (!drawerLayout.isDrawerOpen(Gravity.LEFT)) {
            drawerLayout.openDrawer(Gravity.LEFT);
        } else {
            drawerLayout.closeDrawer(Gravity.LEFT);
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

    protected TextView getUsernameTextView() {
        return username;
    }

    protected TextView getUserInfoTextView() {
        return userInfo;
    }

    @MenuRes
    protected abstract int getNavigationMenu();

    protected abstract boolean onItemSelected(MenuItem item);
}
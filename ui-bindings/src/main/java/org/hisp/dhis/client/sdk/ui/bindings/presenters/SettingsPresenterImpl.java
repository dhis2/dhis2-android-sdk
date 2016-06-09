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

package org.hisp.dhis.client.sdk.ui.bindings.presenters;

import android.content.ContentResolver;

import org.hisp.dhis.client.sdk.ui.AppPreferences;
import org.hisp.dhis.client.sdk.ui.bindings.commons.AppAccountManager;
import org.hisp.dhis.client.sdk.ui.bindings.views.SettingsView;
import org.hisp.dhis.client.sdk.ui.bindings.views.View;

/**
 * This is the presenter, using MVP.
 * This class controls what is shown in the view.
 * <p/>
 * Created by Vladislav Georgiev Alfredov on 1/15/16.
 */
public class SettingsPresenterImpl implements SettingsPresenter {
    public static final String TAG = SettingsPresenterImpl.class.getSimpleName();

    private SettingsView settingsView;

    private final AppPreferences appPreferences;
    private final AppAccountManager appAccountManager;

    public SettingsPresenterImpl(AppPreferences appPreferences, AppAccountManager appAccountManager) {
        this.appPreferences = appPreferences;
        this.appAccountManager = appAccountManager;
    }

    @Override
    public void attachView(View view) {
        settingsView = (SettingsView) view;
    }

    @Override
    public void detachView() {
        settingsView = null;
    }

    @Override
    public void synchronize() {
        appAccountManager.syncNow();
    }

    @Override
    public void setUpdateFrequency(int minutes) {
        appPreferences.setBackgroundSyncFrequency(minutes);
        appAccountManager.setPeriodicSync(minutes);
    }

    @Override
    public int getUpdateFrequency() {
        return appPreferences.getBackgroundSyncFrequency();
    }

    @Override
    public void setBackgroundSynchronisation(Boolean enabled, String warning) {
        appPreferences.setBackgroundSyncState(enabled);

        if (enabled) {
            if (!ContentResolver.getMasterSyncAutomatically()) {
                //display a notification to the user to enable synchronization globally.
                settingsView.showMessage(warning);
            }
            synchronize();
            appAccountManager.setPeriodicSync(getUpdateFrequency());
        } else {
            appAccountManager.removePeriodicSync();
        }
    }

    @Override
    public Boolean getBackgroundSynchronisation() {
        return appPreferences.getBackgroundSyncState();
    }

    @Override
    public Boolean getCrashReports() {
        return appPreferences.getCrashReportsState();
    }

    @Override
    public void setCrashReports(Boolean enabled) {
        appPreferences.setCrashReportsState(enabled);
    }

    @Override
    public void setSyncNotifications(boolean isEnabled) {
        appPreferences.setSyncNotifications(isEnabled);
    }
}

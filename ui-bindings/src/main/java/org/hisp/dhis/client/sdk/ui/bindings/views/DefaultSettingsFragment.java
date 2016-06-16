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

package org.hisp.dhis.client.sdk.ui.bindings.views;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.View;

import org.hisp.dhis.client.sdk.ui.bindings.R;
import org.hisp.dhis.client.sdk.ui.bindings.commons.Inject;
import org.hisp.dhis.client.sdk.ui.bindings.presenters.SettingsPresenter;
import org.hisp.dhis.client.sdk.ui.fragments.AbsSettingsFragment;


public class DefaultSettingsFragment extends AbsSettingsFragment implements SettingsView {
    private String androidSyncWarning;
    private SettingsPresenter settingsPresenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        androidSyncWarning = getResources().getString(R.string.sys_sync_disabled_warning);

        Inject.getUserComponent().inject(this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        settingsPresenter.attachView(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        settingsPresenter.attachView(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        settingsPresenter.detachView();
    }

    @Override
    public boolean onBackgroundSynchronizationClick() {
        //stub implementation
        return false;
    }

    @Override
    public boolean onBackgroundSynchronizationChanged(boolean isEnabled) {
        settingsPresenter.setBackgroundSynchronisation(isEnabled, androidSyncWarning);
        return true;
    }

    @Override
    public boolean onSynchronizationPeriodClick() {
        return false;
    }

    @Override
    public boolean onSyncNotificationsChanged(boolean isEnabled) {
        settingsPresenter.setSyncNotifications(isEnabled);
        return true;
    }

    @Override
    public boolean onSynchronizationPeriodChanged(String newPeriodMinutes) {
        settingsPresenter.setUpdateFrequency(Integer.parseInt(newPeriodMinutes));
        return true;
    }

    @Override
    public boolean onCrashReportsClick() {
        return false;
    }

    @Override
    public boolean onCrashReportsChanged(boolean isEnabled) {
        settingsPresenter.setCrashReports(isEnabled);
        return true;
    }

    @Override
    public void showMessage(CharSequence msg) {
        if (getView() != null) {
            Snackbar.make(getView(), msg, Snackbar.LENGTH_LONG).show();
        }
    }

    public void setSettingsPresenter(SettingsPresenter settingsPresenter) {
        this.settingsPresenter = settingsPresenter;
    }
}

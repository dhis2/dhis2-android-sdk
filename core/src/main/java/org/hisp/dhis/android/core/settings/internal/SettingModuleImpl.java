/*
 *  Copyright (c) 2004-2022, University of Oslo
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *  Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *  Neither the name of the HISP project nor the names of its contributors may
 *  be used to endorse or promote products derived from this software without
 *  specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.core.settings.internal;

import org.hisp.dhis.android.core.settings.AppearanceSettingsObjectRepository;
import org.hisp.dhis.android.core.settings.AnalyticsSettingObjectRepository;
import org.hisp.dhis.android.core.settings.DataSetSettingsObjectRepository;
import org.hisp.dhis.android.core.settings.GeneralSettingObjectRepository;
import org.hisp.dhis.android.core.settings.ProgramSettingsObjectRepository;
import org.hisp.dhis.android.core.settings.SettingModule;
import org.hisp.dhis.android.core.settings.SynchronizationSettingObjectRepository;
import org.hisp.dhis.android.core.settings.SystemSettingCollectionRepository;
import org.hisp.dhis.android.core.settings.UserSettingsObjectRepository;

import javax.inject.Inject;

import dagger.Reusable;

@Reusable
public final class SettingModuleImpl implements SettingModule {

    private final SystemSettingCollectionRepository systemSetting;

    private final GeneralSettingObjectRepository generalSetting;
    private final DataSetSettingsObjectRepository dataSetSetting;
    private final ProgramSettingsObjectRepository programSetting;
    private final SynchronizationSettingObjectRepository synchronizationSetting;
    private final AnalyticsSettingObjectRepository analyticsSetting;
    private final UserSettingsObjectRepository userSettings;
    private final AppearanceSettingsObjectRepository appearanceSettings;

    @Inject
    SettingModuleImpl(SystemSettingCollectionRepository systemSettingRepository,
                      GeneralSettingObjectRepository generalSetting,
                      DataSetSettingsObjectRepository dataSetSetting,
                      ProgramSettingsObjectRepository programSetting,
                      SynchronizationSettingObjectRepository synchronizationSetting,
                      AnalyticsSettingObjectRepository analyticsSetting,
                      UserSettingsObjectRepository userSettings,
                      AppearanceSettingsObjectRepository appearanceSettings) {
        this.systemSetting = systemSettingRepository;
        this.generalSetting = generalSetting;
        this.dataSetSetting = dataSetSetting;
        this.programSetting = programSetting;
        this.synchronizationSetting = synchronizationSetting;
        this.analyticsSetting = analyticsSetting;
        this.userSettings = userSettings;
        this.appearanceSettings = appearanceSettings;
    }

    @Override
    public SystemSettingCollectionRepository systemSetting() {
        return systemSetting;
    }

    @Override
    public GeneralSettingObjectRepository generalSetting() {
        return generalSetting;
    }

    @Override
    public DataSetSettingsObjectRepository dataSetSetting() {
        return dataSetSetting;
    }

    @Override
    public ProgramSettingsObjectRepository programSetting() {
        return programSetting;
    }

    @Override
    public SynchronizationSettingObjectRepository synchronizationSettings() {
        return synchronizationSetting;
    }

    @Override
    public AnalyticsSettingObjectRepository analyticsSetting() {
        return analyticsSetting;
    }

    @Override
    public UserSettingsObjectRepository userSettings() {
        return userSettings;
    }

    @Override
    public AppearanceSettingsObjectRepository appearanceSettings() {
        return appearanceSettings;
    }
}
/*
 *  Copyright (c) 2004-2023, University of Oslo
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
package org.hisp.dhis.android.core.settings.internal

import org.hisp.dhis.android.core.settings.AnalyticsSettingObjectRepository
import org.hisp.dhis.android.core.settings.AppearanceSettingsObjectRepository
import org.hisp.dhis.android.core.settings.CustomIntentCollectionRepository
import org.hisp.dhis.android.core.settings.CustomIntentService
import org.hisp.dhis.android.core.settings.DataSetSettingsObjectRepository
import org.hisp.dhis.android.core.settings.GeneralSettingObjectRepository
import org.hisp.dhis.android.core.settings.LatestAppVersionObjectRepository
import org.hisp.dhis.android.core.settings.ProgramSettingsObjectRepository
import org.hisp.dhis.android.core.settings.SettingModule
import org.hisp.dhis.android.core.settings.SynchronizationSettingObjectRepository
import org.hisp.dhis.android.core.settings.SystemSettingCollectionRepository
import org.hisp.dhis.android.core.settings.UserSettingsObjectRepository
import org.koin.core.annotation.Singleton

@Singleton
@Suppress("LongParameterList")
internal class SettingModuleImpl(
    private val systemSetting: SystemSettingCollectionRepository,
    private val generalSetting: GeneralSettingObjectRepository,
    private val dataSetSetting: DataSetSettingsObjectRepository,
    private val programSetting: ProgramSettingsObjectRepository,
    private val synchronizationSetting: SynchronizationSettingObjectRepository,
    private val analyticsSetting: AnalyticsSettingObjectRepository,
    private val userSettings: UserSettingsObjectRepository,
    private val appearanceSettings: AppearanceSettingsObjectRepository,
    private val latestAppVersion: LatestAppVersionObjectRepository,
    private val customIntents: CustomIntentCollectionRepository,
    private val customIntentService: CustomIntentService,
) : SettingModule {
    override fun systemSetting(): SystemSettingCollectionRepository {
        return systemSetting
    }

    override fun generalSetting(): GeneralSettingObjectRepository {
        return generalSetting
    }

    override fun dataSetSetting(): DataSetSettingsObjectRepository {
        return dataSetSetting
    }

    override fun programSetting(): ProgramSettingsObjectRepository {
        return programSetting
    }

    override fun synchronizationSettings(): SynchronizationSettingObjectRepository {
        return synchronizationSetting
    }

    override fun analyticsSetting(): AnalyticsSettingObjectRepository {
        return analyticsSetting
    }

    override fun userSettings(): UserSettingsObjectRepository {
        return userSettings
    }

    override fun appearanceSettings(): AppearanceSettingsObjectRepository {
        return appearanceSettings
    }

    override fun latestAppVersion(): LatestAppVersionObjectRepository {
        return latestAppVersion
    }

    override fun customIntents(): CustomIntentCollectionRepository {
        return customIntents
    }

    override fun customIntentService(): CustomIntentService {
        return customIntentService
    }
}

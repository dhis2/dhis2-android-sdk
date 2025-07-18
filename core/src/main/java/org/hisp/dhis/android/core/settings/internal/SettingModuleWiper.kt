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

import org.hisp.dhis.android.core.wipe.internal.ModuleWiper
import org.hisp.dhis.android.core.wipe.internal.TableWiper
import org.hisp.dhis.android.persistence.settings.AnalyticsDhisVisualizationTableInfo
import org.hisp.dhis.android.persistence.settings.AnalyticsTeiAttributeTableInfo
import org.hisp.dhis.android.persistence.settings.AnalyticsTeiDataElementTableInfo
import org.hisp.dhis.android.persistence.settings.AnalyticsTeiIndicatorTableInfo
import org.hisp.dhis.android.persistence.settings.AnalyticsTeiSettingTableInfo
import org.hisp.dhis.android.persistence.settings.CustomIntentAttributeTableInfo
import org.hisp.dhis.android.persistence.settings.CustomIntentDataElementTableInfo
import org.hisp.dhis.android.persistence.settings.CustomIntentTableInfo
import org.hisp.dhis.android.persistence.settings.DataSetConfigurationSettingTableInfo
import org.hisp.dhis.android.persistence.settings.DataSetSettingTableInfo
import org.hisp.dhis.android.persistence.settings.FilterSettingTableInfo
import org.hisp.dhis.android.persistence.settings.GeneralSettingTableInfo
import org.hisp.dhis.android.persistence.settings.LatestAppVersionTableInfo
import org.hisp.dhis.android.persistence.settings.ProgramConfigurationSettingTableInfo
import org.hisp.dhis.android.persistence.settings.ProgramSettingTableInfo
import org.hisp.dhis.android.persistence.settings.SynchronizationSettingTableInfo
import org.hisp.dhis.android.persistence.settings.SystemSettingTableInfo
import org.hisp.dhis.android.persistence.settings.UserSettingsTableInfo
import org.koin.core.annotation.Singleton

@Singleton
internal class SettingModuleWiper(
    private val tableWiper: TableWiper,
) : ModuleWiper {
    override suspend fun wipeMetadata() {
        tableWiper.wipeTable(SystemSettingTableInfo.TABLE_INFO)
        tableWiper.wipeTable(GeneralSettingTableInfo.TABLE_INFO)
        tableWiper.wipeTable(DataSetSettingTableInfo.TABLE_INFO)
        tableWiper.wipeTable(ProgramSettingTableInfo.TABLE_INFO)
        tableWiper.wipeTable(SynchronizationSettingTableInfo.TABLE_INFO)
        tableWiper.wipeTable(FilterSettingTableInfo.TABLE_INFO)
        tableWiper.wipeTable(ProgramConfigurationSettingTableInfo.TABLE_INFO)
        tableWiper.wipeTable(DataSetConfigurationSettingTableInfo.TABLE_INFO)
        tableWiper.wipeTable(AnalyticsTeiSettingTableInfo.TABLE_INFO)
        tableWiper.wipeTable(AnalyticsTeiDataElementTableInfo.TABLE_INFO)
        tableWiper.wipeTable(AnalyticsTeiIndicatorTableInfo.TABLE_INFO)
        tableWiper.wipeTable(AnalyticsTeiAttributeTableInfo.TABLE_INFO)
        tableWiper.wipeTable(CustomIntentTableInfo.TABLE_INFO)
        tableWiper.wipeTable(CustomIntentDataElementTableInfo.TABLE_INFO)
        tableWiper.wipeTable(CustomIntentAttributeTableInfo.TABLE_INFO)
        tableWiper.wipeTable(UserSettingsTableInfo.TABLE_INFO)
        tableWiper.wipeTable(AnalyticsDhisVisualizationTableInfo.TABLE_INFO)
        tableWiper.wipeTable(LatestAppVersionTableInfo.TABLE_INFO)
    }

    override suspend fun wipeData() {
        // No data to wipe
    }
}

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
package org.hisp.dhis.android.core.settings

import kotlinx.coroutines.runBlocking
import org.hisp.dhis.android.core.arch.repositories.collection.ReadOnlyWithDownloadObjectRepository
import org.hisp.dhis.android.core.arch.repositories.`object`.internal.ReadOnlyAnyObjectWithDownloadRepositoryImpl
import org.hisp.dhis.android.core.settings.AppearanceSettingsHelper.getGlobal
import org.hisp.dhis.android.core.settings.AppearanceSettingsHelper.getSpecifics
import org.hisp.dhis.android.core.settings.AppearanceSettingsHelper.programToCompletionSpinner
import org.hisp.dhis.android.core.settings.AppearanceSettingsHelper.toCompletionSpinner
import org.hisp.dhis.android.core.settings.internal.AppearanceSettingCall
import org.hisp.dhis.android.core.settings.internal.DataSetConfigurationSettingStore
import org.hisp.dhis.android.core.settings.internal.FilterSettingStore
import org.hisp.dhis.android.core.settings.internal.ProgramConfigurationSettingStore
import org.hisp.dhis.android.network.settings.FilterSettingDTO.Companion.FILTERSETTING_GLOBAL_ID
import org.koin.core.annotation.Singleton

@Singleton(binds = [AppearanceSettingsObjectRepository::class])
@Suppress("TooManyFunctions")
class AppearanceSettingsObjectRepository internal constructor(
    private val filterSettingStore: FilterSettingStore,
    private val programConfigurationSettingStore: ProgramConfigurationSettingStore,
    private val dataSetConfigurationSettingStore: DataSetConfigurationSettingStore,
    appearanceSettingCall: AppearanceSettingCall,
) : ReadOnlyAnyObjectWithDownloadRepositoryImpl<AppearanceSettings>(appearanceSettingCall),
    ReadOnlyWithDownloadObjectRepository<AppearanceSettings> {
    override suspend fun getInternal(): AppearanceSettings? {
        val filters = filterSettingStore.selectAll()
        val programConfigurationSettingList = programConfigurationSettingStore.selectAll()
        val dataSetConfigurationSettingList = dataSetConfigurationSettingStore.selectAll()

        return if (filters.isEmpty() &&
            programConfigurationSettingList.isEmpty() &&
            dataSetConfigurationSettingList.isEmpty()
        ) {
            null
        } else {
            // FilterSorting
            val filterSorting = FilterSorting.builder()
                .home(getHomeFilters(filters))
                .dataSetSettings(getDataSetFilters(filters))
                .programSettings(getProgramFilters(filters))
                .build()

            // ProgramConfigurationSettings
            val programConfigurationSettings = ProgramConfigurationSettings.builder()
                .globalSettings(getGlobal(programConfigurationSettingList))
                .specificSettings(getSpecifics(programConfigurationSettingList))
                .build()

            val dataSetConfigurationSettings = DataSetConfigurationSettings.builder()
                .globalSettings(getGlobal(dataSetConfigurationSettingList))
                .specificSettings(getSpecifics(dataSetConfigurationSettingList))
                .build()

            AppearanceSettings.builder()
                .filterSorting(filterSorting)
                .programConfiguration(programConfigurationSettings)
                .dataSetConfiguration(dataSetConfigurationSettings)
                .completionSpinner(programToCompletionSpinner(programConfigurationSettings))
                .build()
        }
    }

    fun getHomeFilters(): Map<HomeFilter, FilterSetting>? {
        return blockingGet()?.filterSorting()?.home()
    }

    fun getDataSetFiltersByUid(uid: String?): Map<DataSetFilter, FilterSetting>? {
        val dataSetSettings = blockingGet()?.filterSorting()?.dataSetSettings()

        return dataSetSettings?.specificSettings()?.get(uid)
            ?: dataSetSettings?.globalSettings()
    }

    fun getTrackedEntityTypeFilters(): Map<ProgramFilter, FilterSetting>? {
        return blockingGet()?.filterSorting()?.programSettings()?.globalSettings()
    }

    fun getProgramFiltersByUid(uid: String?): Map<ProgramFilter, FilterSetting>? {
        val programFilters = blockingGet()?.filterSorting()?.programSettings()

        return programFilters?.specificSettings()?.get(uid)
            ?: programFilters?.globalSettings()
    }

    private fun getHomeFilters(filters: List<FilterSetting>): Map<HomeFilter, FilterSetting> {
        return filters
            .filter { it.scope() == HomeFilter::class.java.simpleName }
            .associateBy { HomeFilter.valueOf(it.filterType()!!) }
    }

    private fun getDataSetFilters(filters: List<FilterSetting>): DataSetFilters {
        val dataSetFilters = filters
            .filter { it.scope() == DataSetFilter::class.java.simpleName }

        val global = dataSetFilters
            .filter { it.uid() == FILTERSETTING_GLOBAL_ID }
            .associateBy { DataSetFilter.valueOf(it.filterType()!!) }

        val specific = dataSetFilters
            .filter { it.uid() != FILTERSETTING_GLOBAL_ID }
            .groupBy { it.uid() }
            .mapValues { entry ->
                entry.value.associateBy {
                    DataSetFilter.valueOf(it.filterType()!!)
                }
            }

        return DataSetFilters.builder()
            .globalSettings(global)
            .specificSettings(specific)
            .build()
    }

    private fun getProgramFilters(filters: List<FilterSetting>): ProgramFilters {
        val programFilters = filters
            .filter { it.scope() == ProgramFilter::class.java.simpleName }

        val global = programFilters
            .filter { it.uid() == FILTERSETTING_GLOBAL_ID }
            .associateBy { ProgramFilter.valueOf(it.filterType()!!) }

        val specific = programFilters
            .filter { it.uid() != FILTERSETTING_GLOBAL_ID }
            .groupBy { it.uid() }
            .mapValues { entry ->
                entry.value.associateBy {
                    ProgramFilter.valueOf(it.filterType()!!)
                }
            }

        return ProgramFilters.builder()
            .globalSettings(global)
            .specificSettings(specific)
            .build()
    }

    fun getGlobalProgramConfigurationSetting(): ProgramConfigurationSetting? {
        val programSettingList = runBlocking { programConfigurationSettingStore.selectAll() }
        return getGlobal(programSettingList)
    }

    fun getGlobalDataSetConfigurationSetting(): DataSetConfigurationSetting? {
        val dataSetSettingList = runBlocking { dataSetConfigurationSettingStore.selectAll() }
        return getGlobal(dataSetSettingList)
    }

    @Deprecated("")
    fun getGlobalCompletionSpinner(): CompletionSpinner? {
        val setting = getGlobalProgramConfigurationSetting()
        return toCompletionSpinner(setting)
    }

    fun getProgramConfigurationByUid(uid: String?): ProgramConfigurationSetting? {
        val programSettingList = runBlocking { programConfigurationSettingStore.selectAll() }
        val result = getSpecifics(programSettingList)[uid]

        return result ?: getGlobalProgramConfigurationSetting()
    }

    fun getDataSetConfigurationByUid(uid: String?): DataSetConfigurationSetting? {
        val dataSetSettingList = runBlocking { dataSetConfigurationSettingStore.selectAll() }
        val result = getSpecifics(dataSetSettingList)[uid]

        return result ?: getGlobalDataSetConfigurationSetting()
    }

    @Deprecated("")
    fun getCompletionSpinnerByUid(uid: String?): CompletionSpinner? {
        val setting = getProgramConfigurationByUid(uid)
        return toCompletionSpinner(setting)
    }
}

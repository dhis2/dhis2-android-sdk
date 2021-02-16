/*
 * Copyright (c) 2004-2019, University of Oslo
 * All rights reserved.
 *
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
package org.hisp.dhis.android.core.settings.internal

import org.hisp.dhis.android.core.settings.AppearanceSettings
import org.hisp.dhis.android.core.settings.DataSetFilter
import org.hisp.dhis.android.core.settings.DataSetSetting
import org.hisp.dhis.android.core.settings.DataSetSettings
import org.hisp.dhis.android.core.settings.FilterConfig
import org.hisp.dhis.android.core.settings.FilterScopesSettings
import org.hisp.dhis.android.core.settings.HomeFilter
import org.hisp.dhis.android.core.settings.ProgramFilter
import org.hisp.dhis.android.core.settings.ProgramSetting
import org.hisp.dhis.android.core.settings.ProgramSettings

internal object SettingsAppHelper {

    fun getDataSetSettingList(dataSetSettings: DataSetSettings): List<DataSetSetting> {
        return dataSetSettings.specificSettings().values + dataSetSettings.globalSettings()
    }

    fun getProgramSettingList(programSettings: ProgramSettings): List<ProgramSetting> {
        return (programSettings.specificSettings().values + programSettings.globalSettings()).filterNotNull()
    }

    fun getAppearanceSettings(appearanceSettings: AppearanceSettings): List<FilterConfig> {
        val result: MutableList<FilterConfig> = arrayListOf()

        appearanceSettings.filterSorting()?.let {
            result.addAll(getHomeFilters(it.home().filters()))
            result.addAll(getDataSetFilters(it.dataSettings()))
            result.addAll(getProgramFilters(it.programSettings()))
        }

        return result
    }

    private fun getHomeFilters(filters: MutableMap<HomeFilter, FilterConfig>) = filters.map { entry ->
        entry.value.toBuilder()
            .scope(HomeFilter::class.simpleName)
            .filterType(entry.key.name)
            .build()
    }

    private fun getDataSetFilters(dataSetScope: FilterScopesSettings<DataSetFilter>): List<FilterConfig> {

        val globalFilters = dataSetScope.globalSettings().filters().map { entry ->
            entry.value.toBuilder()
                .scope(DataSetFilter::class.simpleName)
                .filterType(entry.key.name)
                .build()
        }

        val specificFilters = dataSetScope.specificSettings().flatMap { entry ->
            entry.value.filters().map { filter ->
                filter.value.toBuilder()
                    .scope(DataSetFilter::class.simpleName)
                    .filterType(filter.key.name)
                    .uid(entry.key)
                    .build()
            }
        }

        return listOf(globalFilters, specificFilters).flatten()
    }

    private fun getProgramFilters(programScope: FilterScopesSettings<ProgramFilter>): List<FilterConfig> {

        val globalFilters = programScope.globalSettings().filters().map { entry ->
            entry.value.toBuilder()
                .scope(ProgramFilter::class.simpleName)
                .filterType(entry.key.name)
                .build()
        }

        val specificFilters = programScope.specificSettings().flatMap { entry ->
            entry.value.filters().map { filter ->
                filter.value.toBuilder()
                    .scope(ProgramFilter::class.simpleName)
                    .filterType(filter.key.name)
                    .uid(entry.key)
                    .build()
            }
        }

        return listOf(globalFilters, specificFilters).flatten()
    }
}

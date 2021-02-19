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

import org.hisp.dhis.android.core.settings.*

internal object SettingsAppHelper {

    fun getDataSetSettingList(dataSetSettings: DataSetSettings): List<DataSetSetting> {
        return dataSetSettings.specificSettings().values + dataSetSettings.globalSettings()
    }

    fun getProgramSettingList(programSettings: ProgramSettings): List<ProgramSetting> {
        return (programSettings.specificSettings().values + programSettings.globalSettings()).filterNotNull()
    }

    fun getFilterSettingsList(appearanceSettings: AppearanceSettings): List<FilterSetting> {
        val result: MutableList<FilterSetting> = arrayListOf()

        appearanceSettings.filterSorting()?.let {
            result.addAll(getHomeFilters(it.home()))
            result.addAll(getDataSetFilters(it.dataSetSettings()))
            result.addAll(getProgramFilters(it.programSettings()))
        }

        return result
    }

    private fun getHomeFilters(filters: MutableMap<HomeFilter, FilterSetting>) = filters.map { entry ->
        entry.value.toBuilder()
            .scope(HomeFilter::class.simpleName)
            .filterType(entry.key.name)
            .build()
    }

    private fun getDataSetFilters(dataSetScope: DataSetFilters): List<FilterSetting> {

        val globalFilters = dataSetScope.globalSettings().map { entry ->
            entry.value.toBuilder()
                .scope(DataSetFilter::class.simpleName)
                .filterType(entry.key.name)
                .build()
        }

        val specificFilters = dataSetScope.specificSettings().flatMap { entry ->
            entry.value.map { filter ->
                filter.value.toBuilder()
                    .scope(DataSetFilter::class.simpleName)
                    .filterType(filter.key.name)
                    .uid(entry.key)
                    .build()
            }
        }

        return listOf(globalFilters, specificFilters).flatten()
    }

    private fun getProgramFilters(programScope: ProgramFilters): List<FilterSetting> {

        val globalFilters = programScope.globalSettings().map { entry ->
            entry.value.toBuilder()
                .scope(ProgramFilter::class.simpleName)
                .filterType(entry.key.name)
                .build()
        }

        val specificFilters = programScope.specificSettings().flatMap { entry ->
            entry.value.map { filter ->
                filter.value.toBuilder()
                    .scope(ProgramFilter::class.simpleName)
                    .filterType(filter.key.name)
                    .uid(entry.key)
                    .build()
            }
        }

        return listOf(globalFilters, specificFilters).flatten()
    }

    fun getCompletionSpinnerList(appearanceSettings: AppearanceSettings): List<CompletionSpinner> {
        val list = mutableListOf<CompletionSpinner>()
        appearanceSettings.completionSpinner()?.let { settings ->
            settings.globalSettings()?.let {
                list.add(it)
            }
            list.addAll(
                settings.specificSettings()?.map { entry ->
                    entry.value.toBuilder()
                        .uid(entry.key)
                        .build()
                } ?: emptyList()
            )
        }
        return list
    }

    @JvmStatic
    fun buildAnalyticsSettings(
        teiSettings: List<AnalyticsTeiSetting>,
        teiDataElements: List<AnalyticsTeiDataElement>,
        teiIndicators: List<AnalyticsTeiIndicator>,
        teiAttributes: List<AnalyticsTeiAttribute>
    ): AnalyticsSettings? {
        return if (teiSettings.isNullOrEmpty()) {
            null
        } else {
            val teiSettingsWithData = teiSettings.map { item ->
                val data = AnalyticsTeiData.builder()
                    .dataElements(teiDataElements.filter { it.teiSetting() == item.uid() })
                    .indicators(teiIndicators.filter { it.teiSetting() == item.uid() })
                    .attributes(teiAttributes.filter { it.teiSetting() == item.uid() })
                    .build()

                item.toBuilder().data(data).build()
            }

            AnalyticsSettings.builder()
                .tei(teiSettingsWithData)
                .build()
        }
    }
}

/*
 *  Copyright (c) 2004-2021, University of Oslo
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

package org.hisp.dhis.android.core.settings;

import org.hisp.dhis.android.core.arch.db.stores.internal.ObjectWithoutUidStore;
import org.hisp.dhis.android.core.arch.repositories.collection.ReadOnlyWithDownloadObjectRepository;
import org.hisp.dhis.android.core.arch.repositories.object.internal.ReadOnlyAnyObjectWithDownloadRepositoryImpl;
import org.hisp.dhis.android.core.settings.internal.AppearanceSettingCall;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.inject.Inject;

import dagger.Reusable;

@Reusable
public class AppearanceSettingsObjectRepository
        extends ReadOnlyAnyObjectWithDownloadRepositoryImpl<AppearanceSettings>
        implements ReadOnlyWithDownloadObjectRepository<AppearanceSettings> {

    private final ObjectWithoutUidStore<FilterSetting> filterSettingStore;
    private final ObjectWithoutUidStore<CompletionSpinner> completionSpinnerStore;

    @Inject
    AppearanceSettingsObjectRepository(ObjectWithoutUidStore<FilterSetting> filterSettingStore,
                                       ObjectWithoutUidStore<CompletionSpinner> completionSpinnerStore,
                                       AppearanceSettingCall appearanceSettingCall) {
        super(appearanceSettingCall);
        this.filterSettingStore = filterSettingStore;
        this.completionSpinnerStore = completionSpinnerStore;
    }

    @Override
    public AppearanceSettings blockingGet() {
        List<FilterSetting> filters = filterSettingStore.selectAll();
        List<CompletionSpinner> completionSpinnerList = completionSpinnerStore.selectAll();

        if (filters.isEmpty() && completionSpinnerList.isEmpty()) {
            return null;
        }

        //FilterSorting
        FilterSorting.Builder filterSortingBuilder = FilterSorting.builder();
        filterSortingBuilder.home(getHomeFilters(filters));
        filterSortingBuilder.dataSetSettings(getDataSetFilters(filters));
        filterSortingBuilder.programSettings(getProgramFilters(filters));
        FilterSorting filterSorting = filterSortingBuilder.build();

        //CompletionSpinner
        CompletionSpinnerSetting.Builder completionSpinnerSettingBuilder = CompletionSpinnerSetting.builder();
        completionSpinnerSettingBuilder.globalSettings(getGlobalCompletionSpinner(completionSpinnerList));
        completionSpinnerSettingBuilder.specificSettings(getSpecificCompletionsSpinners(completionSpinnerList));
        CompletionSpinnerSetting completionSpinnerSetting = completionSpinnerSettingBuilder.build();

        //Appearance
        AppearanceSettings.Builder appearanceSettingsBuilder = AppearanceSettings.builder();
        appearanceSettingsBuilder.filterSorting(filterSorting);
        appearanceSettingsBuilder.completionSpinner(completionSpinnerSetting);

        return appearanceSettingsBuilder.build();
    }

    public Map<HomeFilter, FilterSetting> getHomeFilters() {
        return blockingGet().filterSorting().home();
    }

    public Map<DataSetFilter, FilterSetting> getDataSetFiltersByUid(String uid) {
        Map<DataSetFilter, FilterSetting> filters = blockingGet()
                .filterSorting()
                .dataSetSettings()
                .specificSettings()
                .get(uid);
        if (filters == null) {
            filters = blockingGet().filterSorting().dataSetSettings().globalSettings();
        }
        return filters;
    }

    public Map<ProgramFilter, FilterSetting> getTrackedEntityTypeFilters() {
        return blockingGet().filterSorting().programSettings().globalSettings();
    }

    public Map<ProgramFilter, FilterSetting> getProgramFiltersByUid(String uid) {
        Map<ProgramFilter, FilterSetting> filters = blockingGet()
                .filterSorting()
                .programSettings()
                .specificSettings()
                .get(uid);
        if (filters == null) {
            filters = blockingGet().filterSorting().programSettings().globalSettings();
        }
        return filters;
    }

    private Map<String, CompletionSpinner> getSpecificCompletionsSpinners(
            List<CompletionSpinner> completionSpinnerList
    ) {
        Map<String, CompletionSpinner> result = new HashMap<>();
        for (CompletionSpinner completionSpinner : completionSpinnerList) {
            if (completionSpinner.uid() != null) {
                result.put(completionSpinner.uid(), completionSpinner);
            }
        }

        return result;
    }

    private CompletionSpinner getGlobalCompletionSpinner(List<CompletionSpinner> completionSpinnerList) {
        for (CompletionSpinner completionSpinner : completionSpinnerList) {
            if (completionSpinner.uid() == null) {
                return completionSpinner;
            }
        }
        return null;
    }

    private Map<HomeFilter, FilterSetting> getHomeFilters(List<FilterSetting> filters) {
        Map<HomeFilter, FilterSetting> homeFilters = new HashMap<>();
        for (FilterSetting filter : filters) {
            if (Objects.equals(filter.scope(), HomeFilter.class.getSimpleName())) {
                homeFilters.put(HomeFilter.valueOf(filter.filterType()), filter);
            }
        }
        return homeFilters;
    }

    private DataSetFilters getDataSetFilters(List<FilterSetting> filters) {
        Map<DataSetFilter, FilterSetting> globalDataSetFilters = new HashMap<>();
        Map<String, Map<DataSetFilter, FilterSetting>> specificDataSetFilters = new HashMap<>();
        for (FilterSetting filter : filters) {
            if (Objects.equals(filter.scope(), DataSetFilter.class.getSimpleName())) {
                if (filter.uid() == null) {
                    globalDataSetFilters.put(DataSetFilter.valueOf(filter.filterType()), filter);
                } else {
                    Map<DataSetFilter, FilterSetting> uidFilters = specificDataSetFilters.get(filter.uid());
                    if (uidFilters == null) {
                        specificDataSetFilters.put(filter.uid(), createRegistryForSpecificDataSetFilters(filter));
                    } else {
                        uidFilters.put(DataSetFilter.valueOf(filter.filterType()), filter);
                    }
                }
            }
        }

        DataSetFilters.Builder dataSetScopeBuilder = DataSetFilters.builder();
        dataSetScopeBuilder.globalSettings(globalDataSetFilters);
        dataSetScopeBuilder.specificSettings(specificDataSetFilters);
        return dataSetScopeBuilder.build();
    }

    private Map<DataSetFilter, FilterSetting> createRegistryForSpecificDataSetFilters(FilterSetting filter) {
        Map<DataSetFilter, FilterSetting> dataSetFilters = new HashMap<>();
        dataSetFilters.put(DataSetFilter.valueOf(filter.filterType()), filter);
        return dataSetFilters;
    }

    private ProgramFilters getProgramFilters(List<FilterSetting> filters) {
        Map<ProgramFilter, FilterSetting> globalDataSetFilters = new HashMap<>();
        Map<String, Map<ProgramFilter, FilterSetting>> specificDataSetFilters = new HashMap<>();
        for (FilterSetting filter : filters) {
            if (Objects.equals(filter.scope(), ProgramFilter.class.getSimpleName())) {
                if (filter.uid() == null) {
                    globalDataSetFilters.put(ProgramFilter.valueOf(filter.filterType()), filter);
                } else {
                    Map<ProgramFilter, FilterSetting> uidFilters = specificDataSetFilters.get(filter.uid());
                    if (uidFilters == null) {
                        specificDataSetFilters.put(filter.uid(), createRegistryForSpecificProgramFilters(filter));
                    } else {
                        uidFilters.put(ProgramFilter.valueOf(filter.filterType()), filter);
                    }
                }
            }
        }

        ProgramFilters.Builder dataSetScopeBuilder = ProgramFilters.builder();
        dataSetScopeBuilder.globalSettings(globalDataSetFilters);
        dataSetScopeBuilder.specificSettings(specificDataSetFilters);
        return dataSetScopeBuilder.build();
    }

    private Map<ProgramFilter, FilterSetting> createRegistryForSpecificProgramFilters(FilterSetting filter) {
        Map<ProgramFilter, FilterSetting> programFilters = new HashMap<>();
        programFilters.put(ProgramFilter.valueOf(filter.filterType()), filter);
        return programFilters;
    }

    public CompletionSpinner getGlobalCompletionSpinner() {
        List<CompletionSpinner> completionSpinnerList = completionSpinnerStore.selectAll();
        return getGlobalCompletionSpinner(completionSpinnerList);
    }

    public CompletionSpinner getCompletionSpinnerByUid(String uid) {
        List<CompletionSpinner> completionSpinnerList = completionSpinnerStore.selectAll();
        CompletionSpinner result = getSpecificCompletionsSpinners(completionSpinnerList).get(uid);
        if (result == null) {
            result = getGlobalCompletionSpinner();
        }
        return result;
    }
}

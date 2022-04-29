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
    private final ObjectWithoutUidStore<ProgramConfigurationSetting> programConfigurationSettingStore;

    @Inject
    AppearanceSettingsObjectRepository(ObjectWithoutUidStore<FilterSetting> filterSettingStore,
                                       ObjectWithoutUidStore<ProgramConfigurationSetting>
                                               programConfigurationSettingStore,
                                       AppearanceSettingCall appearanceSettingCall) {
        super(appearanceSettingCall);
        this.filterSettingStore = filterSettingStore;
        this.programConfigurationSettingStore = programConfigurationSettingStore;
    }

    @Override
    public AppearanceSettings blockingGet() {
        List<FilterSetting> filters = filterSettingStore.selectAll();
        List<ProgramConfigurationSetting> programConfigurationSettingList =
                programConfigurationSettingStore.selectAll();

        if (filters.isEmpty() && programConfigurationSettingList.isEmpty()) {
            return null;
        }

        //FilterSorting
        FilterSorting filterSorting = FilterSorting.builder()
                .home(getHomeFilters(filters))
                .dataSetSettings(getDataSetFilters(filters))
                .programSettings(getProgramFilters(filters))
                .build();

        //ProgramConfigurationSettings
        ProgramConfigurationSettings programConfigurationSettings = ProgramConfigurationSettings.builder()
                .globalSettings(AppearanceSettingsHelper.getGlobal(programConfigurationSettingList))
                .specificSettings(AppearanceSettingsHelper.getSpecifics(programConfigurationSettingList))
                .build();

        return AppearanceSettings.builder()
                .filterSorting(filterSorting)
                .programConfiguration(programConfigurationSettings)
                .completionSpinner(AppearanceSettingsHelper.programToCompletionSpinner(programConfigurationSettings))
                .build();
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

    public ProgramConfigurationSetting getGlobalProgramConfigurationSetting() {
        List<ProgramConfigurationSetting> programSettingList = programConfigurationSettingStore.selectAll();
        return AppearanceSettingsHelper.getGlobal(programSettingList);
    }

    @Deprecated
    public CompletionSpinner getGlobalCompletionSpinner() {
        ProgramConfigurationSetting setting = getGlobalProgramConfigurationSetting();
        return AppearanceSettingsHelper.toCompletionSpinner(setting);
    }

    public ProgramConfigurationSetting getProgramConfigurationByUid(String uid) {
        List<ProgramConfigurationSetting> programSettingList = programConfigurationSettingStore.selectAll();
        ProgramConfigurationSetting result = AppearanceSettingsHelper.getSpecifics(programSettingList).get(uid);
        if (result == null) {
            result = getGlobalProgramConfigurationSetting();
        }
        return result;
    }

    @Deprecated
    public CompletionSpinner getCompletionSpinnerByUid(String uid) {
        ProgramConfigurationSetting setting = getProgramConfigurationByUid(uid);
        return AppearanceSettingsHelper.toCompletionSpinner(setting);
    }
}

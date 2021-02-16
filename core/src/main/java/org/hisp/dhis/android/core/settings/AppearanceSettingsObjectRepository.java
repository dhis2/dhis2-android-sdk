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

    private final ObjectWithoutUidStore<FilterSetting> store;

    @Inject
    AppearanceSettingsObjectRepository(ObjectWithoutUidStore<FilterSetting> store,
                                       AppearanceSettingCall appearanceSettingCall) {
        super(appearanceSettingCall);
        this.store = store;
    }

    public Map<HomeFilter, FilterSetting> getHomeFilters() {
        return blockingGet().filterSorting().home();
    }

    public Map<DataSetFilter, FilterSetting> getDataSetFiltersByUid(String uid) {
        Map<DataSetFilter, FilterSetting> filters = blockingGet().filterSorting().dataSetSettings().specificSettings().get(uid);
        if (filters == null) {
            filters = blockingGet().filterSorting().dataSetSettings().globalSettings();
        }
        return filters;
    }

    public Map<ProgramFilter, FilterSetting> getTrackedEntityTypeFilters() {
        return blockingGet().filterSorting().programSettings().globalSettings();
    }

    public Map<ProgramFilter, FilterSetting> getProgramFiltersByUid(String uid) {
        Map<ProgramFilter, FilterSetting> filters = blockingGet().filterSorting().programSettings().specificSettings().get(uid);
        if (filters == null) {
            filters = blockingGet().filterSorting().programSettings().globalSettings();
        }
        return filters;
    }

    @Override
    public AppearanceSettings blockingGet() {
        List<FilterSetting> filters = store.selectAll();

        //Home
        Map<HomeFilter, FilterSetting> homeFilters = new HashMap<>();
        for (FilterSetting filter : filters) {
            if (Objects.equals(filter.scope(), HomeFilter.class.getSimpleName())) {
                homeFilters.put(HomeFilter.valueOf(filter.filterType()), filter);
            }
        }

        //FilterSorting
        FilterSorting.Builder filterSortingBuilder = FilterSorting.builder();
        filterSortingBuilder.home(homeFilters);
        filterSortingBuilder.dataSetSettings(getFiltersInScope(filters, DataSetFilter.class));
        filterSortingBuilder.programSettings(getFiltersInScope(filters, ProgramFilter.class));
        FilterSorting filterSorting = filterSortingBuilder.build();

        //Appearance
        AppearanceSettings.Builder appearanceSettingsBuilder = AppearanceSettings.builder();
        appearanceSettingsBuilder.filterSorting(filterSorting);
        return appearanceSettingsBuilder.build();
    }

    private <T extends Enum<T>> FilterScopeSettings<T> getFiltersInScope(List<FilterSetting> filters, Class<T> filterClass) {

        Map<T, FilterSetting> globalDataSetFilters = new HashMap<>();
        Map<String, Map<T, FilterSetting>> specificDataSetFilters = new HashMap<>();
        for (FilterSetting filter : filters) {
            if (Objects.equals(filter.scope(), filterClass.getSimpleName())) {
                if (filter.uid() == null) {
                    globalDataSetFilters.put(getFilterType(filterClass, filter.filterType()), filter);
                } else {
                    Map<T, FilterSetting> uidFilters = specificDataSetFilters.get(filter.uid());
                    if (uidFilters != null) {
                        uidFilters.put(getFilterType(filterClass, filter.filterType()), filter);
                    } else {
                        Map<T, FilterSetting> dataSetFilters = new HashMap<>();
                        dataSetFilters.put(getFilterType(filterClass, filter.filterType()), filter);
                        specificDataSetFilters.put(filter.uid(), dataSetFilters);
                    }
                }
            }
        }

        FilterScopeSettings.Builder<T> dataSetScopeBuilder = FilterScopeSettings.builder();
        dataSetScopeBuilder.globalSettings(globalDataSetFilters);
        dataSetScopeBuilder.specificSettings(specificDataSetFilters);
        return dataSetScopeBuilder.build();
    }

    private <T extends Enum<T>> T getFilterType(Class<T> enumClass, String value) {
        return Enum.valueOf(enumClass, value);
    }
}

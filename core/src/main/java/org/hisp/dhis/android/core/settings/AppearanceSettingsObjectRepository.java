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

    private final ObjectWithoutUidStore<FilterConfig> store;

    @Inject
    AppearanceSettingsObjectRepository(ObjectWithoutUidStore<FilterConfig> store,
                                       AppearanceSettingCall appearanceSettingCall) {
        super(appearanceSettingCall);
        this.store = store;
    }

    public Map<HomeFilter, FilterConfig> getHomeFilters() {
        return blockingGet().filterSorting().home().filters();
    }

    public Map<DataSetFilter, FilterConfig> getDataSetFilters(String uid) {
        FiltersSet<DataSetFilter> filterSet = blockingGet().filterSorting().dataSettings().specificSettings().get(uid);
        if (filterSet == null) {
            filterSet = blockingGet().filterSorting().dataSettings().globalSettings();
        }
        return filterSet.filters();
    }

    public Map<ProgramFilter, FilterConfig> getProgramFilters(String uid) {
        FiltersSet<ProgramFilter> filterSet = blockingGet().filterSorting().programSettings().specificSettings().get(uid);
        if (filterSet == null) {
            filterSet = blockingGet().filterSorting().programSettings().globalSettings();
        }
        return filterSet.filters();
    }

    @Override
    public AppearanceSettings blockingGet() {
        List<FilterConfig> filters = store.selectAll();

        //Home
        Map<HomeFilter, FilterConfig> homeFilters = new HashMap<>();
        for (FilterConfig filter : filters) {
            if (Objects.equals(filter.scope(), HomeFilter.class.getSimpleName())) {
                homeFilters.put(HomeFilter.valueOf(filter.filterType()), filter);
            }
        }
        FiltersSet.Builder<HomeFilter> homeBuilder = FiltersSet.builder();
        homeBuilder.filters(homeFilters);

        //DataSet
        Map<DataSetFilter, FilterConfig> globalDataSetFilters = new HashMap<>();
        Map<String, FiltersSet<DataSetFilter>> specificDataSetFilters = new HashMap<>();
        for (FilterConfig filter : filters) {
            if (Objects.equals(filter.scope(), DataSetFilter.class.getSimpleName())) {
                if (filter.uid() == null) {
                    globalDataSetFilters.put(DataSetFilter.valueOf(filter.filterType()), filter);
                } else {
                    FiltersSet<DataSetFilter> uidFilters = specificDataSetFilters.get(filter.uid());
                    if (uidFilters != null) {
                        uidFilters.filters().put(DataSetFilter.valueOf(filter.filterType()), filter);
                    } else {
                        Map<DataSetFilter, FilterConfig> dataSetFilters = new HashMap<>();
                        dataSetFilters.put(DataSetFilter.valueOf(filter.filterType()), filter);

                        FiltersSet.Builder<DataSetFilter> dataSetBuilder = FiltersSet.builder();
                        dataSetBuilder.filters(dataSetFilters);

                        specificDataSetFilters.put(filter.uid(), dataSetBuilder.build());
                    }
                }
            }
        }

        FiltersSet.Builder<DataSetFilter> dataSetGlobalSettingsBuilder = FiltersSet.builder();
        dataSetGlobalSettingsBuilder.filters(globalDataSetFilters);

        FilterScopesSettings.Builder<DataSetFilter> dataSetScopeBuilder = FilterScopesSettings.builder();
        dataSetScopeBuilder.globalSettings(dataSetGlobalSettingsBuilder.build());
        dataSetScopeBuilder.specificSettings(specificDataSetFilters);
        FilterScopesSettings<DataSetFilter> dataSetScopesSettings = dataSetScopeBuilder.build();

        //Program
        Map<ProgramFilter, FilterConfig> globalProgramFilters = new HashMap<>();
        Map<String, FiltersSet<ProgramFilter>> specificProgramFilters = new HashMap<>();
        for (FilterConfig filter : filters) {
            if (Objects.equals(filter.scope(), ProgramFilter.class.getSimpleName())) {
                if (filter.uid() == null) {
                    globalProgramFilters.put(ProgramFilter.valueOf(filter.filterType()), filter);
                } else {
                    FiltersSet<ProgramFilter> uidFilters = specificProgramFilters.get(filter.uid());
                    if (uidFilters != null) {
                        uidFilters.filters().put(ProgramFilter.valueOf(filter.filterType()), filter);
                    } else {
                        Map<ProgramFilter, FilterConfig> programFilters = new HashMap<>();
                        programFilters.put(ProgramFilter.valueOf(filter.filterType()), filter);

                        FiltersSet.Builder<ProgramFilter> programBuilder = FiltersSet.builder();
                        programBuilder.filters(programFilters);

                        specificProgramFilters.put(filter.uid(), programBuilder.build());
                    }
                }
            }
        }

        FiltersSet.Builder<ProgramFilter> programGlobalSettingsBuilder = FiltersSet.builder();
        programGlobalSettingsBuilder.filters(globalProgramFilters);

        FilterScopesSettings.Builder<ProgramFilter> programScopeBuilder = FilterScopesSettings.builder();
        programScopeBuilder.globalSettings(programGlobalSettingsBuilder.build());
        programScopeBuilder.specificSettings(specificProgramFilters);
        FilterScopesSettings<ProgramFilter> programScopesSettings = programScopeBuilder.build();

        //FilterSorting
        FilterSorting.Builder filterSortingBuilder = FilterSorting.builder();
        filterSortingBuilder.home(homeBuilder.build());
        filterSortingBuilder.dataSettings(dataSetScopesSettings);
        filterSortingBuilder.programSettings(programScopesSettings);

        //Appearance
        AppearanceSettings.Builder appearanceSettingsBuilder = AppearanceSettings.builder();
        appearanceSettingsBuilder.filterSorting(filterSortingBuilder.build());

        return appearanceSettingsBuilder.build();
    }

    /*private <T> FilterScopesSettings<T> getFiltersInScope(List<FilterConfig> filters, Class<T> programFilterClass) {

        Map<T, FilterConfig> globalDataSetFilters = new HashMap<>();
        Map<String, FiltersSet<T>> specificDataSetFilters = new HashMap<>();
        for (FilterConfig filter : filters) {
            if (Objects.equals(filter.scope(), "dataSet")) {
                if (filter.uid() == null) {
                    globalDataSetFilters.put(getFilterType(programFilterClass, filter.filterType()), filter);
                } else {
                    FiltersSet<T> uidFilters = specificDataSetFilters.get(filter.uid());
                    if (uidFilters != null) {
                        uidFilters.filters().put(getFilterType(filterType, filter.filterType()), filter);
                    } else {
                        Map<T, FilterConfig> dataSetFilters = new HashMap<>();
                        dataSetFilters.put(getFilterType(filterType, filter.filterType()), filter);

                        FiltersSet.Builder<T> dataSetBuilder = FiltersSet.builder();
                        dataSetBuilder.filters(dataSetFilters);

                        specificDataSetFilters.put(filter.uid(), dataSetBuilder.build());
                    }
                }
            }
        }

        FiltersSet.Builder<T> globalSettingsBuilder = FiltersSet.builder();
        globalSettingsBuilder.filters(globalDataSetFilters);

        FilterScopesSettings.Builder<T> dataSetScopeBuilder = FilterScopesSettings.builder();
        dataSetScopeBuilder.globalSettings(globalSettingsBuilder.build());
        dataSetScopeBuilder.specificSettings(specificDataSetFilters);

        return dataSetScopeBuilder.build();
    }

    private <E extends Enum<E>> E getFilterType(Class<E> enumClass, String value) {
        return Enum.valueOf(enumClass, value);
    }*/
}

package org.hisp.dhis.android.core.settings;

import org.hisp.dhis.android.core.common.BaseObjectShould;
import org.hisp.dhis.android.core.common.ObjectShould;
import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;
import java.util.Map;

import static com.google.common.truth.Truth.assertThat;

public class AppearanceSettingsShould extends BaseObjectShould implements ObjectShould {

    public AppearanceSettingsShould() {
        super("settings/appearance_settings.json");
    }

    @Override
    @Test
    public void map_from_json_string() throws IOException, ParseException {

        AppearanceSettings appearanceSettings = objectMapper.readValue(jsonStream, AppearanceSettings.class);

        FilterSorting filterSorting = appearanceSettings.filterSorting();

        Map<HomeFilter, FilterSetting> homeFilters = filterSorting.home();
        FilterSetting homeDateFilter = homeFilters.get(HomeFilter.DATE);

        assertThat(homeDateFilter.scope()).isNull();
        assertThat(homeDateFilter.filterType()).isNull();
        assertThat(homeDateFilter.uid()).isNull();
        assertThat(homeDateFilter.sort()).isEqualTo(true);
        assertThat(homeDateFilter.filter()).isEqualTo(true);

        DataSetFilters dataSetFilters = filterSorting.dataSetSettings();
        Map<DataSetFilter, FilterSetting> dataSetGlobalFilters = dataSetFilters.globalSettings();
        FilterSetting dataSetPeriodFilter = dataSetGlobalFilters.get(DataSetFilter.PERIOD);
        assertThat(dataSetPeriodFilter.scope()).isNull();
        assertThat(dataSetPeriodFilter.filterType()).isNull();
        assertThat(dataSetPeriodFilter.uid()).isNull();
        assertThat(dataSetPeriodFilter.sort()).isEqualTo(true);
        assertThat(dataSetPeriodFilter.filter()).isEqualTo(true);

        ProgramFilters programFilters = filterSorting.programSettings();
        Map<ProgramFilter, FilterSetting> programGlobalFilters = programFilters.globalSettings();
        FilterSetting programEventDateFilter = programGlobalFilters.get(ProgramFilter.EVENT_DATE);
        assertThat(programEventDateFilter.scope()).isNull();
        assertThat(programEventDateFilter.filterType()).isNull();
        assertThat(programEventDateFilter.uid()).isNull();
        assertThat(programEventDateFilter.sort()).isEqualTo(true);
        assertThat(programEventDateFilter.filter()).isEqualTo(true);

        CompletionSpinnerSetting completionSpinnerSetting = appearanceSettings.completionSpinner();
        assertThat(completionSpinnerSetting.globalSettings().uid()).isNull();

        Map<String, CompletionSpinner> specificCompletionSpinnerList = completionSpinnerSetting.specificSettings();
        CompletionSpinner specificCompletionSpinner = specificCompletionSpinnerList.get("IpHINAT79UW");
        assertThat(specificCompletionSpinner.uid()).isNull();
        assertThat(specificCompletionSpinner.visible()).isEqualTo(true);
    }
}

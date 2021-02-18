package org.hisp.dhis.android.core.data.settings;

import org.hisp.dhis.android.core.settings.FilterSetting;

public class FilterSettingSamples {

    public static FilterSetting getFilterSetting() {
        return FilterSetting.builder()
                .id(1L)
                .scope("ProgramFilter")
                .filterType("event")
                .uid("aBcDeFg")
                .filter(true)
                .sort(true)
                .build();
    }
}

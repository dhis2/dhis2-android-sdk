package org.hisp.dhis.android.core.datavalue;

import org.junit.Test;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.hisp.dhis.android.core.data.datavalue.DataValueUtils.getDataSetUids;
import static org.hisp.dhis.android.core.data.datavalue.DataValueUtils.getOrgUnitUids;
import static org.hisp.dhis.android.core.data.datavalue.DataValueUtils.getPeriodIds;

public class DataValueQueryShould {

    @Test
    public void create_data_value_query_successfully() {
        DataValueQuery dataValueQuery = DataValueQuery.create(getDataSetUids(), getPeriodIds(), getOrgUnitUids());
        assertThat(dataValueQuery.dataSetUids()).isEqualTo(getDataSetUids());
        assertThat(dataValueQuery.periodIds()).isEqualTo(getPeriodIds());
        assertThat(dataValueQuery.orgUnitUids()).isEqualTo(getOrgUnitUids());
    }
}

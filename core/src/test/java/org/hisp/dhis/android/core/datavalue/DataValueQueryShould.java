package org.hisp.dhis.android.core.datavalue;

import org.junit.Test;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.hisp.dhis.android.core.data.datavalue.DataValueUtils.dataSetUids;
import static org.hisp.dhis.android.core.data.datavalue.DataValueUtils.orgUnitUids;
import static org.hisp.dhis.android.core.data.datavalue.DataValueUtils.periodIds;

public class DataValueQueryShould {

    @Test
    public void create_data_value_query_successfully() {
        DataValueQuery dataValueQuery = DataValueQuery.create(dataSetUids, periodIds, orgUnitUids);
        assertThat(dataValueQuery.dataSetUids()).isEqualTo(dataSetUids);
        assertThat(dataValueQuery.periodIds()).isEqualTo(periodIds);
        assertThat(dataValueQuery.orgUnitUids()).isEqualTo(orgUnitUids);
    }
}

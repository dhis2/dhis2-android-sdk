package org.hisp.dhis.android.core.datavalue;

import org.hisp.dhis.android.core.utils.Utils;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.hisp.dhis.android.core.data.datavalue.DataValueUtils.dataSetUids;
import static org.hisp.dhis.android.core.data.datavalue.DataValueUtils.orgUnitUids;
import static org.junit.Assert.assertThat;

public class DataValueQueryShould {

    @Test
    public void create_data_value_query_successfully() {

        DataValueQuery dataValueQuery = DataValueQuery.create(dataSetUids, orgUnitUids,
                Utils.generateFormattedStartDateStr(), Utils.generateFormattedEndDateStr());

        assertThat(dataValueQuery, is(not(nullValue())));
    }
}

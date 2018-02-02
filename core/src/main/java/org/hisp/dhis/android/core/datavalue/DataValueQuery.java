package org.hisp.dhis.android.core.datavalue;

import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.BaseQuery;

import java.util.Set;

@AutoValue
public abstract class DataValueQuery extends BaseQuery {
    public abstract Set<String> dataSetUids();

    public abstract Set<String> periodIds();

    public abstract Set<String> orgUnitUids();

    public static DataValueQuery create(Set<String> dataSetUids, Set<String> periodIds, Set<String> orgUnitUids) {
        return new AutoValue_DataValueQuery(1, BaseQuery.DEFAULT_PAGE_SIZE, false,
                dataSetUids, periodIds, orgUnitUids);
    }
}

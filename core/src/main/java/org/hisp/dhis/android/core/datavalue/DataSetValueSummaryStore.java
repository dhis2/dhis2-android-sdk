/*
 * Copyright (c) 2004-2019, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.core.datavalue;

import org.hisp.dhis.android.core.common.BaseDataModel;
import org.hisp.dhis.android.core.common.CursorModelFactory;
import org.hisp.dhis.android.core.common.ReadableStoreImpl;
import org.hisp.dhis.android.core.common.SQLStatementBuilder;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.dataelement.DataElementTableInfo;
import org.hisp.dhis.android.core.dataset.DataSetDataElementLinkTableInfo;
import org.hisp.dhis.android.core.period.PeriodTableInfo;

import java.util.ArrayList;
import java.util.List;

public class DataSetValueSummaryStore extends ReadableStoreImpl<DataSetValueSummary> {

    private final String DATAVALUE_ALIAS = "dv";
    private final String PERIOD_ALIAS = "pe";
    private final String DATASETELEMENT_ALIAS = "dse";
    private final String DATASET_ALIAS = "ds";

    private final String STATE = DATAVALUE_ALIAS + "." + BaseDataModel.Columns.STATE;

    private final String SELECT_STATE_ORDERING = " MAX(CASE " +
            "WHEN " + STATE + " = '" + State.SYNCED + "' THEN 1 " +
            "WHEN " + STATE + " = '" + State.TO_DELETE + "' THEN 2 " +
            "WHEN " + STATE + " IN ('" + State.TO_POST + "','" + State.TO_UPDATE + "') THEN 3 " +
            "ELSE 4 END)";

    private final String SELECT = "SELECT " +
            DATASETELEMENT_ALIAS + "." + DataSetDataElementLinkTableInfo.Columns.DATA_SET + ", " +
            DATAVALUE_ALIAS + "." + DataValueFields.PERIOD + ", " +
            DATAVALUE_ALIAS + "." + DataValueTableInfo.ORGANISATION_UNIT + ", " +
            DATAVALUE_ALIAS + "." + DataValueFields.ATTRIBUTE_OPTION_COMBO + ", " +
            STATE + ", " +
            // Auxiliary field to order the 'state' column and to prioritize TO_POST and TO_UPDATE
            SELECT_STATE_ORDERING + " " +

            "FROM " + DataValueTableInfo.TABLE_INFO.name() + " as " + DATAVALUE_ALIAS +
            " INNER JOIN " + PeriodTableInfo.TABLE_INFO.name() + " as " + PERIOD_ALIAS +
            " ON " + DATAVALUE_ALIAS + "." + DataValueFields.PERIOD + " = " + PERIOD_ALIAS + "." + PeriodTableInfo.Columns.PERIOD_ID +
            " INNER JOIN " + DataSetDataElementLinkTableInfo.TABLE_INFO.name() + " as " + DATASETELEMENT_ALIAS +
            " ON " + DATAVALUE_ALIAS + "." + DataValueFields.DATA_ELEMENT + " = " + DATASETELEMENT_ALIAS + "." + DataSetDataElementLinkTableInfo.Columns.DATA_ELEMENT;

    private final String GROUP_BY_CLAUSE = " GROUP BY " +
            DATASETELEMENT_ALIAS + "." + DataSetDataElementLinkTableInfo.Columns.DATA_SET + "," +
            DATAVALUE_ALIAS + "." + DataValueFields.PERIOD + "," +
            DATAVALUE_ALIAS + "." + DataValueTableInfo.ORGANISATION_UNIT + "," +
            DATAVALUE_ALIAS + "." + DataValueFields.ATTRIBUTE_OPTION_COMBO;

    private final String WHERE_CLAUSE = " WHERE dv.period = '201806' and dse.dataSet = " +
            "'Zi5D24rZvYd' ";

    private DataSetValueSummaryStore(DatabaseAdapter databaseAdapter,
                                     SQLStatementBuilder builder,
                                     CursorModelFactory<DataSetValueSummary> modelFactory) {
        super(databaseAdapter, builder, modelFactory);
    }

    public List<DataSetValueSummary> query(String whereClause) {
        String query = SELECT + WHERE_CLAUSE + GROUP_BY_CLAUSE;
        List<DataSetValueSummary> result = new ArrayList<>();
        addObjectsToCollection(databaseAdapter.query(query), result);

        return result;
    }

    public static DataSetValueSummaryStore create(DatabaseAdapter databaseAdapter) {
        return new DataSetValueSummaryStore(databaseAdapter,
                // TODO Just to make it compile
                new SQLStatementBuilder(DataElementTableInfo.TABLE_INFO),
                DataSetValueSummary::create);
    }
}

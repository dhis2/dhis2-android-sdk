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

package org.hisp.dhis.android.core.dataset.internal;

import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter;
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.SQLStatementBuilderImpl;
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder;
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.StatementBinder;
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.WhereStatementBinder;
import org.hisp.dhis.android.core.arch.db.stores.internal.ObjectWithoutUidStoreImpl;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.dataset.DataSetCompleteRegistration;
import org.hisp.dhis.android.core.dataset.DataSetCompleteRegistrationTableInfo;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitTableInfo;

import java.util.Collection;

final class DataSetCompleteRegistrationStoreImpl extends
        ObjectWithoutUidStoreImpl<DataSetCompleteRegistration> implements DataSetCompleteRegistrationStore {

    private static final StatementBinder<DataSetCompleteRegistration> BINDER =
            (dataSetCompleteRegistration, w) -> {
                w.bind(1, dataSetCompleteRegistration.period());
                w.bind(2, dataSetCompleteRegistration.dataSet());
                w.bind(3, dataSetCompleteRegistration.organisationUnit());
                w.bind(4, dataSetCompleteRegistration.attributeOptionCombo());
                w.bind(5, dataSetCompleteRegistration.date());
                w.bind(6, dataSetCompleteRegistration.storedBy());
                w.bind(7, dataSetCompleteRegistration.syncState());
                w.bind(8, dataSetCompleteRegistration.deleted());
            };

    private static final WhereStatementBinder<DataSetCompleteRegistration> WHERE_UPDATE_BINDER =
            (dataSetCompleteRegistration, w) -> {
                w.bind(9, dataSetCompleteRegistration.period());
                w.bind(10, dataSetCompleteRegistration.dataSet());
                w.bind(11, dataSetCompleteRegistration.organisationUnit());
                w.bind(12, dataSetCompleteRegistration.attributeOptionCombo());
            };

    private static final WhereStatementBinder<DataSetCompleteRegistration> WHERE_DELETE_BINDER =
            (dataSetCompleteRegistration, w) -> {
                w.bind(1, dataSetCompleteRegistration.period());
                w.bind(2, dataSetCompleteRegistration.dataSet());
                w.bind(3, dataSetCompleteRegistration.organisationUnit());
                w.bind(4, dataSetCompleteRegistration.attributeOptionCombo());
            };

    private DataSetCompleteRegistrationStoreImpl(DatabaseAdapter databaseAdapter,
                                                 SQLStatementBuilderImpl builder) {
        super(databaseAdapter, builder, BINDER, WHERE_UPDATE_BINDER, WHERE_DELETE_BINDER,
                DataSetCompleteRegistration::create);
    }

    public static DataSetCompleteRegistrationStoreImpl create(DatabaseAdapter databaseAdapter) {

        SQLStatementBuilderImpl sqlStatementBuilder =
                new SQLStatementBuilderImpl(DataSetCompleteRegistrationTableInfo.TABLE_INFO.name(),
                        DataSetCompleteRegistrationTableInfo.TABLE_INFO.columns());

        return new DataSetCompleteRegistrationStoreImpl(databaseAdapter, sqlStatementBuilder);
    }

    /**
     * @param dataSetCompleteRegistration DataSetCompleteRegistration element you want to update
     * @param newState                    The new state to be set for the DataValue
     */
    @Override
    public void setState(DataSetCompleteRegistration dataSetCompleteRegistration, State newState) {
        DataSetCompleteRegistration updatedDataSetCompleteRegistration
                = dataSetCompleteRegistration.toBuilder().syncState(newState).build();

        updateWhere(updatedDataSetCompleteRegistration);
    }

    @Override
    public void setDeleted(DataSetCompleteRegistration dataSetCompleteRegistration) {
        DataSetCompleteRegistration updatedDataSetCompleteRegistration
                = dataSetCompleteRegistration.toBuilder().deleted(true).build();

        updateWhere(updatedDataSetCompleteRegistration);
    }

    @Override
    public boolean removeNotPresentAndSynced(Collection<String> dataSetUids,
                                             Collection<String> periodIds,
                                             String rootOrgunitUid) {
        WhereClauseBuilder whereClause = new WhereClauseBuilder();

        whereClause.appendInKeyStringValues(DataSetCompleteRegistrationTableInfo.Columns.DATA_SET, dataSetUids);
        whereClause.appendInKeyStringValues(DataSetCompleteRegistrationTableInfo.Columns.PERIOD, periodIds);

        String subQuery = String.format("SELECT %s FROM %s WHERE %s LIKE '%s'",
                OrganisationUnitTableInfo.Columns.UID,
                OrganisationUnitTableInfo.TABLE_INFO.name(),
                OrganisationUnitTableInfo.Columns.PATH,
                "%" + rootOrgunitUid + "%");
        whereClause.appendInSubQuery(DataSetCompleteRegistrationTableInfo.Columns.ORGANISATION_UNIT, subQuery);

        whereClause.appendKeyStringValue(DataSetCompleteRegistrationTableInfo.Columns.SYNC_STATE, State.SYNCED);

        return deleteWhere(whereClause.build());
    }

    @Override
    public boolean isBeingUpload(DataSetCompleteRegistration dscr) {
        String whereClause = new WhereClauseBuilder()
                .appendKeyStringValue(DataSetCompleteRegistrationTableInfo.Columns.PERIOD, dscr.period())
                .appendKeyStringValue(DataSetCompleteRegistrationTableInfo.Columns.DATA_SET, dscr.dataSet())
                .appendKeyStringValue(DataSetCompleteRegistrationTableInfo.Columns.ORGANISATION_UNIT,
                        dscr.organisationUnit())
                .appendKeyStringValue(DataSetCompleteRegistrationTableInfo.Columns.ATTRIBUTE_OPTION_COMBO,
                        dscr.attributeOptionCombo())
                .appendKeyStringValue(DataSetCompleteRegistrationTableInfo.Columns.SYNC_STATE, State.UPLOADING)
                .build();
        return selectWhere(whereClause).size() > 0;
    }
}
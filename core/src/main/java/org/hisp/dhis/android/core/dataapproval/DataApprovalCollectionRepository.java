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

package org.hisp.dhis.android.core.dataapproval;

import org.hisp.dhis.android.core.arch.db.stores.internal.ObjectWithoutUidStore;
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppender;
import org.hisp.dhis.android.core.arch.repositories.collection.internal.ReadOnlyCollectionRepositoryImpl;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.EnumFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.FilterConnectorFactory;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.StringFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope;

import java.util.Map;

import javax.inject.Inject;

import dagger.Reusable;

@Reusable
public class DataApprovalCollectionRepository extends ReadOnlyCollectionRepositoryImpl<DataApproval,
        DataApprovalCollectionRepository> {


    @Inject
    DataApprovalCollectionRepository(final ObjectWithoutUidStore<DataApproval> dataApprovalStore,
                                     final Map<String, ChildrenAppender<DataApproval>> childrenAppenders,
                                     final RepositoryScope repositoryScope) {

        super(dataApprovalStore, childrenAppenders, repositoryScope, new FilterConnectorFactory<>(repositoryScope,
                s -> new DataApprovalCollectionRepository(dataApprovalStore, childrenAppenders, s)));
    }

    public StringFilterConnector<DataApprovalCollectionRepository> byWorkflowUid() {
        return cf.string(DataApprovalTableInfo.Columns.WORKFLOW);
    }

    public StringFilterConnector<DataApprovalCollectionRepository> byOrganisationUnitUid() {
        return cf.string(DataApprovalTableInfo.Columns.ORGANISATION_UNIT);
    }

    public StringFilterConnector<DataApprovalCollectionRepository> byPeriodId() {
        return cf.string(DataApprovalTableInfo.Columns.PERIOD);
    }

    public StringFilterConnector<DataApprovalCollectionRepository> byAttributeOptionComboUid() {
        return cf.string(DataApprovalTableInfo.Columns.ATTRIBUTE_OPTION_COMBO);
    }

    public EnumFilterConnector<DataApprovalCollectionRepository, DataApprovalState> byState() {
        return cf.enumC(DataApprovalTableInfo.Columns.STATE);
    }

}

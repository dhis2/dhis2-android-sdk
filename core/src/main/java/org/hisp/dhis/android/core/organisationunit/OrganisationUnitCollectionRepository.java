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
package org.hisp.dhis.android.core.organisationunit;

import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore;
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppender;
import org.hisp.dhis.android.core.arch.repositories.collection.internal.ReadOnlyIdentifiableCollectionRepositoryImpl;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.DateFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.EnumFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.FilterConnectorFactory;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.IntegerFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.StringFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope;
import org.hisp.dhis.android.core.common.FeatureType;
import org.hisp.dhis.android.core.common.IdentifiableColumns;
import org.hisp.dhis.android.core.dataset.DataSetOrganisationUnitLinkTableInfo;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitTableInfo.Columns;
import org.hisp.dhis.android.core.organisationunit.internal.OrganisationUnitFields;
import org.hisp.dhis.android.core.user.UserOrganisationUnitLinkTableInfo;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import dagger.Reusable;

@Reusable
public class OrganisationUnitCollectionRepository
        extends ReadOnlyIdentifiableCollectionRepositoryImpl<OrganisationUnit, OrganisationUnitCollectionRepository> {

    @Inject
    OrganisationUnitCollectionRepository(final IdentifiableObjectStore<OrganisationUnit> store,
                                         final Map<String, ChildrenAppender<OrganisationUnit>> childrenAppenders,
                                         final RepositoryScope scope) {
        super(store, childrenAppenders, scope, new FilterConnectorFactory<>(scope,
                s -> new OrganisationUnitCollectionRepository(store, childrenAppenders, s)));
    }

    public StringFilterConnector<OrganisationUnitCollectionRepository> byParentUid() {
        return cf.string(Columns.PARENT);
    }

    public StringFilterConnector<OrganisationUnitCollectionRepository> byPath() {
        return cf.string(Columns.PATH);
    }

    public DateFilterConnector<OrganisationUnitCollectionRepository> byOpeningDate() {
        return cf.date(Columns.OPENING_DATE);
    }

    public DateFilterConnector<OrganisationUnitCollectionRepository> byClosedDate() {
        return cf.date(Columns.CLOSED_DATE);
    }

    public IntegerFilterConnector<OrganisationUnitCollectionRepository> byLevel() {
        return cf.integer(Columns.LEVEL);
    }


    public EnumFilterConnector<OrganisationUnitCollectionRepository, FeatureType> byGeometryType() {
        return cf.enumC(Columns.GEOMETRY_TYPE);
    }

    public StringFilterConnector<OrganisationUnitCollectionRepository> byGeometryCoordinates() {
        return cf.string(Columns.GEOMETRY_COORDINATES);
    }

    public OrganisationUnitCollectionRepository byOrganisationUnitScope(OrganisationUnit.Scope scope) {
        return cf.subQuery(IdentifiableColumns.UID).inLinkTable(
                UserOrganisationUnitLinkTableInfo.TABLE_INFO.name(),
                UserOrganisationUnitLinkTableInfo.Columns.ORGANISATION_UNIT,
                UserOrganisationUnitLinkTableInfo.Columns.ORGANISATION_UNIT_SCOPE,
                Collections.singletonList(scope.name()));
    }

    public OrganisationUnitCollectionRepository byRootOrganisationUnit(Boolean isRoot) {
        return cf.subQuery(IdentifiableColumns.UID).inLinkTable(
                UserOrganisationUnitLinkTableInfo.TABLE_INFO.name(),
                UserOrganisationUnitLinkTableInfo.Columns.ORGANISATION_UNIT,
                UserOrganisationUnitLinkTableInfo.Columns.ROOT,
                Collections.singletonList(isRoot ? "1" : "0"));
    }

    public OrganisationUnitCollectionRepository byProgramUids(List<String> programUids) {
        return cf.subQuery(IdentifiableColumns.UID).inLinkTable(
                OrganisationUnitProgramLinkTableInfo.TABLE_INFO.name(),
                OrganisationUnitProgramLinkTableInfo.Columns.ORGANISATION_UNIT,
                OrganisationUnitProgramLinkTableInfo.Columns.PROGRAM,
                programUids);
    }

    public OrganisationUnitCollectionRepository byDataSetUids(List<String> dataSetUids) {
        return cf.subQuery(IdentifiableColumns.UID).inLinkTable(
                DataSetOrganisationUnitLinkTableInfo.TABLE_INFO.name(),
                DataSetOrganisationUnitLinkTableInfo.Columns.ORGANISATION_UNIT,
                DataSetOrganisationUnitLinkTableInfo.Columns.DATA_SET,
                dataSetUids);
    }

    public OrganisationUnitCollectionRepository withProgramUids() {
        return cf.withChild(OrganisationUnitFields.PROGRAMS);
    }

    public OrganisationUnitCollectionRepository withDataSetUids() {
        return cf.withChild(OrganisationUnitFields.DATA_SETS);
    }

    public OrganisationUnitCollectionRepository withOrganisationUnitGroups() {
        return cf.withChild(OrganisationUnitFields.ORGANISATION_UNIT_GROUPS);
    }
}
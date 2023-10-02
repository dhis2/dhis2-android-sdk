/*
 *  Copyright (c) 2004-2023, University of Oslo
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
package org.hisp.dhis.android.core.organisationunit

import dagger.Reusable
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppender
import org.hisp.dhis.android.core.arch.repositories.collection.internal.ReadOnlyIdentifiableCollectionRepositoryImpl
import org.hisp.dhis.android.core.arch.repositories.filters.internal.DateFilterConnector
import org.hisp.dhis.android.core.arch.repositories.filters.internal.EnumFilterConnector
import org.hisp.dhis.android.core.arch.repositories.filters.internal.FilterConnectorFactory
import org.hisp.dhis.android.core.arch.repositories.filters.internal.IntegerFilterConnector
import org.hisp.dhis.android.core.arch.repositories.filters.internal.StringFilterConnector
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope
import org.hisp.dhis.android.core.common.FeatureType
import org.hisp.dhis.android.core.common.IdentifiableColumns
import org.hisp.dhis.android.core.dataset.DataSetOrganisationUnitLinkTableInfo
import org.hisp.dhis.android.core.organisationunit.internal.OrganisationUnitFields
import org.hisp.dhis.android.core.organisationunit.internal.OrganisationUnitStore
import org.hisp.dhis.android.core.user.UserOrganisationUnitLinkTableInfo
import javax.inject.Inject

@Reusable
@Suppress("TooManyFunctions")
class OrganisationUnitCollectionRepository @Inject internal constructor(
    store: OrganisationUnitStore,
    childrenAppenders: MutableMap<String, ChildrenAppender<OrganisationUnit>>,
    scope: RepositoryScope,
) : ReadOnlyIdentifiableCollectionRepositoryImpl<OrganisationUnit, OrganisationUnitCollectionRepository>(
    store,
    childrenAppenders,
    scope,
    FilterConnectorFactory(
        scope,
    ) { s: RepositoryScope ->
        OrganisationUnitCollectionRepository(
            store,
            childrenAppenders,
            s,
        )
    },
) {
    fun byParentUid(): StringFilterConnector<OrganisationUnitCollectionRepository> {
        return cf.string(OrganisationUnitTableInfo.Columns.PARENT)
    }

    fun byPath(): StringFilterConnector<OrganisationUnitCollectionRepository> {
        return cf.string(OrganisationUnitTableInfo.Columns.PATH)
    }

    fun byOpeningDate(): DateFilterConnector<OrganisationUnitCollectionRepository> {
        return cf.date(OrganisationUnitTableInfo.Columns.OPENING_DATE)
    }

    fun byClosedDate(): DateFilterConnector<OrganisationUnitCollectionRepository> {
        return cf.date(OrganisationUnitTableInfo.Columns.CLOSED_DATE)
    }

    fun byLevel(): IntegerFilterConnector<OrganisationUnitCollectionRepository> {
        return cf.integer(OrganisationUnitTableInfo.Columns.LEVEL)
    }

    fun byGeometryType(): EnumFilterConnector<OrganisationUnitCollectionRepository, FeatureType> {
        return cf.enumC(OrganisationUnitTableInfo.Columns.GEOMETRY_TYPE)
    }

    fun byGeometryCoordinates(): StringFilterConnector<OrganisationUnitCollectionRepository> {
        return cf.string(OrganisationUnitTableInfo.Columns.GEOMETRY_COORDINATES)
    }

    fun byOrganisationUnitScope(scope: OrganisationUnit.Scope): OrganisationUnitCollectionRepository {
        return cf.subQuery(IdentifiableColumns.UID).inLinkTable(
            UserOrganisationUnitLinkTableInfo.TABLE_INFO.name(),
            UserOrganisationUnitLinkTableInfo.Columns.ORGANISATION_UNIT,
            UserOrganisationUnitLinkTableInfo.Columns.ORGANISATION_UNIT_SCOPE,
            listOf(scope.name),
        )
    }

    fun byRootOrganisationUnit(isRoot: Boolean): OrganisationUnitCollectionRepository {
        return cf.subQuery(IdentifiableColumns.UID).inLinkTable(
            UserOrganisationUnitLinkTableInfo.TABLE_INFO.name(),
            UserOrganisationUnitLinkTableInfo.Columns.ORGANISATION_UNIT,
            UserOrganisationUnitLinkTableInfo.Columns.ROOT,
            listOf(if (isRoot) "1" else "0"),
        )
    }

    fun byProgramUids(programUids: List<String>): OrganisationUnitCollectionRepository {
        return cf.subQuery(IdentifiableColumns.UID).inLinkTable(
            OrganisationUnitProgramLinkTableInfo.TABLE_INFO.name(),
            OrganisationUnitProgramLinkTableInfo.Columns.ORGANISATION_UNIT,
            OrganisationUnitProgramLinkTableInfo.Columns.PROGRAM,
            programUids,
        )
    }

    fun byDataSetUids(dataSetUids: List<String>): OrganisationUnitCollectionRepository {
        return cf.subQuery(IdentifiableColumns.UID).inLinkTable(
            DataSetOrganisationUnitLinkTableInfo.TABLE_INFO.name(),
            DataSetOrganisationUnitLinkTableInfo.Columns.ORGANISATION_UNIT,
            DataSetOrganisationUnitLinkTableInfo.Columns.DATA_SET,
            dataSetUids,
        )
    }

    fun withProgramUids(): OrganisationUnitCollectionRepository {
        return cf.withChild(OrganisationUnitFields.PROGRAMS)
    }

    fun withDataSetUids(): OrganisationUnitCollectionRepository {
        return cf.withChild(OrganisationUnitFields.DATA_SETS)
    }

    fun withOrganisationUnitGroups(): OrganisationUnitCollectionRepository {
        return cf.withChild(OrganisationUnitFields.ORGANISATION_UNIT_GROUPS)
    }
}

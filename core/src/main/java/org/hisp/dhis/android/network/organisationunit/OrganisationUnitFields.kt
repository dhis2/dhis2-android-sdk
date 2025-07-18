/*
 *  Copyright (c) 2004-2025, University of Oslo
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
package org.hisp.dhis.android.network.organisationunit

import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitGroup
import org.hisp.dhis.android.network.common.fields.BaseFields
import org.hisp.dhis.android.network.common.fields.Fields
import org.hisp.dhis.android.persistence.organisationunit.OrganisationUnitTableInfo.Columns

internal object OrganisationUnitFields : BaseFields<OrganisationUnit>() {
    const val PROGRAMS = "programs"
    const val DATA_SETS = "dataSets"
    private const val ANCESTORS = "ancestors"
    const val ORGANISATION_UNIT_GROUPS = "organisationUnitGroups"
    const val FEATURE_TYPE = "featureType"

    val uid = fh.uid()
    val path = fh.field(Columns.PATH)
    private val displayName = fh.displayName()
    private val openingDate = fh.field(Columns.OPENING_DATE)
    private val closedDate = fh.field(Columns.CLOSED_DATE)
    val ASC_ORDER = uid.name + ":" + RepositoryScope.OrderByDirection.ASC.api

    val fieldsInUserCall = Fields.from(
        uid,
        path,
    )

    val allFields = Fields.from(
        fh.getNameableFields(),
        path,
        openingDate,
        closedDate,
        fh.field(Columns.LEVEL),
        fh.field(FEATURE_TYPE),
        fh.nestedFieldWithUid(Columns.PARENT),
        fh.nestedFieldWithUid(PROGRAMS),
        fh.nestedFieldWithUid(DATA_SETS),
        fh.nestedField<OrganisationUnit>(ANCESTORS).with(uid, displayName),
        fh.nestedField<OrganisationUnitGroup>(ORGANISATION_UNIT_GROUPS).with(OrganisationUnitGroupFields.allFields),
    )
}

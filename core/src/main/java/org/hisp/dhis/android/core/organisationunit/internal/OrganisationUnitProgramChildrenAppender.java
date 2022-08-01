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

package org.hisp.dhis.android.core.organisationunit.internal;

import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter;
import org.hisp.dhis.android.core.arch.db.stores.internal.ObjectWithUidChildStore;
import org.hisp.dhis.android.core.arch.db.stores.internal.StoreFactory;
import org.hisp.dhis.android.core.arch.db.stores.projections.internal.LinkTableChildProjection;
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppender;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitProgramLinkTableInfo;
import org.hisp.dhis.android.core.program.ProgramTableInfo;

final class OrganisationUnitProgramChildrenAppender extends ChildrenAppender<OrganisationUnit> {

    private static final LinkTableChildProjection CHILD_PROJECTION = new LinkTableChildProjection(
            ProgramTableInfo.TABLE_INFO,
            OrganisationUnitProgramLinkTableInfo.Columns.ORGANISATION_UNIT,
            OrganisationUnitProgramLinkTableInfo.Columns.PROGRAM);

    private final ObjectWithUidChildStore<OrganisationUnit> childStore;

    private OrganisationUnitProgramChildrenAppender(
            ObjectWithUidChildStore<OrganisationUnit> childStore) {
        this.childStore = childStore;
    }

    @Override
    public OrganisationUnit appendChildren(OrganisationUnit organisationUnit) {
        OrganisationUnit.Builder builder = organisationUnit.toBuilder();
        builder.programs(childStore.getChildren(organisationUnit));
        return builder.build();
    }

    static ChildrenAppender<OrganisationUnit> create(DatabaseAdapter databaseAdapter) {
        return new OrganisationUnitProgramChildrenAppender(
                StoreFactory.objectWithUidChildStore(
                        databaseAdapter,
                        OrganisationUnitProgramLinkTableInfo.TABLE_INFO,
                        CHILD_PROJECTION
                )
        );
    }
}
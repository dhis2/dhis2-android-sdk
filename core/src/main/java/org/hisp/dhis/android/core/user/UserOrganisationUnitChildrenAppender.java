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
package org.hisp.dhis.android.core.user;

import org.hisp.dhis.android.core.arch.db.WhereClauseBuilder;
import org.hisp.dhis.android.core.arch.db.stores.LinkModelChildStore;
import org.hisp.dhis.android.core.arch.repositories.children.ChildrenAppender;
import org.hisp.dhis.android.core.common.StoreFactory;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitModel;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitStore;

import static org.hisp.dhis.android.core.user.UserOrganisationUnitLinkTableInfo.Columns.ORGANISATION_UNIT_SCOPE;

final class UserOrganisationUnitChildrenAppender extends ChildrenAppender<User> {

    private final LinkModelChildStore<User, OrganisationUnit> linkModelChildStore;

    private UserOrganisationUnitChildrenAppender(LinkModelChildStore<User, OrganisationUnit> linkModelChildStore) {
        this.linkModelChildStore = linkModelChildStore;
    }

    @Override
    protected User appendChildren(User user) {
        User.Builder builder = user.toBuilder();

        String dataCaptureWhere = new WhereClauseBuilder().appendKeyStringValue(
                ORGANISATION_UNIT_SCOPE, OrganisationUnitModel.Scope.SCOPE_DATA_CAPTURE).build();

        String searchWhere = new WhereClauseBuilder().appendKeyStringValue(
                ORGANISATION_UNIT_SCOPE, OrganisationUnitModel.Scope.SCOPE_TEI_SEARCH).build();

        builder.organisationUnits(linkModelChildStore.getChildrenWhere(user, dataCaptureWhere));
        builder.teiSearchOrganisationUnits(linkModelChildStore.getChildrenWhere(user, searchWhere));
        return builder.build();
    }

    static ChildrenAppender<User> create(DatabaseAdapter databaseAdapter) {
        return new UserOrganisationUnitChildrenAppender(
                StoreFactory.linkModelChildStore(
                        databaseAdapter,
                        UserOrganisationUnitLinkTableInfo.TABLE_INFO,
                        UserOrganisationUnitLinkTableInfo.CHILD_PROJECTION,
                        OrganisationUnitStore.FACTORY
                )
        );
    }
}
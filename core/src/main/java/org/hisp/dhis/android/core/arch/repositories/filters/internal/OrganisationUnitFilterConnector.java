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

package org.hisp.dhis.android.core.arch.repositories.filters.internal;

import androidx.annotation.NonNull;

import org.hisp.dhis.android.core.arch.repositories.collection.BaseRepository;
import org.hisp.dhis.android.core.common.OrganisationUnitFilter;
import org.hisp.dhis.android.core.common.RelativeOrganisationUnit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class OrganisationUnitFilterConnector<R extends BaseRepository> {

    private final ScopedRepositoryFilterFactory<R, List<OrganisationUnitFilter>> repositoryFactory;

    OrganisationUnitFilterConnector(ScopedRepositoryFilterFactory<R, List<OrganisationUnitFilter>> repositoryFactory) {
        this.repositoryFactory = repositoryFactory;
    }

    /**
     * Returns a new repository whose scope is the one of the current repository plus the new filter being applied.
     * The eq filter checks if the given field has the same the orgunit as the one provided.
     * @param orgunitUid value to compare with the target field
     * @return the new repository
     */
    public R eq(@NonNull String orgunitUid) {
        OrganisationUnitFilter filter = new OrganisationUnitFilter(orgunitUid, null);
        return repositoryFactory.updated(Collections.singletonList(filter));
    }

    /**
     * Returns a new repository whose scope is the one of the current repository plus the new filter being applied.
     * The eq filter checks if the given field has the same the orgunit as the one provided.
     * @param relative value to compare with the target field
     * @return the new repository
     */
    public R eq(@NonNull RelativeOrganisationUnit relative) {
        OrganisationUnitFilter filter = new OrganisationUnitFilter(null, relative);
        return repositoryFactory.updated(Collections.singletonList(filter));
    }

    /**
     * Returns a new repository whose scope is the one of the current repository plus the new filter being applied.
     * The in filter checks if the given field is included in the list provided.
     * @param orgunitUids list of uids organisation units to compare
     * @return the new repository
     */
    public R in(@NonNull String...orgunitUids) {
        List<OrganisationUnitFilter> filters = new ArrayList<>();
        for (String uid : orgunitUids) {
            filters.add(new OrganisationUnitFilter(uid, null));
        }
        return repositoryFactory.updated(filters);
    }

    /**
     * Returns a new repository whose scope is the one of the current repository plus the new filter being applied.
     * The in filter checks if the given field is included in the list provided.
     * @param relatives list of relative organisation units to compare
     * @return the new repository
     */
    public R in(@NonNull RelativeOrganisationUnit...relatives) {
        List<OrganisationUnitFilter> filters = new ArrayList<>();
        for (RelativeOrganisationUnit relative : relatives) {
            filters.add(new OrganisationUnitFilter(null, relative));
        }
        return repositoryFactory.updated(filters);
    }
}
/*
 * Copyright (c) 2017, University of Oslo
 *
 * All rights reserved.
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
package org.hisp.dhis.android.core.organisationunit;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.hisp.dhis.android.core.common.Call;
import org.hisp.dhis.android.core.common.Payload;
import org.hisp.dhis.android.core.data.api.Fields;
import org.hisp.dhis.android.core.data.api.Filter;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.program.Program;
import org.hisp.dhis.android.core.resource.ResourceHandler;
import org.hisp.dhis.android.core.user.User;
import org.hisp.dhis.android.core.utils.HeaderUtils;

import java.io.IOException;
import java.util.Date;
import java.util.Set;

import retrofit2.Response;

import static org.hisp.dhis.android.core.organisationunit.OrganisationUnitTree.findRoots;

public class OrganisationUnitCall implements Call<Response<Payload<OrganisationUnit>>> {

    private final User user;
    private final OrganisationUnitService organisationUnitService;
    private final DatabaseAdapter database;
    private final OrganisationUnitHandler organisationUnitHandler;
    private final ResourceHandler resourceHandler;
    private boolean isExecuted;

    public OrganisationUnitCall(@NonNull User user,
                                @NonNull OrganisationUnitService organisationUnitService,
                                @NonNull DatabaseAdapter database,
                                @NonNull OrganisationUnitHandler organisationUnitHandler,
                                @NonNull ResourceHandler resourceHandler
    ) {
        this.user = user;
        this.organisationUnitService = organisationUnitService;
        this.database = database;
        this.organisationUnitHandler = organisationUnitHandler;
        this.resourceHandler = resourceHandler;
    }

    @Override
    public boolean isExecuted() {
        synchronized (this) {
            return isExecuted;
        }
    }

    @Override
    public Response<Payload<OrganisationUnit>> call() throws Exception {
        synchronized (this) {
            if (isExecuted) {
                throw new IllegalStateException("AlreadyExecuted");
            }
            isExecuted = true;
        }
        Date serverDate = null;
        Response<Payload<OrganisationUnit>> response = null;
        database.beginTransaction();
        try {
            Set<String> rootOrgUnitUids = findRoots(user.organisationUnits());
            Filter<OrganisationUnit, String> lastUpdatedFilter = OrganisationUnit.lastUpdated.gt(
                    resourceHandler.getLastUpdated(OrganisationUnit.class.getSimpleName()));
            // Call OrganisationUnitService for each tree root & try to persist sub-tree:
            for (String uid : rootOrgUnitUids) {
                response = getOrganisationUnit(uid, lastUpdatedFilter);
                if (response.isSuccessful()) {
                    if (serverDate == null) {//only get the very first date-time for the entire call.
                        serverDate = response.headers().getDate(HeaderUtils.DATE);
                    }
                    organisationUnitHandler.handleOrganisationUnits(
                            response.body().items(),
                            OrganisationUnitModel.Scope.SCOPE_DATA_CAPTURE,
                            user.uid()
                    );
                } else {
                    break; //stop early unsuccessful:
                }
            }
            if (response != null && response.isSuccessful()) {
                resourceHandler.handleResource(OrganisationUnit.class.getSimpleName(), serverDate);
                database.setTransactionSuccessful();
            }
        } finally {
            database.endTransaction();
        }
        return response;
    }

    private Response<Payload<OrganisationUnit>> getOrganisationUnit(
            @NonNull String uid,
            @Nullable Filter<OrganisationUnit, String> lastUpdatedFilter) throws IOException {

        Fields<OrganisationUnit> fields = Fields.<OrganisationUnit>builder().fields(
                OrganisationUnit.uid, OrganisationUnit.code, OrganisationUnit.name,
                OrganisationUnit.displayName, OrganisationUnit.created, OrganisationUnit.lastUpdated,
                OrganisationUnit.shortName, OrganisationUnit.displayShortName,
                OrganisationUnit.description, OrganisationUnit.displayDescription,
                OrganisationUnit.displayDescription, OrganisationUnit.path, OrganisationUnit.openingDate,
                OrganisationUnit.closedDate, OrganisationUnit.level, OrganisationUnit.deleted,
                OrganisationUnit.parent.with(OrganisationUnit.uid),
                //TODO: find out if programs are relevant: can they be updated on their own ?
                OrganisationUnit.programs.with(Program.uid)
        ).build();
        retrofit2.Call<Payload<OrganisationUnit>> call = organisationUnitService.getOrganisationUnits(uid, fields,
                lastUpdatedFilter, true, false);
        return call.execute();
    }
}
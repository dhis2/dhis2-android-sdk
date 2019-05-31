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
package org.hisp.dhis.android.core.organisationunit;

import androidx.annotation.NonNull;

import org.hisp.dhis.android.core.arch.api.executors.APICallExecutor;
import org.hisp.dhis.android.core.common.CollectionCleaner;
import org.hisp.dhis.android.core.common.ObjectWithUid;
import org.hisp.dhis.android.core.common.Payload;
import org.hisp.dhis.android.core.dataset.DataSet;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.program.Program;
import org.hisp.dhis.android.core.resource.Resource;
import org.hisp.dhis.android.core.resource.ResourceHandler;
import org.hisp.dhis.android.core.user.User;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import dagger.Reusable;

import static org.hisp.dhis.android.core.organisationunit.OrganisationUnitTree.findRoots;

@Reusable
class OrganisationUnitCallFactory {

    private final OrganisationUnitService organisationUnitService;
    private final OrganisationUnitHandler handler;

    private final APICallExecutor apiCallExecutor;
    private final ResourceHandler resourceHandler;
    private final CollectionCleaner<ObjectWithUid> programCollectionCleaner;
    private final CollectionCleaner<ObjectWithUid> dataSetCollectionCleaner;


    @Inject
    OrganisationUnitCallFactory(@NonNull OrganisationUnitService organisationUnitService,
                                @NonNull OrganisationUnitHandler handler,
                                @NonNull APICallExecutor apiCallExecutor,
                                @NonNull ResourceHandler resourceHandler,
                                @NonNull CollectionCleaner<ObjectWithUid> programCollectionCleaner,
                                @NonNull CollectionCleaner<ObjectWithUid> dataSetCollectionCleaner) {
        this.organisationUnitService = organisationUnitService;
        this.handler = handler;
        this.apiCallExecutor = apiCallExecutor;
        this.resourceHandler = resourceHandler;
        this.programCollectionCleaner = programCollectionCleaner;
        this.dataSetCollectionCleaner = dataSetCollectionCleaner;
    }

    public Callable<List<OrganisationUnit>> create(final User user,
                                                   final Set<String> programUids,
                                                   final Set<String> dataSetUids) {

        return () -> {
            Set<OrganisationUnit> allOrgunits = new HashSet<>();
            Set<OrganisationUnit> capture = downloadCaptureOrgunits(user, programUids, dataSetUids);
            Set<OrganisationUnit> search = downloadSearchOrgunits(user, programUids, dataSetUids);

            allOrgunits.addAll(capture);
            allOrgunits.addAll(search);

            programCollectionCleaner.deleteNotPresent(getLinkedPrograms(allOrgunits, programUids));
            dataSetCollectionCleaner.deleteNotPresent(getLinkedDatasets(allOrgunits, dataSetUids));

            resourceHandler.handleResource(Resource.Type.ORGANISATION_UNIT);

            return new ArrayList<>(allOrgunits);
        };
    }

    private Set<OrganisationUnit> downloadCaptureOrgunits(final User user,
                                                          final Set<String> programUids,
                                                          final Set<String> dataSetUids) throws D2Error {
        Set<String> captureOrgunitsUids = findRoots(user.organisationUnits());
        Set<OrganisationUnit> captureOrgunits = downloadOrgunits(captureOrgunitsUids);

        handler.setData(programUids, dataSetUids, user, OrganisationUnit.Scope.SCOPE_DATA_CAPTURE);

        handler.handleMany(captureOrgunits, new OrganisationUnitDisplayPathTransformer());

        return captureOrgunits;
    }

    private Set<OrganisationUnit> downloadSearchOrgunits(final User user,
                                                           final Set<String> programUids,
                                                           final Set<String> dataSetUids) throws D2Error {
        Set<String> searchOrgunitsUids = findRoots(user.teiSearchOrganisationUnits());
        Set<OrganisationUnit> searchOrgunits = downloadOrgunits(searchOrgunitsUids);

        handler.setData(programUids, dataSetUids, user, OrganisationUnit.Scope.SCOPE_TEI_SEARCH);

        handler.handleMany(searchOrgunits, new OrganisationUnitDisplayPathTransformer());

        return searchOrgunits;
    }

    private Set<OrganisationUnit> downloadOrgunits(Set<String> orguntis) throws D2Error {
        Set<OrganisationUnit> organisationUnits = new HashSet<>();
        for (String uid : orguntis) {
            organisationUnits.addAll(apiCallExecutor.executePayloadCall(
                    getOrganisationUnitAndDescendants(uid)));
        }

        return organisationUnits;
    }

    private retrofit2.Call<Payload<OrganisationUnit>> getOrganisationUnitAndDescendants(@NonNull String uid) {
        return organisationUnitService.getOrganisationUnitWithDescendants(
                uid, OrganisationUnitFields.allFields, true, false);
    }

    private Set<ObjectWithUid> getLinkedPrograms(Set<OrganisationUnit> capture, Set<String> programUids) {
        Set<ObjectWithUid> linkedPrograms = new HashSet<>();
        for(OrganisationUnit orgunit : capture) {
            List<Program> orgUnitPrograms = orgunit.programs();
            if (orgUnitPrograms != null && programUids != null) {
                for (Program program : orgUnitPrograms) {
                    if (programUids.contains(program.uid())) {
                        linkedPrograms.add(ObjectWithUid.create(program.uid()));
                    }
                }
            }
        }
        return linkedPrograms;
    }

    private Set<ObjectWithUid> getLinkedDatasets(Set<OrganisationUnit> capture, Set<String> dataSetUids) {
        Set<ObjectWithUid> linkedDatasets = new HashSet<>();
        for(OrganisationUnit orgunit : capture) {
            List<DataSet> orgUnitPrograms = orgunit.dataSets();
            if (orgUnitPrograms != null && dataSetUids != null) {
                for (DataSet dataSet : orgUnitPrograms) {
                    if (dataSetUids.contains(dataSet.uid())) {
                        linkedDatasets.add(ObjectWithUid.create(dataSet.uid()));
                    }
                }
            }
        }
        return linkedDatasets;
    }
}
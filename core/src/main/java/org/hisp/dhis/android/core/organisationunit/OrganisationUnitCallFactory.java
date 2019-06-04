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

import org.hisp.dhis.android.core.arch.api.executors.internal.APICallExecutor;
import org.hisp.dhis.android.core.arch.cleaners.internal.CollectionCleaner;
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
    private final CollectionCleaner<Program> programCollectionCleaner;
    private final CollectionCleaner<DataSet> dataSetCollectionCleaner;
    private final CollectionCleaner<OrganisationUnitGroup> organisationUnitGroupCollectionCleaner;

    @Inject
    OrganisationUnitCallFactory(@NonNull OrganisationUnitService organisationUnitService,
                                @NonNull OrganisationUnitHandler handler,
                                @NonNull APICallExecutor apiCallExecutor,
                                @NonNull ResourceHandler resourceHandler,
                                @NonNull CollectionCleaner<Program> programCollectionCleaner,
                                @NonNull CollectionCleaner<DataSet> dataSetCollectionCleaner,
                                @NonNull CollectionCleaner<OrganisationUnitGroup>
                                        organisationUnitGroupCollectionCleaner) {
        this.organisationUnitService = organisationUnitService;
        this.handler = handler;
        this.apiCallExecutor = apiCallExecutor;
        this.resourceHandler = resourceHandler;
        this.programCollectionCleaner = programCollectionCleaner;
        this.dataSetCollectionCleaner = dataSetCollectionCleaner;
        this.organisationUnitGroupCollectionCleaner = organisationUnitGroupCollectionCleaner;
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

            if (programUids != null) {
                programCollectionCleaner.deleteNotPresent(getLinkedPrograms(allOrgunits, programUids));
            }
            if (dataSetUids != null) {
                dataSetCollectionCleaner.deleteNotPresent(getLinkedDatasets(allOrgunits, dataSetUids));
            }
            organisationUnitGroupCollectionCleaner.deleteNotPresent(getLinkedGroups(allOrgunits));

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

    private Set<Program> getLinkedPrograms(Set<OrganisationUnit> capture, @NonNull Set<String> programUids) {
        Set<Program> linkedPrograms = new HashSet<>();
        for (OrganisationUnit orgunit : capture) {
            List<Program> orgUnitPrograms = orgunit.programs();
            if (orgUnitPrograms != null) {
                for (Program program : orgUnitPrograms) {
                    if (programUids.contains(program.uid())) {
                        linkedPrograms.add(program);
                    }
                }
            }
        }
        return linkedPrograms;
    }

    private Set<DataSet> getLinkedDatasets(Set<OrganisationUnit> capture, @NonNull Set<String> dataSetUids) {
        Set<DataSet> linkedDatasets = new HashSet<>();
        for (OrganisationUnit orgunit : capture) {
            List<DataSet> orgUnitPrograms = orgunit.dataSets();
            if (orgUnitPrograms != null) {
                for (DataSet dataSet : orgUnitPrograms) {
                    if (dataSetUids.contains(dataSet.uid())) {
                        linkedDatasets.add(dataSet);
                    }
                }
            }
        }
        return linkedDatasets;
    }

    private Set<OrganisationUnitGroup> getLinkedGroups(Set<OrganisationUnit> capture) {
        Set<OrganisationUnitGroup> linkedGroups = new HashSet<>();
        for (OrganisationUnit orgunit : capture) {
            List<OrganisationUnitGroup> orgUnitGroups = orgunit.organisationUnitGroups();
            if (orgUnitGroups != null) {
                linkedGroups.addAll(orgUnitGroups);
            }
        }
        return linkedGroups;
    }
}
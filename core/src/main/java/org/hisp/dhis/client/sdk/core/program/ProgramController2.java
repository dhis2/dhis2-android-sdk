/*
 * Copyright (c) 2016, University of Oslo
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

package org.hisp.dhis.client.sdk.core.program;

import org.hisp.dhis.client.sdk.core.common.Fields;
import org.hisp.dhis.client.sdk.core.common.network.ApiException;
import org.hisp.dhis.client.sdk.core.common.persistence.DbUtils;
import org.hisp.dhis.client.sdk.core.common.persistence.IDbOperation;
import org.hisp.dhis.client.sdk.core.common.persistence.ITransactionManager;
import org.hisp.dhis.client.sdk.core.common.preferences.ILastUpdatedPreferences;
import org.hisp.dhis.client.sdk.core.common.preferences.ResourceType;
import org.hisp.dhis.client.sdk.models.program.Program;
import org.hisp.dhis.client.sdk.models.utils.IModelUtils;
import org.joda.time.DateTime;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public class ProgramController2 implements IProgramController {
    /* Api clients */
    private final IProgramApiClient programApiClient;

    /* Local storage */
    private final IProgramStore programStore;

    /* Utilities */
    private final ITransactionManager transactionManager;
    private final ILastUpdatedPreferences lastUpdatedPreferences;
    private final IModelUtils modelUtils;

    public ProgramController2(IProgramApiClient programApiClient, IProgramStore programStore,
                              ITransactionManager transactionManager,
                              ILastUpdatedPreferences lastUpdatedPreferences,
                              IModelUtils modelUtils) {
        this.programApiClient = programApiClient;
        this.programStore = programStore;
        this.transactionManager = transactionManager;
        this.lastUpdatedPreferences = lastUpdatedPreferences;
        this.modelUtils = modelUtils;
    }

    /* TODO implement this method first, then reuse similar logic in sync(uids) method */
    /* TODO consider taking into account programs which are directly assigned to user */
    @Override
    public void sync() throws ApiException {
        DateTime lastUpdated = lastUpdatedPreferences.get(ResourceType.PROGRAM);

        List<Program> allExistingPrograms = programApiClient.getPrograms(Fields.BASIC, null);
        List<Program> updatedPrograms = programApiClient.getPrograms(Fields.ALL, lastUpdated);
        List<Program> persistedPrograms = programStore.queryAll();

        for (Program program : allExistingPrograms) {
            System.out.println("Program: " + program.getDisplayName());
        }
    }

    @Override
    public void sync(Collection<String> uids) throws ApiException {
        DateTime lastUpdated = lastUpdatedPreferences.get(ResourceType.PROGRAM);

        List<Program> persistedPrograms = programStore.queryAll();

        for (Program program : persistedPrograms) {
            System.out.println("Stored program: " + program.getDisplayName());
        }

        // we have to download all ids from server in order to
        // find out what was removed on the server side
        List<Program> allExistingPrograms = programApiClient
                .getPrograms(Fields.BASIC, null);

        // here we want to get list of ids of programs which are
        // stored locally and list of programs which we want to get
        Set<String> persistedProgramIds = modelUtils.toUidSet(persistedPrograms);
        persistedProgramIds.addAll(uids);

        List<Program> updatedPrograms = programApiClient.getPrograms(Fields.ALL, lastUpdated,
                persistedProgramIds.toArray(new String[persistedProgramIds.size()]));

        for (Program program : updatedPrograms) {
            System.out.println("Program downloaded displayName: " + program.getDisplayName());
        }

        // sync new programs we got from server to database
        // we will have to perform something similar to what happens in AbsController
        List<IDbOperation> dbOperations = DbUtils.createOperations(allExistingPrograms,
                updatedPrograms, persistedPrograms, programStore, modelUtils);
        transactionManager.transact(dbOperations);

        // determine which relationships programs have to other models;
        // and which type of relationships (one to one, one to many, many to one and etc)

        // TODO build relationships with OrganisationUnits (possible through corresponding store)
        // TODO implement ForeignKey support for tracked entity (save only uid in table)
        // TODO find another way to inject dependencies: injecting them through constructor becomes too verbose.
    }

    /*
         * First we need to check what was removed on the server while we were offline:
            - Download list of basic items (uid, displayName)
            - Compare to what we have locally
            - Create delete operations for programs
         * After this, check what was updated among existing items, fetch those updates,
           merge them with existing elements in database
         * Now, when we have updated existing entries in database, we can work
           on downloading given uids:
            - First, check if any of requested UIDS are already there in database.
            - if some of them already there, remove UID from list of programs to download
            - All remaining uids should be downloaded, direct properties should be saved to
            program table.
            - After that we need to build relationships with related resources by using link tables
              TODO decide how to work with link tables through Store abstraction.
     */
}

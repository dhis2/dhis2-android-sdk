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
import org.hisp.dhis.client.sdk.core.common.controllers.AbsController;
import org.hisp.dhis.client.sdk.core.common.network.ApiException;
import org.hisp.dhis.client.sdk.core.common.persistence.DbOperation;
import org.hisp.dhis.client.sdk.core.common.persistence.IDbOperation;
import org.hisp.dhis.client.sdk.core.common.persistence.ITransactionManager;
import org.hisp.dhis.client.sdk.core.common.preferences.ILastUpdatedPreferences;
import org.hisp.dhis.client.sdk.core.common.preferences.ResourceType;
import org.hisp.dhis.client.sdk.core.dataelement.IDataElementStore;
import org.hisp.dhis.client.sdk.core.optionset.IOptionSetStore;
import org.hisp.dhis.client.sdk.core.optionset.IOptionStore;
import org.hisp.dhis.client.sdk.core.systeminfo.ISystemInfoApiClient;
import org.hisp.dhis.client.sdk.core.trackedentity.ITrackedEntityAttributeStore;
import org.hisp.dhis.client.sdk.core.user.IUserApiClient;
import org.hisp.dhis.client.sdk.models.dataelement.DataElement;
import org.hisp.dhis.client.sdk.models.optionset.Option;
import org.hisp.dhis.client.sdk.models.optionset.OptionSet;
import org.hisp.dhis.client.sdk.models.program.*;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntityAttribute;
import org.hisp.dhis.client.sdk.models.utils.IModelUtils;
import org.joda.time.DateTime;

import java.util.*;

public class ProgramController extends AbsController<Program> implements IProgramController {

    // API clients
    private final IProgramApiClient programApiClient;

    private final ITransactionManager transactionManager;
    private final ILastUpdatedPreferences lastUpdatedPreferences;
    private final IProgramStore programStore;
    private final ISystemInfoApiClient systemInfoApiClient;
    private final IModelUtils modelUtils;

    public ProgramController(IProgramApiClient programApiClient, ITransactionManager transactionManager, ILastUpdatedPreferences lastUpdatedPreferences, IProgramStore programStore, ISystemInfoApiClient systemInfoApiClient, IModelUtils modelUtils) {
        this.programApiClient = programApiClient;
        this.transactionManager = transactionManager;
        this.lastUpdatedPreferences = lastUpdatedPreferences;
        this.programStore = programStore;
        this.systemInfoApiClient = systemInfoApiClient;
        this.modelUtils = modelUtils;
    }

    private void getProgramsDataFromServer() throws ApiException {
        ResourceType resource = ResourceType.PROGRAMS;
        DateTime serverTime = systemInfoApiClient.getSystemInfo().getServerDate();
        DateTime lastUpdated = lastUpdatedPreferences.get(resource);

        List<Program> allProgramsOnServer = programApiClient.getPrograms(Fields.BASIC, null);
        List<Program> updatedPrograms = programApiClient.getPrograms(Fields.ALL, lastUpdated);
        List<Program> persistedPrograms = programStore.queryAll();
        transactionManager.transact(getMergeOperations(allProgramsOnServer, updatedPrograms, persistedPrograms, programStore, modelUtils));
        lastUpdatedPreferences.save(resource, serverTime);
    }

    private void getProgramsDataFromServer(Set<String> programUidsToLoad) throws ApiException {
        for(String uid : programUidsToLoad) {
            getProgramDataFromServer(uid);
        }
    }

    private void getProgramDataFromServer(String uid) throws ApiException {
        DateTime serverTime = systemInfoApiClient.getSystemInfo().getServerDate();
        DateTime lastUpdated = lastUpdatedPreferences.get(ResourceType.PROGRAM, uid);

        List<Program> allProgramsOnServer = programApiClient.getPrograms(Fields.BASIC, null);
        Program updatedProgram = programApiClient.getProgram(uid, Fields.ALL, lastUpdated);
        List<Program> updatedPrograms = new ArrayList<>();
        updatedPrograms.add(updatedProgram);
        List<Program> persistedPrograms = programStore.queryAll();
        transactionManager.transact(getMergeOperations(allProgramsOnServer, updatedPrograms, persistedPrograms, programStore, modelUtils));
        lastUpdatedPreferences.save(ResourceType.PROGRAM, serverTime, uid);
    }

    @Override
    public void sync() throws ApiException {
        getProgramsDataFromServer();
    }

    @Override
    public void sync(Set<String> uids) throws ApiException {
        getProgramsDataFromServer(uids);
    }

    @Override
    public void sync(String uid) throws ApiException {
        getProgramDataFromServer(uid);
    }
}


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
import org.hisp.dhis.client.sdk.core.common.controllers.AbsSyncStrategyController;
import org.hisp.dhis.client.sdk.core.common.controllers.SyncStrategy;
import org.hisp.dhis.client.sdk.core.common.persistence.DbOperation;
import org.hisp.dhis.client.sdk.core.common.persistence.DbUtils;
import org.hisp.dhis.client.sdk.core.common.persistence.TransactionManager;
import org.hisp.dhis.client.sdk.core.common.preferences.DateType;
import org.hisp.dhis.client.sdk.core.common.preferences.LastUpdatedPreferences;
import org.hisp.dhis.client.sdk.core.common.preferences.ResourceType;
import org.hisp.dhis.client.sdk.core.systeminfo.ISystemInfoController;
import org.hisp.dhis.client.sdk.core.user.UserApiClient;
import org.hisp.dhis.client.sdk.models.program.Program;
import org.hisp.dhis.client.sdk.models.utils.ModelUtils;
import org.joda.time.DateTime;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class ProgramControllerImpl extends AbsSyncStrategyController<Program>
        implements ProgramController {

    /* Controllers */
    private final ISystemInfoController systemInfoController;

    /* Api clients */
    private final ProgramApiClient programApiClient;
    private final UserApiClient userApiClient;

    /* Utilities */
    private final TransactionManager transactionManager;

    public ProgramControllerImpl(ISystemInfoController systemInfoController,
                                 ProgramApiClient programApiClient, UserApiClient userApiClient,
                                 ProgramStore programStore, TransactionManager transactionManager,
                                 LastUpdatedPreferences lastUpdatedPreferences) {
        super(ResourceType.PROGRAMS, programStore, lastUpdatedPreferences);

        this.systemInfoController = systemInfoController;
        this.programApiClient = programApiClient;
        this.userApiClient = userApiClient;
        this.transactionManager = transactionManager;
    }

    @Override
    protected void synchronize(SyncStrategy syncStrategy, Set<String> uids) {
        DateTime serverTime = systemInfoController.getSystemInfo().getServerDate();
        DateTime lastUpdated = lastUpdatedPreferences.get(ResourceType.PROGRAMS, DateType.SERVER);

        List<Program> persistedPrograms = identifiableObjectStore.queryAll();

        // we have to download all ids from server in order to
        // find out what was removed on the server side
        List<Program> allExistingPrograms = programApiClient.getPrograms(Fields.BASIC, null, null);

        Set<String> uidSet = null;
        if (uids != null) {
            // here we want to get list of ids of programs which are
            // stored locally and list of programs which we want to download
            uidSet = ModelUtils.toUidSet(persistedPrograms);
            uidSet.addAll(uids);
        }

        List<Program> updatedPrograms = programApiClient.getPrograms(
                Fields.ALL, lastUpdated, uidSet);

        // we need to mark assigned programs as "assigned" before storing them
        Map<String, Program> assignedPrograms = ModelUtils.toMap(userApiClient
                .getUserAccount().getPrograms());

        for (Program updatedProgram : updatedPrograms) {
            Program assignedProgram = assignedPrograms.get(updatedProgram.getUId());
            updatedProgram.setIsAssignedToUser(assignedProgram != null);
        }

        // we will have to perform something similar to what happens in AbsController
        List<DbOperation> dbOperations = DbUtils.createOperations(allExistingPrograms,
                updatedPrograms, persistedPrograms, identifiableObjectStore);
        transactionManager.transact(dbOperations);

        lastUpdatedPreferences.save(ResourceType.PROGRAMS, DateType.SERVER, serverTime);
    }
}

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

package org.hisp.dhis.client.sdk.core.user;

import org.hisp.dhis.client.sdk.core.common.controllers.SyncStrategy;
import org.hisp.dhis.client.sdk.core.common.network.ApiException;
import org.hisp.dhis.client.sdk.core.common.utils.ModelUtils;
import org.hisp.dhis.client.sdk.core.program.ProgramController;
import org.hisp.dhis.client.sdk.models.program.Program;
import org.hisp.dhis.client.sdk.models.program.ProgramType;
import org.hisp.dhis.client.sdk.models.user.UserAccount;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.hisp.dhis.client.sdk.utils.Preconditions.isNull;

/**
 * This class is intended to build relationships between organisation units and programs.
 */
public class AssignedProgramsControllerImpl implements AssignedProgramsController {

    /* Program controller */
    private final ProgramController programController;

    /* Api clients */
    private final UserApiClient userApiClient;

    public AssignedProgramsControllerImpl(ProgramController programController,
                                          UserApiClient userApiClient) {
        this.userApiClient = userApiClient;
        this.programController = programController;
    }

    @Override
    public void sync(Set<ProgramType> programTypes) throws ApiException {
        sync(SyncStrategy.DEFAULT, programTypes);
    }

    @Override
    public void sync(SyncStrategy strategy, Set<ProgramType> programTypes) throws ApiException {
        isNull(programTypes, "Set of ProgramType must not be null");

        if (programTypes.isEmpty()) {
            throw new IllegalArgumentException("Specify at least one ProgramType");
        }

        UserAccount userAccount = userApiClient.getUserAccount();

        /* get list of assigned programs */
        List<Program> assignedPrograms = userAccount.getPrograms();
        List<Program> programsToSync = new ArrayList<>();

        if (assignedPrograms != null && !assignedPrograms.isEmpty()) {
            for (Program assignedProgram : assignedPrograms) {
                if (programTypes.contains(assignedProgram.getProgramType())) {
                    programsToSync.add(assignedProgram);
                }
            }
        }

        /* convert them to set of ids */
        Set<String> ids = ModelUtils.toUidSet(programsToSync);

        /* get them through program controller */
        // programController.pull(strategy, ids);
        programController.pull(strategy, ProgramController.ProgramFields.DESCENDANTS, ids);
    }
}

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
import org.hisp.dhis.client.sdk.core.common.preferences.ILastUpdatedPreferences;
import org.hisp.dhis.client.sdk.core.common.preferences.ResourceType;
import org.hisp.dhis.client.sdk.models.program.Program;
import org.joda.time.DateTime;

import java.util.Collection;
import java.util.List;

public class ProgramController2 implements IProgramController {
    /* Api clients */
    private final IProgramApiClient programApiClient;

    /* Local storage */
    private final IProgramStore programStore;

    /* Utilities */
    private final ILastUpdatedPreferences lastUpdatedPreferences;

    public ProgramController2(IProgramApiClient programApiClient,
                              IProgramStore programStore,
                              ILastUpdatedPreferences lastUpdatedPreferences) {
        this.programApiClient = programApiClient;
        this.programStore = programStore;
        this.lastUpdatedPreferences = lastUpdatedPreferences;
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

        System.out.println("Program UIDS: " + uids);
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

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

package org.hisp.dhis.android.core.option;

import org.hisp.dhis.android.core.common.Call;
import org.hisp.dhis.android.core.common.Payload;
import org.hisp.dhis.android.core.data.api.Fields;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.resource.ResourceHandler;
import org.hisp.dhis.android.core.utils.HeaderUtils;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import retrofit2.Response;

public class OptionSetCall implements Call<Response<Payload<OptionSet>>> {
    // retrofit service
    private final OptionSetService optionSetService;

    // database adapter and handler
    private final OptionSetHandler optionSetHandler;
    private final DatabaseAdapter databaseAdapter;
    private final ResourceHandler resourceHandler;

    private boolean isExecuted;

    public OptionSetCall(OptionSetService optionSetService,
                         OptionSetHandler optionSetHandler,
                         DatabaseAdapter databaseAdapter,
                         ResourceHandler resourceHandler) {
        this.optionSetService = optionSetService;
        this.optionSetHandler = optionSetHandler;
        this.databaseAdapter = databaseAdapter;
        this.resourceHandler = resourceHandler;
    }


    @Override
    public boolean isExecuted() {
        synchronized (this) {
            return isExecuted;
        }
    }

    @Override
    public Response<Payload<OptionSet>> call() throws Exception {
        synchronized (this) {
            if (isExecuted) {
                throw new IllegalArgumentException("Already executed");
            }

            isExecuted = true;
        }
        Response<Payload<OptionSet>> response = getOptionSets();

        if (response.isSuccessful()) {
            saveOptionSets(response);
        }
        return response;
    }

    private Response<Payload<OptionSet>> getOptionSets() throws IOException {
        Fields<OptionSet> optionSetFields = Fields.<OptionSet>builder().fields(OptionSet.uid,
                OptionSet.code, OptionSet.name, OptionSet.displayName,
                OptionSet.created, OptionSet.lastUpdated,
                OptionSet.version, OptionSet.valueType,
                OptionSet.options.with(Option.uid, Option.code, Option.created,
                        Option.name, Option.displayName, Option.created,
                        Option.lastUpdated)).build();


        return optionSetService.optionSets(false, optionSetFields).execute();
    }

    private void saveOptionSets(Response<Payload<OptionSet>> response) {
        List<OptionSet> optionSets = response.body().items();
        if (optionSets != null && !optionSets.isEmpty()) {
            databaseAdapter.beginTransaction();
            int size = optionSets.size();

            try {
                Date serverDateTime = response.headers().getDate(HeaderUtils.DATE);

                for (int i = 0; i < size; i++) {
                    OptionSet optionSet = optionSets.get(i);
                    optionSetHandler.handleOptionSet(optionSet);
                }
                resourceHandler.handleResource(OptionSet.class.getSimpleName(), serverDateTime);

                databaseAdapter.setTransactionSuccessful();
            } finally {
                databaseAdapter.endTransaction();
            }
        }
    }
}

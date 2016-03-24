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

package org.hisp.dhis.client.sdk.android.optionset;

import org.hisp.dhis.client.sdk.android.api.persistence.flow.OptionSetFlow;
import org.hisp.dhis.client.sdk.android.common.AbsMapper;
import org.hisp.dhis.client.sdk.models.optionset.OptionSet;


public class OptionSetMapper extends AbsMapper<OptionSet, OptionSetFlow> {

    @Override
    public OptionSetFlow mapToDatabaseEntity(OptionSet optionSet) {
        if (optionSet == null) {
            return null;
        }

        OptionSetFlow optionSetFlow = new OptionSetFlow();
        optionSetFlow.setId(optionSet.getId());
        optionSetFlow.setUId(optionSet.getUId());
        optionSetFlow.setCreated(optionSet.getCreated());
        optionSetFlow.setLastUpdated(optionSet.getLastUpdated());
        optionSetFlow.setName(optionSet.getName());
        optionSetFlow.setDisplayName(optionSet.getDisplayName());
        optionSetFlow.setAccess(optionSet.getAccess());
        optionSetFlow.setVersion(optionSet.getVersion());
        return optionSetFlow;
    }

    @Override
    public OptionSet mapToModel(OptionSetFlow optionSetFlow) {
        if (optionSetFlow == null) {
            return null;
        }

        OptionSet optionSet = new OptionSet();
        optionSet.setId(optionSetFlow.getId());
        optionSet.setUId(optionSetFlow.getUId());
        optionSet.setCreated(optionSetFlow.getCreated());
        optionSet.setLastUpdated(optionSetFlow.getLastUpdated());
        optionSet.setName(optionSetFlow.getName());
        optionSet.setDisplayName(optionSetFlow.getDisplayName());
        optionSet.setAccess(optionSetFlow.getAccess());
        optionSet.setVersion(optionSetFlow.getVersion());
        return optionSet;
    }

    @Override
    public Class<OptionSet> getModelTypeClass() {
        return OptionSet.class;
    }

    @Override
    public Class<OptionSetFlow> getDatabaseEntityTypeClass() {
        return OptionSetFlow.class;
    }
}

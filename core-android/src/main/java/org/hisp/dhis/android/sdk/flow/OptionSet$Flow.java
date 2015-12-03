/*
 * Copyright (c) 2015, University of Oslo
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

package org.hisp.dhis.android.sdk.flow;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.Table;

import org.hisp.dhis.android.sdk.common.meta.DbDhis;
import org.hisp.dhis.java.sdk.models.optionset.OptionSet;

import java.util.ArrayList;
import java.util.List;

@Table(databaseName = DbDhis.NAME)
public final class OptionSet$Flow extends BaseIdentifiableObject$Flow {

    @Column
    int version;

    List<Option$Flow> options;

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public List<Option$Flow> getOptions() {
        return options;
    }

    public void setOptions(List<Option$Flow> options) {
        this.options = options;
    }

    public OptionSet$Flow() {
        // empty constructor
    }

    /*public static OptionSet toModel(OptionSet$Flow optionSetFlow) {
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
        optionSet.setOptions(Option$Flow.toModels(optionSetFlow.getOptions()));
        return optionSet;
    }

    public static OptionSet$Flow fromModel(OptionSet optionSet) {
        if (optionSet == null) {
            return null;
        }

        OptionSet$Flow optionSetFlow = new OptionSet$Flow();
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

    public static List<OptionSet> toModels(List<OptionSet$Flow> optionSetFlows) {
        List<OptionSet> optionSets = new ArrayList<>();

        if (optionSetFlows != null && !optionSetFlows.isEmpty()) {
            for (OptionSet$Flow optionSetFlow : optionSetFlows) {
                optionSets.add(toModel(optionSetFlow));
            }
        }

        return optionSets;
    }

    public static List<OptionSet$Flow> fromModels(List<OptionSet> optionSets) {
        List<OptionSet$Flow> optionSetFlows = new ArrayList<>();

        if (optionSets != null && !optionSets.isEmpty()) {
            for (OptionSet optionSet : optionSets) {
                optionSetFlows.add(fromModel(optionSet));
            }
        }

        return optionSetFlows;
    }*/
}

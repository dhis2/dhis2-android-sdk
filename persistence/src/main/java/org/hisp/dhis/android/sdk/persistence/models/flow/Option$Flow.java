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

package org.hisp.dhis.android.sdk.persistence.models.flow;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.Table;

import org.hisp.dhis.android.sdk.persistence.models.common.meta.DbDhis;
import org.hisp.dhis.android.sdk.models.optionset.Option;

import java.util.ArrayList;
import java.util.List;

@Table(databaseName = DbDhis.NAME)
public final class Option$Flow extends BaseIdentifiableObject$Flow {

    @Column
    int sortOrder;

    @Column
    String optionSet;

    @Column
    String code;

    public int getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    public String getOptionSet() {
        return optionSet;
    }

    public void setOptionSet(String optionSet) {
        this.optionSet = optionSet;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Option$Flow() {
        // empty constructor
    }

    public static Option toModel(Option$Flow optionFlow) {
        if (optionFlow == null) {
            return null;
        }

        Option option = new Option();
        option.setId(optionFlow.getId());
        option.setUId(optionFlow.getUId());
        option.setCreated(optionFlow.getCreated());
        option.setLastUpdated(optionFlow.getLastUpdated());
        option.setName(optionFlow.getName());
        option.setDisplayName(optionFlow.getDisplayName());
        option.setAccess(optionFlow.getAccess());
        option.setSortOrder(optionFlow.getSortOrder());
        option.setOptionSet(optionFlow.getOptionSet());
        option.setCode(optionFlow.getCode());
        return option;
    }

    public static Option$Flow fromModel(Option option) {
        if (option == null) {
            return null;
        }

        Option$Flow optionFlow = new Option$Flow();
        optionFlow.setId(option.getId());
        optionFlow.setUId(option.getUId());
        optionFlow.setCreated(option.getCreated());
        optionFlow.setLastUpdated(option.getLastUpdated());
        optionFlow.setName(option.getName());
        optionFlow.setDisplayName(option.getDisplayName());
        optionFlow.setAccess(option.getAccess());
        optionFlow.setSortOrder(option.getSortOrder());
        optionFlow.setOptionSet(option.getOptionSet());
        optionFlow.setCode(option.getCode());
        return optionFlow;
    }

    public static List<Option> toModels(List<Option$Flow> optionFlows) {
        List<Option> options = new ArrayList<>();

        if (optionFlows != null && !optionFlows.isEmpty()) {
            for (Option$Flow optionFlow : optionFlows) {
                options.add(toModel(optionFlow));
            }
        }

        return options;
    }

    public static List<Option$Flow> fromModels(List<Option> options) {
        List<Option$Flow> optionFlows = new ArrayList<>();

        if (options != null && !options.isEmpty()) {
            for (Option option : options) {
                optionFlows.add(fromModel(option));
            }
        }

        return optionFlows;
    }
}

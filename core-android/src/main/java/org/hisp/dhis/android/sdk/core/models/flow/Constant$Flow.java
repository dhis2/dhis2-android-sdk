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

package org.hisp.dhis.android.sdk.core.models.flow;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.Table;

import org.hisp.dhis.android.sdk.core.models.common.meta.DbDhis;
import org.hisp.dhis.android.sdk.models.constant.Constant;

import java.util.ArrayList;
import java.util.List;

@Table(databaseName = DbDhis.NAME)
public final class Constant$Flow extends BaseIdentifiableObject$Flow {

    @Column
    double value;

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public Constant$Flow() {
        // empty constructor
    }

    public static Constant toModel(Constant$Flow constantFlow) {
        if (constantFlow == null) {
            return null;
        }

        Constant constant = new Constant();
        constant.setId(constantFlow.getId());
        constant.setUId(constantFlow.getUId());
        constant.setCreated(constantFlow.getCreated());
        constant.setLastUpdated(constantFlow.getLastUpdated());
        constant.setName(constantFlow.getName());
        constant.setDisplayName(constantFlow.getDisplayName());
        constant.setAccess(constantFlow.getAccess());
        constant.setValue(constantFlow.getValue());
        return constant;
    }

    public static Constant$Flow fromModel(Constant constant) {
        if (constant == null) {
            return null;
        }

        Constant$Flow constantFlow = new Constant$Flow();
        constantFlow.setId(constant.getId());
        constantFlow.setUId(constant.getUId());
        constantFlow.setCreated(constant.getCreated());
        constantFlow.setLastUpdated(constant.getLastUpdated());
        constantFlow.setName(constant.getName());
        constantFlow.setDisplayName(constant.getDisplayName());
        constantFlow.setAccess(constant.getAccess());
        constantFlow.setValue(constant.getValue());
        return constantFlow;
    }

    public static List<Constant> toModels(List<Constant$Flow> constantFlows) {
        List<Constant> constants = new ArrayList<>();

        if (constantFlows != null && !constantFlows.isEmpty()) {
            for (Constant$Flow constantFlow : constantFlows) {
                constants.add(toModel(constantFlow));
            }
        }

        return constants;
    }

    public static List<Constant$Flow> fromModels(List<Constant> constants) {
        List<Constant$Flow> constantFlows = new ArrayList<>();

        if (constants != null && !constants.isEmpty()) {
            for (Constant constant : constants) {
                constantFlows.add(fromModel(constant));
            }
        }

        return constantFlows;
    }
}

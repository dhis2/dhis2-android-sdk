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

package org.hisp.dhis.android.sdk.constant;

import org.hisp.dhis.android.sdk.common.base.AbsMapper;
import org.hisp.dhis.android.sdk.flow.Constant$Flow;
import org.hisp.dhis.java.sdk.models.constant.Constant;

public class ConstantMapper extends AbsMapper<Constant, Constant$Flow> {

    public ConstantMapper() {
        // empty constructor
    }

    @Override
    public Constant$Flow mapToDatabaseEntity(Constant constant) {
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

    @Override
    public Constant mapToModel(Constant$Flow constantFlow) {
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

    @Override
    public Class<Constant> getModelTypeClass() {
        return Constant.class;
    }

    @Override
    public Class<Constant$Flow> getDatabaseEntityTypeClass() {
        return Constant$Flow.class;
    }
}

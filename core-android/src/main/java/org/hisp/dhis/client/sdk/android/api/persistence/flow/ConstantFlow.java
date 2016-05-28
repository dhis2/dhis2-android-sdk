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

package org.hisp.dhis.client.sdk.android.api.persistence.flow;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.Table;

import org.hisp.dhis.client.sdk.android.api.persistence.DbDhis;
import org.hisp.dhis.client.sdk.android.common.AbsMapper;
import org.hisp.dhis.client.sdk.android.common.Mapper;
import org.hisp.dhis.client.sdk.models.constant.Constant;

@Table(database = DbDhis.class)
public final class ConstantFlow extends BaseIdentifiableObjectFlow {
    public static final Mapper<Constant, ConstantFlow> MAPPER = new ConstantMapper();

    @Column
    double value;

    public ConstantFlow() {
        // empty constructor
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    private static class ConstantMapper extends AbsMapper<Constant, ConstantFlow> {

        public ConstantMapper() {
            // empty constructor
        }

        @Override
        public ConstantFlow mapToDatabaseEntity(Constant constant) {
            if (constant == null) {
                return null;
            }

            ConstantFlow constantFlow = new ConstantFlow();
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
        public Constant mapToModel(ConstantFlow constantFlow) {
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
        public Class<ConstantFlow> getDatabaseEntityTypeClass() {
            return ConstantFlow.class;
        }
    }
}

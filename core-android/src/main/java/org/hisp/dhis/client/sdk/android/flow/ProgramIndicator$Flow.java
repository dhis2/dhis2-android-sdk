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

package org.hisp.dhis.client.sdk.android.flow;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.Table;

import org.hisp.dhis.client.sdk.android.common.meta.DbDhis;

@Table(databaseName = DbDhis.NAME)
public final class ProgramIndicator$Flow extends BaseIdentifiableObject$Flow {

    @Column
    String code;

    @Column
    String expression;

    @Column
    String displayDescription;

    @Column
    String rootDate;

    @Column
    boolean externalAccess;

    @Column
    String valueType;

    @Column
    String displayShortName;

    @Column
    String program;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public String getDisplayDescription() {
        return displayDescription;
    }

    public void setDisplayDescription(String displayDescription) {
        this.displayDescription = displayDescription;
    }

    public String getRootDate() {
        return rootDate;
    }

    public void setRootDate(String rootDate) {
        this.rootDate = rootDate;
    }

    public boolean isExternalAccess() {
        return externalAccess;
    }

    public void setExternalAccess(boolean externalAccess) {
        this.externalAccess = externalAccess;
    }

    public String getValueType() {
        return valueType;
    }

    public void setValueType(String valueType) {
        this.valueType = valueType;
    }

    public String getDisplayShortName() {
        return displayShortName;
    }

    public void setDisplayShortName(String displayShortName) {
        this.displayShortName = displayShortName;
    }

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    public ProgramIndicator$Flow() {
        // empty constructor
    }

    /*public static ProgramIndicator toModel(ProgramIndicator$Flow programIndicatorFlow) {
        if (programIndicatorFlow == null) {
            return null;
        }

        ProgramIndicator programIndicator = new ProgramIndicator();
        programIndicator.setId(programIndicatorFlow.getId());
        programIndicator.setUId(programIndicatorFlow.getUId());
        programIndicator.setCreated(programIndicatorFlow.getCreated());
        programIndicator.setLastUpdated(programIndicatorFlow.getLastUpdated());
        programIndicator.setName(programIndicatorFlow.getName());
        programIndicator.setDisplayName(programIndicatorFlow.getDisplayName());
        programIndicator.setAccess(programIndicatorFlow.getAccess());
        programIndicator.setCode(programIndicatorFlow.getCode());
        programIndicator.setExpression(programIndicatorFlow.getExpression());
        programIndicator.setDisplayDescription(programIndicatorFlow.getDisplayDescription());
        programIndicator.setRootDate(programIndicatorFlow.getRootDate());
        programIndicator.setExternalAccess(programIndicatorFlow.isExternalAccess());
        programIndicator.setValueType(programIndicatorFlow.getValueType());
        programIndicator.setDisplayShortName(programIndicatorFlow.getDisplayShortName());
        programIndicator.setProgram(programIndicatorFlow.getProgram());
        return programIndicator;
    }

    public static ProgramIndicator$Flow fromModel(ProgramIndicator programIndicator) {
        if (programIndicator == null) {
            return null;
        }

        ProgramIndicator$Flow programIndicatorFlow = new ProgramIndicator$Flow();
        programIndicatorFlow.setId(programIndicator.getId());
        programIndicatorFlow.setUId(programIndicator.getUId());
        programIndicatorFlow.setCreated(programIndicator.getCreated());
        programIndicatorFlow.setLastUpdated(programIndicator.getLastUpdated());
        programIndicatorFlow.setName(programIndicator.getName());
        programIndicatorFlow.setDisplayName(programIndicator.getDisplayName());
        programIndicatorFlow.setAccess(programIndicator.getAccess());
        programIndicatorFlow.setCode(programIndicator.getCode());
        programIndicatorFlow.setExpression(programIndicator.getExpression());
        programIndicatorFlow.setDisplayDescription(programIndicator.getDisplayDescription());
        programIndicatorFlow.setRootDate(programIndicator.getRootDate());
        programIndicatorFlow.setExternalAccess(programIndicator.isExternalAccess());
        programIndicatorFlow.setValueType(programIndicator.getValueType());
        programIndicatorFlow.setDisplayShortName(programIndicator.getDisplayShortName());
        programIndicatorFlow.setProgram(programIndicator.getProgram());
        return programIndicatorFlow;
    }

    public static List<ProgramIndicator> toModels(List<ProgramIndicator$Flow> programIndicatorFlows) {
        List<ProgramIndicator> programIndicators = new ArrayList<>();

        if (programIndicatorFlows != null && !programIndicatorFlows.isEmpty()) {
            for (ProgramIndicator$Flow programIndicatorFlow : programIndicatorFlows) {
                programIndicators.add(toModel(programIndicatorFlow));
            }
        }

        return programIndicators;
    }

    public static List<ProgramIndicator$Flow> fromModels(List<ProgramIndicator> programIndicators) {
        List<ProgramIndicator$Flow> programIndicatorFlows = new ArrayList<>();

        if (programIndicators != null && !programIndicators.isEmpty()) {
            for (ProgramIndicator programIndicator : programIndicators) {
                programIndicatorFlows.add(fromModel(programIndicator));
            }
        }

        return programIndicatorFlows;
    }*/
}

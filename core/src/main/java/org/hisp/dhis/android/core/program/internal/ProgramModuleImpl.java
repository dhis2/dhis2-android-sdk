/*
 *  Copyright (c) 2004-2022, University of Oslo
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *  Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *  Neither the name of the HISP project nor the names of its contributors may
 *  be used to endorse or promote products derived from this software without
 *  specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.core.program.internal;

import org.hisp.dhis.android.core.program.ProgramCollectionRepository;
import org.hisp.dhis.android.core.program.ProgramIndicatorCollectionRepository;
import org.hisp.dhis.android.core.program.ProgramModule;
import org.hisp.dhis.android.core.program.ProgramRuleActionCollectionRepository;
import org.hisp.dhis.android.core.program.ProgramRuleCollectionRepository;
import org.hisp.dhis.android.core.program.ProgramRuleVariableCollectionRepository;
import org.hisp.dhis.android.core.program.ProgramSectionCollectionRepository;
import org.hisp.dhis.android.core.program.ProgramStageCollectionRepository;
import org.hisp.dhis.android.core.program.ProgramStageDataElementCollectionRepository;
import org.hisp.dhis.android.core.program.ProgramStageSectionsCollectionRepository;
import org.hisp.dhis.android.core.program.ProgramTrackedEntityAttributeCollectionRepository;
import org.hisp.dhis.android.core.program.programindicatorengine.ProgramIndicatorEngine;

import javax.inject.Inject;

import dagger.Reusable;

@Reusable
public final class ProgramModuleImpl implements ProgramModule {

    private final ProgramCollectionRepository programs;
    private final ProgramIndicatorCollectionRepository programIndicators;
    private final ProgramRuleCollectionRepository programRules;
    private final ProgramRuleActionCollectionRepository programRuleActions;
    private final ProgramRuleVariableCollectionRepository programRuleVariables;
    private final ProgramSectionCollectionRepository programSections;
    private final ProgramStageCollectionRepository programStages;
    private final ProgramStageSectionsCollectionRepository programStageSections;
    private final ProgramStageDataElementCollectionRepository programStageDataElements;
    private final ProgramTrackedEntityAttributeCollectionRepository programTrackedEntityAttributes;

    private final ProgramIndicatorEngine programIndicatorEngine;

    @Inject
    ProgramModuleImpl(ProgramCollectionRepository programs,
                      ProgramIndicatorCollectionRepository programIndicators,
                      ProgramRuleCollectionRepository programRules,
                      ProgramRuleActionCollectionRepository programRuleActions,
                      ProgramRuleVariableCollectionRepository programRuleVariables,
                      ProgramSectionCollectionRepository programSections,
                      ProgramStageCollectionRepository programStages,
                      ProgramStageSectionsCollectionRepository programStageSections,
                      ProgramStageDataElementCollectionRepository programStageDataElements,
                      ProgramTrackedEntityAttributeCollectionRepository programTrackedEntityAttributes,
                      ProgramIndicatorEngine programIndicatorEngine) {
        this.programs = programs;
        this.programIndicators = programIndicators;
        this.programRules = programRules;
        this.programRuleActions = programRuleActions;
        this.programRuleVariables = programRuleVariables;
        this.programSections = programSections;
        this.programStages = programStages;
        this.programStageSections = programStageSections;
        this.programStageDataElements = programStageDataElements;
        this.programTrackedEntityAttributes = programTrackedEntityAttributes;
        this.programIndicatorEngine = programIndicatorEngine;
    }

    @Override
    public ProgramCollectionRepository programs() {
        return programs;
    }

    @Override
    public ProgramIndicatorCollectionRepository programIndicators() {
        return programIndicators;
    }

    @Override
    public ProgramRuleCollectionRepository programRules() {
        return programRules;
    }

    @Override
    public ProgramRuleActionCollectionRepository programRuleActions() {
        return programRuleActions;
    }

    @Override
    public ProgramRuleVariableCollectionRepository programRuleVariables() {
        return programRuleVariables;
    }

    @Override
    public ProgramSectionCollectionRepository programSections() {
        return programSections;
    }

    @Override
    public ProgramStageCollectionRepository programStages() {
        return programStages;
    }

    @Override
    public ProgramStageSectionsCollectionRepository programStageSections() {
        return programStageSections;
    }

    @Override
    public ProgramStageDataElementCollectionRepository programStageDataElements() {
        return programStageDataElements;
    }

    @Override
    public ProgramTrackedEntityAttributeCollectionRepository programTrackedEntityAttributes() {
        return programTrackedEntityAttributes;
    }

    @Override
    public ProgramIndicatorEngine programIndicatorEngine() {
        return programIndicatorEngine;
    }
}

/*
 * Copyright (c) 2004-2019, University of Oslo
 * All rights reserved.
 *
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

package org.hisp.dhis.android.core.maintenance;

import org.hisp.dhis.android.core.common.IdentifiableObjectStore;
import org.hisp.dhis.android.core.common.UidsHelper;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitStore;
import org.hisp.dhis.android.core.program.Program;
import org.hisp.dhis.android.core.program.ProgramRule;
import org.hisp.dhis.android.core.program.ProgramRuleStore;
import org.hisp.dhis.android.core.program.ProgramStore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PerformanceHintsService {

    private final IdentifiableObjectStore<OrganisationUnit> organisationUnitStore;
    private final IdentifiableObjectStore<Program> programStore;
    private final IdentifiableObjectStore<ProgramRule> programRuleStore;

    private final int organisationUnitThreshold;
    private final int programRulesPerProgramThreshold;

    PerformanceHintsService(IdentifiableObjectStore<OrganisationUnit> organisationUnitStore,
                            IdentifiableObjectStore<Program> programStore,
                            IdentifiableObjectStore<ProgramRule> programRuleStore,
                            int organisationUnitThreshold,
                            int programRulesPerProgramThreshold) {

        this.organisationUnitStore = organisationUnitStore;
        this.programStore = programStore;
        this.programRuleStore = programRuleStore;
        this.organisationUnitThreshold = organisationUnitThreshold;
        this.programRulesPerProgramThreshold = programRulesPerProgramThreshold;
    }

    public boolean areThereExcessiveOrganisationUnits() {
        return this.organisationUnitStore.count() > organisationUnitThreshold;
    }

    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    public List<Program> getProgramsWithExcessiveProgramRules() {
        // TODO use Program repository when it's ready to retrieve directly program with program rules
        List<Program> programs = programStore.selectAll();
        List<ProgramRule> programRules = programRuleStore.selectAll();

        List<Program> programsWithEmptyProgramRules = new ArrayList<>(programs.size());
        for (Program program : programs) {
            programsWithEmptyProgramRules.add(program.toBuilder().programRules(new ArrayList<>()).build());
        }

        Map<String, Program> programsMap = UidsHelper.mapByUid(programsWithEmptyProgramRules);

        for (ProgramRule rule : programRules) {
            Program program = programsMap.get(rule.program().uid());
            if (program != null) {
                program.programRules().add(rule);
            }
        }

        List<Program> programsWithExcessiveProgramRules = new ArrayList<>();
        for (Program program : programsWithEmptyProgramRules) {
            if (program.programRules().size() > programRulesPerProgramThreshold) {
                programsWithExcessiveProgramRules.add(program);
            }
        }

        return programsWithExcessiveProgramRules;
    }

    public boolean areThereProgramsWithExcessiveProgramRules() {
        return !getProgramsWithExcessiveProgramRules().isEmpty();
    }

    public boolean areThereVulnerabilities() {
        return this.areThereExcessiveOrganisationUnits() || areThereProgramsWithExcessiveProgramRules();
    }

    static PerformanceHintsService create(DatabaseAdapter databaseAdapter,
                                          int organisationUnitThreshold,
                                          int programRulesPerProgramThreshold) {
        return new PerformanceHintsService(
                OrganisationUnitStore.create(databaseAdapter),
                ProgramStore.create(databaseAdapter),
                ProgramRuleStore.create(databaseAdapter),
                organisationUnitThreshold,
                programRulesPerProgramThreshold);
    }
}
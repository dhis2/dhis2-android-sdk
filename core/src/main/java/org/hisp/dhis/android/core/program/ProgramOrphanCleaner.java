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
package org.hisp.dhis.android.core.program;

import org.hisp.dhis.android.core.common.ObjectWithUid;
import org.hisp.dhis.android.core.common.OrphanCleaner;
import org.hisp.dhis.android.core.common.OrphanCleanerImpl;
import org.hisp.dhis.android.core.common.ParentOrphanCleaner;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;

final class ProgramOrphanCleaner implements ParentOrphanCleaner<Program> {

    private final OrphanCleaner<Program, ProgramRuleVariable> programRuleVariableCleaner;
    private final OrphanCleaner<Program, ProgramIndicator> programIndicatorCleaner;
    private final OrphanCleaner<Program, ProgramRule> programRuleCleaner;
    private final OrphanCleaner<Program, ProgramTrackedEntityAttribute> programTrackedEntityAttributeCleaner;
    private final OrphanCleaner<Program, ProgramSection> programSectionCleaner;
    private final OrphanCleaner<Program, ObjectWithUid> programStageCleaner;

    private ProgramOrphanCleaner(
            OrphanCleaner<Program, ProgramRuleVariable> programRuleVariableCleaner,
            OrphanCleaner<Program, ProgramIndicator> programIndicatorCleaner,
            OrphanCleaner<Program, ProgramRule> programRuleCleaner,
            OrphanCleaner<Program, ProgramTrackedEntityAttribute>
                    programTrackedEntityAttributeCleaner,
            OrphanCleaner<Program, ProgramSection> programSectionCleaner,
            OrphanCleaner<Program, ObjectWithUid> programStageCleaner) {
        this.programRuleVariableCleaner = programRuleVariableCleaner;
        this.programIndicatorCleaner = programIndicatorCleaner;
        this.programRuleCleaner = programRuleCleaner;
        this.programTrackedEntityAttributeCleaner = programTrackedEntityAttributeCleaner;
        this.programSectionCleaner = programSectionCleaner;
        this.programStageCleaner = programStageCleaner;
    }

    @Override
    public void deleteOrphan(Program program) {
        programRuleVariableCleaner.deleteOrphan(program, program.programRuleVariables());
        programIndicatorCleaner.deleteOrphan(program, program.programIndicators());
        programRuleCleaner.deleteOrphan(program, program.programRules());
        programTrackedEntityAttributeCleaner.deleteOrphan(program, program.programTrackedEntityAttributes());
        programSectionCleaner.deleteOrphan(program, program.programSections());
        programStageCleaner.deleteOrphan(program, program.programStages());
    }

    public static ProgramOrphanCleaner create(DatabaseAdapter databaseAdapter) {
        return new ProgramOrphanCleaner(
                new OrphanCleanerImpl<Program, ProgramRuleVariable>(ProgramRuleVariableModel.TABLE,
                        ProgramRuleVariableModel.Columns.PROGRAM, databaseAdapter),
                new OrphanCleanerImpl<Program, ProgramIndicator>(ProgramIndicatorTableInfo.TABLE_INFO.name(),
                        ProgramIndicatorFields.PROGRAM, databaseAdapter),
                new OrphanCleanerImpl<Program, ProgramRule>(ProgramRuleModel.TABLE,
                        ProgramRuleModel.Columns.PROGRAM, databaseAdapter),
                new OrphanCleanerImpl<Program, ProgramTrackedEntityAttribute>(ProgramTrackedEntityAttributeTableInfo.
                        TABLE_INFO.name(), ProgramTrackedEntityAttributeFields.PROGRAM, databaseAdapter),
                new OrphanCleanerImpl<Program, ProgramSection>(ProgramSectionModel.TABLE,
                        ProgramSectionModel.Columns.PROGRAM, databaseAdapter),
                new OrphanCleanerImpl<Program, ObjectWithUid>(ProgramStageModel.TABLE,
                        ProgramStageModel.Columns.PROGRAM, databaseAdapter)
        );
    }
}
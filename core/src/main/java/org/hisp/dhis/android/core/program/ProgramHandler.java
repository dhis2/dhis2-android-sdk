/*
 * Copyright (c) 2017, University of Oslo
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
package org.hisp.dhis.android.core.program;

import org.hisp.dhis.android.core.common.GenericHandler;
import org.hisp.dhis.android.core.common.ObjectStyle;
import org.hisp.dhis.android.core.common.ObjectStyleHandler;
import org.hisp.dhis.android.core.common.ObjectStyleModel;
import org.hisp.dhis.android.core.common.ObjectStyleModelBuilder;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.relationship.RelationshipTypeHandler;

import static org.hisp.dhis.android.core.utils.Utils.isDeleted;

public class ProgramHandler {

    private final ProgramStore programStore;

    private final ProgramRuleVariableHandler programRuleVariableHandler;
    private final ProgramIndicatorHandler programIndicatorHandler;
    private final ProgramRuleHandler programRuleHandler;
    private final ProgramTrackedEntityAttributeHandler programTrackedEntityAttributeHandler;
    private final RelationshipTypeHandler relationshipHandler;
    private final GenericHandler<ObjectStyle, ObjectStyleModel> styleHandler;

    ProgramHandler(ProgramStore programStore,
                          ProgramRuleVariableHandler programRuleVariableHandler,
                          ProgramIndicatorHandler programIndicatorHandler,
                          ProgramRuleHandler programRuleHandler,
                          ProgramTrackedEntityAttributeHandler programTrackedEntityAttributeHandler,
                          RelationshipTypeHandler relationshipHandler,
                          GenericHandler<ObjectStyle, ObjectStyleModel> styleHandler) {
        this.programStore = programStore;
        this.programRuleVariableHandler = programRuleVariableHandler;
        this.programIndicatorHandler = programIndicatorHandler;
        this.programRuleHandler = programRuleHandler;
        this.programTrackedEntityAttributeHandler = programTrackedEntityAttributeHandler;
        this.relationshipHandler = relationshipHandler;
        this.styleHandler = styleHandler;
    }

    public void handleProgram(Program program) {
        if (program == null) {
            return;
        }
        if (isDeleted(program)) {
            programStore.delete(program.uid());
        } else {
            String relatedProgramUid = null;
            if (program.relatedProgram() != null) {
                relatedProgramUid = program.relatedProgram().uid();
            }
            String trackedEntityUid = null;
            if (program.trackedEntityType() != null) {
                trackedEntityUid = program.trackedEntityType().uid();
            }
            String categoryCombo = null;
            if (program.categoryCombo() != null) {
                categoryCombo = program.categoryCombo().uid();
            }
            String relationshipTypeUid = null;
            if (program.relationshipType() != null) {
                relationshipTypeUid = program.relationshipType().uid();
            }

            int updatedRow = programStore.update(
                    program.uid(), program.code(), program.name(), program.displayName(), program.created(),
                    program.lastUpdated(), program.shortName(), program.displayShortName(), program.description(),
                    program.displayDescription(), program.version(), program.onlyEnrollOnce(),
                    program.enrollmentDateLabel(), program.displayIncidentDate(),
                    program.incidentDateLabel(), program.registration(), program.selectEnrollmentDatesInFuture(),
                    program.dataEntryMethod(), program.ignoreOverdueEvents(), program.relationshipFromA(),
                    program.selectIncidentDatesInFuture(), program.captureCoordinates(),
                    program.useFirstStageDuringRegistration(), program.displayFrontPageList(),
                    program.programType(), relationshipTypeUid, program.relationshipText(),
                    relatedProgramUid, trackedEntityUid, categoryCombo,
                    program.access().data().write(), program.uid());

            if (updatedRow <= 0) {
                programStore.insert(
                        program.uid(), program.code(), program.name(), program.displayName(), program.created(),
                        program.lastUpdated(), program.shortName(), program.displayShortName(), program.description(),
                        program.displayDescription(), program.version(), program.onlyEnrollOnce(),
                        program.enrollmentDateLabel(), program.displayIncidentDate(),
                        program.incidentDateLabel(), program.registration(), program.selectEnrollmentDatesInFuture(),
                        program.dataEntryMethod(), program.ignoreOverdueEvents(), program.relationshipFromA(),
                        program.selectIncidentDatesInFuture(), program.captureCoordinates(),
                        program.useFirstStageDuringRegistration(), program.displayFrontPageList(),
                        program.programType(), relationshipTypeUid, program.relationshipText(),
                        relatedProgramUid, trackedEntityUid, categoryCombo, program.access().data().write());
            }
        }
        // programStageHandler will invoke programStageSectionHandler, programStageDataElementHandler,
        // programIndicatorHandler, dataElement handler and optionSetHandler
        programTrackedEntityAttributeHandler.handleProgramTrackedEntityAttributes(
                program.programTrackedEntityAttributes());
        programIndicatorHandler.handleProgramIndicator(null, program.programIndicators());
        programRuleHandler.handleProgramRules(program.programRules());
        programRuleVariableHandler.handleProgramRuleVariables(program.programRuleVariables());
        relationshipHandler.handleRelationshipType(program.relationshipType());
        styleHandler.handle(program.style(), new ObjectStyleModelBuilder(program.uid(), ProgramModel.TABLE));
    }

    public static ProgramHandler create(DatabaseAdapter databaseAdapter) {
        return new ProgramHandler(
                new ProgramStoreImpl(databaseAdapter),
                ProgramRuleVariableHandler.create(databaseAdapter),
                ProgramIndicatorHandler.create(databaseAdapter),
                ProgramRuleHandler.create(databaseAdapter),
                ProgramTrackedEntityAttributeHandler.create(databaseAdapter),
                RelationshipTypeHandler.create(databaseAdapter),
                ObjectStyleHandler.create(databaseAdapter)
        );
    }
}

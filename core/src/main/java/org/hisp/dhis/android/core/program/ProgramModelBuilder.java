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

import org.hisp.dhis.android.core.common.ModelBuilder;

public class ProgramModelBuilder extends ModelBuilder<Program, ProgramModel> {

    @Override
    public ProgramModel buildModel(Program program) {
        return ProgramModel.builder()
                .uid(program.uid())
                .code(program.code())
                .name(program.name())
                .displayName(program.displayName())
                .created(program.created())
                .lastUpdated(program.lastUpdated())
                .shortName(program.shortName())
                .displayShortName(program.displayShortName())
                .description(program.description())
                .displayDescription(program.displayDescription())
                .version(program.version())
                .onlyEnrollOnce(program.onlyEnrollOnce())
                .enrollmentDateLabel(program.enrollmentDateLabel())
                .displayIncidentDate(program.displayIncidentDate())
                .incidentDateLabel(program.incidentDateLabel())
                .registration(program.registration())
                .selectEnrollmentDatesInFuture(program.selectEnrollmentDatesInFuture())
                .dataEntryMethod(program.dataEntryMethod())
                .ignoreOverdueEvents(program.ignoreOverdueEvents())
                .relationshipFromA(program.relationshipFromA())
                .selectIncidentDatesInFuture(program.selectIncidentDatesInFuture())
                .captureCoordinates(program.captureCoordinates())
                .useFirstStageDuringRegistration(program.useFirstStageDuringRegistration())
                .displayFrontPageList(program.displayFrontPageList())
                .programType(program.programType())
                .relationshipType(program.relationshipTypeUid())
                .relationshipText(program.relationshipText())
                .relatedProgram(program.relatedProgramUid())
                .trackedEntityType(program.trackedEntityTypeUid())
                .categoryCombo(program.categoryComboUid())
                .accessDataWrite(program.access().data().write())
                .expiryDays(program.expiryDays())
                .completeEventsExpiryDays(program.completeEventsExpiryDays())
                .expiryPeriodType(program.expiryPeriodType())
                .minAttributesRequiredToSearch(program.minAttributesRequiredToSearch())
                .maxTeiCountToReturn(program.maxTeiCountToReturn())
                .build();
    }
}

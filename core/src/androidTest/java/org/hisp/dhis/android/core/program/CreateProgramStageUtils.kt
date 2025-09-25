/*
 *  Copyright (c) 2004-2023, University of Oslo
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
package org.hisp.dhis.android.core.program

import org.hisp.dhis.android.core.common.FormType
import org.hisp.dhis.android.core.common.ObjectWithUid

object CreateProgramStageUtils {
    private const val CODE = "test_code"
    private const val NAME = "test_name"
    private const val DISPLAY_NAME = "test_display_name"

    private const val EXECUTION_DATE_LABEL = "test_executionDateLabel"
    private const val DUE_DATE_LABEL = "test_dueDateLabel"
    private const val ALLOW_GENERATE_NEXT_VISIT = false
    private const val VALID_COMPLETE_ONLY = false
    private const val REPORT_DATE_TO_USE = "test_reportDateToUse"
    private const val OPEN_AFTER_ENROLLMENT = false

    private const val REPEATABLE = false
    private val FORM_TYPE = FormType.DEFAULT
    private const val DISPLAY_GENERATE_EVENT_BOX = true
    private const val GENERATED_BY_ENROLMENT_DATE = true
    private const val AUTO_GENERATE_EVENT = false
    private const val SORT_ORDER = 0
    private const val HIDE_DUE_DATE = true
    private const val BLOCK_ENTRY_FORM = false
    private const val MIN_DAYS_FROM_START = 5
    private const val STANDARD_INTERVAL = 7

    // used for timestamps
    private const val DATE = "2017-01-05T15:39:00.000"

    fun create(uid: String?, programId: String?): ProgramStage {
        return ProgramStage.builder()
            .uid(uid)
            .code(CODE)
            .name(NAME)
            .displayName(DISPLAY_NAME)
            .created(DATE)
            .lastUpdated(DATE)
            .executionDateLabel(EXECUTION_DATE_LABEL)
            .dueDateLabel(DUE_DATE_LABEL)
            .allowGenerateNextVisit(ALLOW_GENERATE_NEXT_VISIT)
            .validCompleteOnly(VALID_COMPLETE_ONLY)
            .reportDateToUse(REPORT_DATE_TO_USE)
            .openAfterEnrollment(OPEN_AFTER_ENROLLMENT)
            .repeatable(REPEATABLE)
            .formType(FORM_TYPE)
            .displayGenerateEventBox(DISPLAY_GENERATE_EVENT_BOX)
            .generatedByEnrollmentDate(GENERATED_BY_ENROLMENT_DATE)
            .autoGenerateEvent(AUTO_GENERATE_EVENT)
            .sortOrder(SORT_ORDER)
            .hideDueDate(HIDE_DUE_DATE)
            .blockEntryForm(BLOCK_ENTRY_FORM)
            .minDaysFromStart(MIN_DAYS_FROM_START)
            .standardInterval(STANDARD_INTERVAL)
            .program(ObjectWithUid.create(programId))
            .build()
    }
}

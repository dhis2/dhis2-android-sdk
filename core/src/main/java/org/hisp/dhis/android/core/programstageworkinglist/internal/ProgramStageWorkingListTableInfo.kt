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
package org.hisp.dhis.android.core.programstageworkinglist.internal

import org.hisp.dhis.android.core.arch.db.tableinfos.TableInfo
import org.hisp.dhis.android.core.arch.helpers.CollectionsHelper
import org.hisp.dhis.android.core.common.CoreColumns
import org.hisp.dhis.android.core.common.IdentifiableColumns

internal object ProgramStageWorkingListTableInfo {
    val TABLE_INFO: TableInfo = object : TableInfo() {
        override fun name(): String {
            return "ProgramStageWorkingList"
        }

        override fun columns(): CoreColumns {
            return Columns()
        }
    }

    class Columns : IdentifiableColumns() {
        override fun all(): Array<String> {
            return CollectionsHelper.appendInNewArray(
                super.all(),
                DESCRIPTION,
                PROGRAM,
                PROGRAM_STAGE,
                EVENT_STATUS,
                EVENT_CREATED_AT,
                EVENT_OCCURRED_AT,
                EVENT_SCHEDULED_AT,
                ENROLLMENT_STATUS,
                ENROLLMENT_AT,
                ENROLLMENT_OCCURRED_AT,
                ORDER,
                DISPLAY_COLUMN_ORDER,
                ORG_UNIT,
                OU_MODE,
                ASSIGNED_USER_MODE
            )
        }

        companion object {
            const val DESCRIPTION = "description"
            const val PROGRAM = "program"
            const val PROGRAM_STAGE = "programStage"
            const val EVENT_STATUS = "eventStatus"
            const val EVENT_CREATED_AT = "eventCreatedAt"
            const val EVENT_OCCURRED_AT = "eventOccurredAt"
            const val EVENT_SCHEDULED_AT = "eventScheduledAt"
            const val ENROLLMENT_STATUS = "enrollmentStatus"
            const val ENROLLMENT_AT = "enrolledAt"
            const val ENROLLMENT_OCCURRED_AT = "enrollmentOccurredAt"
            const val ORDER = "orderProperty"
            const val DISPLAY_COLUMN_ORDER = "displayColumnOrder"
            const val ORG_UNIT = "orgUnit"
            const val OU_MODE = "ouMode"
            const val ASSIGNED_USER_MODE = "assignedUserMode"
        }
    }
}

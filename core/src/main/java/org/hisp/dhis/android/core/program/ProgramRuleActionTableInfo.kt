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

import org.hisp.dhis.android.core.arch.db.tableinfos.TableInfo
import org.hisp.dhis.android.core.arch.helpers.CollectionsHelper.appendInNewArray
import org.hisp.dhis.android.core.common.IdentifiableColumns

object ProgramRuleActionTableInfo {
    val TABLE_INFO: TableInfo = object : TableInfo() {
        public override fun name(): String {
            return "ProgramRuleAction"
        }

        public override fun columns(): Columns {
            return Columns()
        }
    }

    class Columns : IdentifiableColumns() {
        public override fun all(): Array<String> {
            return appendInNewArray(
                super.all(),
                DATA,
                CONTENT,
                LOCATION,
                TRACKED_ENTITY_ATTRIBUTE,
                PROGRAM_INDICATOR,
                PROGRAM_STAGE_SECTION,
                PROGRAM_RULE_ACTION_TYPE,
                PROGRAM_STAGE,
                DATA_ELEMENT,
                PROGRAM_RULE,
                OPTION,
                OPTION_GROUP,
                DISPLAYCONTENT,
            )
        }

        companion object {
            const val DATA: String = "data"
            const val CONTENT: String = "content"
            const val LOCATION: String = "location"
            const val TRACKED_ENTITY_ATTRIBUTE: String = "trackedEntityAttribute"
            const val PROGRAM_INDICATOR: String = "programIndicator"
            const val PROGRAM_STAGE_SECTION: String = "programStageSection"
            const val PROGRAM_RULE_ACTION_TYPE: String = "programRuleActionType"
            const val PROGRAM_RULE: String = "programRule"
            const val PROGRAM_STAGE: String = "programStage"
            const val DATA_ELEMENT: String = "dataElement"
            const val OPTION: String = "option"
            const val OPTION_GROUP: String = "optionGroup"
            const val DISPLAYCONTENT: String = "displayContent"
        }
    }
}

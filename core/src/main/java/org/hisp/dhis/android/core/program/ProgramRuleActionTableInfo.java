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

package org.hisp.dhis.android.core.program;

import org.hisp.dhis.android.core.arch.db.tableinfos.TableInfo;
import org.hisp.dhis.android.core.arch.helpers.CollectionsHelper;
import org.hisp.dhis.android.core.common.IdentifiableColumns;

public final class ProgramRuleActionTableInfo {

    private ProgramRuleActionTableInfo() {
    }

    public static final TableInfo TABLE_INFO = new TableInfo() {

        @Override
        public String name() {
            return "ProgramRuleAction";
        }

        @Override
        public Columns columns() {
            return new Columns();
        }
    };

    public static class Columns extends IdentifiableColumns {
        public static final String DATA = "data";
        public static final String CONTENT = "content";
        public static final String LOCATION = "location";
        public static final String TRACKED_ENTITY_ATTRIBUTE = "trackedEntityAttribute";
        public static final String PROGRAM_INDICATOR = "programIndicator";
        public static final String PROGRAM_STAGE_SECTION = "programStageSection";
        public static final String PROGRAM_RULE_ACTION_TYPE = "programRuleActionType";
        public static final String PROGRAM_RULE = "programRule";
        public static final String PROGRAM_STAGE = "programStage";
        public static final String DATA_ELEMENT = "dataElement";
        public static final String OPTION = "option";
        public static final String OPTION_GROUP = "optionGroup";

        @Override
        public String[] all() {
            return CollectionsHelper.appendInNewArray(super.all(),
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
                    OPTION_GROUP
            );
        }
    }
}
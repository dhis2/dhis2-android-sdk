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

package org.hisp.dhis.android.core.trackedentity;

import org.hisp.dhis.android.core.arch.db.tableinfos.TableInfo;
import org.hisp.dhis.android.core.arch.helpers.CollectionsHelper;
import org.hisp.dhis.android.core.common.NameableWithStyleColumns;

public final class TrackedEntityAttributeTableInfo {

    private TrackedEntityAttributeTableInfo() {
    }

    public static final TableInfo TABLE_INFO = new TableInfo() {

        @Override
        public String name() {
            return "TrackedEntityAttribute";
        }

        @Override
        public Columns columns() {
            return new Columns();
        }
    };

    public static class Columns extends NameableWithStyleColumns {
        public static final String UNIQUE = "uniqueProperty";
        public static final String PATTERN = "pattern";
        public static final String SORT_ORDER_IN_LIST_NO_PROGRAM = "sortOrderInListNoProgram";
        public static final String OPTION_SET = "optionSet";
        public static final String VALUE_TYPE = "valueType";
        public static final String EXPRESSION = "expression";
        public static final String PROGRAM_SCOPE = "programScope";
        public static final String AGGREGATION_TYPE = "aggregationType";
        public static final String DISPLAY_IN_LIST_NO_PROGRAM = "displayInListNoProgram";
        public static final String GENERATED = "generated";
        public static final String DISPLAY_ON_VISIT_SCHEDULE = "displayOnVisitSchedule";
        public static final String ORG_UNIT_SCOPE = "orgunitScope";
        public static final String INHERIT = "inherit";
        public static final String FIELD_MASK = "fieldMask";
        public static final String FORM_NAME = "formName";
        public static final String DISPLAY_FORM_NAME = "displayFormName";

        @Override
        public String[] all() {
            return CollectionsHelper.appendInNewArray(super.all(),
                    PATTERN,
                    SORT_ORDER_IN_LIST_NO_PROGRAM,
                    OPTION_SET,
                    VALUE_TYPE,
                    EXPRESSION,
                    PROGRAM_SCOPE,
                    DISPLAY_IN_LIST_NO_PROGRAM,
                    GENERATED,
                    DISPLAY_ON_VISIT_SCHEDULE,
                    ORG_UNIT_SCOPE,
                    UNIQUE,
                    INHERIT,
                    FORM_NAME,
                    DISPLAY_FORM_NAME,
                    FIELD_MASK,
                    AGGREGATION_TYPE
            );
        }
    }
}
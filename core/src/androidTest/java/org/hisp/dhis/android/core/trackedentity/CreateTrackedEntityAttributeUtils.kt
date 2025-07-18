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

package org.hisp.dhis.android.core.trackedentity;

import android.content.ContentValues;

import org.hisp.dhis.android.core.common.ValueType;
import org.hisp.dhis.android.persistence.trackedentity.TrackedEntityAttributeTableInfo.Columns;

public class CreateTrackedEntityAttributeUtils {

    /**
     * BaseIdentifiable properties
     */
    private static final String CODE = "test_code";
    private static final String NAME = "test_name";
    private static final String DISPLAY_NAME = "test_display_name";
    private static final String DATE = "2011-12-24T12:24:25.203";

    /**
     * BaseNameableProperties
     */
    private static final String SHORT_NAME = "test_short_name";
    private static final String DISPLAY_SHORT_NAME = "test_display_short_name";
    private static final String DESCRIPTION = "test_description";
    private static final String DISPLAY_DESCRIPTION = "test_display_description";

    /**
     * Properties bound to TrackedEntityAttribute
     */
    private static final String PATTERN = "test_pattern";
    private static final Integer SORT_ORDER_IN_LIST_NO_PROGRAM = 1;
    private static final ValueType VALUE_TYPE = ValueType.BOOLEAN;
    private static final String EXPRESSION = "test_expression";
    private static final Integer PROGRAM_SCOPE = 0; // false
    private static final Integer DISPLAY_IN_LIST_NO_PROGRAM = 1; // true
    private static final Integer GENERATED = 0; // false
    private static final Integer DISPLAY_ON_VISIT_SCHEDULE = 1; // true
    private static final Integer ORG_UNIT_SCOPE = 0; // false
    private static final Integer UNIQUE = 1; // true
    private static final Integer INHERIT = 0; // false
    private static final Integer CONFIDENTIAL = 0; // false

    public static ContentValues create(String uid, String optionSetUid) {

        ContentValues values = new ContentValues();

        values.put(Columns.UID, uid);
        values.put(Columns.CODE, CODE);
        values.put(Columns.NAME, NAME);
        values.put(Columns.DISPLAY_NAME, DISPLAY_NAME);
        values.put(Columns.CREATED, DATE);
        values.put(Columns.LAST_UPDATED, DATE);
        values.put(Columns.SHORT_NAME, SHORT_NAME);
        values.put(Columns.DISPLAY_SHORT_NAME, DISPLAY_SHORT_NAME);
        values.put(Columns.DESCRIPTION, DESCRIPTION);
        values.put(Columns.DISPLAY_DESCRIPTION, DISPLAY_DESCRIPTION);
        values.put(Columns.PATTERN, PATTERN);
        values.put(Columns.SORT_ORDER_IN_LIST_NO_PROGRAM, SORT_ORDER_IN_LIST_NO_PROGRAM);
        values.put(Columns.OPTION_SET, optionSetUid);
        values.put(Columns.VALUE_TYPE, VALUE_TYPE.name());
        values.put(Columns.EXPRESSION, EXPRESSION);
        values.put(Columns.PROGRAM_SCOPE, PROGRAM_SCOPE);
        values.put(Columns.DISPLAY_IN_LIST_NO_PROGRAM, DISPLAY_IN_LIST_NO_PROGRAM);
        values.put(Columns.GENERATED, GENERATED);
        values.put(Columns.DISPLAY_ON_VISIT_SCHEDULE, DISPLAY_ON_VISIT_SCHEDULE);
        values.put(Columns.CONFIDENTIAL, CONFIDENTIAL);
        values.put(Columns.ORG_UNIT_SCOPE, ORG_UNIT_SCOPE);
        values.put(Columns.UNIQUE, UNIQUE);
        values.put(Columns.INHERIT, INHERIT);

        return values;
    }

}

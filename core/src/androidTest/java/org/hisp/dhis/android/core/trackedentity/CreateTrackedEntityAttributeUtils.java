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

package org.hisp.dhis.android.core.trackedentity;

import android.content.ContentValues;

import org.hisp.dhis.android.core.common.BaseIdentifiableObjectModel;
import org.hisp.dhis.android.core.common.BaseModel;
import org.hisp.dhis.android.core.common.BaseNameableObjectModel;
import org.hisp.dhis.android.core.common.ValueType;

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
    private static final TrackedEntityAttributeSearchScope SEARCH_SCOPE = TrackedEntityAttributeSearchScope.SEARCH_ORG_UNITS;
    private static final Integer PROGRAM_SCOPE = 0; // false
    private static final Integer DISPLAY_IN_LIST_NO_PROGRAM = 1; // true
    private static final Integer GENERATED = 0; // false
    private static final Integer DISPLAY_ON_VISIT_SCHEDULE = 1; // true
    private static final Integer ORG_UNIT_SCOPE = 0; // false
    private static final Integer UNIQUE = 1; // true
    private static final Integer INHERIT = 0; // false

    public static ContentValues create(long id, String uid, String optionSetUid) {

        ContentValues values = new ContentValues();

        values.put(BaseModel.Columns.ID, id);
        values.put(BaseIdentifiableObjectModel.Columns.UID, uid);
        values.put(BaseIdentifiableObjectModel.Columns.CODE, CODE);
        values.put(BaseIdentifiableObjectModel.Columns.NAME, NAME);
        values.put(BaseIdentifiableObjectModel.Columns.DISPLAY_NAME, DISPLAY_NAME);
        values.put(BaseIdentifiableObjectModel.Columns.CREATED, DATE);
        values.put(BaseIdentifiableObjectModel.Columns.LAST_UPDATED, DATE);
        values.put(BaseNameableObjectModel.Columns.SHORT_NAME, SHORT_NAME);
        values.put(BaseNameableObjectModel.Columns.DISPLAY_SHORT_NAME, DISPLAY_SHORT_NAME);
        values.put(BaseNameableObjectModel.Columns.DESCRIPTION, DESCRIPTION);
        values.put(BaseNameableObjectModel.Columns.DISPLAY_DESCRIPTION, DISPLAY_DESCRIPTION);
        values.put(TrackedEntityAttributeFields.PATTERN, PATTERN);
        values.put(TrackedEntityAttributeFields.SORT_ORDER_IN_LIST_NO_PROGRAM, SORT_ORDER_IN_LIST_NO_PROGRAM);
        values.put(TrackedEntityAttributeFields.OPTION_SET, optionSetUid);
        values.put(TrackedEntityAttributeFields.VALUE_TYPE, VALUE_TYPE.name());
        values.put(TrackedEntityAttributeFields.EXPRESSION, EXPRESSION);
        values.put(TrackedEntityAttributeFields.SEARCH_SCOPE, SEARCH_SCOPE.name());
        values.put(TrackedEntityAttributeFields.PROGRAM_SCOPE, PROGRAM_SCOPE);
        values.put(TrackedEntityAttributeFields.DISPLAY_IN_LIST_NO_PROGRAM, DISPLAY_IN_LIST_NO_PROGRAM);
        values.put(TrackedEntityAttributeFields.GENERATED, GENERATED);
        values.put(TrackedEntityAttributeFields.DISPLAY_ON_VISIT_SCHEDULE, DISPLAY_ON_VISIT_SCHEDULE);
        values.put(TrackedEntityAttributeFields.ORG_UNIT_SCOPE, ORG_UNIT_SCOPE);
        values.put(TrackedEntityAttributeTableInfo.Columns.UNIQUE, UNIQUE);
        values.put(TrackedEntityAttributeFields.INHERIT, INHERIT);

        return values;
    }

}

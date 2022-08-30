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

package org.hisp.dhis.android.core.attribute.internal;

import org.hisp.dhis.android.core.arch.api.fields.internal.Fields;
import org.hisp.dhis.android.core.arch.fields.internal.FieldsHelper;
import org.hisp.dhis.android.core.attribute.Attribute;
import org.hisp.dhis.android.core.attribute.AttributeTableInfo.Columns;
import org.hisp.dhis.android.core.common.ValueType;

public final class AttributeFields {
    public static final String UNIQUE = "unique";

    private static final FieldsHelper<Attribute> fh = new FieldsHelper<>();

    public static final Fields<Attribute> allFields = Fields.<Attribute>builder()
            .fields(fh.getNameableFields())
            .fields(
                    fh.<ValueType>field(Columns.VALUE_TYPE),
                    fh.<String>field(UNIQUE),
                    fh.<String>field(Columns.MANDATORY),
                    fh.<Boolean>field(Columns.INDICATOR_ATTRIBUTE),
                    fh.<Boolean>field(Columns.INDICATOR_GROUP_ATTRIBUTE),
                    fh.<Boolean>field(Columns.USER_GROUP_ATTRIBUTE),
                    fh.<Boolean>field(Columns.DATA_ELEMENT_ATTRIBUTE),
                    fh.<Boolean>field(Columns.CONSTANT_ATTRIBUTE),
                    fh.<Boolean>field(Columns.CATEGORY_OPTION_ATTRIBUTE),
                    fh.<Boolean>field(Columns.OPTION_SET_ATTRIBUTE),
                    fh.<Boolean>field(Columns.SQL_VIEW_ATTRIBUTE),
                    fh.<Boolean>field(Columns.LEGEND_SET_ATTRIBUTE),
                    fh.<Boolean>field(Columns.TRACKED_ENTITY_ATTRIBUTE_ATTRIBUTE),
                    fh.<Boolean>field(Columns.ORGANISATION_UNIT_ATTRIBUTE),
                    fh.<Boolean>field(Columns.DATA_SET_ATTRIBUTE),
                    fh.<Boolean>field(Columns.DOCUMENT_ATTRIBUTE),
                    fh.<Boolean>field(Columns.VALIDATION_RULE_GROUP_ATTRIBUTE),
                    fh.<Boolean>field(Columns.DATA_ELEMENT_GROUP_ATTRIBUTE),
                    fh.<Boolean>field(Columns.SECTION_ATTRIBUTE),
                    fh.<Boolean>field(Columns.TRACKED_ENTITY_TYPE_ATTRIBUTE),
                    fh.<Boolean>field(Columns.USER_ATTRIBUTE),
                    fh.<Boolean>field(Columns.CATEGORY_OPTION_GROUP_ATTRIBUTE),
                    fh.<Boolean>field(Columns.PROGRAM_STAGE_ATTRIBUTE),
                    fh.<Boolean>field(Columns.PROGRAM_ATTRIBUTE),
                    fh.<Boolean>field(Columns.CATEGORY_ATTRIBUTE),
                    fh.<Boolean>field(Columns.CATEGORY_OPTION_COMBO_ATTRIBUTE),
                    fh.<Boolean>field(Columns.CATEGORY_OPTION_GROUP_SET_ATTRIBUTE),
                    fh.<Boolean>field(Columns.VALIDATION_RULE_ATTRIBUTE),
                    fh.<Boolean>field(Columns.PROGRAM_INDICATOR_ATTRIBUTE),
                    fh.<Boolean>field(Columns.ORGANISATION_UNIT_GROUP_ATTRIBUTE),
                    fh.<Boolean>field(Columns.DATA_ELEMENT_GROUP_SET_ATTRIBUTE),
                    fh.<Boolean>field(Columns.ORGANISATION_UNIT_GROUP_SET_ATTRIBUTE),
                    fh.<Boolean>field(Columns.OPTION_ATTRIBUTE)
            ).build();

    private AttributeFields() {
    }
}
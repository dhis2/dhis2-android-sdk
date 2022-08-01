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

package org.hisp.dhis.android.core.attribute;

import org.hisp.dhis.android.core.arch.db.tableinfos.TableInfo;
import org.hisp.dhis.android.core.arch.helpers.CollectionsHelper;
import org.hisp.dhis.android.core.common.NameableColumns;

public final class AttributeTableInfo {

    private AttributeTableInfo() {
    }

    public static final TableInfo TABLE_INFO = new TableInfo() {

        @Override
        public String name() {
            return "Attribute";
        }

        @Override
        public Columns columns() {
            return new Columns();
        }
    };

    public static class Columns extends NameableColumns {
        public static final String VALUE_TYPE = "valueType";
        public static final String UNIQUE = "uniqueProperty";
        public static final String MANDATORY = "mandatory";
        public static final String INDICATOR_ATTRIBUTE = "indicatorAttribute";
        public static final String INDICATOR_GROUP_ATTRIBUTE = "indicatorGroupAttribute";
        public static final String USER_GROUP_ATTRIBUTE = "userGroupAttribute";
        public static final String DATA_ELEMENT_ATTRIBUTE = "dataElementAttribute";
        public static final String CONSTANT_ATTRIBUTE = "constantAttribute";
        public static final String CATEGORY_OPTION_ATTRIBUTE = "categoryOptionAttribute";
        public static final String OPTION_SET_ATTRIBUTE = "optionSetAttribute";
        public static final String SQL_VIEW_ATTRIBUTE = "sqlViewAttribute";
        public static final String LEGEND_SET_ATTRIBUTE = "legendSetAttribute";
        public static final String TRACKED_ENTITY_ATTRIBUTE_ATTRIBUTE =
                "trackedEntityAttributeAttribute";
        public static final String ORGANISATION_UNIT_ATTRIBUTE = "organisationUnitAttribute";
        public static final String DATA_SET_ATTRIBUTE = "dataSetAttribute";
        public static final String DOCUMENT_ATTRIBUTE = "documentAttribute";
        public static final String VALIDATION_RULE_GROUP_ATTRIBUTE = "validationRuleGroupAttribute";
        public static final String DATA_ELEMENT_GROUP_ATTRIBUTE = "dataElementGroupAttribute";
        public static final String SECTION_ATTRIBUTE = "sectionAttribute";
        public static final String TRACKED_ENTITY_TYPE_ATTRIBUTE = "trackedEntityTypeAttribute";
        public static final String USER_ATTRIBUTE = "userAttribute";
        public static final String CATEGORY_OPTION_GROUP_ATTRIBUTE = "categoryOptionGroupAttribute";
        public static final String PROGRAM_STAGE_ATTRIBUTE = "programStageAttribute";
        public static final String PROGRAM_ATTRIBUTE = "programAttribute";
        public static final String CATEGORY_ATTRIBUTE = "categoryAttribute";
        public static final String CATEGORY_OPTION_COMBO_ATTRIBUTE = "categoryOptionComboAttribute";
        public static final String CATEGORY_OPTION_GROUP_SET_ATTRIBUTE =
                "categoryOptionGroupSetAttribute";
        public static final String VALIDATION_RULE_ATTRIBUTE = "validationRuleAttribute";
        public static final String PROGRAM_INDICATOR_ATTRIBUTE = "programIndicatorAttribute";
        public static final String ORGANISATION_UNIT_GROUP_ATTRIBUTE =
                "organisationUnitGroupAttribute";
        public static final String DATA_ELEMENT_GROUP_SET_ATTRIBUTE =
                "dataElementGroupSetAttribute";
        public static final String ORGANISATION_UNIT_GROUP_SET_ATTRIBUTE =
                "organisationUnitGroupSetAttribute";
        public static final String OPTION_ATTRIBUTE = "optionAttribute";

        @Override
        public String[] all() {
            return CollectionsHelper.appendInNewArray(super.all(),
                    VALUE_TYPE,
                    UNIQUE,
                    MANDATORY,
                    INDICATOR_ATTRIBUTE,
                    INDICATOR_GROUP_ATTRIBUTE,
                    USER_GROUP_ATTRIBUTE,
                    DATA_ELEMENT_ATTRIBUTE,
                    CONSTANT_ATTRIBUTE,
                    CATEGORY_OPTION_ATTRIBUTE,
                    OPTION_SET_ATTRIBUTE,
                    SQL_VIEW_ATTRIBUTE,
                    LEGEND_SET_ATTRIBUTE,
                    TRACKED_ENTITY_ATTRIBUTE_ATTRIBUTE,
                    ORGANISATION_UNIT_ATTRIBUTE,
                    DATA_SET_ATTRIBUTE,
                    DOCUMENT_ATTRIBUTE,
                    VALIDATION_RULE_GROUP_ATTRIBUTE,
                    DATA_ELEMENT_GROUP_ATTRIBUTE,
                    SECTION_ATTRIBUTE,
                    TRACKED_ENTITY_TYPE_ATTRIBUTE,
                    USER_ATTRIBUTE,
                    CATEGORY_OPTION_GROUP_ATTRIBUTE,
                    PROGRAM_STAGE_ATTRIBUTE,
                    PROGRAM_ATTRIBUTE,
                    CATEGORY_ATTRIBUTE,
                    CATEGORY_OPTION_COMBO_ATTRIBUTE,
                    CATEGORY_OPTION_GROUP_SET_ATTRIBUTE,
                    VALIDATION_RULE_ATTRIBUTE,
                    PROGRAM_INDICATOR_ATTRIBUTE,
                    ORGANISATION_UNIT_GROUP_ATTRIBUTE,
                    DATA_ELEMENT_GROUP_SET_ATTRIBUTE,
                    ORGANISATION_UNIT_GROUP_SET_ATTRIBUTE,
                    OPTION_ATTRIBUTE
            );
        }
    }
}
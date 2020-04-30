/*
 * Copyright (c) 2004-2019, University of Oslo
 * All rights reserved.
 *
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

package org.hisp.dhis.android.core.validation;

import org.hisp.dhis.android.core.arch.db.tableinfos.TableInfo;
import org.hisp.dhis.android.core.arch.helpers.CollectionsHelper;
import org.hisp.dhis.android.core.common.CoreColumns;
import org.hisp.dhis.android.core.common.NameableColumns;

public final class ValidationRuleTableInfo {

    private ValidationRuleTableInfo() {
    }

    public static final TableInfo TABLE_INFO = new TableInfo() {

        @Override
        public String name() {
            return "ValidationRule";
        }

        @Override
        public CoreColumns columns() {
            return new Columns();
        }
    };

    public static class Columns extends NameableColumns {
        public static final String INSTRUCTION = "instruction";
        public static final String IMPORTANCE = "importance";
        public static final String OPERATOR = "operator";
        public static final String PERIOD_TYPE = "periodType";
        public static final String SKIP_FORM_VALIDATION = "skipFormValidation";
        public static final String LEFT_SIDE_EXPRESSION = "leftSideExpression";
        public static final String LEFT_SIDE_DESCRIPTION = "leftSideDescription";
        public static final String LEFT_SIDE_MISSING_VALUE_STRATEGY = "leftSideMissingValueStrategy";
        public static final String RIGHT_SIDE_EXPRESSION = "rightSideExpression";
        public static final String RIGHT_SIDE_DESCRIPTION = "rightSideDescription";
        public static final String RIGHT_SIDE_MISSING_VALUE_STRATEGY = "rightSideMissingValueStrategy";

        @Override
        public String[] all() {
            return CollectionsHelper.appendInNewArray(super.all(),
                    INSTRUCTION,
                    IMPORTANCE,
                    OPERATOR,
                    PERIOD_TYPE,
                    SKIP_FORM_VALIDATION,
                    LEFT_SIDE_EXPRESSION,
                    LEFT_SIDE_DESCRIPTION,
                    LEFT_SIDE_MISSING_VALUE_STRATEGY,
                    RIGHT_SIDE_EXPRESSION,
                    RIGHT_SIDE_DESCRIPTION,
                    RIGHT_SIDE_MISSING_VALUE_STRATEGY
            );
        }
    }
}

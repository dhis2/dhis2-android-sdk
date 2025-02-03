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

package org.hisp.dhis.android.core.dataset;

import org.hisp.dhis.android.core.arch.db.tableinfos.TableInfo;
import org.hisp.dhis.android.core.arch.helpers.CollectionsHelper;
import org.hisp.dhis.android.core.common.CoreColumns;
import org.hisp.dhis.android.core.common.IdentifiableColumns;

public final class SectionTableInfo {

    private SectionTableInfo() {
    }

    public static final TableInfo TABLE_INFO = new TableInfo() {

        @Override
        public String name() {
            return "Section";
        }

        @Override
        public CoreColumns columns() {
            return new Columns();
        }
    };

    public static class Columns extends IdentifiableColumns {

        public static final String DESCRIPTION = "description";
        public static final String SORT_ORDER = "sortOrder";
        public static final String DATA_SET = "dataSet";
        public static final String SHOW_ROW_TOTALS = "showRowTotals";
        public static final String SHOW_COLUMN_TOTALS = "showColumnTotals";
        public static final String DISABLE_DATA_ELEMENT_AUTO_GROUPING = "disableDataElementAutoGrouping";
        public static final String BEFORE_SECTION_TEXT = "beforeSectionText";
        public static final String AFTER_SECTION_TEXT = "afterSectionText";
        public static final String PIVOT_MODE = "pivotMode";
        public static final String PIVOTED_CATEGORY = "pivotedCategory";

        @Override
        public String[] all() {
            return CollectionsHelper.appendInNewArray(super.all(),
                    DESCRIPTION,
                    SORT_ORDER,
                    DATA_SET,
                    SHOW_ROW_TOTALS,
                    SHOW_COLUMN_TOTALS,
                    DISABLE_DATA_ELEMENT_AUTO_GROUPING,
                    BEFORE_SECTION_TEXT,
                    AFTER_SECTION_TEXT,
                    PIVOT_MODE,
                    PIVOTED_CATEGORY
            );
        }
    }
}

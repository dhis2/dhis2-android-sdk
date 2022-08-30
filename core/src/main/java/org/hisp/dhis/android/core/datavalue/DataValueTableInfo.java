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

package org.hisp.dhis.android.core.datavalue;

import org.hisp.dhis.android.core.arch.db.tableinfos.TableInfo;
import org.hisp.dhis.android.core.arch.helpers.CollectionsHelper;
import org.hisp.dhis.android.core.common.CoreColumns;
import org.hisp.dhis.android.core.common.DeletableDataColumns;

public class DataValueTableInfo {

    private DataValueTableInfo() {}

    public static final TableInfo TABLE_INFO = new TableInfo() {

        @Override
        public String name() {
            return "DataValue";
        }

        @Override
        public CoreColumns columns() {
            return new DataValueTableInfo.Columns();
        }
    };

    public static class Columns extends DeletableDataColumns {

        public static final String DATA_ELEMENT = "dataElement";
        public static final String PERIOD = "period";
        public static final String ORGANISATION_UNIT =  "organisationUnit";
        public static final String CATEGORY_OPTION_COMBO = "categoryOptionCombo";
        public static final String ATTRIBUTE_OPTION_COMBO = "attributeOptionCombo";
        public static final String VALUE = "value";
        public static final String STORED_BY = "storedBy";
        public static final String CREATED = "created";
        public static final String LAST_UPDATED = "lastUpdated";
        public static final String COMMENT = "comment";
        public static final String FOLLOW_UP = "followUp";

        @Override
        public String[] all() {
            return CollectionsHelper.appendInNewArray(super.all(),
                    DATA_ELEMENT,
                    PERIOD,
                    ORGANISATION_UNIT,
                    CATEGORY_OPTION_COMBO,
                    ATTRIBUTE_OPTION_COMBO,
                    VALUE,
                    STORED_BY,
                    CREATED,
                    LAST_UPDATED,
                    COMMENT,
                    FOLLOW_UP,
                    SYNC_STATE,
                    DELETED);
        }

        @Override
        public String[] whereUpdate() {
            return new String[]{
                    DATA_ELEMENT,
                    PERIOD,
                    ORGANISATION_UNIT,
                    CATEGORY_OPTION_COMBO,
                    ATTRIBUTE_OPTION_COMBO
            };
        }
    }

}

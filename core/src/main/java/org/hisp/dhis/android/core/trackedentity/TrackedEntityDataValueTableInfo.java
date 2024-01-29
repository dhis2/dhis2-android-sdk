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

import org.hisp.dhis.android.core.arch.db.tableinfos.TableInfo;
import org.hisp.dhis.android.core.arch.helpers.CollectionsHelper;
import org.hisp.dhis.android.core.common.CoreColumns;
import org.hisp.dhis.android.core.common.DataColumns;

public final class TrackedEntityDataValueTableInfo {

    private TrackedEntityDataValueTableInfo() {
    }

    public static final TableInfo TABLE_INFO = new TableInfo() {

        @Override
        public String name() {
            return "TrackedEntityDataValue";
        }

        @Override
        public Columns columns() {
            return new Columns();
        }
    };

    public static class Columns extends CoreColumns {
        public static final String EVENT = "event";
        public final static String DATA_ELEMENT = "dataElement";
        public final static String STORED_BY = "storedBy";
        public final static String VALUE = "value";
        public final static String CREATED = "created";
        public final static String LAST_UPDATED = "lastUpdated";
        public final static String PROVIDED_ELSEWHERE = "providedElsewhere";
        public final static String SYNC_STATE = DataColumns.SYNC_STATE;

        @Override
        public String[] all() {
            return CollectionsHelper.appendInNewArray(super.all(),
                    EVENT,
                    CREATED,
                    LAST_UPDATED,
                    DATA_ELEMENT,
                    STORED_BY,
                    VALUE,
                    PROVIDED_ELSEWHERE,
                    SYNC_STATE
            );
        }

        @Override
        public String[] whereUpdate() {
            return CollectionsHelper.appendInNewArray(super.all(),
                    EVENT,
                    DATA_ELEMENT
            );
        }
    }
}
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

package org.hisp.dhis.android.core.dataset;

import org.hisp.dhis.android.core.arch.db.tableinfos.TableInfo;
import org.hisp.dhis.android.core.common.BaseModel;
import org.hisp.dhis.android.core.common.BaseNameableObjectModel;
import org.hisp.dhis.android.core.dataset.internal.DataSetFields;
import org.hisp.dhis.android.core.utils.Utils;

public final class DataSetTableInfo {

    private DataSetTableInfo() {
    }

    public static final TableInfo TABLE_INFO = new TableInfo() {

        @Override
        public String name() {
            return "DataSet";
        }

        @Override
        public BaseModel.Columns columns() {
            return new Columns();
        }
    };

    static class Columns extends BaseNameableObjectModel.Columns {

        @Override
        public String[] all() {
            return Utils.appendInNewArray(super.all(),
                    DataSetFields.PERIOD_TYPE,
                    DataSetFields.CATEGORY_COMBO,
                    DataSetFields.MOBILE,
                    DataSetFields.VERSION,
                    DataSetFields.EXPIRY_DAYS,
                    DataSetFields.TIMELY_DAYS,
                    DataSetFields.NOTIFY_COMPLETING_USER,
                    DataSetFields.OPEN_FUTURE_PERIODS,
                    DataSetFields.FIELD_COMBINATION_REQUIRED,
                    DataSetFields.VALID_COMPLETE_ONLY,
                    DataSetFields.NO_VALUE_REQUIRES_COMMENT,
                    DataSetFields.SKIP_OFFLINE,
                    DataSetFields.DATA_ELEMENT_DECORATION,
                    DataSetFields.RENDER_AS_TABS,
                    DataSetFields.RENDER_HORIZONTALLY,
                    DataSetFields.ACCESS_DATA_WRITE,
                    DataSetFields.WORKFLOW
            );
        }
    }
}

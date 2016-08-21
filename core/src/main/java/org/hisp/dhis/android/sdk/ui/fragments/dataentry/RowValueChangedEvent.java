/*
 *  Copyright (c) 2016, University of Oslo
 *  * All rights reserved.
 *  *
 *  * Redistribution and use in source and binary forms, with or without
 *  * modification, are permitted provided that the following conditions are met:
 *  * Redistributions of source code must retain the above copyright notice, this
 *  * list of conditions and the following disclaimer.
 *  *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *  * this list of conditions and the following disclaimer in the documentation
 *  * and/or other materials provided with the distribution.
 *  * Neither the name of the HISP project nor the names of its contributors may
 *  * be used to endorse or promote products derived from this software without
 *  * specific prior written permission.
 *  *
 *  * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package org.hisp.dhis.android.sdk.ui.fragments.dataentry;

import org.hisp.dhis.android.sdk.persistence.models.BaseValue;
import org.hisp.dhis.android.sdk.persistence.models.DataValue;
import org.hisp.dhis.android.sdk.persistence.models.TrackedEntityAttributeValue;
import org.hisp.dhis.android.sdk.ui.adapters.rows.dataentry.Row;

public final class RowValueChangedEvent {

    public static final String DATAVALUE = "datavalue";
    public static final String TRACKEDENTITYATTRIBUTEVALUE = "trackedentityattributevalue";
    private String type;
    private String id;
    private BaseValue baseValue;
    private String rowType;
    private Row row;

    public RowValueChangedEvent(BaseValue baseValue, String rowType ) {
        this.rowType = rowType;

        if(baseValue instanceof DataValue) {
            type = DATAVALUE;
            id = ( ( DataValue ) baseValue ).getDataElement();
        } else if(baseValue instanceof TrackedEntityAttributeValue) {
            type = TRACKEDENTITYATTRIBUTEVALUE;
            id = ( ( TrackedEntityAttributeValue ) baseValue ).getTrackedEntityAttributeId();
        }
    }

    public String getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public boolean isDataValue() {
        return type.equals(DATAVALUE);
    }

    public BaseValue getBaseValue() {
        return baseValue;
    }

    public String getRowType() {
        return rowType;
    }

    public Row getRow() {
        return row;
    }

    public void setRow(Row row) {
        this.row = row;
    }
}

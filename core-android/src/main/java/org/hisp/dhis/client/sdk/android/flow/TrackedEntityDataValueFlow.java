/*
 * Copyright (c) 2016, University of Oslo
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

package org.hisp.dhis.client.sdk.android.flow;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ConflictAction;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ForeignKeyAction;
import com.raizlabs.android.dbflow.annotation.ForeignKeyReference;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.annotation.Unique;
import com.raizlabs.android.dbflow.annotation.UniqueGroup;

import org.hisp.dhis.client.sdk.android.common.meta.DbDhis;

@Table(database = DbDhis.class, uniqueColumnGroups = {
        @UniqueGroup(
                groupNumber = TrackedEntityDataValueFlow.UNIQUE_EVENT_DATAVALUE,
                uniqueConflict = ConflictAction.FAIL)
})
public final class TrackedEntityDataValueFlow extends BaseModelFlow {
    static final int UNIQUE_EVENT_DATAVALUE = 57;
    static final String EVENT_KEY = "event";

    @Column
    @Unique(unique = true, uniqueGroups = {UNIQUE_EVENT_DATAVALUE})
    @ForeignKey(
            references = {
                    @ForeignKeyReference(columnName = EVENT_KEY,
                            columnType = String.class, foreignKeyColumnName = "eventUid"),
            }, saveForeignKeyModel = false, onDelete = ForeignKeyAction.CASCADE
    )
    EventFlow event;

    @Column
    @Unique(unique = true, uniqueGroups = {UNIQUE_EVENT_DATAVALUE})
    String dataElement;

    @Column
    boolean providedElsewhere;

    @Column
    String storedBy;

    @Column
    String value;

    public EventFlow getEvent() {
        return event;
    }

    public void setEvent(EventFlow event) {
        this.event = event;
    }

    public String getDataElement() {
        return dataElement;
    }

    public void setDataElement(String dataElement) {
        this.dataElement = dataElement;
    }

    public boolean isProvidedElsewhere() {
        return providedElsewhere;
    }

    public void setProvidedElsewhere(boolean providedElsewhere) {
        this.providedElsewhere = providedElsewhere;
    }

    public String getStoredBy() {
        return storedBy;
    }

    public void setStoredBy(String storedBy) {
        this.storedBy = storedBy;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public TrackedEntityDataValueFlow() {
        // empty constructor
    }
}

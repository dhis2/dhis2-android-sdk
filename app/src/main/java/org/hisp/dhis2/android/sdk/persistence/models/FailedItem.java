/*
 *  Copyright (c) 2015, University of Oslo
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

package org.hisp.dhis2.android.sdk.persistence.models;

import android.util.Log;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKeyReference;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.hisp.dhis2.android.sdk.controllers.Dhis2;

/**
 * Class for holding information on items that have failed to upload to the server.
 * @author Simen Skogly Russnes on 26.02.15.
 */
@Table
public class FailedItem extends BaseModel {

    private final static String CLASS_TAG = "FailedItem";

    public static final String EVENT = "Event";
    public static final String ENROLLMENT = "Enrollment";
    // add more like TrackedEntityInstance, Enrollment .. in the future

    @Column(columnType = Column.PRIMARY_KEY)
    public long itemId;

    @Column
    public String itemType;

    @Column
    public int httpStatusCode; // 401, 500 .. etc

    @Column
    public String errorMessage; // the web api sometimes crashes with status 500, so for example the stack trace could be here.

    @Column(columnType = Column.FOREIGN_KEY,
            references = {@ForeignKeyReference(columnName = "importSummary",
                    columnType = int.class, foreignColumnName = "id")})
    public ImportSummary importSummary;

    public BaseModel getItem() {
        BaseModel item = null;
        if(itemType.equals(EVENT)) {
            item = Dhis2.getInstance().getDataValueController().getEvent(itemId);
        }
        return item;
    }

}

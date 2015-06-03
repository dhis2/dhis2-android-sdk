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

package org.hisp.dhis.android.sdk.persistence.models;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ForeignKeyReference;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.hisp.dhis.android.sdk.controllers.Dhis2;
import org.hisp.dhis.android.sdk.persistence.Dhis2Database;

/**
 * Class for holding information on items that have failed to upload to the server.
 * @author Simen Skogly Russnes on 26.02.15.
 */
@Table(databaseName = Dhis2Database.NAME)
public class FailedItem extends BaseModel {

    private final static String CLASS_TAG = "FailedItem";

    public static final String EVENT = "Event";
    public static final String ENROLLMENT = "Enrollment";
    public static final String TRACKEDENTITYINSTANCE = "TrackedEntityInstance";

    @Column
    @PrimaryKey
    private long itemId;

    @Column
    private String itemType;

    @Column
    private int httpStatusCode; // 401, 500 .. etc

    @Column
    private String errorMessage; // the web api sometimes crashes with status 500, so for example the stack trace could be here.

    @Column
    @ForeignKey(references = {@ForeignKeyReference(columnName = "importSummary",
                    columnType = int.class, foreignColumnName = "id")})
    protected ImportSummary importSummary;

    /**
     * Returns the item for the given FailedItem. Can be cast to either of the model types
     * @return
     */
    public BaseModel getItem() {
        BaseModel item = null;
        if(itemType.equals(EVENT)) {
            item = Dhis2.getInstance().getDataValueController().getEvent(itemId);
        } else if (itemType.equals(ENROLLMENT)) {
            item = Dhis2.getInstance().getDataValueController().getEnrollment(itemId);
        } else if (itemType.equals(TRACKEDENTITYINSTANCE)) {
            item = Dhis2.getInstance().getDataValueController().getTrackedEntityInstance(itemId);
        }
        return item;
    }

    public ImportSummary getImportSummary() {
        return importSummary;
    }

    public long getItemId() {
        return itemId;
    }

    public String getItemType() {
        return itemType;
    }

    public int getHttpStatusCode() {
        return httpStatusCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setItemId(long itemId) {
        this.itemId = itemId;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public void setHttpStatusCode(int httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public void setImportSummary(ImportSummary importSummary) {
        this.importSummary = importSummary;
    }
}

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

package org.hisp.dhis.client.sdk.core.commons.database;

import android.net.Uri;

public interface DbContract {
    String AUTHORITY = "org.hisp.dhis.client.sdk.core";
    Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    interface VersionColumn {
        String COLUMN_VERSION = "version";
    }

    interface IdColumn {
        String COLUMN_ID = "id";
    }

    interface TimeStampColumns {
        String COLUMN_CREATED = "created";
        String COLUMN_LAST_UPDATED = "lastUpdated";
    }

    interface IdentifiableColumns extends IdColumn, TimeStampColumns {
        String COLUMN_UID = "uid";
        String COLUMN_CODE = "code";
        String COLUMN_NAME = "name";
        String COLUMN_DISPLAY_NAME = "displayName";
    }

    interface NameableColumns extends IdentifiableColumns {
        String COLUMN_SHORT_NAME = "shortName";
        String COLUMN_DESCRIPTION = "description";
        String COLUMN_DISPLAY_SHORT_NAME = "displayShortName";
        String COLUMN_DISPLAY_DESCRIPTION = "displayDescription";
    }

    interface CoordinatesColumn {
        String COLUMN_LONGITUDE = "longitude";
        String COLUMN_LATITUDE = "latitude";
    }

    interface BodyColumn {
        String COLUMN_BODY = "body";
    }

    interface StateColumn {
        String COLUMN_STATE = "state";
    }
}

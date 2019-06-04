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

package org.hisp.dhis.android.core.trackedentity;

import org.hisp.dhis.android.core.arch.fields.internal.FieldsHelper;
import org.hisp.dhis.android.core.arch.api.fields.internal.Fields;

import java.util.Date;

public final class TrackedEntityDataValueFields {

    final static String DATA_ELEMENT = "dataElement";
    final static String STORED_BY = "storedBy";
    final static String VALUE = "value";
    final static String CREATED = "created";
    final static String LAST_UPDATED = "lastUpdated";
    final static String PROVIDED_ELSEWHERE = "providedElsewhere";

    private static final FieldsHelper<TrackedEntityDataValue> fh = new FieldsHelper<>();

    public static final Fields<TrackedEntityDataValue> allFields = Fields.<TrackedEntityDataValue>builder()
            .fields(
                    fh.<String>field(DATA_ELEMENT),
                    fh.<String>field(STORED_BY),
                    fh.<String>field(VALUE),
                    fh.<Date>field(CREATED),
                    fh.<Date>field(LAST_UPDATED),
                    fh.<Boolean>field(PROVIDED_ELSEWHERE)
            ).build();

    private TrackedEntityDataValueFields() {
    }
}
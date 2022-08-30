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

package org.hisp.dhis.android.core.trackedentity.search;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.Reusable;

@Reusable
class SearchGridMapper {
    private static final Integer NON_ATTRIBUTE_LENGTH = 7;

    @Inject
    SearchGridMapper() {
        // Empty constructor to add Dagger annotation
    }

    List<TrackedEntityInstance> transform(SearchGrid searchGrid) throws ParseException {
        List<TrackedEntityInstance> teis = new ArrayList<>(searchGrid.rows().size());
        for (List<String> row : searchGrid.rows()) {
            TrackedEntityInstance tei = TrackedEntityInstance.builder()
                    .uid(row.get(0))
                    .created(BaseIdentifiableObject.parseSpaceDate(row.get(1)))
                    .lastUpdated(BaseIdentifiableObject.parseSpaceDate(row.get(2)))
                    .organisationUnit(row.get(3))
                    .trackedEntityType(row.get(5))
                    .trackedEntityAttributeValues(getAttributes(searchGrid.headers(), row))
                    .build();

            teis.add(tei);
        }
        return teis;
    }

    private List<TrackedEntityAttributeValue> getAttributes(List<SearchGridHeader> headers, List<String> row) {
        List<TrackedEntityAttributeValue> attributeValues = new ArrayList<>(row.size()
                - NON_ATTRIBUTE_LENGTH);

        for (int i = NON_ATTRIBUTE_LENGTH; i < row.size(); i++) {
            TrackedEntityAttributeValue attributeValue = TrackedEntityAttributeValue.builder()
                    .trackedEntityAttribute(headers.get(i).name())
                    .value(row.get(i))
                    .build();
            attributeValues.add(attributeValue);
        }

        return attributeValues;
    }
}
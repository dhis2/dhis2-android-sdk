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

package org.hisp.dhis.android.core.relationship;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.BaseObjectShould;
import org.hisp.dhis.android.core.common.ObjectShould;
import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;

import static com.google.common.truth.Truth.assertThat;

public class RelationshipType32Should extends BaseObjectShould implements ObjectShould {

    public RelationshipType32Should() {
        super("relationship/relationship_type_32.json");
    }

    @Override
    @Test
    public void map_from_json_string() throws IOException, ParseException {
        RelationshipType relationshipType = objectMapper.readValue(jsonStream, RelationshipType.class);

        assertThat(relationshipType.uid()).isEqualTo("WiH6923nMtb");
        assertThat(relationshipType.name()).isEqualTo("Sibling_b-to-a_(Person-Person)");
        assertThat(relationshipType.displayName()).isEqualTo("Sibling_b-to-a_(Person-Person)");
        assertThat(relationshipType.created()).isEqualTo(
                BaseIdentifiableObject.DATE_FORMAT.parse("2014-04-14T13:53:38.659"));
        assertThat(relationshipType.lastUpdated()).isEqualTo(
                BaseIdentifiableObject.DATE_FORMAT.parse("2014-04-14T13:53:41.066"));
        assertThat(relationshipType.aIsToB()).isNull();
        assertThat(relationshipType.bIsToA()).isNull();
        assertThat(relationshipType.toFromName()).isEqualTo("Sibling_a-to-b_(Person-Person)");
        assertThat(relationshipType.fromToName()).isEqualTo("Sibling_b-to-a_(Person-Person)");
        assertThat(relationshipType.fromConstraint()).isNotNull();
        assertThat(relationshipType.fromConstraint().relationshipEntity()).isEqualTo(RelationshipEntityType.TRACKED_ENTITY_INSTANCE);
        assertThat(relationshipType.fromConstraint().trackedEntityType().uid()).isEqualTo("nEenWmSyUEp");
        assertThat(relationshipType.fromConstraint().trackerDataView().attributes().get(0)).isEqualTo("b0vcadVrn08");
        assertThat(relationshipType.fromConstraint().trackerDataView().dataElements().isEmpty()).isTrue();
        assertThat(relationshipType.toConstraint()).isNotNull();
        assertThat(relationshipType.toConstraint().relationshipEntity()).isEqualTo(RelationshipEntityType.PROGRAM_INSTANCE);
        assertThat(relationshipType.toConstraint().program().uid()).isEqualTo("WSGAb5XwJ3Y");
        assertThat(relationshipType.toConstraint().trackerDataView().attributes().get(0)).isEqualTo("b0vcadVrn08");
        assertThat(relationshipType.toConstraint().trackerDataView().attributes().get(1)).isEqualTo("qXS2NDUEAOS");
        assertThat(relationshipType.toConstraint().trackerDataView().dataElements().get(0)).isEqualTo("ciWE5jde1ax");
        assertThat(relationshipType.toConstraint().trackerDataView().dataElements().get(1)).isEqualTo("hB9F8vKFmlk");
        assertThat(relationshipType.toConstraint().trackerDataView().dataElements().get(2)).isEqualTo("uFAQYm3UgBL");
        assertThat(relationshipType.bidirectional()).isTrue();
        assertThat(relationshipType.access().data().read()).isTrue();
        assertThat(relationshipType.access().data().write()).isFalse();
    }
}

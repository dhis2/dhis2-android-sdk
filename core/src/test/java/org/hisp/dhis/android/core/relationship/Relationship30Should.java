/*
 * Copyright (c) 2017, University of Oslo
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

package org.hisp.dhis.android.core.relationship;

import org.hisp.dhis.android.core.common.BaseObjectShould;
import org.hisp.dhis.android.core.common.ObjectShould;
import org.hisp.dhis.android.core.data.relationship.RelationshipSamples;
import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class Relationship30Should extends BaseObjectShould implements ObjectShould {

    private RelationshipSamples samples = new RelationshipSamples();

    public Relationship30Should() {
        super("relationship/relationship_30.json");
    }

    @Override
    @Test
    public void map_from_json_string() throws IOException, ParseException {
        Relationship229Compatible relationship = deserialize(Relationship229Compatible.class);

        assertThat(relationship.uid()).isEqualTo("nEenWmSyUEp");
        assertThat(relationship.relationshipType()).isEqualTo("V2kkHafqs8G");
        assertThat(relationship.name()).isEqualTo("Mother-Child");

        assertThat(relationship.from()).isNotNull();
        assertThat(relationship.from().trackedEntityInstance()).isNotNull();
        assertThat(relationship.from().trackedEntityInstance().trackedEntityInstance()).isEqualTo("o51cUNONthg");

        assertThat(relationship.to()).isNotNull();
        assertThat(relationship.to().enrollment()).isNotNull();
        assertThat(relationship.to().enrollment().enrollment()).isEqualTo("lxAQ7Zs9VYR");
    }

    @Test
    public void serialize_and_deserialize_to_same_object() throws IOException {
        Relationship229Compatible initialRelationship = samples.get230Compatible();
        Relationship229Compatible newRelationship = deserialize(serialize(initialRelationship), Relationship229Compatible.class);
        assertThat(newRelationship).isEqualTo(initialRelationship);
    }
}
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
package org.hisp.dhis.android.core.relationship

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.common.BaseIdentifiableObject
import org.hisp.dhis.android.core.common.BaseObjectKotlinxShould
import org.hisp.dhis.android.core.common.ObjectShould
import org.hisp.dhis.android.network.relationshiptype.RelationshipTypeDTO
import org.junit.Test

class RelationshipType30Should :
    BaseObjectKotlinxShould("relationship/relationship_type_30.json"),
    ObjectShould {
    @Test
    override fun map_from_json_string() {
        val relationshipTypeDTO = deserialize(RelationshipTypeDTO.serializer())
        val relationshipType = relationshipTypeDTO.toDomain()

        assertThat(relationshipType.uid()).isEqualTo("V2kkHafqs8G")
        assertThat(relationshipType.name()).isEqualTo("Mother-Child_b-to-a_(Person-Person)")
        assertThat(relationshipType.displayName())
            .isEqualTo("Mother-Child_b-to-a_(Person-Person)")
        assertThat(relationshipType.created()).isEqualTo(
            BaseIdentifiableObject.DATE_FORMAT.parse("2013-09-19T15:17:41.000"),
        )
        assertThat(relationshipType.lastUpdated()).isEqualTo(
            BaseIdentifiableObject.DATE_FORMAT.parse("2014-04-14T13:53:20.166"),
        )
        assertThat(relationshipType.toFromName()).isNull()
        assertThat(relationshipType.fromToName()).isNull()
        assertThat(relationshipType.fromConstraint()).isNotNull()
        assertThat(
            relationshipType.fromConstraint()!!.relationshipEntity(),
        ).isEqualTo(RelationshipEntityType.TRACKED_ENTITY_INSTANCE)
        assertThat(
            relationshipType.fromConstraint()!!.trackedEntityType()!!.uid(),
        ).isEqualTo("nEenWmSyUEp")
        assertThat(
            relationshipType.fromConstraint()!!.trackerDataView()!!.attributes()!![0],
        ).isEqualTo("b0vcadVrn08")
        assertThat(
            relationshipType.fromConstraint()!!.trackerDataView()!!.attributes()!![1],
        ).isEqualTo("qXS2NDUEAOS")
        assertThat(
            relationshipType.fromConstraint()!!.trackerDataView()!!.dataElements()!![0],
        ).isEqualTo("ciWE5jde1ax")
        assertThat(
            relationshipType.fromConstraint()!!.trackerDataView()!!.dataElements()!![1],
        ).isEqualTo("hB9F8vKFmlk")
        assertThat(
            relationshipType.fromConstraint()!!.trackerDataView()!!.dataElements()!![2],
        ).isEqualTo("uFAQYm3UgBL")
        assertThat(relationshipType.toConstraint()).isNotNull()
        assertThat(
            relationshipType.toConstraint()!!.relationshipEntity(),
        ).isEqualTo(RelationshipEntityType.PROGRAM_INSTANCE)
        assertThat(relationshipType.toConstraint()!!.program()!!.uid())
            .isEqualTo("WSGAb5XwJ3Y")
        assertThat(
            relationshipType.toConstraint()!!.trackerDataView()!!.attributes()!![0],
        ).isEqualTo("b0vcadVrn08")
        assertThat(
            relationshipType.toConstraint()!!.trackerDataView()!!.dataElements()!!.isEmpty(),
        ).isTrue()
        assertThat(relationshipType.bidirectional()).isFalse()
        assertThat(relationshipType.access().data().read()).isTrue()
        assertThat(relationshipType.access().data().write()).isFalse()
    }
}

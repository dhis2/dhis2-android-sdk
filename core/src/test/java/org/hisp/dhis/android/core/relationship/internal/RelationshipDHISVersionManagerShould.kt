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
package org.hisp.dhis.android.core.relationship.internal

import org.hisp.dhis.android.core.data.relationship.RelationshipSamples
import org.junit.Before
import org.junit.Test
import java.lang.Exception

class RelationshipDHISVersionManagerShould {
    @Mock
    private val relationshipTypeStore: IdentifiableObjectStore<RelationshipType>? = null

    @Mock
    private val relationshipType: RelationshipType? = null
    private var relationshipDHISVersionManager: RelationshipDHISVersionManager? = null
    @Before
    @Throws(Exception::class)
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        relationshipDHISVersionManager = RelationshipDHISVersionManager(relationshipTypeStore)
    }

    @Test
    fun get_owned_relationships_in_bidirectional() {
        Mockito.`when`<RelationshipType>(relationshipTypeStore.selectByUid(TYPE)).thenReturn(relationshipType)
        Mockito.`when`<Boolean>(relationshipType.bidirectional()).thenReturn(true)
        val relationships: Collection<Relationship> = listOf(get230())
        val ownedRelationships: Collection<Relationship> =
            relationshipDHISVersionManager!!.getOwnedRelationships(relationships, TO_UID)
        Truth.assertThat(ownedRelationships.size).isEqualTo(1)
    }

    @Test
    fun get_owned_relationships_in_non_bidirectional() {
        Mockito.`when`<RelationshipType>(relationshipTypeStore.selectByUid(TYPE)).thenReturn(relationshipType)
        Mockito.`when`<Boolean>(relationshipType.bidirectional()).thenReturn(false)
        val relationships: Collection<Relationship> = listOf(get230())
        val ownedToRelationships: Collection<Relationship> =
            relationshipDHISVersionManager!!.getOwnedRelationships(relationships, TO_UID)
        val ownedFromRelationships: Collection<Relationship> =
            relationshipDHISVersionManager!!.getOwnedRelationships(relationships, FROM_UID)
        Truth.assertThat(ownedToRelationships).isEmpty()
        Truth.assertThat(ownedFromRelationships.size).isEqualTo(1)
    }
}
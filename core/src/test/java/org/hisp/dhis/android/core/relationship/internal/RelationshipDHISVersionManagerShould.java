/*
 *  Copyright (c) 2004-2021, University of Oslo
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

package org.hisp.dhis.android.core.relationship.internal;

import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore;
import org.hisp.dhis.android.core.data.relationship.RelationshipSamples;
import org.hisp.dhis.android.core.relationship.Relationship;
import org.hisp.dhis.android.core.relationship.RelationshipType;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.Collections;


public class RelationshipDHISVersionManagerShould extends RelationshipSamples {

    @Mock
    private IdentifiableObjectStore<RelationshipType> relationshipTypeStore;

    @Mock
    private RelationshipType relationshipType;

    private RelationshipDHISVersionManager relationshipDHISVersionManager;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        relationshipDHISVersionManager = new RelationshipDHISVersionManager(relationshipTypeStore);
    }

    @Test
    public void get_owned_relationships_in_bidirectional() {
        when(relationshipTypeStore.selectByUid(TYPE)).thenReturn(relationshipType);
        when(relationshipType.bidirectional()).thenReturn(true);

        Collection<Relationship> relationships = Collections.singletonList(get230());

        Collection<Relationship> ownedRelationships =
                relationshipDHISVersionManager.getOwnedRelationships(relationships, TO_UID);

        assertThat(ownedRelationships.size()).isEqualTo(1);
    }

    @Test
    public void get_owned_relationships_in_non_bidirectional() {
        when(relationshipTypeStore.selectByUid(TYPE)).thenReturn(relationshipType);
        when(relationshipType.bidirectional()).thenReturn(false);

        Collection<Relationship> relationships = Collections.singletonList(get230());

        Collection<Relationship> ownedToRelationships =
                relationshipDHISVersionManager.getOwnedRelationships(relationships, TO_UID);

        Collection<Relationship> ownedFromRelationships =
                relationshipDHISVersionManager.getOwnedRelationships(relationships, FROM_UID);

        assertThat(ownedToRelationships).isEmpty();
        assertThat(ownedFromRelationships.size()).isEqualTo(1);
    }
}
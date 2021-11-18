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
import org.hisp.dhis.android.core.relationship.BaseRelationship;
import org.hisp.dhis.android.core.relationship.Relationship;
import org.hisp.dhis.android.core.relationship.RelationshipType;
import org.hisp.dhis.android.core.systeminfo.DHISVersionManager;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static com.google.common.truth.Truth.assertThat;
import static org.hisp.dhis.android.core.data.utils.FillPropertiesTestUtils.CREATED;
import static org.hisp.dhis.android.core.data.utils.FillPropertiesTestUtils.LAST_UPDATED;
import static org.hisp.dhis.android.core.data.utils.FillPropertiesTestUtils.NAME;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.Collections;


public class RelationshipDHISVersionManagerShould extends RelationshipSamples {

    @Mock
    private DHISVersionManager versionManager;

    @Mock
    private IdentifiableObjectStore<RelationshipType> relationshipTypeStore;

    @Mock
    private RelationshipType relationshipType;

    private RelationshipDHISVersionManager relationshipDHISVersionManager;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        relationshipDHISVersionManager = new RelationshipDHISVersionManager(versionManager, relationshipTypeStore);
    }

    private void assertCommonFields(BaseRelationship relationship) {
        assertThat(relationship.name()).isEqualTo(NAME);
        assertThat(relationship.created()).isEqualTo(CREATED);
        assertThat(relationship.lastUpdated()).isEqualTo(LAST_UPDATED);
    }

    private void assert230Fields(BaseRelationship relationship) {
        assertCommonFields(relationship);
        assertThat(relationship.relationshipType()).isEqualTo(TYPE);
        assertThat(relationship.from()).isEqualTo(fromItem);
        assertThat(relationship.to()).isEqualTo(toItem);
    }

    @Test
    public void transform_2_30_compatible_relationship_into_equivalent_relationship() {
        when(versionManager.is2_29()).thenReturn(false);

        Relationship229Compatible relationship230Compatible = get230Compatible();

        Relationship relationship230 = relationshipDHISVersionManager.from229Compatible(relationship230Compatible);
        assert230Fields(relationship230);
        assertThat(relationship230.uid()).isEqualTo(UID);
    }

    @Test
    public void transform_2_29_compatible_relationship_into_2_30_relationship() {
        when(versionManager.is2_29()).thenReturn(true);
        Relationship relationship229_as_230 = relationshipDHISVersionManager.from229Compatible(get229Compatible());
        assert230Fields(relationship229_as_230);
    }

    @Test
    public void generate_uid_for_229_relationship() {
        when(versionManager.is2_29()).thenReturn(true);
        Relationship relationship229_as_230 = relationshipDHISVersionManager.from229Compatible(get229Compatible());
        assertThat(relationship229_as_230.uid()).isNotNull();
    }

    @Test
    public void transform_2_30_relationship_into_equivalent_compatible_relationship() {
        when(versionManager.is2_29()).thenReturn(false);
        Relationship229Compatible compatible = relationshipDHISVersionManager.to229Compatible(get230(), FROM_UID);
        assert230Fields(compatible);
        assertThat(compatible.uid()).isEqualTo(UID);
        assertThat(compatible.relative()).isNull();
        assertThat(compatible.syncState()).isEqualTo(STATE);
        assertThat(compatible.deleted()).isEqualTo(DELETED);
    }

    @Test
    public void not_include_relative_in_2_20_relationship() {
        when(versionManager.is2_29()).thenReturn(false);
        Relationship229Compatible compatible = relationshipDHISVersionManager.to229Compatible(get230(), FROM_UID);
        assertThat(compatible.relative()).isNull();
    }

    @Test
    public void transform_2_30_relationship_into_compatible_229_relationship() {
        when(versionManager.is2_29()).thenReturn(true);
        Relationship229Compatible compatible = relationshipDHISVersionManager.to229Compatible(get230(), FROM_UID);
        assertCommonFields(compatible);
        assertThat(compatible.trackedEntityInstanceA()).isEqualTo(FROM_UID);
        assertThat(compatible.trackedEntityInstanceB()).isEqualTo(TO_UID);
        assertThat(compatible.uid()).isEqualTo(TYPE);
        assertThat(compatible.syncState()).isEqualTo(STATE);
        assertThat(compatible.deleted()).isEqualTo(DELETED);
    }

    @Test
    public void include_to_as_relative_when_passing_from_in_2_29() {
        when(versionManager.is2_29()).thenReturn(true);
        when(versionManager.is2_29()).thenReturn(true);
        Relationship229Compatible compatible = relationshipDHISVersionManager.to229Compatible(get230(), FROM_UID);
        assertThat(compatible.relative().uid()).isEqualTo(TO_UID);
    }

    @Test
    public void include_from_as_relative_when_passing_to_in_2_29() {
        when(versionManager.is2_29()).thenReturn(true);
        Relationship229Compatible compatible = relationshipDHISVersionManager.to229Compatible(get230(), TO_UID);
        assertThat(compatible.relative().uid()).isEqualTo(FROM_UID);
    }

    @Test
    public void get_owned_relationships_in_2_29() {
        when(versionManager.is2_29()).thenReturn(true);

        Collection<Relationship> relationships = Collections.singletonList(get230());

        Collection<Relationship> ownedRelationships =
                relationshipDHISVersionManager.getOwnedRelationships(relationships, TO_UID);

        assertThat(ownedRelationships.size()).isEqualTo(1);
    }

    @Test
    public void get_owned_relationships_in_bidirectional() {
        when(versionManager.is2_29()).thenReturn(false);
        when(relationshipTypeStore.selectByUid(TYPE)).thenReturn(relationshipType);
        when(relationshipType.bidirectional()).thenReturn(true);

        Collection<Relationship> relationships = Collections.singletonList(get230());

        Collection<Relationship> ownedRelationships =
                relationshipDHISVersionManager.getOwnedRelationships(relationships, TO_UID);

        assertThat(ownedRelationships.size()).isEqualTo(1);
    }

    @Test
    public void get_owned_relationships_in_non_bidirectional() {
        when(versionManager.is2_29()).thenReturn(false);
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
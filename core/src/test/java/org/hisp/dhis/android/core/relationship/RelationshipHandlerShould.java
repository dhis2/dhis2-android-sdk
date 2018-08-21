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

import org.hisp.dhis.android.core.common.IdentifiableObjectStore;
import org.hisp.dhis.android.core.data.relationship.RelationshipSamples;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceStore;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;

import static org.hisp.dhis.android.core.relationship.RelationshipConstraintType.FROM;
import static org.hisp.dhis.android.core.relationship.RelationshipConstraintType.TO;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class RelationshipHandlerShould extends RelationshipSamples {

    @Mock
    private IdentifiableObjectStore<Relationship> relationshipStore;

    @Mock
    private RelationshipItemStoreInterface relationshipItemStore;

    @Mock
    private TrackedEntityInstanceStore trackedEntityInstanceStore;

    private final String NEW_UID = "new-uid";

    private final String TEI_3_UID = "tei3";
    private final String TEI_4_UID = "tei4";

    private Relationship existingRelationship = get230();

    private Relationship existingRelationshipWithNewUid = get230().toBuilder().uid(NEW_UID).build();

    private Relationship newRelationship = get230(NEW_UID, TEI_3_UID, TEI_4_UID);


    // object to test
    private RelationshipHandlerImpl relationshipHandler;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        relationshipHandler = new RelationshipHandlerImpl(relationshipStore, relationshipItemStore, trackedEntityInstanceStore);
        when(trackedEntityInstanceStore.exists(FROM_UID)).thenReturn(true);
        when(trackedEntityInstanceStore.exists(TO_UID)).thenReturn(true);
        when(trackedEntityInstanceStore.exists(TEI_3_UID)).thenReturn(true);
        when(trackedEntityInstanceStore.exists(TEI_4_UID)).thenReturn(true);
        when(relationshipItemStore.getRelationshipsFromAndToTEI(FROM_UID, TO_UID)).thenReturn(Collections.singletonList(UID));
        when(relationshipItemStore.getRelationshipsFromAndToTEI(TEI_3_UID, TEI_4_UID)).thenReturn(Collections.<String>emptyList());
        when(relationshipStore.selectByUid(UID, Relationship.factory)).thenReturn(get230());
    }

    private RelationshipItemModel getItem(String uid, RelationshipConstraintType constraint, String teiUid) {
        return RelationshipItemModel.builder()
                .relationship(uid)
                .relationshipItemType(constraint)
                .trackedEntityInstance(teiUid)
                .build();
    }

    @Test(expected = RuntimeException.class)
    public void throw_exception_when_from_is_no_tei() {
        Relationship relationship = existingRelationship.toBuilder().from(eventItem).build();
        relationshipHandler.handle(relationship);
    }

    @Test(expected = RuntimeException.class)
    public void throw_exception_when_to_is_no_tei() {
        Relationship relationship = existingRelationship.toBuilder().to(eventItem).build();
        relationshipHandler.handle(relationship);
    }

    @Test(expected = RuntimeException.class)
    public void throw_exception_when_from_tei_not_in_db() {
        when(trackedEntityInstanceStore.exists(FROM_UID)).thenReturn(false);
        relationshipHandler.handle(existingRelationship);
    }

    @Test(expected = RuntimeException.class)
    public void throw_exception_when_to_tei_not_in_db() {
        when(trackedEntityInstanceStore.exists(TO_UID)).thenReturn(false);
        relationshipHandler.handle(existingRelationship);
    }

    @Test()
    public void call_relationship_item_store_for_existing_relationship() {
        relationshipHandler.handle(existingRelationship);
        verify(relationshipItemStore).getRelationshipsFromAndToTEI(FROM_UID, TO_UID);
    }

    @Test()
    public void call_relationship_store_for_existing_relationship() {
        relationshipHandler.handle(existingRelationship);
        verify(relationshipStore).selectByUid(UID, Relationship.factory);
    }

    @Test()
    public void not_delete_relationship_if_existing_id_matches() {
        relationshipHandler.handle(existingRelationship);
        verify(relationshipStore, never()).delete(UID);
    }

    @Test()
    public void not_call_delete_if_no_existing_relationship() {
        relationshipHandler.handle(newRelationship);
        verify(relationshipStore, never()).delete(UID);
    }

    @Test()
    public void delete_relationship_if_existing_id_doesnt_match() {
        relationshipHandler.handle(existingRelationshipWithNewUid);
        verify(relationshipStore).delete(UID);
    }

    @Test()
    public void update_relationship_store_for_existing_relationship() {
        relationshipHandler.handle(existingRelationship);
        verify(relationshipStore).updateOrInsert(existingRelationship);
    }

    @Test()
    public void update_relationship_store_for_existing_relationship_with_new_uid() {
        relationshipHandler.handle(existingRelationshipWithNewUid);
        verify(relationshipStore).updateOrInsert(existingRelationshipWithNewUid);
    }

    @Test()
    public void update_relationship_store_for_new_relationship() {
        relationshipHandler.handle(newRelationship);
        verify(relationshipStore).updateOrInsert(newRelationship);
    }

    @Test()
    public void update_relationship_item_store_for_existing_relationship() {
        relationshipHandler.handle(existingRelationship);
        verify(relationshipItemStore).updateOrInsertWhere(getItem(UID, FROM, FROM_UID));
        verify(relationshipItemStore).updateOrInsertWhere(getItem(UID, TO, TO_UID));
    }

    @Test()
    public void update_relationship_item_store_for_existing_relationship_with_new_uid() {
        relationshipHandler.handle(existingRelationshipWithNewUid);
        verify(relationshipItemStore).updateOrInsertWhere(getItem(NEW_UID, FROM, FROM_UID));
        verify(relationshipItemStore).updateOrInsertWhere(getItem(NEW_UID, TO, TO_UID));
    }

    @Test()
    public void update_relationship_item_store_for_new_relationship() {
        relationshipHandler.handle(newRelationship);
        verify(relationshipItemStore).updateOrInsertWhere(getItem(NEW_UID, FROM, TEI_3_UID));
        verify(relationshipItemStore).updateOrInsertWhere(getItem(NEW_UID, TO, TEI_4_UID));
    }
}

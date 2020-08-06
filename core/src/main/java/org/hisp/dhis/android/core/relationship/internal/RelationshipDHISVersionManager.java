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

package org.hisp.dhis.android.core.relationship.internal;

import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableDeletableDataObjectStore;
import org.hisp.dhis.android.core.arch.handlers.internal.Transformer;
import org.hisp.dhis.android.core.arch.helpers.UidGeneratorImpl;
import org.hisp.dhis.android.core.common.DeletableDataObject;
import org.hisp.dhis.android.core.common.ObjectWithUidInterface;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.enrollment.Enrollment;
import org.hisp.dhis.android.core.enrollment.EnrollmentInternalAccessor;
import org.hisp.dhis.android.core.enrollment.internal.EnrollmentStore;
import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.event.EventInternalAccessor;
import org.hisp.dhis.android.core.event.internal.EventStore;
import org.hisp.dhis.android.core.relationship.BaseRelationship;
import org.hisp.dhis.android.core.relationship.Relationship;
import org.hisp.dhis.android.core.relationship.RelationshipHelper;
import org.hisp.dhis.android.core.relationship.RelationshipItem;
import org.hisp.dhis.android.core.relationship.RelationshipItemTableInfo;
import org.hisp.dhis.android.core.systeminfo.DHISVersionManager;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceInternalAccessor;
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityInstanceStore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import dagger.Reusable;

import static org.hisp.dhis.android.core.arch.helpers.CollectionsHelper.isDeleted;

@Reusable
public class RelationshipDHISVersionManager {

    private final DHISVersionManager versionManager;
    private final TrackedEntityInstanceStore teiStore;
    private final EnrollmentStore enrollmentStore;
    private final EventStore eventStore;

    @Inject
    public RelationshipDHISVersionManager(DHISVersionManager versionManager,
                                          TrackedEntityInstanceStore teiStore,
                                          EnrollmentStore enrollmentStore,
                                          EventStore eventStore) {
        this.versionManager = versionManager;
        this.teiStore = teiStore;
        this.enrollmentStore = enrollmentStore;
        this.eventStore = eventStore;
    }

    public List<Relationship> getOwnedRelationships(List<Relationship> relationships, String teiUid) {
        List<Relationship> ownedRelationships = new ArrayList<>();
        for (Relationship relationship : relationships) {
            RelationshipItem fromTei = relationship.from();
            if (versionManager.is2_29() || fromTei != null && fromTei.trackedEntityInstance() != null &&
                    fromTei.trackedEntityInstance().trackedEntityInstance().equals(teiUid)) {
                ownedRelationships.add(relationship);
            }
        }
        return ownedRelationships;
    }

    public List<Relationship229Compatible> to229Compatible(List<Relationship> storedRelationships, String teiUid) {
        List<Relationship229Compatible> transformedRelationships = new ArrayList<>();
        for (Relationship relationship : storedRelationships) {
            transformedRelationships.add(to229Compatible(relationship, teiUid));
        }
        return transformedRelationships;
    }

    Relationship229Compatible to229Compatible(Relationship relationship, String teiUid) {
        Relationship229Compatible.Builder builder = Relationship229Compatible.builder()
                .id(relationship.id())
                .name(relationship.name())
                .created(relationship.created())
                .lastUpdated(relationship.lastUpdated())
                .state(relationship.state())
                .deleted(relationship.deleted());

        if (versionManager.is2_29()) {
            return builder
                    .uid(relationship.relationshipType())
                    .trackedEntityInstanceA(relationship.from().trackedEntityInstance().trackedEntityInstance())
                    .trackedEntityInstanceB(relationship.to().trackedEntityInstance().trackedEntityInstance())
                    .relative(getRelativeTEI230(relationship, teiUid))
                    .build();
        } else {
            return builder
                    .uid(relationship.uid())
                    .relationshipType(relationship.relationshipType())
                    .from(relationship.from())
                    .to(relationship.to())
                    .build();
        }
    }

    public Relationship from229Compatible(Relationship229Compatible relationship229Compatible) {
        Relationship.Builder builder = Relationship.builder()
                .name(relationship229Compatible.name())
                .created(relationship229Compatible.created())
                .lastUpdated(relationship229Compatible.lastUpdated())
                .state(relationship229Compatible.state())
                .deleted(relationship229Compatible.deleted());

        if (versionManager.is2_29()) {
            return builder
                    .uid(new UidGeneratorImpl().generate())
                    .relationshipType(relationship229Compatible.uid())
                    .from(RelationshipHelper.teiItem(relationship229Compatible.trackedEntityInstanceA()))
                    .to(RelationshipHelper.teiItem(relationship229Compatible.trackedEntityInstanceB()))
                    .build();
        } else {
            return builder
                    .uid(relationship229Compatible.uid())
                    .relationshipType(relationship229Compatible.relationshipType())
                    .from(relationship229Compatible.from())
                    .to(relationship229Compatible.to())
                    .build();
        }
    }

    public Collection<Relationship> from229Compatible(Collection<Relationship229Compatible> list) {
        List<Relationship> result = new ArrayList<>(list.size());
        for (Relationship229Compatible r : list) {
            result.add(from229Compatible(r));
        }
        return result;
    }

    public TrackedEntityInstance getRelativeTei(Relationship229Compatible relationship229Compatible, String teiUid) {
        if (versionManager.is2_29()) {
            return relationship229Compatible.relative();
        } else {
            return getRelativeTEI230(relationship229Compatible, teiUid);
        }
    }

    boolean isRelationshipSupported(BaseRelationship relationship) {
        return isItemSupported(relationship.from()) && isItemSupported(relationship.to());
    }

    private boolean isItemSupported(RelationshipItem item) {
        if (versionManager.is2_29()) {
            return item.hasTrackedEntityInstance();
        } else {
            return true;
        }
    }

    public TrackedEntityInstance getRelativeTEI230(BaseRelationship baseRelationship, String teiUid) {
        String fromTEIUid = RelationshipHelper.getTeiUid(baseRelationship.from());
        String toTEIUid = RelationshipHelper.getTeiUid(baseRelationship.to());

        if (fromTEIUid == null || toTEIUid == null) {
            return null;
        }

        String relatedTEIUid = teiUid.equals(fromTEIUid) ? toTEIUid : fromTEIUid;

        return TrackedEntityInstanceInternalAccessor.insertRelationships(
                TrackedEntityInstance.builder(), Collections.emptyList())
                .uid(relatedTEIUid)
                .deleted(false)
                .build();
    }

    public TrackedEntityInstance getRelativeTEI(RelationshipItem relationshipItem) {
        return TrackedEntityInstanceInternalAccessor.insertRelationships(
                TrackedEntityInstance.builder(), Collections.emptyList())
                .uid(relationshipItem.elementUid())
                .deleted(false)
                .build();
    }

    public Enrollment getRelativeEnrollment(RelationshipItem relationshipItem) {
        return EnrollmentInternalAccessor.insertRelationships(Enrollment.builder(), Collections.emptyList())
                .uid(relationshipItem.elementUid())
                .deleted(false)
                .build();
    }

    public Event getRelativeEvent(RelationshipItem relationshipItem) {
        return EventInternalAccessor.insertRelationships(Event.builder(), Collections.emptyList())
                .uid(relationshipItem.elementUid())
                .deleted(false)
                .build();
    }


    public RelationshipItem getRelatedRelationshipItem(BaseRelationship baseRelationship, String relationshipUid) {
        String fromUid = baseRelationship.from() == null ? null : baseRelationship.from().elementUid();
        String toUid = baseRelationship.to() == null ? null : baseRelationship.to().elementUid();

        if (fromUid == null || toUid == null) {
            return null;
        }

        return relationshipUid.equals(fromUid) ? baseRelationship.to() : baseRelationship.from();
    }

    public void createRelativesIfNotExist(Collection<Relationship> relationships) {
        for (BaseRelationship relationship : relationships) {
            RelationshipItem item = getRelatedRelationshipItem(relationship, relationship.uid());
            if (item != null) {
                switch (item.elementType()) {
                    case RelationshipItemTableInfo.Columns.TRACKED_ENTITY_INSTANCE:
                        TrackedEntityInstance relativeTEI = getRelativeTEI(item);
                        if (relativeTEI != null && !teiStore.exists(relativeTEI.uid())) {
                            handleObject(relativeTEI, trackedEntityInstanceTransformer(), teiStore);
                        }
                        break;
                    case RelationshipItemTableInfo.Columns.ENROLLMENT:
                        Enrollment relativeEnrollment = getRelativeEnrollment(item);
                        if (relativeEnrollment != null && !enrollmentStore.exists(relativeEnrollment.uid())) {
                            handleObject(relativeEnrollment, enrollmentTransformer(), enrollmentStore);
                        }
                        break;
                    case RelationshipItemTableInfo.Columns.EVENT:
                        Event relativeEvent = getRelativeEvent(item);
                        if (relativeEvent != null && !eventStore.exists(relativeEvent.uid())) {
                            handleObject(relativeEvent, eventTransformer(), eventStore);
                        }
                        break;
                }
            }
        }
    }

    private Transformer<TrackedEntityInstance, TrackedEntityInstance> trackedEntityInstanceTransformer() {
        return object -> {
            {
                State currentState = teiStore.getState(object.uid());
                if (currentState == State.RELATIONSHIP || currentState == null) {
                    return object.toBuilder().state(State.RELATIONSHIP).build();
                } else {
                    return object;
                }
            }
        };
    }

    private Transformer<Enrollment, Enrollment> enrollmentTransformer() {
        return object -> {
            {
                State currentState = enrollmentStore.getState(object.uid());
                if (currentState == State.RELATIONSHIP || currentState == null) {
                    return object.toBuilder().state(State.RELATIONSHIP).build();
                } else {
                    return object;
                }
            }
        };
    }

    private Transformer<Event, Event> eventTransformer() {
        return object -> {
            {
                State currentState = eventStore.getState(object.uid());
                if (currentState == State.RELATIONSHIP || currentState == null) {
                    return object.toBuilder().state(State.RELATIONSHIP).build();
                } else {
                    return object;
                }
            }
        };
    }

    private <O extends ObjectWithUidInterface & DeletableDataObject> void handleObject(
            O object, Transformer<O, O> transformer, IdentifiableDeletableDataObjectStore<O> store) {
        O oTransformed = transformer.transform(object);
        deleteOrPersist(oTransformed, store);
    }

    private <O extends ObjectWithUidInterface & DeletableDataObject> void deleteOrPersist(
            O o, IdentifiableDeletableDataObjectStore<O> store) {
        String modelUid = o.uid();
        if (isDeleted(o) && modelUid != null) {
            store.deleteIfExists(modelUid);
        } else {
            store.updateOrInsert(o);
        }
    }
}
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

package org.hisp.dhis.android.core.arch.handlers.internal;

import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder;
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableDataObjectStore;
import org.hisp.dhis.android.core.arch.helpers.UidsHelper;
import org.hisp.dhis.android.core.common.DataColumns;
import org.hisp.dhis.android.core.common.DeletableDataObject;
import org.hisp.dhis.android.core.common.IdentifiableColumns;
import org.hisp.dhis.android.core.common.ObjectWithUidInterface;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.relationship.Relationship;
import org.hisp.dhis.android.core.relationship.internal.RelationshipDHISVersionManager;
import org.hisp.dhis.android.core.relationship.internal.RelationshipHandler;
import org.hisp.dhis.android.core.relationship.internal.RelationshipItemRelatives;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.hisp.dhis.android.core.arch.helpers.CollectionsHelper.isDeleted;

public abstract class IdentifiableDataHandlerImpl<O extends DeletableDataObject & ObjectWithUidInterface>
         implements IdentifiableDataHandler<O> {

    final IdentifiableDataObjectStore<O> store;
    private final RelationshipDHISVersionManager relationshipVersionManager;
    private final RelationshipHandler relationshipHandler;

    public IdentifiableDataHandlerImpl(IdentifiableDataObjectStore<O> store,
                                       RelationshipDHISVersionManager relationshipVersionManager,
                                       RelationshipHandler relationshipHandler) {
        this.store = store;
        this.relationshipVersionManager = relationshipVersionManager;
        this.relationshipHandler = relationshipHandler;
    }

    protected void handle(O o, Transformer<O, O> transformer, List<O> oTransformedCollection, Boolean overwrite) {
        if (o == null) {
            return;
        }
        O oTransformed = handleInternal(o, transformer, overwrite);
        oTransformedCollection.add(oTransformed);
    }

    protected void handle(O o, Transformer<O, O> transformer, List<O> oTransformedCollection, Boolean overwrite,
                          RelationshipItemRelatives relatives) {
        if (o == null) {
            return;
        }
        O oTransformed = handleInternal(o, transformer, overwrite, relatives);
        oTransformedCollection.add(oTransformed);
    }

    private O handleInternal(O o, Transformer<O, O> transformer, Boolean overwrite) {
        O object = beforeObjectHandled(o, overwrite);
        O oTransformed = transformer.transform(object);
        HandleAction action = deleteOrPersist(oTransformed);
        afterObjectHandled(oTransformed, action, overwrite, null);
        return oTransformed;
    }

    private O handleInternal(O o, Transformer<O, O> transformer, Boolean overwrite,
                             RelationshipItemRelatives relatives) {
        O object = beforeObjectHandled(o, overwrite);
        O oTransformed = transformer.transform(object);
        HandleAction action = deleteOrPersist(oTransformed);
        afterObjectHandled(oTransformed, action, overwrite, relatives);
        return oTransformed;
    }

    @Override
    public final void handleMany(Collection<O> oCollection, Transformer<O, O> transformer, Boolean overwrite) {
        if (oCollection != null) {
            Collection<O> preHandledCollection = beforeCollectionHandled(oCollection, overwrite);
            List<O> oTransformedCollection = new ArrayList<>(oCollection.size());
            for (O o : preHandledCollection) {
                handle(o, transformer, oTransformedCollection, overwrite);
            }
            afterCollectionHandled(oTransformedCollection, overwrite);
        }
    }

    @Override
    public void handleMany(final Collection<O> oCollection, boolean asRelationship, boolean isFullUpdate,
                           boolean overwrite, RelationshipItemRelatives relatives) {
        if (oCollection == null) {
            return;
        }

        Transformer<O, O> transformer;
        if (asRelationship) {
            transformer = relationshipTransformer();
        } else {
            transformer = this::addSyncedState;
        }

        Collection<O> preHandledCollection = beforeCollectionHandled(oCollection, overwrite);

        List<O> transformedCollection = new ArrayList<>(preHandledCollection.size());

        for (O object : preHandledCollection) {

            handle(object, transformer, transformedCollection, overwrite, relatives);

            if (isFullUpdate) {
                deleteOrphans(object);
            }
        }

        afterCollectionHandled(transformedCollection, overwrite);
    }

    protected Transformer<O, O> relationshipTransformer() {
        return object -> {
            State currentState = store.getState(object.uid());
            if (currentState == State.RELATIONSHIP || currentState == null) {
                return addRelationshipState(object);
            } else {
                return object;
            }
        };
    }

    protected void handleRelationships(Collection<Relationship> relationships, ObjectWithUidInterface parent,
                                       RelationshipItemRelatives relatives) {
        if (relatives != null) {
            relationshipVersionManager.createRelativesIfNotExist(relationships, parent.uid(), relatives);
        }
        relationshipHandler.handleMany(relationships, relationship -> relationship.toBuilder()
                .state(State.SYNCED)
                .deleted(false)
                .build());
    }

    protected abstract O addRelationshipState(O object);

    protected abstract O addSyncedState(O object);

    protected abstract void deleteOrphans(O object);

    protected HandleAction deleteOrPersist(O o) {
        String modelUid = o.uid();
        if ((isDeleted(o) || deleteIfCondition(o)) && modelUid != null) {
            store.deleteIfExists(modelUid);
            return HandleAction.Delete;
        } else {
            return store.updateOrInsert(o);
        }
    }

    protected boolean deleteIfCondition(O o) {
        return false;
    }

    protected O beforeObjectHandled(O o, Boolean overwrite) {
        return o;
    }

    protected abstract void afterObjectHandled(O o, HandleAction action, Boolean overwrite,
                                               RelationshipItemRelatives relatives);

    protected Collection<O> beforeCollectionHandled(Collection<O> oCollection, Boolean overwrite) {
        if (overwrite) {
            return oCollection;
        } else {
            return removeExistingNotSyncedObjects(oCollection);
        }
    }

    @SuppressWarnings("PMD.EmptyMethodInAbstractClassShouldBeAbstract")
    protected void afterCollectionHandled(Collection<O> oCollection, Boolean overwrite) {
        /* Method is not abstract since empty action is the default action and we don't want it to
         * be unnecessarily written in every child.
         */
    }

    private Collection<O> removeExistingNotSyncedObjects(Collection<O> os) {
        List<String> storedObjectUids = storedObjectUids(os);
        List<String> syncedObjectUids = syncedObjectUids(storedObjectUids);

        List<O> objectsToStore = new ArrayList<>();
        for (O object : os) {
            if (!storedObjectUids.contains(object.uid()) || syncedObjectUids.contains(object.uid())
                    || isDeleted(object)) {
                objectsToStore.add(object);
            }
        }

        return objectsToStore;
    }

    private List<String> storedObjectUids(Collection<O> os) {
        List<String> objectUids = UidsHelper.getUidsList(os);

        String storedObjectUidsWhereClause = new WhereClauseBuilder()
                .appendInKeyStringValues(IdentifiableColumns.UID, objectUids).build();
        return store.selectUidsWhere(storedObjectUidsWhereClause);
    }

    private List<String> syncedObjectUids(List<String> storedObjectUids) {
        if (!storedObjectUids.isEmpty()) {
            String syncedObjectUidsWhereClause2 = new WhereClauseBuilder()
                    .appendInKeyStringValues(IdentifiableColumns.UID, storedObjectUids)
                    .appendInKeyStringValues(DataColumns.STATE,
                            Arrays.asList(State.SYNCED.name(), State.RELATIONSHIP.name(), State.SYNCED_VIA_SMS.name()))
                    .build();
            return store.selectUidsWhere(syncedObjectUidsWhereClause2);
        }

        return new ArrayList<>();
    }
}
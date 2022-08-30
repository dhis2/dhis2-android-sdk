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
package org.hisp.dhis.android.core.relationship.internal;

import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore;
import org.hisp.dhis.android.core.arch.handlers.internal.HandleAction;
import org.hisp.dhis.android.core.arch.handlers.internal.Handler;
import org.hisp.dhis.android.core.arch.handlers.internal.IdentifiableHandlerImpl;
import org.hisp.dhis.android.core.common.ObjectWithUid;
import org.hisp.dhis.android.core.relationship.RelationshipConstraint;
import org.hisp.dhis.android.core.relationship.RelationshipConstraintType;
import org.hisp.dhis.android.core.relationship.RelationshipType;

import javax.inject.Inject;

final class RelationshipTypeHandler extends IdentifiableHandlerImpl<RelationshipType> {

    private final Handler<RelationshipConstraint> relationshipConstraintHandler;

    @Inject
    RelationshipTypeHandler(
            IdentifiableObjectStore<RelationshipType> relationshipTypeStore,
            Handler<RelationshipConstraint> relationshipConstraintHandler) {
        super(relationshipTypeStore);
        this.relationshipConstraintHandler = relationshipConstraintHandler;
    }

    @Override
    protected void afterObjectHandled(RelationshipType relationshipType, HandleAction handleAction) {
        handleConstraint(relationshipType, relationshipType.fromConstraint(), RelationshipConstraintType.FROM);
        handleConstraint(relationshipType, relationshipType.toConstraint(), RelationshipConstraintType.TO);
    }

    private void handleConstraint(RelationshipType relationshipType, RelationshipConstraint downloadedConstraint,
                             RelationshipConstraintType type) {
        if (downloadedConstraint != null) {
            RelationshipConstraint fromConstraintToHanldle = downloadedConstraint.toBuilder()
                    .relationshipType(ObjectWithUid.fromIdentifiable(relationshipType))
                    .constraintType(type)
                    .build();

            this.relationshipConstraintHandler.handle(fromConstraintToHanldle);
        }
    }
}

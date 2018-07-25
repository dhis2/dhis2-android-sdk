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

import org.hisp.dhis.android.core.common.PojoBuilder;

import java.util.Set;

public class RelationshipTypeBuilder extends PojoBuilder<RelationshipType, RelationshipTypeModel> {

    private Set<RelationshipConstraintModel> constraints;

    RelationshipTypeBuilder(Set<RelationshipConstraintModel> constraints) {
        this.constraints = constraints;
    }

    @Override
    public RelationshipType buildPojo(RelationshipTypeModel model) {

        RelationshipConstraintBuilder relationshipConstraintBuilder = new RelationshipConstraintBuilder();
        RelationshipConstraint fromConstraint = null, toConstraint = null;

        for (RelationshipConstraintModel constraint : this.constraints) {
            if (constraint.relationshipType().equals(model.uid())) {
                if (constraint.constraintType().equals(RelationshipConstraintType.FROM)) {
                    fromConstraint = relationshipConstraintBuilder.buildPojo(constraint);
                } else if (constraint.constraintType().equals(RelationshipConstraintType.TO)) {
                    toConstraint = relationshipConstraintBuilder.buildPojo(constraint);
                }
            }
        }

        return RelationshipType.create(
                model.uid(),
                model.code(),
                model.name(),
                model.displayName(),
                model.created(),
                model.lastUpdated(),
                false,
                model.bIsToA(),
                model.aIsToB(),
                fromConstraint,
                toConstraint
        );
    }
}
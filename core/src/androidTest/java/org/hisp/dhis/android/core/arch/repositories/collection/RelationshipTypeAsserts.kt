/*
 *  Copyright (c) 2004-2023, University of Oslo
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
package org.hisp.dhis.android.core.arch.repositories.collection

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.BaseRealIntegrationTest
import org.hisp.dhis.android.core.relationship.RelationshipType

internal object RelationshipTypeAsserts : BaseRealIntegrationTest() {

    fun assertTypesWithoutConstraints(target: RelationshipType, reference: RelationshipType) {
        val prunedTarget = target.toBuilder()
            .id(null)
            .toConstraint(null)
            .fromConstraint(null)
            .build()

        val prunedReference = reference.toBuilder()
            .id(null)
            .toConstraint(null)
            .fromConstraint(null)
            .build()

        assertThat(prunedTarget).isEqualTo(prunedReference)
    }

    fun assertTypeWithConstraints(target: RelationshipType, reference: RelationshipType) {
        val prunedTarget = target.toBuilder()
            .id(null)
            .toConstraint(
                target.toConstraint()?.toBuilder()?.id(null)
                    ?.trackerDataView(
                        target.toConstraint()?.trackerDataView()?.toBuilder()?.id(null)?.build(),
                    )?.build(),
            )
            .fromConstraint(
                target.fromConstraint()?.toBuilder()?.id(null)
                    ?.trackerDataView(
                        target.fromConstraint()?.trackerDataView()?.toBuilder()?.id(null)?.build(),
                    )?.build(),
            )
            .build()

        val prunedReference = reference.toBuilder()
            .id(null)
            .toConstraint(
                reference.toConstraint()?.toBuilder()?.id(null)
                    ?.trackerDataView(
                        reference.toConstraint()?.trackerDataView()?.toBuilder()?.id(null)?.build(),
                    )?.build(),
            )
            .fromConstraint(
                reference.fromConstraint()?.toBuilder()?.id(null)
                    ?.trackerDataView(
                        reference.fromConstraint()?.trackerDataView()?.toBuilder()?.id(null)?.build(),
                    )?.build(),
            )
            .build()

        assertThat(prunedTarget).isEqualTo(prunedReference)
    }
}

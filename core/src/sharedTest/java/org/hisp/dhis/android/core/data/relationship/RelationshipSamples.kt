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

package org.hisp.dhis.android.core.data.relationship

import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.data.utils.FillPropertiesTestUtils.CREATED
import org.hisp.dhis.android.core.data.utils.FillPropertiesTestUtils.LAST_UPDATED
import org.hisp.dhis.android.core.data.utils.FillPropertiesTestUtils.NAME
import org.hisp.dhis.android.core.relationship.Relationship
import org.hisp.dhis.android.core.relationship.RelationshipHelper
import org.hisp.dhis.android.core.relationship.RelationshipItem

object RelationshipSamples {

    const val UID = "uid"

    const val FROM_UID = "fromUid"

    const val TO_UID = "toUid"

    const val TYPE = "type"

    @JvmField
    val fromItem: RelationshipItem = RelationshipHelper.teiItem(FROM_UID)

    @JvmField
    val toItem: RelationshipItem = RelationshipHelper.teiItem(TO_UID)

    @JvmField
    val eventItem: RelationshipItem = RelationshipHelper.eventItem(TO_UID)

    @JvmField
    val STATE: State = State.SYNCED

    const val DELETED = false

    private val commonBuilder: Relationship.Builder = Relationship
        .builder()
        .created(CREATED)
        .lastUpdated(LAST_UPDATED)
        .name(NAME)
        .syncState(STATE)
        .deleted(DELETED)

    @JvmStatic
    fun get230(): Relationship {
        return commonBuilder
            .uid(UID)
            .relationshipType(TYPE)
            .from(fromItem)
            .to(toItem)
            .build()
    }

    @JvmStatic
    fun get230(uid: String, fromUid: String, toUid: String): Relationship {
        return get230(uid, RelationshipHelper.teiItem(fromUid), RelationshipHelper.teiItem(toUid))
    }

    @JvmStatic
    fun get230(uid: String, from: RelationshipItem, to: RelationshipItem): Relationship {
        return commonBuilder
            .uid(uid)
            .relationshipType(TYPE)
            .from(from)
            .to(to)
            .build()
    }

    @JvmStatic
    fun getRelationshipToInsertOnDB(): Relationship {
        return commonBuilder
            .uid(UID)
            .relationshipType(TYPE)
            .build()
    }
}

/*
 *  Copyright (c) 2004-2025, University of Oslo
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
package org.hisp.dhis.android.core.scopedaccess

import org.hisp.dhis.android.core.organisationunit.OrganisationUnitMode

/**
 * The organisation unit restriction of a [D2DataScope].
 *
 * Unlike [UidScope] this needs a hierarchy mode, because granting an org unit almost always means
 * granting the sub-tree below it. [Only] with [OrganisationUnitMode.DESCENDANTS] is the usual
 * choice; resolution of the sub-tree is done by the SDK, not by the caller.
 */
sealed interface OrgUnitScope {

    /** No restriction — every organisation unit on the device is in scope. */
    data object All : OrgUnitScope

    /** No organisation unit is in scope. The default. */
    data object None : OrgUnitScope

    /** The organisation units the logged-in user can capture data in, and their descendants. */
    data object Capture : OrgUnitScope

    /**
     * Only [uids] are in scope, interpreted according to [mode].
     *
     * [OrganisationUnitMode.SELECTED] means exactly those units;
     * [OrganisationUnitMode.DESCENDANTS] (the default) means those units and everything beneath
     * them; [OrganisationUnitMode.CHILDREN] means those units and their immediate children.
     */
    data class Only(
        val uids: Set<String>,
        val mode: OrganisationUnitMode = OrganisationUnitMode.DESCENDANTS,
    ) : OrgUnitScope

    /** True if this scope permits nothing. */
    fun isEmpty(): Boolean = when (this) {
        is All, is Capture -> false
        is None -> true
        is Only -> uids.isEmpty()
    }

    companion object {
        /** [Only] over [uids] with the given [mode], or [None] when empty. */
        @JvmStatic
        @JvmOverloads
        fun of(
            uids: Collection<String>,
            mode: OrganisationUnitMode = OrganisationUnitMode.DESCENDANTS,
        ): OrgUnitScope = if (uids.isEmpty()) None else Only(uids.toSet(), mode)
    }
}

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
package org.hisp.dhis.android.core.arch.repositories.scope.internal

import org.hisp.dhis.android.core.maintenance.D2Error

/**
 * Vetoes writes that fall outside the scope a repository was created with.
 *
 * Read access is enforced by the filters in [RepositoryScope][
 * org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope]: those are append-only, so a
 * pre-narrowed repository can only ever be narrowed further. Writes have no such protection — the
 * write entry points hand a fully-formed object straight to the store, and the object carries its
 * own organisation unit, program or data element regardless of what the query said. This interface
 * is the missing check.
 *
 * A guard is carried *on the scope*, not on the repository, which is what makes it unforgeable:
 * `RepositoryScope` is copied field-by-field on every builder call, its generated setter for this
 * field is `internal`, and no API removes it. So the guard survives every `by*()`, `orderBy*()`,
 * `withChild()` and `uid()` in a fluent chain, and cannot be cleared by the code being restricted.
 *
 * Implementations live in `org.hisp.dhis.android.core.scopedaccess.internal`.
 */
internal interface AccessGuard {

    /**
     * Verifies that [target] may be written.
     *
     * @param target the object about to be inserted, updated or deleted.
     * @throws D2Error with [D2ErrorCode.SCOPE_VIOLATION][
     *     org.hisp.dhis.android.core.maintenance.D2ErrorCode.SCOPE_VIOLATION] if it may not.
     */
    @Throws(D2Error::class)
    suspend fun checkWrite(target: Any?)
}

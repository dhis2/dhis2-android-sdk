/*
 *  Copyright (c) 2004-2026, University of Oslo
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

package org.hisp.dhis.android.core.fileresource

import org.hisp.dhis.android.annotations.ModelBuilder
import org.hisp.dhis.android.core.common.DataObjectKt
import org.hisp.dhis.android.core.common.ObjectWithUidInterface
import org.hisp.dhis.android.core.common.State
import java.util.Date

@Suppress("TooManyFunctions")
@ModelBuilder
data class FileResource(
    val uid: String,
    val name: String?,
    val created: Date?,
    val lastUpdated: Date?,
    val contentType: String?,
    val contentLength: Long?,
    val path: String?,
    override val syncState: State?,
    val domain: FileResourceDomain?,
    internal val storageStatus: FileResourceStorageStatus?,
) : DataObjectKt, ObjectWithUidInterface {

    override fun uid(): String = uid
    fun name(): String? = name
    fun created(): Date? = created
    fun lastUpdated(): Date? = lastUpdated
    fun contentType(): String? = contentType
    fun contentLength(): Long? = contentLength
    fun path(): String? = path
    fun domain(): FileResourceDomain? = domain
    internal fun storageStatus(): FileResourceStorageStatus? = storageStatus

    @Deprecated("Use syncState() instead")
    override fun state(): State? = syncState

    fun toBuilder(): Builder = FileResourceBuilder.from(this)

    class Builder : FileResourceBuilder()

    companion object {
        @JvmStatic
        fun builder(): Builder = Builder()
    }
}

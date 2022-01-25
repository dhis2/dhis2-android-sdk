/*
 *  Copyright (c) 2004-2021, University of Oslo
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
package org.hisp.dhis.android.core.fileresource.internal

import javax.inject.Inject
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore
import org.hisp.dhis.android.core.dataelement.DataElement
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttribute
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValue

internal class FileResourceHelper @Inject constructor(
    private val dataElementStore: IdentifiableObjectStore<DataElement>,
    private val attributeStore: IdentifiableObjectStore<TrackedEntityAttribute>
) {

    fun isFileDataElement(dataElementUid: String?): Boolean {
        return dataElementUid?.let { dataElementStore.selectByUid(it)?.valueType()?.isFile } ?: false
    }

    fun isFileAttribute(attributeUid: String?): Boolean {
        return attributeUid?.let { attributeStore.selectByUid(it)?.valueType()?.isFile } ?: false
    }

    fun isPresentInDataValues(fileResourceUid: String, dataValues: Collection<TrackedEntityDataValue>?): Boolean {
        return dataValues?.any {
            fileResourceUid == it.value() && isFileDataElement(it.dataElement())
        } ?: false
    }

    fun isPresentInAttributeValues(
        fileResourceUid: String,
        attributeValues: Collection<TrackedEntityAttributeValue>?
    ): Boolean {
        return attributeValues?.any {
            fileResourceUid == it.value() && isFileAttribute(it.trackedEntityAttribute())
        } ?: false
    }
}

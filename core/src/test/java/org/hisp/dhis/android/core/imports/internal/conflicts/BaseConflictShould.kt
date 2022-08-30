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
package org.hisp.dhis.android.core.imports.internal.conflicts

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore
import org.hisp.dhis.android.core.dataelement.DataElement
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttribute
import org.junit.Before

internal open class BaseConflictShould {

    protected val context: TrackerImportConflictItemContext = mock()

    protected val attributeStore: IdentifiableObjectStore<TrackedEntityAttribute> = mock()
    protected val dataElementStore: IdentifiableObjectStore<DataElement> = mock()

    protected val attribute: TrackedEntityAttribute = mock()
    protected val dataElement: DataElement = mock()

    protected val attributeUid = "DI85uC13Bzo"
    protected val value = "attribute value"
    protected val optionSetUid = "Q2nhc0pmcZ8"

    protected val dataElementUid = "NTlMmRqGWCM"
    protected val eventUid = "ohAH6BXIMad"
    protected val enrollmentUid = "tliijiEnCp5"
    protected val relatedTeiUid = "QGyxOe1zewj"
    protected val teiUid = "9iKols8763J"
    protected val fileResourceUid = "V8co7IqOJsS"

    protected val relationshipUid = "AJOytZW7OaI"

    @Before
    fun setUp() {
        whenever(context.attributeStore) doReturn attributeStore
        whenever(context.dataElementStore) doReturn dataElementStore

        whenever(attributeStore.selectByUid(attributeUid)) doReturn attribute
        whenever(dataElementStore.selectByUid(dataElementUid)) doReturn dataElement
    }
}

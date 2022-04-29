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

package org.hisp.dhis.android.core.datavalue.internal.conflicts

import org.hisp.dhis.android.core.datavalue.DataValue
import org.hisp.dhis.android.core.datavalue.internal.conflicts.DataValueImportConflictSamples.indexedImportConflict
import org.hisp.dhis.android.core.imports.internal.ImportConflict
import org.hisp.dhis.android.core.sms.mockrepos.testobjects.MockObjects.getDataValues
import org.junit.Before
import org.junit.Test

internal class IndexedDataValueConflictShould {

    private lateinit var indexedDataValueConflict: IndexedDataValueConflict
    private val conflict: ImportConflict = indexedImportConflict()
    private val dataValues: List<DataValue> = getDataValues()

    @Before
    fun setUp() {
        indexedDataValueConflict = IndexedDataValueConflict()
    }

    @Test
    fun `Should get data value conflicts right`() {
        val result = indexedDataValueConflict.getDataValues(conflict, dataValues)
        assert(result.size == 2)
        assert(result[0].value() == "Replacement")
        assert(result[1].value() == "true")
    }

    @Test
    fun `Should return no data value conflicts`() {
        val result = indexedDataValueConflict.getDataValues(
            conflict.toBuilder().indexes(emptyList()).build(),
            dataValues
        )
        assert(result.isEmpty())
    }
}

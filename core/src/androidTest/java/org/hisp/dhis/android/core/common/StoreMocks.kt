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
package org.hisp.dhis.android.core.common

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore
import org.hisp.dhis.android.core.option.OptionSet
import java.util.Date

internal object StoreMocks {
    fun generateOptionSet(): OptionSet {
        return OptionSet.builder()
            .uid("1234567890")
            .code("code")
            .name("name")
            .displayName("displayName")
            .created(Date())
            .lastUpdated(Date())
            .version(1)
            .valueType(ValueType.AGE)
            .build()
    }

    fun generateUpdatedOptionSet(): OptionSet {
        return OptionSet.builder()
            .uid("1234567890")
            .code("updated_code")
            .name("name")
            .displayName("updated_displayName")
            .created(Date())
            .lastUpdated(Date())
            .version(2)
            .valueType(ValueType.AGE)
            .build()
    }

    fun generateOptionSetWithoutUid(): OptionSet {
        return OptionSet.builder()
            .uid(null)
            .code("code")
            .name("name")
            .displayName("displayName")
            .created(Date())
            .lastUpdated(Date())
            .version(1)
            .valueType(ValueType.AGE)
            .build()
    }

    suspend fun optionSetSelectAssert(store: IdentifiableObjectStore<OptionSet>, o: OptionSet) {
        val insertedOptionSet = store.selectAll()
        assertThat(insertedOptionSet).hasSize(1)
        assertThat(insertedOptionSet[0]).isEqualTo(o)
    }

    suspend fun assertIsEmpty(store: IdentifiableObjectStore<OptionSet>) {
        val insertedOptionSet = store.selectAll()
        assertThat(insertedOptionSet).isEmpty()
    }
}

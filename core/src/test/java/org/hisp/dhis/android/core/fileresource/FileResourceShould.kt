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
package org.hisp.dhis.android.core.fileresource

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.arch.helpers.DateUtils
import org.hisp.dhis.android.core.common.BaseObjectKotlinxShould
import org.hisp.dhis.android.core.common.ObjectShould
import org.hisp.dhis.android.network.fileresource.FileResourceDTO
import org.junit.Test

class FileResourceShould : BaseObjectKotlinxShould("fileresource/file_resource.json"), ObjectShould {

    @Test
    override fun map_from_json_string() {
        val fileResourceDTO = deserialize(FileResourceDTO.serializer())
        val fileResource = fileResourceDTO.toDomain()

        assertThat(fileResource.uid()).isEqualTo("SyPJ9weHqBM")
        assertThat(fileResource.name()).isEqualTo("doc.pdf")
        assertThat(fileResource.created()).isEqualTo(DateUtils.DATE_FORMAT.parse("2016-08-04T15:15:40.959"))
        assertThat(fileResource.lastUpdated()).isEqualTo(DateUtils.DATE_FORMAT.parse("2016-08-04T15:15:41.808"))
        assertThat(fileResource.contentType()).isEqualTo("application/pdf")
        assertThat(fileResource.contentLength()).isEqualTo(1238782)
        assertThat(fileResource.path()).isNull()
        assertThat(fileResource.storageStatus()).isEqualTo(FileResourceStorageStatus.PENDING)
        assertThat(fileResource.domain()).isEqualTo(FileResourceDomain.DATA_VALUE)
    }
}

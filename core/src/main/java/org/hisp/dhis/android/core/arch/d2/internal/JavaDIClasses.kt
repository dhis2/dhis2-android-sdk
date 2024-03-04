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

package org.hisp.dhis.android.core.arch.d2.internal

import org.hisp.dhis.android.core.arch.call.executors.internal.D2CallExecutor
import org.hisp.dhis.android.core.arch.db.access.internal.DatabaseAdapterFactory
import org.hisp.dhis.android.core.arch.db.access.internal.DatabaseExport
import org.hisp.dhis.android.core.configuration.internal.DatabaseEncryptionPasswordGenerator
import org.hisp.dhis.android.core.configuration.internal.DatabaseEncryptionPasswordManager
import org.hisp.dhis.android.core.maintenance.internal.ForeignKeyCleaner
import org.hisp.dhis.android.core.maintenance.internal.ForeignKeyCleanerImpl
import org.hisp.dhis.android.core.note.internal.NoteUniquenessManager
import org.hisp.dhis.android.core.period.internal.PeriodHelper
import org.hisp.dhis.android.core.period.internal.PeriodParser
import org.hisp.dhis.android.core.sms.data.localdbrepository.internal.DataSetsStore
import org.hisp.dhis.android.core.sms.data.localdbrepository.internal.FileResourceCleaner
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceService
import org.koin.dsl.module

internal val javaDIClasses = module {
    single<ForeignKeyCleaner> { ForeignKeyCleanerImpl(get(), get()) }
    single { FileResourceCleaner(get(), get(), get()) }
    single { NoteUniquenessManager(get()) }
    single { PeriodHelper(get(), get(), get(), get(), get()) }
    single { PeriodParser(get()) }
    single { DatabaseEncryptionPasswordManager(get(), get()) }
    single { DatabaseEncryptionPasswordGenerator() }
    single { TrackedEntityInstanceService(get(), get(), get(), get()) }
    single { DatabaseAdapterFactory(get(), get()) }
    single { D2CallExecutor(get(), get()) }
    single { DataSetsStore(get(), get(), get(), get()) }
}

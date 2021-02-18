package org.hisp.dhis.android.core.settings.internal

import dagger.Module
import dagger.Provides
import dagger.Reusable
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.db.stores.internal.ObjectWithoutUidStore
import org.hisp.dhis.android.core.arch.handlers.internal.Handler
import org.hisp.dhis.android.core.settings.CompletionSpinner

@Module
internal class CompletionSpinnerEntityDIModule {

    @Provides
    @Reusable
    fun store(databaseAdapter: DatabaseAdapter): ObjectWithoutUidStore<CompletionSpinner> {
        return CompletionSpinnerStore.create(databaseAdapter)
    }

    @Provides
    @Reusable
    fun handler(store: ObjectWithoutUidStore<CompletionSpinner>): Handler<CompletionSpinner> {
        return CompletionSpinnerHandler(store)
    }
}

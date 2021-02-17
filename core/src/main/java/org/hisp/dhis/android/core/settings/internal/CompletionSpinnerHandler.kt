package org.hisp.dhis.android.core.settings.internal

import org.hisp.dhis.android.core.arch.db.stores.internal.ObjectWithoutUidStore
import org.hisp.dhis.android.core.arch.handlers.internal.ObjectWithoutUidHandlerImpl
import org.hisp.dhis.android.core.settings.CompletionSpinner

internal class CompletionSpinnerHandler(store: ObjectWithoutUidStore<CompletionSpinner>) :
    ObjectWithoutUidHandlerImpl<CompletionSpinner>(store) {

    override fun beforeCollectionHandled(oCollection: Collection<CompletionSpinner>): Collection<CompletionSpinner> {
        store.delete()
        return oCollection
    }
}
package org.hisp.dhis.android.core.settings.internal

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import java.lang.Exception
import org.hisp.dhis.android.core.arch.db.stores.internal.ObjectWithoutUidStore
import org.hisp.dhis.android.core.arch.handlers.internal.HandleAction
import org.hisp.dhis.android.core.settings.CompletionSpinner
import org.junit.Before
import org.junit.Test

class CompletionSettingHandlerShould {
    private val completionSettingStore: ObjectWithoutUidStore<CompletionSpinner> = mock()

    private val completionSpinner = CompletionSpinner.builder()
        .visible(true)
        .build()
    private lateinit var completionSpinnerHandler: CompletionSpinnerHandler
    private lateinit var completionSpinnerList: List<CompletionSpinner>

    @Before
    @Throws(Exception::class)
    fun setUp() {
        completionSpinnerList = listOf(completionSpinner)
        whenever(completionSettingStore.updateOrInsertWhere(any())) doReturn HandleAction.Insert
        completionSpinnerHandler = CompletionSpinnerHandler(completionSettingStore)
    }

    @Test
    fun clean_database_before_insert_collection() {
        completionSpinnerHandler.handleMany(completionSpinnerList)
        verify(completionSettingStore).delete()
        verify(completionSettingStore).updateOrInsertWhere(completionSpinner)
    }

    @Test
    fun clean_database_if_empty_collection() {
        completionSpinnerHandler.handleMany(emptyList())
        verify(completionSettingStore).delete()
        verify(completionSettingStore, never()).updateOrInsertWhere(completionSpinner)
    }
}

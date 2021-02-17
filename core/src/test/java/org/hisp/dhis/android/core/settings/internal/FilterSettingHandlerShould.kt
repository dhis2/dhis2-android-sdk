package org.hisp.dhis.android.core.settings.internal

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.hisp.dhis.android.core.arch.db.stores.internal.ObjectWithoutUidStore
import org.hisp.dhis.android.core.arch.handlers.internal.HandleAction
import org.hisp.dhis.android.core.settings.FilterSetting
import org.junit.Before
import org.junit.Test

class FilterSettingHandlerShould {

    private val filterSettingStore: ObjectWithoutUidStore<FilterSetting> = mock()

    private val filterSetting: FilterSetting = FilterSetting.builder().build()
    private lateinit var filterSettingsHandler: FilterSettingHandler
    private lateinit var filterSettingsList: List<FilterSetting>

    @Before
    @Throws(Exception::class)
    fun setUp() {
        filterSettingsList = listOf(filterSetting)
        whenever(filterSettingStore.updateOrInsertWhere(any())) doReturn HandleAction.Insert
        filterSettingsHandler = FilterSettingHandler(filterSettingStore)
    }

    @Test
    fun clean_database_before_insert_collection() {
        filterSettingsHandler.handleMany(filterSettingsList)
        verify(filterSettingStore).delete()
        verify(filterSettingStore).updateOrInsertWhere(filterSetting)
    }

    @Test
    fun clean_database_if_empty_collection() {
        filterSettingsHandler.handleMany(emptyList())
        verify(filterSettingStore).delete()
        verify(filterSettingStore, never()).updateOrInsertWhere(filterSetting)
    }
}
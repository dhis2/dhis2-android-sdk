package org.hisp.dhis.android.core.settings.internal

import org.hisp.dhis.android.core.data.database.ObjectStoreAbstractIntegrationShould
import org.hisp.dhis.android.core.data.settings.FilterSettingSamples
import org.hisp.dhis.android.core.settings.FilterSetting
import org.hisp.dhis.android.core.settings.FilterSettingTableInfo
import org.hisp.dhis.android.core.utils.integration.mock.TestDatabaseAdapterFactory
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner
import org.junit.runner.RunWith

@RunWith(D2JunitRunner::class)
class FilterSettingStoreIntegrationShould : ObjectStoreAbstractIntegrationShould<FilterSetting>(
    FilterSettingStore.create(TestDatabaseAdapterFactory.get()),
    FilterSettingTableInfo.TABLE_INFO,
    TestDatabaseAdapterFactory.get()
) {
    override fun buildObject(): FilterSetting {
        return FilterSettingSamples.getFilterSetting()
    }
}

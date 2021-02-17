package org.hisp.dhis.android.testapp.settings;

import org.hisp.dhis.android.core.settings.AppearanceSettings;
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestFullDispatcher;
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.google.common.truth.Truth.assertThat;

@RunWith(D2JunitRunner.class)
public class AppearanceSettingsObjectRepositoryMockIntegrationShould extends BaseMockIntegrationTestFullDispatcher {

    @Test
    public void find_appearance_settings() {
        AppearanceSettings appearanceSettings = d2.settingModule().appearanceSettings().blockingGet();

        assertThat(appearanceSettings.filterSorting()).isNotNull();
        assertThat(appearanceSettings.completionSpinner()).isNotNull();
    }
}

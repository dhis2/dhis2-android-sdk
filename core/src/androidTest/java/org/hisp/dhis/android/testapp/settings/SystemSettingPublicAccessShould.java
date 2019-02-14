package org.hisp.dhis.android.testapp.settings;

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.settings.SystemSetting;
import org.hisp.dhis.android.testapp.arch.BasePublicAccessShould;
import org.junit.runner.RunWith;
import org.mockito.Mock;

@RunWith(AndroidJUnit4.class)
public class SystemSettingPublicAccessShould extends BasePublicAccessShould<SystemSetting> {

    @Mock
    private SystemSetting object;

    @Override
    public SystemSetting object() {
        return object;
    }

    @Override
    public void has_public_create_method() {
        SystemSetting.create(null);
    }

    @Override
    public void has_public_builder_method() {
        SystemSetting.builder();
    }

    @Override
    public void has_public_to_builder_method() {
        object().toBuilder();
    }
}
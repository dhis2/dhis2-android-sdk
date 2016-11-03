package org.hisp.dhis.client.sdk.ui.bindings.presenters;

import org.hisp.dhis.client.sdk.ui.AppPreferences;
import org.hisp.dhis.client.sdk.ui.bindings.commons.DefaultAppAccountManager;
import org.hisp.dhis.client.sdk.ui.bindings.util.SystemSettings;
import org.hisp.dhis.client.sdk.ui.bindings.views.SettingsView;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest(SystemSettings.class)
public class SettingsPresenterTest {

    @Mock
    private AppPreferences appPreferences;

    @Mock
    private DefaultAppAccountManager appAccountManager;

    @Mock
    private SettingsView settingsView;

    private SettingsPresenter settingsPresenter;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        settingsPresenter = new SettingsPresenterImpl(appPreferences, appAccountManager);
        settingsPresenter.attachView(settingsView);
    }

    @Test
    public void synchronize() throws Exception {
        settingsPresenter.synchronize();
        verify(appAccountManager).syncNow();
    }

    @Test
    public void setUpdateFrequency() throws Exception {
        int syncFrequency = 8;
        settingsPresenter.setUpdateFrequency(syncFrequency);

        verify(appPreferences).setBackgroundSyncFrequency(syncFrequency);
        verify(appAccountManager).setPeriodicSync(syncFrequency);
    }

    @Test
    public void getUpdateFrequency() throws Exception {
        settingsPresenter.getUpdateFrequency();
        verify(appPreferences).getBackgroundSyncFrequency();
    }

    @Test
    public void setBackgroundSynchronisation() throws Exception {
        int syncFrequency = 8;
        Boolean syncEnabled = true;

        when(appPreferences.getBackgroundSyncFrequency()).thenReturn(syncFrequency);

        settingsPresenter.setBackgroundSynchronisation(syncEnabled, null);

        verify(appPreferences).setBackgroundSyncState(syncEnabled);
        verify(appAccountManager).syncNow();
        verify(appAccountManager).setPeriodicSync(syncFrequency);
    }

    @Test
    public void showWarningWhenMasterSyncIsOff() throws Exception {
        String warning = "warning";
        mockStatic(SystemSettings.class);

        when(SystemSettings.getMasterSyncAutomatically()).thenReturn(false);
        settingsPresenter.setBackgroundSynchronisation(true, warning);
        verify(settingsView).showMessage(warning);
    }

    @Test
    public void removeBackgroundSynchronisation() throws Exception {
        settingsPresenter.setBackgroundSynchronisation(false, "");
        verify(appAccountManager).removePeriodicSync();
    }

    @Test
    public void getBackgroundSynchronisation() throws Exception {
        settingsPresenter.getBackgroundSynchronisation();
        verify(appPreferences).getBackgroundSyncState();
    }

    @Test
    public void getCrashReports() throws Exception {
        settingsPresenter.getCrashReports();
        verify(appPreferences).getCrashReportsState();
    }

    @Test
    public void setCrashReports() throws Exception {
        boolean crashReportsEnabled = true;
        settingsPresenter.setCrashReports(crashReportsEnabled);
        verify(appPreferences).setCrashReportsState(crashReportsEnabled);
    }

    @Test
    public void setSyncNotifications() throws Exception {
        boolean syncNotificationsEnabled = true;
        settingsPresenter.setSyncNotifications(syncNotificationsEnabled);
        verify(appPreferences).setSyncNotifications(syncNotificationsEnabled);
    }
}
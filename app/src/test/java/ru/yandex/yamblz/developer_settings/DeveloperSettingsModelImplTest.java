package ru.yandex.yamblz.developer_settings;

import ru.yandex.yamblz.App;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DeveloperSettingsModelImplTest {
    private DeveloperSettingsModelImpl developerSettingsModel;
    private DeveloperSettings developerSettings;

    @Before
    public void beforeEachTest() {
        developerSettings = mock(DeveloperSettings.class);

        developerSettingsModel = new DeveloperSettingsModelImpl(
                mock(App.class),
                developerSettings,
                mock(LeakCanaryProxy.class)
        );
    }

    @Test
    public void getBuildVersionCode_shouldNotBeNull() {
        assertThat(developerSettingsModel.getBuildVersionCode()).isNotNull();
    }

    @Test
    public void getBuildVersionCode_shouldReturnSameResultForSeveralCalls() {
        String buildVersionCode1 = developerSettingsModel.getBuildVersionCode();
        String buildVersionCode2 = developerSettingsModel.getBuildVersionCode();
        String buildVersionCode3 = developerSettingsModel.getBuildVersionCode();

        assertThat(buildVersionCode1).isEqualTo(buildVersionCode2).isEqualTo(buildVersionCode3);
    }

    @Test
    public void getBuildVersionName_shouldNotBeNull() {
        assertThat(developerSettingsModel.getBuildVersionName()).isNotNull();
    }

    @Test
    public void getBuildVersionName_shouldReturnSameResultForSeveralCalls() {
        String buildVersionName1 = developerSettingsModel.getBuildVersionName();
        String buildVersionName2 = developerSettingsModel.getBuildVersionName();
        String buildVersionName3 = developerSettingsModel.getBuildVersionName();

        assertThat(buildVersionName1).isEqualTo(buildVersionName2).isEqualTo(buildVersionName3);
    }

    @Test
    public void isStethoEnabled_shouldReturnValueFromDeveloperSettings() {
        when(developerSettings.isStethoEnabled()).thenReturn(true);
        assertThat(developerSettingsModel.isStethoEnabled()).isTrue();
        verify(developerSettings).isStethoEnabled();

        when(developerSettings.isStethoEnabled()).thenReturn(false);
        assertThat(developerSettingsModel.isStethoEnabled()).isFalse();
        verify(developerSettings, times(2)).isStethoEnabled();
    }

    @Test
    public void isLeakCanaryEnabled_shouldReturnValueFromDeveloperSettings() {
        when(developerSettings.isLeakCanaryEnabled()).thenReturn(true);
        assertThat(developerSettingsModel.isLeakCanaryEnabled()).isTrue();
        verify(developerSettings).isLeakCanaryEnabled();

        when(developerSettings.isLeakCanaryEnabled()).thenReturn(false);
        assertThat(developerSettingsModel.isLeakCanaryEnabled()).isFalse();
        verify(developerSettings, times(2)).isLeakCanaryEnabled();
    }

    @Test
    public void isTinyDancerEnabled_shouldReturnValueFromDeveloperSettings() {
        when(developerSettings.isTinyDancerEnabled()).thenReturn(true);
        assertThat(developerSettingsModel.isTinyDancerEnabled()).isTrue();
        verify(developerSettings).isTinyDancerEnabled();

        when(developerSettings.isTinyDancerEnabled()).thenReturn(false);
        assertThat(developerSettingsModel.isTinyDancerEnabled()).isFalse();
        verify(developerSettings, times(2)).isTinyDancerEnabled();
    }

    // To test apply() method we will need a lof of abstractions over the libraries used
    // for Developer Settings, because most of them initialized statically and hardly mockable/verifiable :(
    // So, sorry, no tests for apply(). But, feel free to PR!
}
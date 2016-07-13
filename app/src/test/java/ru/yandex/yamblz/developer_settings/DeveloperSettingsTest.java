package ru.yandex.yamblz.developer_settings;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RuntimeEnvironment;

import ru.yandex.yamblz.YamblzRobolectricUnitTestRunner;

import static android.content.Context.MODE_PRIVATE;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(YamblzRobolectricUnitTestRunner.class)
public class DeveloperSettingsTest {

    private DeveloperSettings developerSettings;

    @Before
    public void beforeEachTest() {
        developerSettings = new DeveloperSettings(RuntimeEnvironment.application.getSharedPreferences("developer_settings", MODE_PRIVATE));
    }

    @Test
    public void isStethoEnabled_shouldReturnFalseByDefault() {
        assertThat(developerSettings.isStethoEnabled()).isFalse();
    }

    @Test
    public void saveIsStethoEnabled_isStethoEnabled() {
        developerSettings.saveIsStethoEnabled(true);
        assertThat(developerSettings.isStethoEnabled()).isTrue();

        developerSettings.saveIsStethoEnabled(false);
        assertThat(developerSettings.isStethoEnabled()).isFalse();
    }

    @Test
    public void isLeakCanaryEnabled_shouldReturnFalseByDefault() {
        assertThat(developerSettings.isLeakCanaryEnabled()).isFalse();
    }

    @Test
    public void saveIsLeakCanaryEnabled_isLeakCanaryEnabled() {
        developerSettings.saveIsLeakCanaryEnabled(true);
        assertThat(developerSettings.isLeakCanaryEnabled()).isTrue();

        developerSettings.saveIsLeakCanaryEnabled(false);
        assertThat(developerSettings.isLeakCanaryEnabled()).isFalse();
    }

    @Test
    public void isTinyDancerEnabled_shouldReturnFalseByDefault() {
        assertThat(developerSettings.isTinyDancerEnabled()).isFalse();
    }

    @Test
    public void saveIsTinyDancerEnabled_isTinyDancerEnabled() {
        developerSettings.saveIsTinyDancerEnabled(true);
        assertThat(developerSettings.isTinyDancerEnabled()).isTrue();

        developerSettings.saveIsTinyDancerEnabled(false);
        assertThat(developerSettings.isTinyDancerEnabled()).isFalse();
    }

}
package ru.yandex.yamblz.developer_settings;

import android.support.annotation.NonNull;

import dagger.Subcomponent;
import ru.yandex.yamblz.ui.fragments.DeveloperSettingsFragment;

@Subcomponent
public interface DeveloperSettingsComponent {
    void inject(@NonNull DeveloperSettingsFragment developerSettingsFragment);
}

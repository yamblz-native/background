package ru.yandex.yamblz.developer_settings;

import android.support.annotation.NonNull;

import ru.yandex.yamblz.ui.fragments.DeveloperSettingsFragment;

import dagger.Subcomponent;

@Subcomponent
public interface DeveloperSettingsComponent {
    void inject(@NonNull DeveloperSettingsFragment developerSettingsFragment);
}

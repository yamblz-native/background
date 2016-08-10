package ru.yandex.yamblz.ui.presenters;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by dalexiv on 8/9/16.
 */

@Module
public class ModulePresenter {
    @Provides
    @Singleton
    GenresPresenter provideGenrePresenter() {
        return new GenresPresenter();
    }

}

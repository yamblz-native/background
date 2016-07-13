package ru.yandex.yamblz.ui.presenters;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Base presenter implementation.
 *
 * @param <V> view.
 */
public class Presenter<V> {

    @Nullable
    private volatile V view;

    @CallSuper
    public void bindView(@NonNull V view) {
        final V previousView = this.view;

        if (previousView != null) {
            throw new IllegalStateException("Previous view is not unbounded! previousView = " + previousView);
        }

        this.view = view;
    }

    @Nullable
    protected V view() {
        return view;
    }

    @CallSuper
    @SuppressWarnings("PMD.CompareObjectsWithEquals")
    public void unbindView(@NonNull V view) {
        final V previousView = this.view;

        if (previousView == view) {
            this.view = null;
        } else {
            throw new IllegalStateException("Unexpected view! previousView = " + previousView + ", view to unbind = " + view);
        }
    }
}

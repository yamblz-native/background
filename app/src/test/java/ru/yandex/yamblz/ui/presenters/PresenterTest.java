package ru.yandex.yamblz.ui.presenters;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;

public class PresenterTest {
    private Presenter<Object> presenter;
    private Object view;

    @Before
    public void beforeEachTest() {
        view = new Object();
        presenter = new Presenter<>();
    }

    @Test
    public void bindView_shouldAttachViewToThePresenter() {
        presenter.bindView(view);
        assertThat(presenter.view()).isSameAs(view);
    }

    @Test
    public void bindView_shouldThrowIfPreviousViewIsNotUnbounded() {
        presenter.bindView(view);

        try {
            presenter.bindView(new Object());
            failBecauseExceptionWasNotThrown(IllegalStateException.class);
        } catch (IllegalStateException expected) {
            assertThat(expected).hasMessage("Previous view is not unbounded! previousView = " + view);
        }
    }

    @Test
    public void view_shouldReturnNullByDefault() {
        assertThat(presenter.view()).isNull();
    }

    @Test
    public void unbindView_shouldNullTheViewReference() {
        presenter.bindView(view);
        assertThat(presenter.view()).isSameAs(view);

        presenter.unbindView(view);
        assertThat(presenter.view()).isNull();
    }

    @Test
    public void unbindView_shouldThrowIfPreviousViewIsNotSameAsExpected() {
        presenter.bindView(view);
        Object unexpectedView = new Object();

        try {
            presenter.unbindView(unexpectedView);
            failBecauseExceptionWasNotThrown(IllegalStateException.class);
        } catch (IllegalStateException expected) {
            assertThat(expected).hasMessage("Unexpected view! previousView = " + view + ", view to unbind = " + unexpectedView);
        }
    }
}
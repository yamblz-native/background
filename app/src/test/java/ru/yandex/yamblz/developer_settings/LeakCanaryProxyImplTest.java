package ru.yandex.yamblz.developer_settings;

import org.junit.Test;

import ru.yandex.yamblz.App;

import static org.mockito.Mockito.mock;

public class LeakCanaryProxyImplTest {

    // Unfortunately, we can not really test init method since launching LeakCanary in the tests is not a great idea.

    @Test
    public void watch_shouldNoOpIfInitWasNotCalledCaseWhenLeakCanaryDisabled() {
        LeakCanaryProxy leakCanaryProxy = new LeakCanaryProxyImpl(mock(App.class));
        leakCanaryProxy.watch(new Object()); // No exceptions expected.
    }
}
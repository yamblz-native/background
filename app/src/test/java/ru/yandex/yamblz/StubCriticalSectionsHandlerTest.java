package ru.yandex.yamblz;

import org.junit.Before;
import org.junit.Test;

import ru.yandex.yamblz.handler.StubCriticalSectionsHandler;
import ru.yandex.yamblz.handler.Task;
import ru.yandex.yamblz.handler.UIThreadExecutor;

import static org.junit.Assert.*;


public class StubCriticalSectionsHandlerTest {
    private StubCriticalSectionsHandler handler;
    private UIThreadExecutor uiThreadExecutor = Task::run;

    @Before
    public void init() {
        handler = new StubCriticalSectionsHandler(uiThreadExecutor);
    }

    @Test
    public void simpleTasksExecution() {

        handler.postLowPriorityTask(createAwaitTask("1", 1000));
        handler.postLowPriorityTask(createAwaitTask("2", 1000));
        handler.postLowPriorityTask(createAwaitTask("3", 1000));

        assertEquals(0, handler.lowPriorityTasksCount());

    }

    @Test
    public void simpleCriticalSectionAwait() throws InterruptedException {
        int sectionId = 10;
        handler.startSection(sectionId);

        handler.postLowPriorityTask(createAwaitTask("1", 1000));
        handler.postLowPriorityTask(createAwaitTask("2", 1000));
        handler.postLowPriorityTask(createAwaitTask("3", 1000));

        Thread.sleep(5000);

        assertEquals(3, handler.lowPriorityTasksCount());
        handler.stopSection(sectionId);
        assertEquals(0, handler.lowPriorityTasksCount());

    }

    @Test
    public void uniqueCriticalSectionId(){
        int sectionId = 12;
        handler.startSection(sectionId);
        handler.startSection(sectionId);
        assertEquals(1, handler.criticalSectionsCount());
    }


    @Test
    public void removeAllLowPriority() {

        postInNewThread(createAwaitTask("1", 1000));
        postInNewThread(createAwaitTask("2", 1000));
        postInNewThread(createAwaitTask("3", 1000));

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        handler.removeLowPriorityTasks();
        assertEquals(0, handler.lowPriorityTasksCount());

    }


    @Test
    public void removeLowPriority() throws InterruptedException {
        Task toRemove = createAwaitTask("3", 5000);

        postInNewThread(createAwaitTask("1", 2000));
        postInNewThread(createAwaitTask("2", 1000));
        postInNewThread(toRemove);

        Thread.sleep(1000);

        handler.removeLowPriorityTask(toRemove);
        assertEquals(2, handler.lowPriorityTasksCount());
    }

    @Test
    public void stopSections() throws InterruptedException {
        handler.startSection(123);
        handler.startSection(12323);
        handler.startSection(43);

        handler.postLowPriorityTask(createAwaitTask("1", 1000));
        handler.postLowPriorityTask(createAwaitTask("2", 1000));
        handler.postLowPriorityTask(createAwaitTask("3", 1000));

        Thread.sleep(5000);
        assertEquals(3, handler.lowPriorityTasksCount());
        handler.stopSections();
        assertEquals(0, handler.lowPriorityTasksCount());
    }

    @Test
    public void postDelayed() throws InterruptedException {
        handler.postLowPriorityTaskDelayed(createAwaitTask("1",3000), 1000);
        assertEquals(0, handler.lowPriorityTasksCount());

        Thread.sleep(2000);
        assertEquals(1,handler.lowPriorityTasksCount());
    }

    private Task createAwaitTask(String name, int delay) {
        return () -> {
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };
    }

    private void postInNewThread(Task task){
        new Thread(()-> handler.postLowPriorityTask(task)).start();
    }
}
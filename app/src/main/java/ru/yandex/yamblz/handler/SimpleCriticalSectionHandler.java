package ru.yandex.yamblz.handler;

import android.os.Handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by platon on 29.07.2016.
 */
public class SimpleCriticalSectionHandler implements CriticalSectionsHandler
{
    private final List<Task> tasks;
    private final Set<Integer> sections;
    private final Handler mainHandler;

    public SimpleCriticalSectionHandler(Handler handler)
    {
        mainHandler = handler;
        tasks = Collections.synchronizedList(new ArrayList<>());
        sections = new TreeSet<>();
    }

    @Override
    public void startSection(int id)
    {
        sections.add(id);
    }

    @Override
    public void stopSection(int id)
    {
        sections.remove(id);

        for (Task task : tasks)
        {
            mainHandler.post(task::run);
        }

        removeLowPriorityTasks();
    }

    @Override
    public void stopSections()
    {
        sections.clear();

        for (Task task : tasks)
        {
            mainHandler.post(task::run);
        }

        removeLowPriorityTasks();
    }

    @Override
    public void postLowPriorityTask(Task task)
    {
        if (sections.isEmpty())
        {
            mainHandler.post(task::run);
        }
        else
        {
            tasks.add(task);
        }
    }

    @Override
    public void postLowPriorityTaskDelayed(Task task, int delay)
    {
        mainHandler.postDelayed(() -> postLowPriorityTask(task), delay);
    }

    @Override
    public void removeLowPriorityTask(Task task)
    {
        Iterator<Task> iterator = tasks.iterator();
        while (iterator.hasNext())
        {
            if (iterator.next().equals(task)) iterator.remove();
            break;
        }
    }

    @Override
    public void removeLowPriorityTasks()
    {
        tasks.clear();
    }
}

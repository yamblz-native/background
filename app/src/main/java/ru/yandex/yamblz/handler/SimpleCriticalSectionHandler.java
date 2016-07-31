package ru.yandex.yamblz.handler;

import android.os.Handler;

import java.util.ArrayList;
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
    private final ConcurrentHashMap<Integer, List<Task>> queue;
    private final Set<Integer> sections;
    private final Handler mainHandler;

    public SimpleCriticalSectionHandler(Handler handler)
    {
        mainHandler = handler;
        queue = new ConcurrentHashMap<>();
        sections = new TreeSet<>();
    }

    @Override
    public void startSection(int id)
    {
        sections.add(id);
        Iterator<Entry<Integer, List<Task>>> entryIterator = queue.entrySet().iterator();

        while (entryIterator.hasNext())
        {
            Entry<Integer, List<Task>> entry = entryIterator.next();
            if (entry.getKey().equals(id))
            {
                entryIterator.remove();
            }
        }
    }

    @Override
    public void stopSection(int id)
    {
        sections.remove(id);
        Iterator<Entry<Integer, List<Task>>> entryIterator = queue.entrySet().iterator();

        while (entryIterator.hasNext())
        {
            Entry<Integer, List<Task>> entry = entryIterator.next();
            if (entry.getKey().equals(id))
            {
                for (Task task : entry.getValue())
                {
                    task.run();
                }
                entryIterator.remove();
            }
        }
    }

    @Override
    public void stopSections()
    {
        sections.clear();

        for (Entry<Integer, List<Task>> entry : queue.entrySet())
        {
            for (Task task : entry.getValue())
            {
                task.run();
            }
        }

        queue.clear();
    }

    @Override
    public void postLowPriorityTask(Task task)
    {
        if (sections.isEmpty() || !sections.contains(task.getId()))
        {
            mainHandler.post(task::run);
        }
        else
        {
            int taskId = task.getId();
            if (queue.containsKey(taskId))
            {
                queue.get(taskId).add(task);
            }
            else
            {
                List<Task> tasks = new ArrayList<>();
                tasks.add(task);
                queue.put(taskId, tasks);
            }
        }
    }

    @Override
    public void postLowPriorityTaskDelayed(Task task, int delay) {}

    @Override
    public void removeLowPriorityTask(Task task)
    {
        queue.get(task.getId()).remove(task);
    }

    @Override
    public void removeLowPriorityTasks()
    {
        queue.clear();
    }
}

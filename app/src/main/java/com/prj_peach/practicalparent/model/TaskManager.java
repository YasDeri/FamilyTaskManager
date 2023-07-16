package com.prj_peach.practicalparent.model;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class TaskManager implements Iterable<Task>{
    private static final String PREFS_KEY = "tasks";
    private List<Task> collection;

    private static TaskManager instance;
    private Gson gson = new Gson();

    private TaskManager(){
        collection = new ArrayList<Task>();
    }

    public static TaskManager getInstance(){
        if (instance == null) {
            instance = new TaskManager();
        }
        return instance;
    }

    public void add(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("Provided task cannot be null");
        }
        collection.add(task);
    }

    public Task get(int i) {
        return collection.get(i);
    }

    public void delete(int i) {
        collection.remove(i);
    }

    public int size() {
        return collection.size();
    }


    @NonNull
    @Override
    public Iterator<Task> iterator() {
        return collection.iterator();
    }

    public static boolean hasInstance() {
        return instance != null;
    }

    public void saveTask(Context context){
        String jsonString = gson.toJson(collection);
        PrefsManager.saveString(context, PREFS_KEY, jsonString);
    }

    public void loadTask(Context context){
        String value = PrefsManager.loadString(context, PREFS_KEY);
        collection = new ArrayList<>();
        if (value == null || value.equals("")) {
            return;
        }
        List<Task> tasks = gson.fromJson(value, new TypeToken<List<Task>>() {
        }.getType());
        for (Task task : tasks) {
            collection.add(task);
        }
    }
}

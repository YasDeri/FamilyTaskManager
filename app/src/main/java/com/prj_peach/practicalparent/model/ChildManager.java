package com.prj_peach.practicalparent.model;

import android.content.Context;
import android.content.res.Resources;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.prj_peach.practicalparent.R;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class ChildManager implements Iterable<Child> {

    private static final Child nobody  = new Child("Deleted Child");
    private static final String PREFS_KEY = "children";

    public List<Child> collection;

    private static ChildManager instance;
    private Gson gson = new Gson();

    public ChildManager(){
        collection = new ArrayList<>();
    }

    public static ChildManager getInstance() {
        if (instance == null) {
            instance = new ChildManager();
        }
        return instance;
    }

    public Child getChildByID(UUID ID) {
        Child selectedChild = null;
        for(Child child : collection)
        {
            if (child.getID().equals(ID))
            {
                selectedChild = child;
            }
        }
        if (selectedChild == null){
            selectedChild = nobody;
        }
        return selectedChild;
    }

    public void add(Child child) {
        if (child == null) {
            throw new IllegalArgumentException("Provided child cannot be null");
        }

        collection.add(child);
    }

    public Child get(int i) {
        if (i >= 0 && i < collection.size()) {
            return collection.get(i);
        }
        return nobody;
    }

    public void delete(int i) {
        collection.remove(i);
    }

    public int size() {
        return collection.size();
    }

    public Child getNext(UUID prevChildId) {
        if(prevChildId == null ||
                prevChildId.equals(nobody.getID())) {
            return nobody;
        }

        Child prevChild = getChildByID(prevChildId);
        if (prevChild.equals(nobody)) {
            if (collection.size() > 0) {
                return collection.get(0);
            } else {
                return nobody;
            }
        }

        int prevIndex = collection.indexOf(prevChild);
        int nextIndex = (prevIndex + 1) % collection.size();
        return collection.get(nextIndex);
    }

    public List<String> getChildSequence(UUID previousChild)
    {
        Child prevChild = getChildByID(previousChild);
        int prevIndex = collection.indexOf(prevChild);
        List<String> list = new ArrayList<>();
        int nextIndex;
        for (int i=0; i < collection.size(); i++){
            nextIndex = (prevIndex + 1) % collection.size();
            list.add(collection.get(nextIndex).getName());
            prevIndex++;
        }
        return list;
    }

    @Override
    public Iterator<Child> iterator() {
        return collection.iterator();
    }

    public static boolean hasInstance() {
        return instance != null;
    }

    public void saveChild(Context context){
        String jsonString = gson.toJson(collection);
        PrefsManager.saveString(context, PREFS_KEY, jsonString);
    }

    public void loadChild(Context context){
        String value = PrefsManager.loadString(context, PREFS_KEY);
        collection = new ArrayList<>();

        if (value == null || value == "") {
            return;
        }

        List<Child> children = gson.fromJson(value, new TypeToken<List<Child>>() {
        }.getType());
        for (Child child : children) {
            collection.add(child);
        }
    }

    public static Child getNobody() {
        return nobody;
    }
}
package com.prj_peach.practicalparent.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TaskManagerTest {

    @Test
    void add() {
        TaskManager instance = TaskManager.getInstance();
        Child child1 = new Child("child1");
        Task myTask = new Task("New Task", "dec", child1.getID());
        instance.add(myTask);
        instance = TaskManager.getInstance();
        assertTrue(instance.iterator().hasNext());
    }

    @Test
    void getAndDeleteAndSize() {
        TaskManager instance = TaskManager.getInstance();
        Child child1 = new Child("child1");
        Task myTask = new Task("New Task", "dec", child1.getID());
        instance.add(myTask);
        assertEquals(instance.get(0).getTaskName(), "New Task");
        assertEquals(instance.get(0).getDescription(), "dec");
        instance.delete(0);
        assertEquals(instance.size(), 0);
        Task myTask2 = new Task("New Task2", "dec2", child1.getID());
        instance.add(myTask);
        instance.add(myTask2);
        assertEquals(instance.size(), 2);
    }

    @Test
    void iterator() {
        TaskManager instance = TaskManager.getInstance();
        Child child1 = new Child("child1");
        Task myTask = new Task("New Task", "dec", child1.getID());
        Task myTask2 = new Task("New Task2", "dec2", child1.getID());
        assertTrue(instance.iterator().hasNext());
    }

    @Test
    void hasInstance() {
        TaskManager instance = TaskManager.getInstance();
        assertTrue(TaskManager.hasInstance());
    }
}
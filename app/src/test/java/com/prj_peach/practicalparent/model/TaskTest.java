package com.prj_peach.practicalparent.model;

import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.junit.rules.ExpectedException;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TaskTest {
    @Rule
    public final ExpectedException exception = ExpectedException.none();
    @Test
    void setAndGetTaskName() {
        ChildManager childManager = ChildManager.getInstance();
        Child child1 = new Child("child1");
        childManager.add(child1);
        Task myTask = new Task("New Task", "dec",child1.getID());
        assertEquals(myTask.getTaskName(), "New Task");
        myTask.setTaskName("123");
        assertEquals(myTask.getTaskName(), "123");
        assertThrows( IllegalArgumentException.class,
                () -> myTask.setTaskName(""));
    }

    @Test
    void setAndGetDescription() {
        ChildManager childManager = ChildManager.getInstance();
        Child child1 = new Child("child1");
        childManager.add(child1);
        Task myTask = new Task("New Task", "dec", child1.getID());
        assertEquals(myTask.getDescription(), "dec");
        myTask.setDescription("123");
        assertEquals(myTask.getDescription(), "123");
        assertThrows( IllegalArgumentException.class,
                () -> myTask.setTaskName(""));
    }

    @Test
    void setChildAndGetChildID() {
        ChildManager childManager = ChildManager.getInstance();
        Child child1 = new Child("child1");
        childManager.add(child1);
        Task myTask = new Task("New Task", "dec", child1.getID());
        assertEquals(myTask.getChildID(), child1.getID());
        Child child2 = new Child("child2");
        myTask.setChild(child2);
        assertEquals(myTask.getChildID(), child2.getID());
    }

    @Test
    void testToString() {
        ChildManager childManager = ChildManager.getInstance();
        Child child1 = new Child("child1");
        childManager.add(child1);
        Task myTask = new Task("New Task", "dec", child1.getID());
        assertEquals(myTask.toString(),
                "{ " +
                        "Task name: " + "New Task" +
                        "\nDescription: " + "dec" +
                        "\nIt's " + child1.getName() + "'s turn!" +
                        " }"
                );
    }

    @Test
    void toNextChild() {
        ChildManager childManager = ChildManager.getInstance();
        Child child1 = new Child("child1");
        Child child2 = new Child("child2");
        childManager.add(child1);
        childManager.add(child2);
        Task myTask = new Task("New Task", "dec", child1.getID());
        myTask.toNextChild();
        assertEquals(childManager.getChildByID(myTask.getChildID()).getName(), child2.getName());
        myTask.toNextChild();
        assertEquals(childManager.getChildByID(myTask.getChildID()).getName(), child1.getName());
        childManager.delete(0);
        childManager.delete(0);
        assertEquals(childManager.getChildByID(myTask.getChildID()).getName(), childManager.getNobody().getName());
        childManager.add(child1);
        assertEquals(childManager.getChildByID(myTask.getChildID()).getName(), childManager.getNobody().getName());
        myTask.toNextChild();
        assertEquals(childManager.getChildByID(myTask.getChildID()).getName(), child1.getName());
    }
}
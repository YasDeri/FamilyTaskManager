package com.prj_peach.practicalparent.model;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ChildManagerTest {

    @Test
    void testGetInstance(){
        ChildManager instance = ChildManager.getInstance();
        Child child = new Child("Peter");

        instance.add(child);
        instance = ChildManager.getInstance();

        assertTrue(instance.iterator().hasNext());
    }

    @Test
    void testAdd(){
        Child child = new Child("Peter");
        Child child2 = new Child("Adam");
        ChildManager.getInstance().add(child);
        ChildManager.getInstance().add(child2);
        assertEquals(ChildManager.getInstance().get(0), child);
        assertThrows(IllegalArgumentException.class, () ->
                ChildManager.getInstance().add(null)
        );
    }

    @Test
    void testGet(){
        assertEquals(ChildManager.getInstance().get(0).getName(), "Peter");
    }

    @Test
    void testSize(){
        assertEquals(ChildManager.getInstance().size(), 2);
    }

    @Test
    void testGetNext(){
        Child child = new Child("Peter");
        Child child2 = new Child("Adam");

        ChildManager children = new ChildManager();
        children.add(child);
        children.add(child2);

        Child next = children.getNext(child.getID());

        assertEquals(next, child2);

        next = children.getNext(child2.getID());

        assertEquals(next, child);
    }

    @Test
    void testNoChildFound() {
        ChildManager manager = new ChildManager();
        UUID randomUUID = UUID.randomUUID();

        assertEquals(ChildManager.getNobody(), manager.getChildByID(randomUUID));
    }

    @Test
    void testDelete() {
        ChildManager.getInstance().delete(0);
        assertEquals(ChildManager.getInstance().size(), 1);
    }
}
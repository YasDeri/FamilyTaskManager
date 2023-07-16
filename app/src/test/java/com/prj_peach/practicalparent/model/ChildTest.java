package com.prj_peach.practicalparent.model;

import android.net.Uri;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ChildTest {

    @Test
    public void testInvalidConstructorArgs() {

        assertThrows(IllegalArgumentException.class, () ->
                new Child(null)
        );

        assertThrows(IllegalArgumentException.class, () ->
                new Child("")
        );
    }

    @Test
    public void testValidConstructorArgs() {
        String name = "Charles";
        Child child = new Child(name);

    }

    @Test
    public void testEquals() {
        // same child
        Child child1 = new Child("tom");
        assertTrue(child1.equals(child1));

        // different child
        Child child2 = new Child("ben");
        assertFalse(child1.equals(child2));
    }

    @Test
    public void testImgUri() {
        Child child = new Child("harry potter");

        assertEquals(Uri.EMPTY, child.getImageUri());
    }

    @Test
    public void testImgUriSetBadInput() {
        Child child = new Child("harry potter");

        assertThrows(IllegalArgumentException.class, () ->
            child.setImageUri(null)
        );
    }
}
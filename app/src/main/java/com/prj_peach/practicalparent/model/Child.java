package com.prj_peach.practicalparent.model;

import android.net.Uri;

import java.util.UUID;

public class Child {

    private String name;
    private String imagePath;
    private UUID ID;

    public Child(String name) {
        if (name == null) {
            throw new IllegalArgumentException("name cannot be null");
        } else if (name.equals("")) {
            throw new IllegalArgumentException("name cannot be an empty string");
        }

        this.name = name;
        this.imagePath = "";
        this.ID = UUID.randomUUID();
    }

    public String getName() {
        return name;
    }

    public String getImagePath() {
        return imagePath;
    }

    public UUID getID() {
        return ID;
    }

    public void setName(String name) {
        if (name == null || name.equals("")) {
            throw new IllegalArgumentException("Provided name cannot be null or empty");
        }
        this.name = name;
    }

    public void setImagePath(String newImgPath) {
        if (newImgPath == null) {
            throw new IllegalArgumentException("Provided Image path cannot be null");
        }

        imagePath = newImgPath;
    }

    @Override
    public boolean equals(Object obj) {
        Child other = (Child) obj;

        if (name.equals(other.name) &&
            ID.equals(other.ID)) {
            return true;
        }
        return false;
    }
}

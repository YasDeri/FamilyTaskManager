package com.prj_peach.practicalparent.model;

import java.util.UUID;

public class Task {
    private String taskName;
    private String description;
    private UUID childID;

    public Task(String taskName, String description, UUID childID) {
        if (taskName == null) {
            throw new IllegalArgumentException("taskName cannot be null");
        } else if (taskName.equals("")) {
            throw new IllegalArgumentException("taskName cannot be an empty string");
        }
        if (description == null) {
            throw new IllegalArgumentException("description cannot be null");
        } else if (description.equals("")) {
            throw new IllegalArgumentException("description cannot be an empty string");
        }
        ChildManager childManager = ChildManager.getInstance();
        if(childManager.getChildByID(childID) == null)
        {
            throw new IllegalArgumentException("Child UUid is not valid");
        }
        this.taskName = taskName;
        this.description = description;
        this.childID = childID;
    }

    public String getTaskName(){
        return taskName;
    }

    public String getDescription() {
        return description;
    }

    public void setTaskName(String taskName) {
        if (taskName == null || taskName.equals("")) {
            throw new IllegalArgumentException("Provided taskName cannot be null or empty");
        }
        this.taskName = taskName;
    }

    public void setDescription(String description) {
        if (description == null || description.equals("")) {
            throw new IllegalArgumentException("Provided description cannot be null or empty");
        }
        this.description = description;
    }

    public UUID getChildID(){
        return childID;
    }

    public void setChild(Child child) {
        ChildManager childManager = ChildManager.getInstance();
        if(childManager.getChildByID(childID) == null) {
            throw new IllegalArgumentException("Provided description cannot be null or empty");
        }
        childID = child.getID();
    }

    public String toString() {
        ChildManager childManager = ChildManager.getInstance();
        return "{ " +
                "Task name: " + taskName +
                "\nDescription: " + description +
                "\nIt's " + childManager.getChildByID(childID).getName() + "'s turn!" +
                " }";
    }

    public void toNextChild() {
        ChildManager childManager = ChildManager.getInstance();
        if(childManager.getChildByID(childID) == null) {
            if (childManager.size() != 0){
                childID = childManager.get(0).getID();
            }
            else {
                    childID = ChildManager.getNobody().getID();
            }
        }
        Child nextChild = childManager.getNext(childID);
        childID = nextChild.getID();
    }
}

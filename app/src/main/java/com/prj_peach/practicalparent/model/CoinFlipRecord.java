package com.prj_peach.practicalparent.model;


import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

public class CoinFlipRecord {

    public enum FlipResult {
        HEADS,
        TAILS
    }

    private UUID pickedByID;
    private FlipResult result;
    private boolean wonFlip;
    private Date date;


    public CoinFlipRecord(UUID pickedByID, FlipResult result, boolean wonFlip, Date date) {
        if (pickedByID == null) {
            throw new IllegalArgumentException("Child ID cannot be null");
        }

        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }

        if (result == null) {
            throw new IllegalArgumentException("Result cannot be null");
        }

        this.pickedByID = pickedByID;
        this.result = result;
        this.wonFlip = wonFlip;
        this.date = date;
    }

    public UUID getPickedByID() {
        return pickedByID;
    }

    public FlipResult getResult() {
        return result;
    }

    public Date getDate() {
        return date;
    }

    public boolean childWon() {
        return wonFlip;
    }
}

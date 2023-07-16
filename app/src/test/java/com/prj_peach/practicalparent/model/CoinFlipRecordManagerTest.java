package com.prj_peach.practicalparent.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CoinFlipRecordManagerTest {

    @Test
    public void testAddRecordBadInput() {
        CoinFlipRecordManager recordManager = new CoinFlipRecordManager();

        assertThrows(IllegalArgumentException.class, () ->
                recordManager.add(null)
        );
    }

    @Test
    public void testAddRecordValidInput() {
        CoinFlipRecord record = new CoinFlipRecord(
                UUID.randomUUID(),
                CoinFlipRecord.FlipResult.HEADS,
                true,
                Calendar.getInstance().getTime());

        CoinFlipRecordManager recordManager = new CoinFlipRecordManager();
        recordManager.add(record);

        for(CoinFlipRecord r : recordManager) {
            assertEquals(record.getPickedByID(), r.getPickedByID());
            assertEquals(record.getResult(), r.getResult());
            assertEquals(record.getDate(), r.getDate());
        }
    }

    @Test
    public void testGetInstance() {
        CoinFlipRecordManager instance = CoinFlipRecordManager.getInstance();

        CoinFlipRecord record = new CoinFlipRecord(
                UUID.randomUUID(),
                CoinFlipRecord.FlipResult.TAILS,
                false,
                Calendar.getInstance().getTime());

        assertFalse(instance.iterator().hasNext());

        instance.add(record);
        instance = CoinFlipRecordManager.getInstance();

        assertTrue(instance.iterator().hasNext());
    }

    @Test void testGetLastHasRecords() {
        CoinFlipRecordManager manager = new CoinFlipRecordManager();
        UUID childId = UUID.randomUUID();

        manager.add(new CoinFlipRecord(
                childId,
                CoinFlipRecord.FlipResult.TAILS,
                true,
                Calendar.getInstance().getTime()
        ));

        UUID output = manager.getLastChildId();
        assertEquals(childId, output);
    }

    @Test void testGetLastNoRecords() {
        CoinFlipRecordManager recordManager = new CoinFlipRecordManager();

        UUID output = recordManager.getLastChildId();

        assertNull(output);
    }
}
package com.prj_peach.practicalparent.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CoinFlipRecordTest {

    @Test
    void getPickedByValidTest() {
        Child child1 = new Child("yasaman");
        UUID child1ID = child1.getID();
        CoinFlipRecord record = new CoinFlipRecord(
                child1ID,
                CoinFlipRecord.FlipResult.TAILS,
                true,
                Calendar.getInstance().getTime());

        assertEquals(child1ID, record.getPickedByID());
    }

    @Test
    void getResultValidTest() {
        Child child1 = new Child("yasaman");
        CoinFlipRecord record = new CoinFlipRecord(
                child1.getID(),
                CoinFlipRecord.FlipResult.HEADS,
                false,
                Calendar.getInstance().getTime());

        assertEquals(CoinFlipRecord.FlipResult.HEADS, record.getResult());
    }

    @Test
    void getDateValidTest() {
        Child child1 = new Child("yasaman");
        UUID child1ID = child1.getID();
        Date date = Calendar.getInstance().getTime();

        CoinFlipRecord record = new CoinFlipRecord(
                child1ID,
                CoinFlipRecord.FlipResult.HEADS,
                true,
                date);

        assertEquals(date, record.getDate());
    }

    @Test
    void testBadConstructorArgs() {
        Child child1 = new Child("yasaman");
        UUID child1ID = child1.getID();

        //NULL pickedBy
        assertThrows(IllegalArgumentException.class, () ->
                new CoinFlipRecord(
                    null,
                    CoinFlipRecord.FlipResult.TAILS,
                    false,
                    Calendar.getInstance().getTime())
        );

        //NULL result
        assertThrows(IllegalArgumentException.class,
                () -> new CoinFlipRecord(child1ID, null, true, Calendar.getInstance().getTime())
        );
    }

    @Test
    public void testChildWon() {
        CoinFlipRecord record = new CoinFlipRecord(
                UUID.randomUUID(),
                CoinFlipRecord.FlipResult.TAILS,
                true,
                Calendar.getInstance().getTime()
        );

        assertTrue(record.childWon());
    }

}



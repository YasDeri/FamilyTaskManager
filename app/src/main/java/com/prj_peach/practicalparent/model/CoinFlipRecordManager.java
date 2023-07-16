package com.prj_peach.practicalparent.model;


import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

/*
This class manages coin flip records
 */

public class CoinFlipRecordManager implements Iterable<CoinFlipRecord> {

    private static final String PREFS_KEY = "flip";

    private Gson gson = new Gson();

    private List<CoinFlipRecord> coinFlipRecordList = new ArrayList<>();

    public CoinFlipRecordManager() {
        coinFlipRecordList = new ArrayList<>();
    }

    //singleton
    private static CoinFlipRecordManager instance;

    public static CoinFlipRecordManager getInstance() {
        if (instance == null) {
            instance = new CoinFlipRecordManager();
        }
        return instance;
    }


    public void add(CoinFlipRecord coinFlipRecord) {
        if (coinFlipRecord == null) {
            throw new IllegalArgumentException("provided CoinFlipRecord cannot be null");
        }
        coinFlipRecordList.add(coinFlipRecord);
    }

    public UUID getLastChildId() {
        if (coinFlipRecordList.size() == 0) {
            return null;
        }

        return coinFlipRecordList.get(coinFlipRecordList.size() - 1)
                .getPickedByID();
    }

    public static boolean hasInstance() {
        return instance != null;
    }

    public void saveRecord(Context context){
        String jsonString = gson.toJson(coinFlipRecordList);
        PrefsManager.saveString(context, PREFS_KEY, jsonString);
    }

    public void loadRecord(Context context){
        String value = PrefsManager.loadString(context, PREFS_KEY);
        coinFlipRecordList = new ArrayList<>();

        if (value == null || value == "") {
            return;
        }

        List<CoinFlipRecord> flips = gson.fromJson(value, new TypeToken<List<CoinFlipRecord>>() {
        }.getType());
        for (CoinFlipRecord coinFlip : flips) {
            coinFlipRecordList.add(coinFlip);
        }
    }


    @Override
    public Iterator<CoinFlipRecord> iterator() {
        return coinFlipRecordList.iterator();
    }


    public CoinFlipRecord get(int i) {
        return coinFlipRecordList.get(i);
    }
}
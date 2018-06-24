package com.gmail.vkhanh234.SaliensAuto.data.Planet;

import java.util.List;

public class Planet{
    public String id;
    public PlanetState state;
    public List<Zone> zones;

    public Zone getAvailableZone() {
        int maxDiff = Integer.MIN_VALUE;
        Zone res = null;
        for(Zone zone:zones){
            if(zone.captured || zone.capture_progress>=0.92) continue;
            int diff = zone.difficulty;
            if(zone.type==4) diff=4;
            if(maxDiff<diff){
                maxDiff=diff;
                res = zone;
            }
        }
        return res;
    }

    public int[] getDifficulties() {
        int[] result = new int[5];
        for(Zone zone:zones){
            if(zone.captured || zone.capture_progress>=0.92) continue;
            if(zone.type==4) result[4]++;
            else result[zone.difficulty]++;
        }
        return result;
    }

    public int getDiffValue(int diff){
        switch (diff){
            case 1: return 1;
            case 2: return 100;
            case 3: return 10000;
        }
        return 1;
    }
}
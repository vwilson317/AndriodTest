package com.vincent.widget;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by vincent on 2/10/17.
 */

public class WorkRoutine {
    public String RountineName;
    public Exercise[] Excerises;
    //TODO: use a configured value for the routine
    public Integer TargetRepAmount = 8;

    public CurrentWorkout CurrentWorkout;

    public WorkRoutine(String name){
        RountineName = name;
        CurrentWorkout = new CurrentWorkout();

        Excerises = new Exercise[]{
                new Exercise("Chest Press"),
                new Exercise("Dumbbell Flies"),
                new Exercise("Dips")
        };
    }
}


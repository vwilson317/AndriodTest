package com.vincent.widget;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;

/**
 * Created by vincent on 2/10/17.
 */

public class WorkRoutine {
    public String RountineName;
    public Exercise[] Excerises;

    public Dictionary<Exercise, List<SetAndRepGroup>> CurrentWorkout;

    public WorkRoutine(String name){
        RountineName = name;

        Excerises = new Exercise[]{
                new Exercise("Chest Press"),
                new Exercise("Dumbbell Flies"),
                new Exercise("Dips")
        };
    }

    public void AddToCurrentWorkout(Exercise exercise, int setNum, int repNum){
        List<SetAndRepGroup> possibleSetAndRep = CurrentWorkout.get(exercise);
        if(possibleSetAndRep == null)
        {
            List<SetAndRepGroup> currentProgress = new ArrayList<SetAndRepGroup>();
            currentProgress.add(new SetAndRepGroup(setNum, repNum));
            CurrentWorkout.put(exercise, currentProgress);{
            };
        }
        else
        {
            possibleSetAndRep.add(new SetAndRepGroup(setNum, repNum));
        }
    }
}


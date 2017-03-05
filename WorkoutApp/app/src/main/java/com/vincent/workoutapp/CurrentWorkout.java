package com.vincent.workoutapp;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * Created by vincent on 2/11/17.
 */
public class CurrentWorkout{
    public HashMap<OutterClass.Exercise, List<Integer>> ExerciseRepLookup;

    public CurrentWorkout(){
        ExerciseRepLookup = new HashMap<OutterClass.Exercise, List<Integer>>();
    }

    public int GetCurrentSet(OutterClass.Exercise exercise){
        List<Integer> reps = ExerciseRepLookup.get(exercise);
        if(reps != null) {
            return ExerciseRepLookup.get(exercise).size();
        }
        else{
            return 0;
        }
    }

    public void FinishSet(OutterClass.Exercise exercise, int repNum){
        List<Integer> reps = ExerciseRepLookup.get(exercise);
        if(reps == null)
        {
            List<Integer> currentProgress = new ArrayList<Integer>();
            currentProgress.add(repNum);
            ExerciseRepLookup.put(exercise, currentProgress);
        }
        else
        {
            reps.add(repNum);
        }
    }
}

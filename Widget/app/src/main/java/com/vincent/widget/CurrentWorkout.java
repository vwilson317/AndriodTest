package com.vincent.widget;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by vincent on 2/11/17.
 */
public class CurrentWorkout{
    public Dictionary<Exercise, List<Integer>> ExerciseRepLookup;

    public CurrentWorkout(){
        ExerciseRepLookup = new Hashtable<Exercise, List<Integer>>();
    }

    public int GetCurrentSet(Exercise exercise){
        List<Integer> reps = ExerciseRepLookup.get(exercise);
        if(reps != null) {
            return ExerciseRepLookup.get(exercise).size() + 1;
        }
        else{
            return 0;
        }
    }

    public void FinishSet(Exercise exercise, int repNum){
        List<Integer> reps = ExerciseRepLookup.get(exercise);
        if(reps == null)
        {
            List<Integer> currentProgress = new ArrayList<Integer>();
            currentProgress.add(repNum);
            ExerciseRepLookup.put(exercise, currentProgress);{
        };
        }
        else
        {
            reps.add(repNum);
        }
    }
}

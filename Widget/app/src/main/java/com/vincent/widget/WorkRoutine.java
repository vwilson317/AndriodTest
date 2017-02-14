package com.vincent.widget;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vincent on 2/10/17.
 */
class OutterClass{
    public static class WorkRoutine {
        public String RoutineName;
        public List<Exercise> Excerises;

        public CurrentWorkout CurrentWorkout;

        public WorkRoutine(){
            RoutineName = "";
            CurrentWorkout = new CurrentWorkout();
            Excerises = new ArrayList<Exercise>();
        }

        public WorkRoutine(String name) {
            RoutineName = name;
            CurrentWorkout = new CurrentWorkout();

            Excerises = new ArrayList<Exercise>();
        }
    }

    public static class Exercise {
        public String Name;

        public Exercise(){
            Name = "";
        }
        public Exercise(String name){
            Name = name;
        }
    }
}



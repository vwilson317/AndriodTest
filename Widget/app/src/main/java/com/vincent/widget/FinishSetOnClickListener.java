package com.vincent.widget;

import android.view.View;
import android.widget.TextView;

public class FinishSetOnClickListener implements View.OnClickListener{
    private final int reps;
    private final Exercise exercise;
    private final WorkRoutine routine;
    public FinishSetOnClickListener(WorkRoutine routine, Exercise exercise, int reps) {
        this.routine = routine;
        this.exercise = exercise;
        this.reps = reps;
    }

    @Override
    public void onClick(View v)
    {
        routine.CurrentWorkout.FinishSet(exercise, reps);
        TextView currentRepAmountTextView = (TextView)v.findViewById(R.id.currentRepAmount);
        currentRepAmountTextView.setText(routine.TargetRepAmount.toString());
    }
}

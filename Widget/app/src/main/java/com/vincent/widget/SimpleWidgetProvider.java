package com.vincent.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.content.Intent;
import android.app.PendingIntent;
import android.widget.TextView;
import android.view.LayoutInflater;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class SimpleWidgetProvider extends AppWidgetProvider {
    private static final String NEXT_ACTION_NAME = "NEXT_ONCLICK";
    private static final String FINISH_ACTION_NAME = "FINISH_ONCLICK";
    private static final String REP_PLUS_ACTION_NAME = "REP_PLUS_ONCLICK";
    private static final String REP_MINUS_ACTION_NAME = "REP_MINUS_ONCLICK";

    private static final String CURRENT_REP_COUNT = "currentRepCount";
    private static final String CURRENT_EXERCISE_INDEX = "currentExerciseIndex";
    private static final Integer Configured_Rep_Count = 8;

    private WorkRoutine routine = new WorkRoutine("Test Routine");
    private Exercise currentExercise = this.routine.Excerises.get(0);

    @Override
    public void onEnabled(Context context){

    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.w("*** SimpleWidget", "onUpdate called");

        SharedPreferences sharedPreferences = getSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        clear(editor);

        final int count = appWidgetIds.length;
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("routine");

        myRef.setValue(this.routine);

        //TODO: make widget a collection for all configured workout routines

        for (int i = 0; i < count; i++) {
            int widgetId = appWidgetIds[i];
            try{
                RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                        R.layout.simple_widget);

                Log.w("*** Exercise name", this.currentExercise.Name);
                setText(this.currentExercise, Configured_Rep_Count,remoteViews);

                PendingIntent nextIntent = getIntent(context, NEXT_ACTION_NAME, appWidgetIds, 0);
                PendingIntent finishIntent = getIntent(context, FINISH_ACTION_NAME, appWidgetIds, 0);
                PendingIntent repPlusIntent = getIntent(context, REP_PLUS_ACTION_NAME, appWidgetIds, 0);
                PendingIntent repMinusIntent = getIntent(context, REP_MINUS_ACTION_NAME, appWidgetIds, 0);

                remoteViews.setOnClickPendingIntent(R.id.actionButton, nextIntent);
                remoteViews.setOnClickPendingIntent(R.id.finishSetButton, finishIntent);
                remoteViews.setOnClickPendingIntent(R.id.repPlusButton, repPlusIntent);
                remoteViews.setOnClickPendingIntent(R.id.repMinusButton, repMinusIntent);

                appWidgetManager.updateAppWidget(widgetId, remoteViews);
            }catch (Exception e){
                Log.w("Error", e.getMessage());
            }

            /*AppWidgetManager appWidgetManager;
            int widgetId;
            Bundle myOptions = appWidgetManager.getAppWidgetOptions (widgetId);

            // Get the value of OPTION_APPWIDGET_HOST_CATEGORY
            int category = myOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_HOST_CATEGORY, -1);

            // If the value is WIDGET_CATEGORY_KEYGUARD, it's a lockscreen widget
            boolean isKeyguard = category == AppWidgetProviderInfo.WIDGET_CATEGORY_KEYGUARD;

            int baseLayout = isKeyguard ? R.layout.keyguard_widget_layout : R.layout.widget_layout;*/
        }
    }

    private void setText(Exercise currentExercise, Integer currentRepCount, RemoteViews remoteViews) {
        //TODO: use db to store current workout
        int currentSet = this.routine.CurrentWorkout.GetCurrentSet(currentExercise);
        String text = "Set #" + currentSet + " " + currentExercise.Name + "Reps # " + currentRepCount;
        Log.w("*** setText", text);
        remoteViews.setTextViewText(R.id.displayTextView, text);
    }

    private PendingIntent getIntent(Context context, String actionName, int[] appWidgetIds, int requestCode){
        Intent intent = new Intent(context, SimpleWidgetProvider.class);
        intent.setAction(actionName);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        return pendingIntent;
    }

    private void increaseReps(Integer currentRepCount, SharedPreferences.Editor editor){
        int repUpperBound = 20;
        if(currentRepCount < repUpperBound){
            currentRepCount++;
            editor.putInt(CURRENT_REP_COUNT, currentRepCount);
            editor.apply();
        }
    }

    private void decreaseReps(Integer currentRepCount, SharedPreferences.Editor editor) {
        if(currentRepCount > 0){
            currentRepCount--;
            editor.putInt(CURRENT_REP_COUNT, currentRepCount);
            editor.apply();
        }
    }

    private View getView(Context context){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService( LAYOUT_INFLATER_SERVICE );
        View view = inflater.inflate(R.layout.simple_widget, null);
        return view;
    }

    private void onNextClickHandler(Integer currentExerciseIndex, SharedPreferences.Editor editor){
        //Reset rep count
        currentExerciseIndex++;

        if(currentExerciseIndex > routine.Excerises.size() -1){
            currentExerciseIndex = 0;
        }

        editor.putInt(CURRENT_EXERCISE_INDEX, currentExerciseIndex);
        editor.apply();
    }

    private void onFinishClickHandler(Exercise currentExercise, int currentRepCount,
                                      Integer defaultRepCount, SharedPreferences.Editor editor){
        editor.putInt(CURRENT_REP_COUNT, defaultRepCount);
        editor.apply();

        this.routine.CurrentWorkout.FinishSet(currentExercise, currentRepCount);
    }

    private void clear(SharedPreferences.Editor editor){
        editor.clear();
        editor.commit();
    }

    private SharedPreferences getSharedPreferences(Context context){
        String perfFileStr = context.getResources().getString(R.string.perf_file);
        SharedPreferences sharedPreferences = context.getSharedPreferences(perfFileStr, context.MODE_PRIVATE);
        return sharedPreferences;
    }

    @Override
    public void onReceive(Context context, Intent intent){
        String actionName = intent.getAction();
        Log.w("*** onReceive", "Intent action " + actionName);

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                R.layout.simple_widget);

        SharedPreferences sharedPreferences = getSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Integer defaultRepCount = context.getResources().getInteger(R.integer.default_rep_count);
        Integer currentRepCount = sharedPreferences.getInt(CURRENT_REP_COUNT, defaultRepCount);
        Integer currentExerciseIndex = sharedPreferences.getInt(CURRENT_EXERCISE_INDEX, 0);

        Exercise currentExercise = null;
        if(currentExerciseIndex <= this.routine.Excerises.size() -1){
           currentExercise = this.routine.Excerises.get(currentExerciseIndex);
        }

        switch (actionName){
            case NEXT_ACTION_NAME:
                this.onNextClickHandler(currentExerciseIndex, editor);
                break;
            case FINISH_ACTION_NAME:
                this.onFinishClickHandler(currentExercise, currentRepCount, defaultRepCount, editor);
                break;
            case REP_PLUS_ACTION_NAME:
                this.increaseReps(currentRepCount, editor);
                break;
            case REP_MINUS_ACTION_NAME:
                this.decreaseReps(currentRepCount, editor);
                break;
        }

        if(currentExercise != null){
            setText(currentExercise, currentRepCount, remoteViews);

            ComponentName componentName = new ComponentName(context, SimpleWidgetProvider.class);
            AppWidgetManager instance = AppWidgetManager.getInstance(context);
            instance.updateAppWidget(componentName, remoteViews);
        }

        super.onReceive(context, intent);
    }
}
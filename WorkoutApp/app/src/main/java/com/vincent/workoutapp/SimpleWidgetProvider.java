package com.vincent.workoutapp;

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
import android.view.LayoutInflater;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
//import com.google.gson.*;
//import com.google.gson.reflect.TypeToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class SimpleWidgetProvider extends AppWidgetProvider {
    private static final String DB_SERVER = "server/saving-data/workout-tracker";

    private static final String NEXT_ACTION_NAME = "NEXT_ONCLICK";
    private static final String FINISH_ACTION_NAME = "FINISH_ONCLICK";
    private static final String REP_PLUS_ACTION_NAME = "REP_PLUS_ONCLICK";
    private static final String REP_MINUS_ACTION_NAME = "REP_MINUS_ONCLICK";

    private static final String CURRENT_REP_COUNT = "currentRepCount";
    private static final String CURRENT_EXERCISE_INDEX = "currentExerciseIndex";
    private static final String CURRENT_ROUTINE = "currentRoutine";
    private static final Integer Configured_Rep_Count = 8;

    @Override
    public void onEnabled(Context context){
        System.out.println("*** SimpleWidget onUpdate called");

        SharedPreferences sharedPreferences = getSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.w("*** SimpleWidget", "onUpdate called");

        // Build the intent to call the service
        Intent intent = new Intent(context.getApplicationContext(), UpdateWidgetService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);

        // Update the widgets via the service
        context.startService(intent);

        SharedPreferences sharedPreferences = getSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        clear(editor);

        final int count = appWidgetIds.length;
        DatabaseReference myRef = getDbReference();

        OutterClass.WorkRoutine routine = null;
        OutterClass.Exercise currentExercise = null;
        String currentRoutineJson = sharedPreferences.getString(CURRENT_ROUTINE, "");
        if (currentRoutineJson == "" || currentRoutineJson == null){
        routine = new OutterClass.WorkRoutine("Test Routine");

        boolean dips = Collections.addAll(routine.Excerises, new OutterClass.Exercise[]{
                new OutterClass.Exercise("Chest Press"),
                new OutterClass.Exercise("Dumbbell Flys"),
                new OutterClass.Exercise("Dips")});

        editor.putString(CURRENT_ROUTINE, toJson(routine));
        editor.apply();
        //Save data to realtime db
        //DatabaseReference routineRef = myRef.child("routines");
        //addValueEventHandler(myRef);

        //routineRef.setValue(routine);
        }
        else{
            routine = fromJson(currentRoutineJson);
        }

        if(routine != null || routine.Excerises.size() > 0) {
            System.out.println("Bad routine: Json = " + currentRoutineJson);
            currentExercise = routine.Excerises.get(0);
        }
        //TODO: make widget a collection for all configured workout routines

        for (int i = 0; i < count; i++) {
            int widgetId = appWidgetIds[i];
            try{
                RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                        R.layout.simple_widget);

                Log.w("*** Exercise name", currentExercise.Name);
                int currentSet = routine.CurrentWorkout.GetCurrentSet(currentExercise);
                setText(currentSet, currentExercise, Configured_Rep_Count,remoteViews);

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

    private DatabaseReference getDbReference(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(DB_SERVER);
        return myRef;
    }

    private void addValueEventHandler(DatabaseReference dbRef){
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.println("onDataChange called");
//                GenericTypeIndicator<List<OutterClass.WorkRoutine>> type = new GenericTypeIndicator<List<OutterClass.WorkRoutine>>() {};
//                List<OutterClass.WorkRoutine> routines = dataSnapshot.getValue(type);
//                System.out.println(dataSnapshot.getKey());
//                System.out.println("Key: " + dataSnapshot.getKey() + " RoutineName: " + routines.get(0).RoutineName);
                List routines = new ArrayList<>();
                for(DataSnapshot currentSnapshot: dataSnapshot.getChildren()){
                    routines.add(currentSnapshot.getValue());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

//    private void addChildEventHandler(DatabaseReference dbRef){
//        dbRef.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
//                Post newPost = dataSnapshot.getValue(OutterClass.WorkRoutine.class);
//                System.out.println("Author: " + newPost.author);
//                System.out.println("Title: " + newPost.title);
//                System.out.println("Previous Post ID: " + prevChildKey);
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {}
//
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {}
//
//            @Override
//            public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {}
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {}
//        });
//    }

    private void setText(int currentSet, OutterClass.Exercise currentExercise, Integer currentRepCount, RemoteViews remoteViews) {
        //TODO: use db to store current workout
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
        System.out.println("increase reps function call");
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

    private void onNextClickHandler(Integer currentExerciseIndex, int routineExeriseCount, SharedPreferences.Editor editor){
        //Reset rep count
        currentExerciseIndex++;

        if(currentExerciseIndex > routineExeriseCount -1){
            currentExerciseIndex = 0;
        }

        editor.putInt(CURRENT_EXERCISE_INDEX, currentExerciseIndex);
        editor.apply();
    }

    private void onFinishClickHandler(OutterClass.WorkRoutine currentWorkoutRoutine,
                                      OutterClass.Exercise currentExercise, int currentRepCount,
                                      Integer defaultRepCount, SharedPreferences.Editor editor){
        editor.putInt(CURRENT_REP_COUNT, defaultRepCount);

        currentWorkoutRoutine.CurrentWorkout.FinishSet(currentExercise, currentRepCount);
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

    private String toJson(OutterClass.WorkRoutine workoutRoutine){
        Gson gson = new Gson();
        return gson.toJson(workoutRoutine);
        //return "";
    }

    private OutterClass.WorkRoutine fromJson(String workoutRoutineJson){
        Gson gson = new Gson();
        Type type;
        type = new TypeToken<OutterClass.WorkRoutine>(){}.getType();
        return gson.fromJson(workoutRoutineJson, type);
        //ObjectMapper mapper = new ObjectMapper();
        //mapper.
        //return null;
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

        //Get current routine from shared pref
        String currentRoutineJson = sharedPreferences.getString(CURRENT_ROUTINE, "");
        OutterClass.WorkRoutine currentRoutine;
        currentRoutine = fromJson(currentRoutineJson);

        //DatabaseReference dbRef = getDbReference();
        //addValueEventHandler(dbRef);

        if(currentRoutine != null){
            OutterClass.Exercise currentExercise = null;
            if(currentExerciseIndex <= currentRoutine.Excerises.size() - 1){
                currentExercise = currentRoutine.Excerises.get(currentExerciseIndex);
            }

            switch (actionName){
                case NEXT_ACTION_NAME:
                    this.onNextClickHandler(currentExerciseIndex, currentRoutine.Excerises.size(), editor);
                    break;
                case FINISH_ACTION_NAME:
                    this.onFinishClickHandler(currentRoutine, currentExercise, currentRepCount, defaultRepCount, editor);
                    break;
                case REP_PLUS_ACTION_NAME:
                    this.increaseReps(currentRepCount, editor);
                    break;
                case REP_MINUS_ACTION_NAME:
                    this.decreaseReps(currentRepCount, editor);
                    break;
            }

            //save currentRoutine to sharedPref
            String json = toJson(currentRoutine);
            editor.putString(CURRENT_ROUTINE, json);
            editor.apply();

            System.out.println("CurrentExercise: " + currentExercise);
            if(currentExercise != null){
                int currentSet = currentRoutine.CurrentWorkout.GetCurrentSet(currentExercise);
                setText(currentSet ,currentExercise, currentRepCount, remoteViews);

                ComponentName componentName = new ComponentName(context, SimpleWidgetProvider.class);
                AppWidgetManager instance = AppWidgetManager.getInstance(context);
                instance.updateAppWidget(componentName, remoteViews);
            }
        }

        super.onReceive(context, intent);
    }
}
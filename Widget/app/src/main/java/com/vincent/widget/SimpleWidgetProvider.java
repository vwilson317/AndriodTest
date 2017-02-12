package com.vincent.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.content.Intent;
import android.app.PendingIntent;
import android.widget.TextView;
import android.view.LayoutInflater;

import java.util.Set;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class SimpleWidgetProvider extends AppWidgetProvider {
    private final WorkRoutine routine;
    private static final String NEXT_ACTION_NAME = "NEXT_ONCLICK";
    private static final String FINISH_ACTION_NAME = "FINISH_ONCLICK";
    private static final String REP_PLUS_ACTION_NAME = "REP_PLUS_ONCLICK";
    private static final String REP_MINUS_ACTION_NAME = "REP_MINUS_ONCLICK";

    public SimpleWidgetProvider(){
        Log.w("*** SimpleWidget", "constuctor called");

        //TODO: make widget a collection for all configured workout routines
        this.routine = new WorkRoutine("Test Routine");
    }

    @Override
    public void onEnabled(Context context){

    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.w("*** SimpleWidget", "onUpdate called");

        final int count = appWidgetIds.length;

        for (int i = 0; i < count; i++) {
            int widgetId = appWidgetIds[i];
            try{
                //int number = new Random().nextInt((routine.Excerises.length - 1) + 1);
                RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                        R.layout.simple_widget);

                Exercise currentExercise = routine.Excerises[0];
                Log.w("*** Exercise name", currentExercise.Name);
                TextView currentExerciseTextView = (TextView)getView(context).findViewById(R.id.currentExcerise);
                currentExerciseTextView.setText(currentExercise.Name);
                Log.w("*** currentExerciseText", currentExerciseTextView.getText().toString());
                setText(context, currentExercise, remoteViews);

                PendingIntent nextIntent = getIntent(context, NEXT_ACTION_NAME, appWidgetIds);
                PendingIntent finishIntent = getIntent(context, FINISH_ACTION_NAME, appWidgetIds);
                PendingIntent repPlusIntent = getIntent(context, REP_PLUS_ACTION_NAME, appWidgetIds);
                PendingIntent repMinusIntent = getIntent(context, REP_MINUS_ACTION_NAME, appWidgetIds);

                PendingIntent updateIntent = getIntent(context, AppWidgetManager.ACTION_APPWIDGET_UPDATE, appWidgetIds);
                remoteViews.setOnClickPendingIntent(R.id.actionButton, updateIntent);
                remoteViews.setOnClickPendingIntent(R.id.finishSetButton, updateIntent);
                remoteViews.setOnClickPendingIntent(R.id.repPlusButton, updateIntent);
                remoteViews.setOnClickPendingIntent(R.id.repMinusButton, updateIntent);

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

    private void setText(Context context, Exercise currentExercise, RemoteViews remoteViews) {
        int repAmount = getRepAmount(null, context);
        int currentSet = routine.CurrentWorkout.GetCurrentSet(currentExercise);
        String text = "Set #" + currentSet + " " + currentExercise.Name + "Reps # " + repAmount;
        remoteViews.setTextViewText(R.id.displayTextView, text);
    }

    private int getRepAmount(TextView textView, Context context) {
        if(textView == null && context != null){
            View view = getView(context);
            textView = (TextView)view.findViewById(R.id.currentRepAmount);
        }
        String repAmountStr = textView.getText().toString();
        return Integer.parseInt(repAmountStr);
    }

    private PendingIntent getIntent(Context context, String actionName, int[] appWidgetIds){
        Intent intent = new Intent(context, SimpleWidgetProvider.class);
        intent.setAction(actionName);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        return pendingIntent;
    }

    private void IncreaseReps(Context context){
        View view = getView(context);
        TextView currentRepAmountTextView = (TextView)view.findViewById(R.id.currentRepAmount);

        Integer currentRepCount = getRepAmount(currentRepAmountTextView, null);
        currentRepCount++;
        currentRepAmountTextView.setText(currentRepCount.toString());
    }

    private View getView(Context context){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService( LAYOUT_INFLATER_SERVICE );
        View view = inflater.inflate(R.layout.simple_widget, null);
        return view;
    }

    private Exercise getCurrentExercise(View view){
        TextView currentExceriseTextView = (TextView)view.findViewById(R.id.currentExcerise);
        String currentExceriseName = currentExceriseTextView.getText().toString();
        Log.w("*** getCurrentExercise", currentExceriseName);
        Exercise exercise = null;
        Set<Exercise> exercises = routine.CurrentWorkout.ExerciseRepLookup.keySet();
        for (Exercise currentExcerise : exercises)
        {
            if(currentExcerise.Name == currentExceriseName)
            {
                exercise = currentExcerise;
            }
        }
        return exercise;
    }

    @Override
    public void onReceive(Context context, Intent intent){
        String actionName = intent.getAction();
        Log.w("*** onReceive", "Intent action " + actionName);

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                R.layout.simple_widget);

        super.onReceive(context, intent);

        switch (actionName){
            case NEXT_ACTION_NAME:
                break;
            case FINISH_ACTION_NAME:
                break;
            case REP_PLUS_ACTION_NAME:
                this.IncreaseReps(context);
                break;
            case REP_MINUS_ACTION_NAME:
                break;
        }

        View view = getView(context);
        Exercise currentExercise = getCurrentExercise(view);
        Log.w("*** currentExercise", (currentExercise == null) ? "NUll": currentExercise.Name);
        if(currentExercise != null)
            setText(context, currentExercise, remoteViews);
    }
}
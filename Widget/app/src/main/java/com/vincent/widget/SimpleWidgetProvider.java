package com.vincent.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.content.Intent;
import android.app.PendingIntent;
import android.widget.TextView;
import android.view.LayoutInflater;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class SimpleWidgetProvider extends AppWidgetProvider {
    private final WorkRoutine routine;
    private static final String NEXT_ACTION_NAME = "NEXT_ONCLICK";
    private static final String FINISH_ACTION_NAME = "FINISH_ONCLICK";
    private static final String REP_PLUS_ACTION_NAME = "REP_PLUS_ONCLICK";
    private static final String REP_MINUS_ACTION_NAME = "REP_MINUS_ONCLICK";
    private Integer currentRepCount;
    private Exercise currentExercise;
    private Integer currentExerciseIndex;

    private static final Integer Configured_Rep_Count = 8;

    public SimpleWidgetProvider(){
        Log.w("*** SimpleWidget", "constuctor called");

        //TODO: make widget a collection for all configured workout routines
        this.routine = new WorkRoutine("Test Routine");
        this.currentExerciseIndex = 0;
        this.currentExercise = this.routine.Excerises[this.currentExerciseIndex];
        //TODO: use a configuration vaule
        this.currentRepCount = Configured_Rep_Count;
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

                Log.w("*** Exercise name", currentExercise.Name);
                setText(context, currentExercise, remoteViews);

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

    private void setText(Context context, Exercise currentExercise, RemoteViews remoteViews) {
        int currentSet = routine.CurrentWorkout.GetCurrentSet(currentExercise);
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

    private void increaseReps(){
        currentRepCount++;
    }

    private View getView(Context context){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService( LAYOUT_INFLATER_SERVICE );
        View view = inflater.inflate(R.layout.simple_widget, null);
        return view;
    }

    private void onNextClickHandler(){
        //Reset rep count
        if(this.currentExerciseIndex > routine.Excerises.length){
            this.currentExerciseIndex = 0;
        }
        currentExerciseIndex++;
        currentExercise = routine.Excerises[this.currentExerciseIndex];
        currentRepCount = Configured_Rep_Count;
    }

    private void onFinishClickHandler(){
        this.routine.CurrentWorkout.FinishSet(currentExercise, currentRepCount);
    }

    @Override
    public void onReceive(Context context, Intent intent){
        String actionName = intent.getAction();
        Log.w("*** onReceive", "Intent action " + actionName);

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                R.layout.simple_widget);

        switch (actionName){
            case NEXT_ACTION_NAME:
                this.onNextClickHandler();
                break;
            case FINISH_ACTION_NAME:
                this.onFinishClickHandler();
                break;
            case REP_PLUS_ACTION_NAME:
                this.increaseReps();
                break;
            case REP_MINUS_ACTION_NAME:
                break;
        }

        ComponentName componentName = new ComponentName(context, SimpleWidgetProvider.class);
        AppWidgetManager instance = AppWidgetManager.getInstance(context);
//        int[] appIds = instance.getAppWidgetIds(componentName);
        instance.updateAppWidget(componentName, remoteViews);

        super.onReceive(context, intent);

//        Log.w("*** currentExercise", (currentExercise == null) ? "NUll": currentExercise.Name);
//        if(currentExercise != null)
//            setText(context, currentExercise, remoteViews);

        //context.sendBroadcast(new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE));
    }
}
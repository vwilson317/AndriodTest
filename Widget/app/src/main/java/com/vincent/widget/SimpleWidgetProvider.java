package com.vincent.widget;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.content.Intent;
import android.app.PendingIntent;
import android.os.BaseBundle;
import android.widget.TextView;
import android.view.LayoutInflater;

import java.util.List;
import java.util.Random;
import java.util.zip.Inflater;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class SimpleWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int count = appWidgetIds.length;

        //TODO: make widget a collection for all configured workout routines
        WorkRoutine routine = new WorkRoutine("Test Routine");

        for (int i = 0; i < count; i++) {
            int widgetId = appWidgetIds[i];
            try{
                //int number = new Random().nextInt((routine.Excerises.length - 1) + 1);
                Exercise currenentExercise = routine.Excerises[0];

                RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                        R.layout.simple_widget);
                LayoutInflater inflater = (LayoutInflater) context.getSystemService( LAYOUT_INFLATER_SERVICE );
                View view = inflater.inflate(R.layout.simple_widget, null);
                TextView currentRepAmountTextView = (TextView)view.findViewById(R.id.currentRepAmount);
               int repAmount = Integer.parseInt(currentRepAmountTextView.getText().toString());
//                act.findViewById(R.id.repPlusButton)
//                        .setOnClickListener(new RepPlusOnClickListener(repAmount));
//
//                act.findViewById(R.id.repMinusButton)
//                        .setOnClickListener(new RepMinusOnClickListener(repAmount));
//
//                act.findViewById(R.id.finishSetButton).setOnClickListener(new FinishSetOnClickListener(routine, currenentExercise, repAmount));

                int currentSet = routine.CurrentWorkout.GetCurrentSet(currenentExercise);
                String text = "Set #" + currentSet + " " + currenentExercise.Name + "Reps # " + repAmount;
                remoteViews.setTextViewText(R.id.textView, text);

                Intent intent = new Intent(context, SimpleWidgetProvider.class);
                intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                        0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                remoteViews.setOnClickPendingIntent(R.id.actionButton, pendingIntent);
                appWidgetManager.updateAppWidget(widgetId, remoteViews);
            }catch (Exception e){
                Log.e("", e.getMessage());
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
}